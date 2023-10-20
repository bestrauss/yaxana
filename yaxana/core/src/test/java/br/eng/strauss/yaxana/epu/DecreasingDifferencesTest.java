package br.eng.strauss.yaxana.epu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Algorithm;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-10
 */
public final class DecreasingDifferencesTest extends YaxanaTest
{

   @WithAlgorithms({ Algorithm.YAXANA })
   @Test
   public void manualTest()
   {

      int N = 24;
      final Algebraic c = new Algebraic(0xFFFFFF).sqrt().sub(new Algebraic(0xFFFFFE).sqrt());
      Algebraic a = c;
      for (int k = 0; k < 15; k++, N += 24)
      {
         System.out.format("%s\n", a);
         final AtomicInteger prec = new AtomicInteger();
         final int signum = a.signum(p -> prec.set(p));
         System.out.format("|a| = %s", a.approximation(52 + N).abs().toString());
         System.out.format(" [%d] %s\n", prec.get(), signum == 1 ? "OK" : "FAIL");
         assertEquals(1, signum);
         a = nwd(a).sub(a);
      }
      assertEquals(1, 1);
   }

   /**
    * Returns a modified clone of a given {@link Algebraic} where all operands are replaced by their
    * original value decremented by one.
    */
   private static Algebraic nwd(final Algebraic a)
   {

      return switch (a.type())
      {
         // @formatter:off
         case TERMINAL -> new Algebraic(a.approximation().sub(BigFloat.ONE));
         case ADD      -> nwd(a.left()).add(nwd(a.right()));
         case SUB      -> nwd(a.left()).sub(nwd(a.right()));
         case MUL      -> nwd(a.left()).mul(nwd(a.right()));
         case DIV      -> nwd(a.left()).div(nwd(a.right()));
         case NEG      -> nwd(a.left()).neg();
         case ABS      -> nwd(a.left()).abs();
         case POW      -> nwd(a.left()).pow(a.index());
         case ROOT     -> nwd(a.left()).root(a.index());
         // @formatter:on
      };
   }
}

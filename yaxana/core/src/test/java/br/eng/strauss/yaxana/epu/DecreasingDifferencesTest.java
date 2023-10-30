package br.eng.strauss.yaxana.epu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Algorithm;
import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-10
 */
public final class DecreasingDifferencesTest extends YaxanaTest
{

   @Test
   public void testX()
   {

      final Algebraic a = new Algebraic("\\16777214-\\16777213-(\\16777215-\\16777214)");
      final Algebraic b = new Algebraic("\\16777214+\\16777213-(\\16777215+\\16777214)");
      System.out.format("|a| = %s\n", a.approximation(100).abs().toString());
      System.out.format("|b| = %s\n", b.approximation(100).abs().toString());
   }

   @WithAlgorithms({ Algorithm.YAXANA })
   @Test
   public void testMinus()
   {

      for (final boolean addBigNumber : new boolean[] { false, true })
      {
         int N = 24;
         final Algebraic c = new Algebraic(0xFFFFFF).sqrt().sub(new Algebraic(0xFFFFFE).sqrt());
         Algebraic a = c;
         for (int k = 0; k < 16; k++, N += 24)
         {
            final AtomicInteger prec = new AtomicInteger();
            final Algebraic b = addBigNumber
                  ? new Algebraic(9999).add(a.left()).sub(new Algebraic(9999).add(a.right()))
                  : a;
            final Robust robust = Robust.valueOf(b.toString());
            final int signum = robust.signum();
            System.out.format("%s\n", robust);
            System.out.format("|a| = %s", a.approximation(52 + N).abs().toString());
            System.out.format(" [%d] %s\n", prec.get(), signum == 1 ? "OK" : "FAIL");
            assertEquals(1, signum);
            a = nwd(a).sub(a);
         }
      }
   }

   @WithAlgorithms({ Algorithm.YAXANA })
   @Test
   public void testPow()
   {

      int N = 24;
      final Algebraic c = new Algebraic(0xFFFFFF).sqrt().sub(new Algebraic(0xFFFFFE).sqrt());
      Algebraic a = c;
      for (int k = 0; k < 8; k++, N += 24)
      {
         System.out.format("%s\n", a);
         final AtomicInteger prec = new AtomicInteger();
         final int signum = a.sub(new Algebraic("1P-2047")).signum(p -> prec.set(p));
         System.out.format("|a| = %s", a.approximation(52 + N).abs().toString());
         System.out.format(" [%d] %s\n", prec.get(), signum == 1 ? "OK" : "FAIL");
         assertEquals(1, signum);
         a = a.pow(2);
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

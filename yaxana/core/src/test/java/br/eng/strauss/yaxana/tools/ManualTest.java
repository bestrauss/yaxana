package br.eng.strauss.yaxana.tools;

import static java.lang.Math.max;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.pdc.Ld;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauß
 * @since 2023-09
 */
public final class ManualTest extends YaxanaTest
{

   @Test
   public void test()
   {

      final Robust robust = Robust.valueOf("1/(\\2+1)-(\\2-1)");
      assertEquals(robust, Robust.ZERO);
      assertTrue(robust == Robust.ZERO);
   }

   @Test
   public void test2()
   {

      final Algebraic e0 = (Algebraic) Robust.valueOf("root(2.5^128+1, 128)").toSyntaxTree();
      final Algebraic e1 = (Algebraic) Robust.valueOf("root(2.5^128-1, 128)").toSyntaxTree();
      final int prec = max(precision(e0), precision(e1));
      final Algebraic f = new Algebraic(BigFloat.twoTo(prec));
      final Algebraic f0 = f.mul(e0);
      final Algebraic f1 = f.mul(e1);
      final Algebraic a = f0.div(f1);
      final Algebraic b = f1.div(f0);
      System.out.format("prec = %s\n", prec);
      System.out.format("a = %s\n", a.approximation(1));
      System.out.format("b = %s\n", b.approximation(1));
      System.out.format("e0-e1 = %s\n", e0.sub(e1).abs().approximation(52));
      System.out.format("a-b   = %s\n", a.sub(b).abs().approximation(52));
   }

   protected final int precision(final Algebraic value)
   {

      // @formatter:off
      return switch (value.type())
      {
         case TERMINAL -> 1 + flatLength(value);
         case ADD, SUB, MUL, DIV -> max(precision(value.left()), precision(value.right()));
         case NEG, ABS, ROOT -> precision(value.left());
         case POW -> precision(value.left()) * Ld.ceilOfLdOfAbsOf(value.index());
      };
      // @formatter:on
   }

   /**
    * Returns the "flat length" of the absolute value of the approximation of a given
    * {@link Algebraic} value.
    * <p>
    * The "flat length" is the number of significant bits of the binary representation of the
    * absolute value of the approximation of a given {@link Algebraic} value, plus the number of
    * zero-bits between the significant bits and the binary point if applicable. Examples:
    * 
    * <pre>
    *  001xxxxxxx10000.00000
    *    ^^^^^^^^^^^^^       scale >= 0 => bitlength + scale
    *  001xxxxxxx1.000000000
    *    ^^^^^^^^^           scale >= 0 => bitlength + scale
    *  001xxxxxxxxx.xxxxx100
    *    ^^^^^^^^^^^^^^^^^   scale < 0 => max(bitlength, -scale)
    *  0000.0001xxxxxxxxx100
    *       ^^^^^^^^^^^^^^   scale < 0 => max(bitlength, -scale)
    * </pre>
    * 
    * @param value
    *           An {@link Algebraic} value.
    * @return the "flat length" of the given {@code value}.
    */
   static int flatLength(final Algebraic value)
   {

      final BigFloat abs = value.approximation().abs();
      final int bl = abs.unscaledValue().bitLength();
      final int sc = abs.scale();
      // return sc >= 0 ? bl + sc : value.type() == TERMINAL ? max(bl, -sc) : -sc;
      return sc >= 0 ? bl + sc : max(bl, -sc);
   }
}

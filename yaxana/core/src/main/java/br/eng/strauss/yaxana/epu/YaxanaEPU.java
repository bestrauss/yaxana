package br.eng.strauss.yaxana.epu;

import static java.lang.Math.max;

import br.eng.strauss.yaxana.Type;
import br.eng.strauss.yaxana.big.BigFloat;

/**
 * Heuristic EPU implementation that
 * <ul>
 * <li>passes all tests,
 * <li>is not proven to work for all cases.
 * </ul>
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
final class YaxanaEPU extends RootBoundEPU
{

   @Override
   protected final int sufficientPrecision(final Algebraic value)
   {

      // @formatter:off
      final Algebraic left = value.left ();
      final Algebraic rite = value.right();
      if (left != null) { sufficientPrecision(left); }
      if (rite != null) { sufficientPrecision(rite); }
      switch (value.type())
      {
         case TERMINAL -> value.vp = flatLength(value.approximation());
         case ADD, SUB -> value.vp = max(flatLength(value.approximation()), max(left.vp, rite.vp));
         case MUL, DIV -> value.vp = left.vp + rite.vp;
         case NEG, ABS -> value.vp = left.vp;
         case POW      -> value.vp = left.vp;
         case ROOT     -> value.vp = 1 + (left.vp - 1 >> 1);
      }
      switch (value.type())
      {
         case TERMINAL      -> value.vn = 0;
         case ADD, SUB      -> value.vn = max(left.vn, rite.vn) + (mayVanish(value) ? 2 * max(left.vp, rite.vp) : 0);
         case MUL, DIV      -> value.vn = max(left.vn, rite.vn);
         case NEG, ABS, POW -> value.vn = left.vn;
         case ROOT          -> value.vn = left.vn + left.vp + value.index();
      }
      return value.vp + value.vn;
      // @formatter:on
   }

   private static boolean mayVanish(final Algebraic value)
   {

      if (value.type() == Type.ADD)
      {
         return value.left().approximation().signum() != value.right().approximation().signum();
      }
      else
      {
         return value.left().approximation().signum() == value.right().approximation().signum();
      }
   }

   /**
    * Returns the "flat length" of a given {@link BigFloat} value.
    * <p>
    * The "flat length" is the number of significant bits of the binary representation of the
    * absolute value of a given {@link BigFloat} value, plus the number of zero-bits between the
    * block of significant bits and the binary point if applicable. Examples:
    * 
    * <pre>
    *  001xxxxxxx10000.00000
    *    ^^^^^^^^^^^^^       scale >= 0 => bitlength + scale
    *  001xxxxxxx1.000000000
    *    ^^^^^^^^^           scale >= 0 => bitlength + scale
    *  001xxxxxxxxx.xxxxx100
    *    ^^^^^^^^^^ ^^^^^^   scale < 0 => max(bitlength, -scale)
    *  0000.0001xxxxxxxxx100
    *       ^^^^^^^^^^^^^^   scale < 0 => max(bitlength, -scale)
    * </pre>
    * 
    * @param value
    *           A {@link BigFloat} value.
    * @return the "flat length" of the given {@code value}.
    */
   static int flatLength(final BigFloat value)
   {

      final BigFloat abs = value.abs();
      final int bl = abs.unscaledValue().bitLength();
      final int sc = abs.scale();
      return sc >= 0 ? bl + sc : max(bl, -sc);
   }
}
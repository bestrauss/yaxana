package br.eng.strauss.yaxana.epu;

import static java.lang.Math.max;

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
public final class YaxanaEPU extends RootBoundEPU
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
         case TERMINAL -> value.vp = flatLength(value);
         case ADD, SUB -> value.vp = max(left.vp, rite.vp);
         case MUL, DIV -> value.vp = max(flatLength(value), max(left.vp, rite.vp));
         case NEG, ABS -> value.vp = left.vp;
         case POW      -> value.vp = max(flatLength(value), left.vp);
         case ROOT     -> value.vp = 1 + (left.vp - 1 >> 1);
      }
      switch (value.type())
      {
         case TERMINAL           -> value.vn = 0;
         case ADD, SUB, MUL, DIV -> value.vn = max(left.vn, rite.vn);
         case NEG, ABS, POW      -> value.vn = left.vn;
         case ROOT               -> value.vn = left.vn + left.vp;
      }
      return value.vp + value.vn;
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
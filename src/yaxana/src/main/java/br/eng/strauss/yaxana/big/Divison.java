package br.eng.strauss.yaxana.big;

import static br.eng.strauss.yaxana.big.BigFloat.overflow;

import java.math.BigInteger;
import java.math.RoundingMode;

import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.exc.RoundingNecessaryException;
import br.eng.strauss.yaxana.exc.UnreachedException;

/**
 * Implementation of the division operation of {@link BigFloat}.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
final class Divison
{

   /**
    * Returns {@code thiz / that} rounded, or {@code null} if there is no rounder or a rounder with
    * {@code precision = 0}.
    * 
    * @param thiz
    *           divisor.
    * @param that
    *           dividend.
    * @param rounder
    *           rounder.
    * @return {@code thiz / that} rounded, or {@code null}.
    */
   public static BigFloat div(final BigFloat thiz, final BigFloat that, final Rounder rounder)
   {

      if (that.signum() == 0)
      {
         throw new DivisionByZeroException();
      }
      if (thiz.signum() == 0)
      {
         return BigFloat.ZERO;
      }
      if (that.isOne())
      {
         return thiz;
      }
      if (that.isMinusOne())
      {
         return thiz.neg();
      }
      if (rounder == null || rounder.getPrecision() == 0)
      {
         /*
          * If the quotient this/divisor has a terminating decimal expansion, the expansion can have
          * no more than (a.precision() + ceil(10*b.precision)/3) digits. Therefore, create a
          * MathContext object with this precision and do a divide with the UNNECESSARY rounding
          * mode.
          * 
          * Commentary from BigDecimal. Should be the same for binary numbers.
          */
         final long p = thiz.precision() + (long) Math.ceil(10.0 * that.precision() / 3.0);
         final int prec = (int) Math.min(p, Integer.MAX_VALUE);
         final Rounder rndr = new Rounder(prec, RoundingMode.UNNECESSARY);
         try
         {
            return div(thiz, that, rndr);
         }
         catch (final ArithmeticException e)
         {
            return null;
         }
      }
      final long scale = (long) thiz.scale - that.scale;
      return div(thiz.unscaledValue, that.unscaledValue, scale, rounder);
   }

   /**
    * Returns {@code thiz / that * 2^scale} rounded, where the trivial and irregular cases are
    * already handled but thiz/that/scale will be first appropriately prepared to ensure a precise
    * result.
    */
   private static BigFloat div(final BigInteger thiz, final BigInteger that, final long scale,
         final Rounder rounder)
   {

      final int thisDrop = thiz.bitLength();
      int thatDrop = that.bitLength();
      {
         final int raise = overflow((long) thisDrop - thatDrop);
         if ((raise >= 0 ? thiz.compareTo(that.shiftLeft(raise))
               : thiz.shiftLeft(-raise).compareTo(that)) > 0)
         {
            thatDrop--;
         }
      }
      final long precision = rounder.getPrecision();
      final int raise = overflow(thisDrop - thatDrop - precision);
      final int scail = overflow(scale + raise);
      if (raise >= 0)
      {
         return divAsIs(thiz, that.shiftLeft(raise), scail, rounder.getRoundingMode())
               .round(rounder);
      }
      else
      {
         return divAsIs(thiz.shiftLeft(-raise), that, scail, rounder.getRoundingMode())
               .round(rounder);
      }
   }

   /**
    * Returns {@code thiz / that * 2^scale} rounded, where the trivial and irregular cases are
    * already handled and thiz/that/scale already are appropriately prepared to ensure a precise
    * result.
    */
   private static BigFloat divAsIs(final BigInteger thiz, final BigInteger that, final int scale,
         final RoundingMode roundingMode)
   {

      final BigInteger[] array = thiz.divideAndRemainder(that);
      final BigInteger quotient = array[0];
      final BigInteger remainder = array[1];
      if (remainder.signum() != 0)
      {
         final int sign = thiz.signum() != that.signum() ? -1 : 1;
         final int cmpFracHalf = remainder.compareTo(that.shiftRight(1));
         final boolean isOdd = quotient.testBit(0);
         if (quotientNeedsIncrement(roundingMode, sign, cmpFracHalf, isOdd))
         {
            return new BigFloat(quotient.add(BigInteger.ONE), scale);
         }
      }
      return new BigFloat(quotient, scale);
   }

   /**
    * Returns whether the quotient needs increment by virtue of rounding.
    * 
    * @param roundingMode
    *           the rounding mode
    * @param sign
    *           the sign of the quotient
    * @param cmpFracHalf
    *           the comparison of the remainder to half of the divisor
    * @param isOdd
    *           whether the quotient is odd
    * @return whether the quotient needs increment by virtue of rounding
    */
   private static boolean quotientNeedsIncrement(final RoundingMode roundingMode, final int sign,
         final int cmpFracHalf, final boolean isOdd)
   {

      switch (roundingMode)
      {
         case UNNECESSARY :
            throw new RoundingNecessaryException();
         case UP :
            return true;
         case DOWN :
            return false;
         case CEILING :
            return sign > 0;
         case FLOOR :
            return sign < 0;
         default :
            if (cmpFracHalf < 0)
            {
               return false;
            }
            if (cmpFracHalf > 0)
            {
               return true;
            }
            switch (roundingMode)
            {
               case HALF_DOWN :
                  return false;
               case HALF_UP :
                  return true;
               case HALF_EVEN :
                  return isOdd;
               default :
                  throw new UnreachedException();
            }
      }
   }

   private Divison()
   {
   }
}

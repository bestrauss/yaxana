package br.eng.strauss.yaxana.big;

import java.math.BigInteger;

/**
 * {@link BigFloat}/{@code double} conversion.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
final class DoubleIO
{

   /**
    * Static methods only.
    */
   private DoubleIO()
   {

   }

   /**
    * Returns a new {@link BigFloat} with a given value.
    * 
    * @param value
    *           the value.
    * @return a new {@link BigFloat} with the given value.
    */
   public static BigFloat bigFloatValueOf(final double value)
   {

      final long bits = Double.doubleToLongBits(value);
      if ((bits & 0x7FFFFFFFFFFFFFFFL) == 0L)
      {
         return BigFloat.ZERO;
      }
      if (bits == 0x3ff0000000000000L)
      {
         return BigFloat.ONE;
      }
      if (bits == 0x3fe0000000000000L)
      {
         return BigFloat.HALF;
      }
      if (bits == 0x4000000000000000L)
      {
         return BigFloat.TWO;
      }
      if (!Double.isFinite(value))
      {
         throw new NumberFormatException("value is not finite");
      }
      final boolean sign = (bits & 0x8000000000000000L) != 0;
      final int expo = (int) ((bits & 0x7ff0000000000000L) >> 52);
      final long mant = expo == 0 ? (bits & 0x000FFFFFFFFFFFFFL) << 1
            : bits & 0x000FFFFFFFFFFFFFL | 0x0010000000000000L;
      final long significand = sign ? -mant : mant;
      final int scale = 52 - (expo - 1023);
      return new BigFloat(BigInteger.valueOf(significand), -scale);
   }

   public static double doubleValueOf(final BigFloat value)
   {

      if (value == BigFloat.ZERO || value.unscaledValue.equals(BigInteger.ZERO))
      {
         return 0d;
      }
      final boolean sign = value.unscaledValue.signum() < 0;
      final BigFloat abs = sign ? value.neg() : value;
      int n = abs.unscaledValue.bitLength();
      BigInteger unscaledValue = abs.unscaledValue;
      int scale = -abs.scale;
      if (n > 53)
      {
         unscaledValue = unscaledValue.shiftRight(n - 53);
         scale -= n - 53;
      }
      else if (n < 53)
      {
         final int preferredScale = 1074;
         final int diffScale = scale - preferredScale;
         final int count = Math.min(0, diffScale);
         if (count != 0)
         {
            unscaledValue = unscaledValue.shiftLeft(-count);
            scale -= count;
         }
      }
      boolean shift = false;
      n = unscaledValue.bitLength();
      int expo = 1022 + n - scale;
      if (expo < -52)
      {
         return 0;
      }
      if (expo > 0x7ff)
      {
         return sign ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
      }
      if (expo == 0)
      {
         shift = true;
      }
      else if (expo < 0)
      {
         shift = true;
         n = unscaledValue.bitLength();
         expo = 0;
      }
      long mant = 0;
      long bit = 1L;
      if (shift)
      {
         for (int kBit = 0; kBit < n; kBit++, bit <<= 1)
         {
            if (unscaledValue.testBit(kBit))
            {
               mant |= bit;
            }
         }
      }
      else
      {
         for (int k = 0; k < 53; k++, bit <<= 1)
         {
            final int kBit = n - 53 + k;
            if (kBit >= 0 && unscaledValue.testBit(kBit))
            {
               mant |= bit;
            }
         }
         mant &= 0x000FFFFFFFFFFFFFL;
      }
      final long bits = (sign ? 1L << 63 : 0L) | (long) expo << 52 | mant;
      return Double.longBitsToDouble(bits);
   }
}

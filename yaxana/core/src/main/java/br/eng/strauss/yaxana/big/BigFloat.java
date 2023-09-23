package br.eng.strauss.yaxana.big;

import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.regex.Pattern;

import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.exc.ExponentOverflowException;
import br.eng.strauss.yaxana.exc.ExponentUnderflowException;
import br.eng.strauss.yaxana.exc.NegativeRadicandException;
import br.eng.strauss.yaxana.exc.NonPositiveLogarithmArgumentException;
import br.eng.strauss.yaxana.exc.NonTerminatingBinaryExpansionException;
import br.eng.strauss.yaxana.exc.UnreachedException;

/**
 * Immutable binary floating point numbers {@code unscaledValue*2^scale} with {@link BigInteger}
 * {@code unscaledValue} and {@code int} {@code scale} normed to avoid {@code unscaledValue}s with
 * trailing zeros.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 * @see Rounder
 */
public final class BigFloat extends Number implements Comparable<BigFloat>
{

   /** Serial version identifier. */
   public static final long serialVersionUID = 1L;

   /** The useful constant {@code 0}. */
   public static final BigFloat ZERO = new BigFloat(0);

   /** The useful constant {@code 1}. */
   public static final BigFloat ONE = new BigFloat(1);

   /** The useful constant {@code 2}. */
   public static final BigFloat TWO = new BigFloat(2);

   /** The useful constant {@code 10}. */
   public static final BigFloat TEN = new BigFloat(10);

   /** The useful constant {@code 0.5}. */
   public static final BigFloat HALF = new BigFloat(BigInteger.ONE, -1);

   /** The useful constant {@code -1}. */
   public static final BigFloat MINUSONE = new BigFloat(-1);

   /** The useful constant {@code -1}. */
   static final BigInteger BigInteger_MINUSONE = BigInteger.ONE.negate();

   /** The unscaled value {@code this = unscaledValue*2^scale}. */
   final BigInteger unscaledValue;

   /** The scale {@code this = unscaledValue*2^scale}. */
   final int scale;

   /**
    * Returns an exact representation of an integer value.
    *
    * @param value
    *           the value.
    */
   public BigFloat(final long value)
   {

      this(BigInteger.valueOf(value));
   }

   /**
    * Returns an exact representation of an integer value.
    *
    * @param value
    *           the value.
    */
   public BigFloat(final BigInteger value)
   {

      this(value, 0);
   }

   /**
    * Returns an exact representation of a finite and valid double value.
    * 
    * @param value
    *           the double value. Must be finite and valid.
    * @throws NumberFormatException
    *            if value is NaN or infinity.
    */
   public BigFloat(final double value) throws NumberFormatException
   {

      this(DoubleIO.bigFloatValueOf(value));
   }

   /**
    * Returns an exact representation of a given string representation.
    * 
    * @param value
    *           the string representation. For details see {@link #terminalPattern()}.
    * @throws NumberFormatException
    *            if the string representation is invalid.
    * @throws ArithmeticException
    *            if the value of the string representation cannot be represented exactly.
    * @see #BigFloat(String, Rounder)
    * @see #terminalPattern()
    * @see #toString()
    * @see #toString(MathContext)
    */
   public BigFloat(final String value) throws NumberFormatException, ArithmeticException
   {

      this(value, null);
   }

   /**
    * Returns a new {@link BigFloat} which (generally) is (only) an approximation of a given string
    * representation.
    * 
    * @param value
    *           the string representation. For details see {@link #terminalPattern()}.
    * @param rounder
    *           a rounder used for rounding or {@code null} to inhibit rounding.
    * @throws NumberFormatException
    *            if the string representation is invalid.
    * @throws ArithmeticException
    *            if the value of string representation cannot be represented exactly by a
    *            {@link BigFloat} and no rounder was provided.
    * @see #BigFloat(String)
    * @see #terminalPattern()
    * @see #toString()
    * @see #toString(MathContext)
    */
   public BigFloat(final String value, final Rounder rounder)
         throws NumberFormatException, ArithmeticException
   {

      this(StringIO.bigFloatValueOf(value, rounder));
   }

   /**
    * Returns a new instance.
    *
    * @param value
    *           the value to initialize this value with.
    */
   private BigFloat(final BigFloat value)
   {

      this.unscaledValue = value.unscaledValue;
      this.scale = value.scale;
   }

   /**
    * Returns a new instance.
    * 
    * @param unscaledValue
    *           the unscaled value.
    * @param scale
    *           the scale.
    */
   public BigFloat(final BigInteger unscaledValue, final int scale)
   {

      if (unscaledValue.equals(BigInteger.ZERO))
      {
         this.unscaledValue = unscaledValue;
         this.scale = 0;
      }
      else
      {
         int sc = 0;
         int k = 0;
         while (!unscaledValue.testBit(k++))
         {
            sc++;
         }
         this.unscaledValue = unscaledValue.shiftRight(sc);
         this.scale = overflow((long) scale + sc);
      }
   }

   /**
    * Returns the unscaled value.
    * 
    * @return the unscaled value.
    */
   public BigInteger unscaledValue()
   {

      return unscaledValue;
   }

   /**
    * Returns the scale.
    * 
    * @return the scale.
    */
   public int scale()
   {

      return scale;
   }

   /**
    * Returns {@code -1, 0, 1} in case this number is negative, zero, positive.
    * 
    * @return {@code -1, 0, 1} in case this number is negative, zero, positive.
    */
   public int signum()
   {

      return this.unscaledValue.signum();
   }

   /**
    * Returns the maximum of {@code this} and {@code that}.
    * 
    * @param that
    *           that.
    * @return the maximum of {@code this} and {@code that}.
    */
   public BigFloat max(final BigFloat that)
   {

      return this.compareTo(that) >= 0 ? this : that;
   }

   /**
    * Returns the minimum of {@code this} and {@code that}.
    * 
    * @param that
    *           that.
    * @return the minimum of {@code this} and {@code that}.
    */
   public BigFloat min(final BigFloat that)
   {

      return this.compareTo(that) <= 0 ? this : that;
   }

   /**
    * Returns the exponent of the scientific representation {@code 1.xxx*2^exponent} of this
    * positive number.
    * <p>
    * The exponent of the scientific representation is the position of the most significant bit,
    * where position 0 is immediately left of the binary point and -1 is immediately right of the
    * binary point.
    * <p>
    * The name of this method was coined by Prof. Chee Yap.
    * 
    * @return the exponent of the scientific representation {@code 1.xxx*2^exponent} of this number.
    * @throws NonPositiveLogarithmArgumentException
    *            if this number is not positive.
    */
   public int msb() throws NonPositiveLogarithmArgumentException
   {

      if (this.signum() > 0)
      {
         return this.unscaledValue.bitLength() - 1 + this.scale;
      }
      throw new NonPositiveLogarithmArgumentException();
   }

   /**
    * Returns a new immutable number {@code this + that}.
    * 
    * @param that
    *           that number.
    * @return a new immutable number.
    */
   public BigFloat add(final BigFloat that)
   {

      return this.add(that, null);
   }

   /**
    * Returns a new immutable number which is a rounded version of {@code this + that}.
    * 
    * @param that
    *           that number.
    * @param rounder
    *           the rounder or {@code null}.
    * @return a new immutable number.
    */
   public BigFloat add(final BigFloat that, final Rounder rounder)
   {

      final boolean thisIsZero = this.signum() == 0;
      final boolean thatIsZero = that.signum() == 0;
      if (thisIsZero && thatIsZero)
      {
         return BigFloat.ZERO;
      }
      if (thisIsZero)
      {
         return that.round(rounder);
      }
      if (thatIsZero)
      {
         return this.round(rounder);
      }
      final int thisScale = this.scale;
      final int thatScale = that.scale;
      BigInteger thisUnscaledValue = this.unscaledValue;
      BigInteger thatUnscaledValue = that.unscaledValue;
      final int scale = thisScale < thatScale ? thisScale : thatScale;
      if (thisScale != thatScale)
      {
         final int drop = overflow((long) thisScale - thatScale);
         if (drop > 0)
         {
            thisUnscaledValue = thisUnscaledValue.shiftLeft(drop);
         }
         else
         {
            thatUnscaledValue = thatUnscaledValue.shiftLeft(-drop);
         }
      }
      return new BigFloat(thisUnscaledValue.add(thatUnscaledValue), scale).round(rounder);
   }

   /**
    * Returns a new immutable number {@code this - that}.
    * 
    * @param that
    *           that number.
    * @return a new immutable number.
    */
   public BigFloat sub(final BigFloat that)
   {

      return this.add(that.neg(), null);
   }

   /**
    * Returns a new immutable number which is a rounded version of {@code this - that}.
    * 
    * @param that
    *           that number.
    * @param rounder
    *           the rounder or {@code null}.
    * @return a new immutable number.
    */
   public BigFloat sub(final BigFloat that, final Rounder rounder)
   {

      return this.add(that.neg(), rounder);
   }

   /**
    * Returns a new immutable number {@code this * that}.
    * 
    * @param that
    *           that number.
    * @return a new immutable number.
    */
   public BigFloat mul(final BigFloat that)
   {

      return this.mul(that, null);
   }

   /**
    * Returns a new immutable number which is a rounded version of {@code this * that}.
    * 
    * @param that
    *           that number.
    * @param rounder
    *           the rounder or {@code null}.
    * @return a new immutable number.
    */
   public BigFloat mul(final BigFloat that, final Rounder rounder)
   {

      if (this.isZero() || that.isZero())
      {
         return ZERO;
      }
      if (this.isOne())
      {
         return that.round(rounder);
      }
      if (that.isOne())
      {
         return this.round(rounder);
      }
      if (this.isMinusOne())
      {
         return that.neg().round(rounder);
      }
      if (that.isMinusOne())
      {
         return this.neg().round(rounder);
      }
      if (rounder != null)
      {
         final Rounder r = new Rounder(rounder.precision + 2, RoundingMode.DOWN);
         final BigFloat a = this.round(r);
         final BigFloat b = that.round(r);
         return a.mul(b, null).round(rounder);
      }
      final int scale = overflow((long) this.scale + that.scale);
      final BigInteger unscaledValue = this.unscaledValue.multiply(that.unscaledValue);
      return new BigFloat(unscaledValue, scale).round(rounder);
   }

   /**
    * Returns a new immutable number {@code this / that}.
    * <p>
    * A rounder is generally needed for division, as non-terminating binary expansions are frequent
    * and will cause a runtime exception.
    * 
    * @param that
    *           that number.
    * @return a new immutable number.
    * @throws NonTerminatingBinaryExpansionException
    *            If such fiasco happens.
    */
   public BigFloat div(final BigFloat that)
   {

      return div(that, null);
   }

   /**
    * Returns a new immutable number which is a rounded version of {@code this / that}.
    * 
    * @param that
    *           that number.
    * @param rounder
    *           the rounder or {@code null}. A rounder is generally needed for division, as
    *           non-terminating binary expansions are frequent and will cause a runtime exception.
    * @return a new immutable number.
    * @throws NonTerminatingBinaryExpansionException
    *            If such fiasco happens.
    */
   public BigFloat div(final BigFloat that, final Rounder rounder)
   {

      final BigFloat quotient = Divison.div(this, that, rounder);
      if (quotient == null)
      {
         throw new NonTerminatingBinaryExpansionException();
      }
      return quotient;
   }

   /**
    * Returns a new immutable number which is exactly {@code this / that}, or {@code null} if a
    * non-terminating binary expansions of the result is detected.
    * 
    * @param that
    *           that number.
    * @return a new immutable number or {@code null}.
    */
   public BigFloat divOrNull(final BigFloat that)
   {

      return Divison.div(this, that, null);
   }

   /**
    * Returns a new immutable number {@code -this}.
    * 
    * @return a new immutable number.
    */
   public BigFloat neg()
   {

      return new BigFloat(unscaledValue.negate(), scale);
   }

   /**
    * Returns a new immutable number {@code abs(this)}.
    * 
    * @return a new immutable number.
    */
   public BigFloat abs()
   {

      return new BigFloat(unscaledValue.abs(), scale);
   }

   /**
    * Returns a new immutable number {@code this*this}.
    * 
    * @return a new immutable number.
    */
   public BigFloat sqr()
   {

      return this.pow(2);
   }

   /**
    * Returns a new immutable number {@code this*this} rounded.
    * 
    * @param rounder
    *           the rounder or {@code null}.
    * @return a new immutable number.
    */
   public BigFloat sqr(final Rounder rounder)
   {

      return this.pow(2, rounder);
   }

   /**
    * Returns a new instance {@code sqrt(this)} rounded.
    * 
    * @param rounder
    *           the rounder.
    * @return a new instance.
    */
   public BigFloat sqrt(final Rounder rounder)
   {

      switch (this.signum())
      {
         case 0 :
            return BigFloat.ZERO;
         case -1 :
            throw new NegativeRadicandException();
      }
      if (this.isOne())
      {
         return ONE;
      }
      {
         final int n = 2;
         final int oom = this.msb();
         if (Math.abs(oom) >= n)
         {
            final int oomRoot = oom / n;
            final int oomRadi = n * oomRoot;
            return this.mulTwoTo(-oomRadi).sqrt(rounder).mulTwoTo(oomRoot);
         }
      }
      final int prec = rounder.getPrecision();
      final BigFloat HALF = new BigFloat(BigInteger.ONE, -1);
      final int maxPrecision = prec + 4 + this.precision();
      final BigFloat acceptableError = BigFloat.twoTo(-(prec + 2));
      BigFloat sqrt = this.mul(HALF, Rounder.SINGLE);
      int adaptivePrecision = 2;
      BigFloat previousSqrt;
      do
      {
         if (adaptivePrecision < maxPrecision)
         {
            adaptivePrecision *= 3;
            if (adaptivePrecision > maxPrecision)
            {
               adaptivePrecision = maxPrecision;
            }
         }
         final Rounder ctx = new Rounder(adaptivePrecision);
         previousSqrt = sqrt;
         sqrt = this.div(sqrt, ctx).add(sqrt, ctx).mul(HALF, ctx);
      }
      while (adaptivePrecision < maxPrecision
            || sqrt.sub(previousSqrt).abs().compareTo(acceptableError) > 0);
      return sqrt.round(rounder);
   }

   /**
    * Returns a new instance {@code n-th root(this)} rounded.
    * 
    * @param n
    *           the index.
    * @param rounder
    *           the rounder.
    * @return a new instance.
    */
   public BigFloat root(final int n, final Rounder rounder)
   {

      if (n == 0)
      {
         throw new DivisionByZeroException();
      }
      if (n < 0)
      {
         return BigFloat.ONE.div(this.root(-n, rounder), rounder);
      }
      if (n == 1)
      {
         return this;
      }
      if (n == 2)
      {
         return this.sqrt(rounder);
      }
      switch (this.signum())
      {
         case 0 :
            return BigFloat.ZERO;
         case -1 :
            if ((n & 1) == 0)
            {
               throw new NegativeRadicandException();
            }
            else
            {
               return this.neg().root(n, rounder).neg();
            }
      }
      if (this.isOne())
      {
         return ONE;
      }
      {
         final int oom = this.msb();
         if (Math.abs(oom) >= n)
         {
            final int oomRoot = oom / n;
            final int oomRadi = n * oomRoot;
            return this.mulTwoTo(-oomRadi).root(n, rounder).mulTwoTo(oomRoot);
         }
      }
      final int prec = rounder.getPrecision();
      final int maxPrecision = prec + 4 + this.precision();
      final BigFloat acceptableError = BigFloat.twoTo(-(prec + 1));
      final BigFloat bn = new BigFloat(n);
      final int nMinus1 = n - 1;
      BigFloat result = this.div(TWO, Rounder.SINGLE);
      int adaptivePrecision = 2;
      BigFloat step;
      do
      {
         adaptivePrecision = adaptivePrecision * 3;
         if (adaptivePrecision > maxPrecision)
         {
            adaptivePrecision = maxPrecision;
         }
         final Rounder ctx = new Rounder(adaptivePrecision);
         step = this.div(result.pow(nMinus1, ctx), ctx).sub(result, ctx).div(bn, ctx);
         result = result.add(step, ctx);
      }
      while (adaptivePrecision < maxPrecision || step.abs().compareTo(acceptableError) > 0);
      return result.round(rounder);
   }

   /**
    * Returns a new immutable number {@code this^n}.
    * 
    * @param n
    *           the exponent, which must not be negative.
    * @return a new immutable number.
    */
   public BigFloat pow(final int n)
   {

      return this.pow(n, null);
   }

   /**
    * Returns a new immutable number {@code this^n}.
    * 
    * @param n
    *           the exponent, which must not be negative.
    * @param rounder
    *           the rounder or {@code null}.
    * @return a new immutable number.
    */
   public BigFloat pow(final int n, final Rounder rounder)
   {

      return new BigFloat(unscaledValue.pow(n), overflow((long) scale * n)).round(rounder);
   }

   /**
    * Returns a new immutable number {@code 2^n}.
    * 
    * @param n
    *           the exponent.
    * @return a new immutable number.
    */
   public static BigFloat twoTo(final int n)
   {

      return new BigFloat(BigInteger.ONE, n);
   }

   /**
    * Returns a new immutable number {@code this*2^n}.
    * 
    * @param n
    *           the exponent.
    * @return a new immutable number.
    */
   public BigFloat mulTwoTo(final int n)
   {

      return new BigFloat(this.unscaledValue, this.scale + n);
   }

   /**
    * Returns a new immutable number which equals this number rounded to a given binary precision
    * using a given rounding mode.
    * 
    * @param rounder
    *           the rounder or {@code null}.
    * @return a new immutable number.
    */
   public BigFloat round(final Rounder rounder)
   {

      return rounder != null ? rounder.round(this) : this;
   }

   /**
    * Returns the integer part of this number.
    * 
    * @return the integer part of this number.
    */
   public BigInteger bigIntegerValue()
   {

      switch (unscaledValue.signum())
      {
         case 0 :
            return BigInteger.ZERO;
         case 1 :
            return unscaledValue.shiftLeft(scale);
         case -1 :
            return unscaledValue.negate().shiftLeft(scale).negate();
      }
      throw new UnreachedException();
   }

   /**
    * Returns the int value of this number.
    * 
    * @return the int value of this number.
    */
   @Override
   public int intValue()
   {

      return bigIntegerValue().intValue();
   }

   /**
    * Returns the long value of this number.
    * 
    * @return the long value of this number.
    */
   @Override
   public long longValue()
   {

      return bigIntegerValue().longValue();
   }

   /**
    * Returns the float value of this number.
    * 
    * @return the float value of this number.
    */
   @Override
   public float floatValue()
   {

      return (float) doubleValue();
   }

   /**
    * Returns the double value of this number.
    * 
    * @return the double value of this number.
    */
   @Override
   public double doubleValue()
   {

      return DoubleIO.doubleValueOf(this);
   }

   /**
    * Returns whether this number is {@code 0}.
    * 
    * @return whether this number is {@code 0}.
    */
   public boolean isZero()
   {

      return unscaledValue.equals(BigInteger.ZERO);
   }

   /**
    * Returns whether this number is {@code 1}.
    * 
    * @return whether this number is {@code 1}.
    */
   public boolean isOne()
   {

      return scale == 0 && unscaledValue.equals(BigInteger.ONE);
   }

   /**
    * Returns whether this number is {@code -1}.
    * 
    * @return whether this number is {@code -1}.
    */
   public boolean isMinusOne()
   {

      return scale == 0 && unscaledValue.equals(BigInteger_MINUSONE);
   }

   /**
    * Returns whether this number is an integer.
    * 
    * @return whether this number is an integer.
    */
   public boolean isInteger()
   {

      return scale >= 0;
   }

   @Override
   public int compareTo(final BigFloat that)
   {

      final int thisSignum = this.unscaledValue.signum();
      final int thatSignum = that.unscaledValue.signum();
      if (thisSignum == thatSignum)
      {
         if (thisSignum == 0)
         {
            return 0;
         }
         if (thisSignum == 1)
         {
            final int thisOom = this.msb();
            final int thatOom = that.msb();
            if (thisOom > thatOom)
            {
               return 1;
            }
            if (thisOom < thatOom)
            {
               return -1;
            }
         }
         else
         {
            final int thisOom = this.neg().msb();
            final int thatOom = that.neg().msb();
            if (thisOom > thatOom)
            {
               return -1;
            }
            if (thisOom < thatOom)
            {
               return 1;
            }
         }
         return this.sub(that).signum();
      }
      return thisSignum > thatSignum ? 1 : -1;
   }

   /**
    * Returns whether a given object is a {@link BigFloat} and the values equal.
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(final Object object)
   {

      if (object != this)
      {
         if (object instanceof BigFloat)
         {
            final BigFloat that = (BigFloat) object;
            return this.scale == that.scale && this.unscaledValue.equals(that.unscaledValue);
         }
         return false;
      }
      return true;
   }

   /**
    * Returns a hash code meeting the contract.
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {

      return this.unscaledValue.hashCode() ^ this.scale;
   }

   /**
    * Returns a pattern which matches allowed terminal string representations.
    * <p>
    * For details see {@link Robusts#terminalPattern()}.
    * <p>
    * The mantissa may have any length. The exponent too, an overflow/underflow of the exponent is
    * not detected.
    * 
    * @return a pattern which matches allowed terminal string representations.
    * @see #BigFloat(String)
    * @see #toString()
    */
   public static Pattern terminalPattern()
   {

      return StringIO.terminalPattern();
   }

   /**
    * Returns the exact string representation of this number.
    * <p>
    * Integer values up to {@code 1E36} have no exponent.
    * </p>
    * <p>
    * Non-integer values are printed with hexadecimal mantissa and base-two exponent.
    * </p>
    * 
    * @return the string representation of this number.
    * @see java.lang.Object#toString()
    * @see #BigFloat(String)
    * @see #BigFloat(String, Rounder)
    * @see #terminalPattern()
    * @see #toString(MathContext)
    */
   @Override
   public String toString()
   {

      return StringIO.stringValueOf(this);
   }

   /**
    * Undocumented.
    * 
    * @return the hexadecimal string representation of this number.
    */
   public String toHexString()
   {

      return StringIO.hexStringValueOf(this);
   }

   /**
    * Returns the decimal string representation of this or of an approximation of this.
    * 
    * @param mc
    *           a math context specifing decimal precision and rounding mode used to solve the
    *           problem if there is no exact decimal string representation of this.
    * @return the decimal string representation ...
    * @see #BigFloat(String)
    * @see #BigFloat(String, Rounder)
    * @see #terminalPattern()
    * @see #toString()
    */
   public String toString(final MathContext mc)
   {

      return StringIO.stringValueOf(this, mc);
   }

   /**
    * Returns the number of binary digits of the absolute value of the unscaled value of this
    * {@link BigFloat} or {@code 1} if this is {@code 0}. The unscaled value always neither has
    * leading nor trailing zeroes.
    * 
    * @return the length in bits of the the absolute value of the unscaled value of this
    *         {@link BigFloat} or {@code 1} if this is {@code 0}.
    */
   int precision()
   {

      return this.unscaledValue.signum() == 0 ? 1 : this.unscaledValue.abs().bitLength();
   }

   /**
    * Checks, whether a given scale is representable as an {@code int} and returns that {@code int}.
    * 
    * @param scale
    *           a scale.
    * @return the {@code int} scale.
    * @throws ExponentOverflowException
    *            if the given scale is too close to infinity to be represented as an {@code int}.
    * @throws ExponentUnderflowException
    *            if the given scale is too close to negative infinity to be represented as an
    *            {@code int}.
    */
   static int overflow(final long scale)
         throws ExponentUnderflowException, ExponentOverflowException
   {

      final int asInt = (int) scale;
      if (asInt != scale)
      {
         if (scale < 0)
         {
            throw new ExponentUnderflowException();
         }
         else
         {
            throw new ExponentOverflowException();
         }
      }
      return asInt;
   }
}

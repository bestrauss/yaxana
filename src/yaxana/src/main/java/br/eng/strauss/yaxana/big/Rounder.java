package br.eng.strauss.yaxana.big;

import static br.eng.strauss.yaxana.big.BigFloat.overflow;
import static java.math.RoundingMode.HALF_UP;

import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * A rounder to round {@link BigFloat}s.
 * <p>
 * A {@link Rounder} is like a {@code java.math.MathContext} but precision is binary and not decimal
 * digits. Also, behind the scenes, the {@link Rounder} actually performs rounding.
 * </p>
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class Rounder
{

   /** A single precision (24bit) rounder with default rounding mode. */
   public static final Rounder SINGLE = new Rounder(24);

   /** A double precision (52bit) rounder with default rounding mode. */
   public static final Rounder DOUBLE = new Rounder(52);

   /** A minmal precision (1bit) rounder with default rounding mode. */
   public static final Rounder MINIMAL = new Rounder(1);

   /** The binary precision. */
   final int precision;

   /** The rounding mode. */
   final RoundingMode roundingMode;

   /**
    * Returns a new instance.
    *
    * @param precision
    *           The precision. Will be limited to minimum {@code 0}. NOTE: This is the number of
    *           significant binary (not decimal) digits. {@code 0} indicates no rounding.
    * @see #Rounder(int, RoundingMode)
    */
   public Rounder(final int precision)
   {

      this(precision, null);
   }

   /**
    * Returns a new instance.
    *
    * @param precision
    *           The precision. Will be limited to minimum {@code 0}. NOTE: This is the number of
    *           significant binary (not decimal) digits. {@code 0} indicates no rounding.
    * @param roundingMode
    *           The rounding mode or {@code null} for default ({@link RoundingMode#HALF_UP}).
    */
   public Rounder(final int precision, final RoundingMode roundingMode)
   {

      this.precision = precision >= 0 ? precision : 0;
      this.roundingMode = roundingMode != null ? roundingMode : HALF_UP;
   }

   /**
    * Returns the binary precision.
    * <p>
    * NOTE: This is the number of significant binary (not decimal) digits. {@code 0} indicates no
    * rounding.
    * 
    * @return the binary precision.
    * @see #Rounder(int, RoundingMode)
    */
   public int getPrecision()
   {

      return precision;
   }

   /**
    * Returns the rounding mode.
    * 
    * @return the rounding mode.
    * @see #Rounder(int, RoundingMode)
    */
   public RoundingMode getRoundingMode()
   {

      return roundingMode;
   }

   /**
    * Implementation of {@link BigFloat#round(Rounder)}.
    */
   BigFloat round(final BigFloat number)
   {

      if (precision > 0 && number.signum() != 0)
      {
         final int drop = number.precision() - precision;
         if (drop > 0)
         {
            final int scale = overflow((long) number.scale + drop);
            final BigInteger unscaledValue = shiftRightAndRound(number.unscaledValue, drop);
            return new BigFloat(unscaledValue, scale);
         }
      }
      return number;
   }

   /**
    * Helper for {@link #round(BigFloat)}.
    */
   private BigInteger shiftRightAndRound(final BigInteger value, final int drop)
   {

      return shiftRightAndRound(value.abs(), value.signum() < 0, drop);
   }

   /**
    * Helper for {@link #shiftRightAndRound(BigInteger, int)}.
    */
   private BigInteger shiftRightAndRound(final BigInteger absValue, final boolean sign,
         final int drop)
   {

      final BigInteger absResult = absValue.shiftRight(drop);
      final BigInteger increment = increment(absValue, sign, drop);
      final BigInteger rndResult = increment != null ? absResult.add(increment) : absResult;
      final BigInteger result = sign ? rndResult.negate() : rndResult;
      return result;
   }

   /**
    * Helper for {@link #shiftRightAndRound(BigInteger, int)}.
    */
   private BigInteger increment(final BigInteger value, final boolean sign, final int drop)
   {

      if (drop > 0)
      {
         switch (roundingMode)
         {
            default :
            case UNNECESSARY :
               return null;
            case UP :
            {
               return BigInteger.ONE;
            }
            case DOWN :
            {
               return null;
            }
            case HALF_UP :
            {
               return halfUp(value, drop);
            }
            case HALF_DOWN :
            {
               return halfDown(value, drop);
            }
            case HALF_EVEN :
            {
               final boolean odd = value.testBit(drop);
               return odd ? halfUp(value, drop) : halfDown(value, drop);
            }
            case CEILING :
            {
               return sign ? null : BigInteger.ONE;
            }
            case FLOOR :
            {
               return sign ? BigInteger.ONE : null;
            }
         }
      }
      return null;
   }

   /**
    * Helper for {@link #increment(BigInteger, boolean, int)}.
    */
   private BigInteger halfUp(final BigInteger value, final int drop)
   {

      final boolean bit = value.testBit(drop - 1);
      return bit ? BigInteger.ONE : null;
   }

   /**
    * Helper for {@link #increment(BigInteger, boolean, int)}.
    */
   private BigInteger halfDown(final BigInteger value, final int drop)
   {

      final boolean bit = value.testBit(drop - 1);
      return drop > 1 && bit ? BigInteger.ONE : null;
   }
}

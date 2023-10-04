package br.eng.strauss.yaxana.pdc;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.exc.PrecisionOverflowException;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class PDCTools
{

   /**
    * Sets the approximation of a given value to zero.
    * 
    * @param a
    *           the value.
    * @return {@code 0}.
    */
   public static int setExactZero(final Algebraic a)
   {

      a.setApproximation(BigFloat.ZERO, Integer.MAX_VALUE);
      return 0;
   }

   /**
    * Ensures that at least one of two given values has a finite approximation.
    * <p>
    * It's the callers duty to ensure that at least one of the true values is finite.
    * 
    * @param a
    *           the one value.
    * @param b
    *           the other value.
    */
   public static void ensureFiniteApproximation(final Algebraic a, final Algebraic b)
   {

      for (int precision = INITIAL_PRECISION;; precision = increment(a, precision))
      {
         a.approximation(precision);
         b.approximation(precision);
         final BigFloat epsilon = BigFloat.twoTo(-precision);
         if (a.approximation().abs().compareTo(epsilon) >= 0
               || b.approximation().abs().compareTo(epsilon) >= 0)
         {
            return;
         }
      }
   }

   /**
    * Ensures a given value has a finite approximation.
    * <p>
    * It's the callers duty to ensure that the true value is finite.
    * 
    * @param a
    *           the value.
    * @return the sign of the finite approximation.
    */
   public static int ensureFiniteApproximation(final Algebraic a)
   {

      for (int precision = INITIAL_PRECISION;; precision = increment(a, precision))
      {
         a.approximation(precision);
         final BigFloat epsilon = BigFloat.twoTo(-precision);
         if (a.approximation().abs().compareTo(epsilon) >= 0)
         {
            return a.approximation().signum();
         }
      }
   }

   /**
    * Returns the next precision.
    * <p>
    * Starting with an initial precision of 64bits, precision is doubled by each call. Doubling is
    * neccessary, just adding a constant would lead to an unbearable overhead of wasted
    * calculations.
    * 
    * @param a
    *           the expression being approximated. This may be needed for an error message.
    * @param precision
    *           the previous precision. Use {@code 0} to get initial the precision.
    * @return the next precision.
    * @throws PrecisionOverflowException
    *            if the next {@code precision} cannot be represented as an {@code int}.
    */
   public static int increment(final Algebraic a, final int precision)
         throws PrecisionOverflowException
   {

      final long newPrecision = precision < INITIAL_PRECISION ? INITIAL_PRECISION
            : (long) precision << 1;
      if (newPrecision > PrecisionOverflowException.MAX_PRECISION)
      {
         throw new PrecisionOverflowException(newPrecision + " expr: " + a.toString() + "");
      }
      return (int) newPrecision;
   }

   private PDCTools()
   {
   }

   /** The initial precision. */
   private static final int INITIAL_PRECISION = 52;
}

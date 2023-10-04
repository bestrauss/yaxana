package br.eng.strauss.yaxana.pdc;

import static br.eng.strauss.yaxana.pdc.ApproximationType.FRACTIONAL_DIGITS;

import java.util.function.Consumer;
import java.util.function.Supplier;

import br.eng.strauss.yaxana.Type;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.EPU;

/**
 * Interface to be implemented by expressions with cached approximations, provides precision driven
 * computation of approximations.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public abstract sealed interface Approximable<A extends Approximable<A>> /**/ permits Algebraic
{

   /**
    * Returns the type of this expression.
    * 
    * @return the type of this expression.
    */
   public abstract Type type();

   /**
    * Returns the left subexpression or {@code null} if this is not a binary or unary expression.
    * 
    * @return the left subexpression or {@code null} if this is not a binary or unary expression.
    */
   public abstract A left();

   /**
    * Returns the right subexpression or {@code null} if this is not a binary expression.
    * 
    * @return the right subexpression or {@code null} if this is not a binary expression.
    */
   public abstract A right();

   /**
    * Returns {@code -1, 0, 1} in case the exact value of this expression is negative, zero or
    * positive.
    * <p>
    * Determines the exact sign in finite time even though both values may be irrational and their
    * decimal representation may have an infinite number of digits.
    * 
    * @return {@code -1, 0, 1} in case the exact value of this expression is negative, zero or
    *         positive.
    */
   public abstract int signum();

   /**
    * Returns {@code -1, 0, 1} in case the exact value of this expression is negative, zero or
    * positive.
    * <p>
    * Determines the exact sign in finite time even though both values may be irrational and their
    * decimal representation may have an infinite number of digits.
    *
    * @param sufficientPrecision
    *           Optional. For profiling purposes.
    * @return {@code -1, 0, 1} in case the exact value of this expression is negative, zero or
    *         positive.
    */
   public abstract int signum(final Consumer<Integer> sufficientPrecision);

   /**
    * Sets the approximation/precision of this expression such that the signum can be read from the
    * approximation.
    * 
    * @param epu
    *           The exact processing unit to be used.
    * @param sufficientPrecision
    *           See {@link EPU#signum(Algebraic, Consumer)}
    */
   public default void ensureSignum(final Supplier<EPU> epu,
         final Consumer<Integer> sufficientPrecision)
   {

      final BigFloat approximation = approximation();
      final int precision = precision();
      if (approximation == null || approximation.signum() != 0
            && approximation.abs().compareTo(BigFloat.twoTo(-precision)) <= 0)
      {
         epu.get().signum((Algebraic) this, sufficientPrecision);
      }
   }

   /**
    * Returns the approximation of the value of this expression or {@code null} if
    * {@link #approximation(int)} has not been called yet.
    * 
    * @return the approximation of the value of this expression.
    * @see #precision()
    * @see #approximation(int)
    */
   public abstract BigFloat approximation();

   /**
    * Returns the precision of the current approximation of the value of this expression or
    * {@link Integer#MAX_VALUE} if the approximation is the exact value.
    * 
    * @return the precision of the current approximation of the value of this expression.
    * @see #approximation()
    * @see #approximation(int)
    */
   public abstract int precision();

   /**
    * Returns {@code 2^-precision} or zero, if the approximation is exact and {@link #precision()}
    * returns {@link Integer#MAX_VALUE}.
    * 
    * @return {@code 2^-precision} or zero.
    */
   public default BigFloat error()
   {

      final int precision = precision();
      return precision == Integer.MAX_VALUE ? BigFloat.ZERO : BigFloat.twoTo(-precision);
   }

   /**
    * Returns the approximation of this {@link Approximable} after having ensured that it has a
    * given absolute precision.
    * <p>
    * As a side effect, ensures that the approximations of all subexpressions have a typically even
    * higher precision.
    * 
    * @param precision
    *           The precision. The absolute value of the difference of the returned approximation
    *           and the true value will be less than or equal to {@code 2^-precision}. Please note
    *           that this doesn't mean that any one of the returned digits is a true digit of the
    *           true value. Consider e.g. the case where a true value {@code 0x1.0000...} is
    *           approximated by {@code 0x0.FFFF}.
    * @return the approximation of this {@link Approximable}.
    */
   public default BigFloat approximation(final int precision)
   {

      if (this.precision() < precision)
      {
         PDC.ensurePrecision(this, precision, FRACTIONAL_DIGITS);
      }
      return approximation();
   }

   /**
    * Sets the values of the approximation and the absolute precision.
    * <p>
    * This method should be protected but is public as a side effect of the implementation; it must
    * not be called by API users.
    * 
    * @param approximation
    *           the new value of the approximation.
    * @param precision
    *           the new value of the absolute precision.
    */
   public abstract void setApproximation(BigFloat approximation, int precision);

   /**
    * Returns the value of the right subexpression of this {@link Type#POW}- or
    * {@link Type#ROOT}-expression, or else the value of this expression, as an integer.
    * 
    * @return the value of the right subexpression of this {@link Type#POW}- or
    *         {@link Type#ROOT}-expression, or else the value of this expression, as an integer.
    */
   public int index();
}

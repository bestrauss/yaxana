package br.eng.strauss.yaxana;

import java.util.function.Consumer;

/**
 * Interface declaring and documenting the supported Comparisons.
 * 
 * @param <C>
 *           The implementing type.
 * @author Burkhard Strauss
 * @since 05-2022
 */
public abstract sealed interface ComfyComparable<C extends ComfyComparable<C>> extends Comparable<C>
      permits OperableParsableComfyComparable
{

   /**
    * Returns {@code 0} if this is equal to the given number value, {@code -1} if this is lower than
    * the given number value, and {@code 1} if this is greater than the given number value.
    * 
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public abstract int compareTo(final C number);

   /**
    * Returns {@code -1, 0, 1} depending on the sign of this number.
    * 
    * @return {@code -1, 0, 1} depending on the sign of this number.
    */
   public default int signum()
   {

      return signum(null);
   }

   /**
    * Returns {@code -1, 0, 1} depending on the sign of this number.
    * 
    * @param sufficientPrecision
    *           Optional. For profiling purposes.
    * @return {@code -1, 0, 1} depending on the sign of this number.
    */
   public abstract int signum(Consumer<Integer> sufficientPrecision);

   /**
    * Returns {@code true} if this number is zero.
    * 
    * @return {@code true} if this number is zero.
    */
   public default boolean isZero()
   {

      return signum() == 0;
   }

   /**
    * Returns {@code true} if this number is positive.
    * 
    * @return {@code true} if this number is positive.
    */
   public default boolean isPositive()
   {

      return signum() > 0;
   }

   /**
    * Returns {@code true} if this number is negative.
    * 
    * @return {@code true} if this number is negative.
    */
   public default boolean isNegative()
   {

      return signum() < 0;
   }

   /**
    * Returns {@code true} if this number is equal to a given number.
    * 
    * @param number
    *           a number.
    * @return {@code true} if this number is equal to the given number.
    */
   public default boolean isEqualTo(final C number)
   {

      return compareTo(number) == 0;
   }

   /**
    * Returns {@code true} if this number is greater than a given number.
    * 
    * @param number
    *           a number.
    * @return {@code true} if this number is greater than the given number.
    */
   public default boolean isGreaterThan(final C number)
   {

      return compareTo(number) > 0;
   }

   /**
    * Returns {@code true} if this number is less than a given number.
    * 
    * @param number
    *           a number.
    * @return {@code true} if this number is less than the given number.
    */
   public default boolean isLessThan(final C number)
   {

      return compareTo(number) < 0;
   }

   /**
    * Returns {@code true} if this number is greater than or equal to a given number.
    * 
    * @param number
    *           a number.
    * @return {@code true} if this number is greater than or equal to the given number.
    */
   public default boolean isGreaterOrEqual(final C number)
   {

      return compareTo(number) >= 0;
   }

   /**
    * Returns {@code true} if this number is less than or equal to a given number.
    * 
    * @param number
    *           a number.
    * @return {@code true} if this number is less than or equal to the given number.
    */
   public default boolean isLessOrEqual(final C number)
   {

      return compareTo(number) <= 0;
   }
}

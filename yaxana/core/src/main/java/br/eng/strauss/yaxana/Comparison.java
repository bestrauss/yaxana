package br.eng.strauss.yaxana;

/**
 * Interface declaring and documenting the supported Comparisons.
 * 
 * @param <C>
 *           The implementing type.
 * @author Burkhard Strauss
 * @since 05-2022
 */
public abstract sealed interface Comparison<C extends Comparison<C>> extends Comparable<C>
      permits Expression
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
   public abstract int signum();

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

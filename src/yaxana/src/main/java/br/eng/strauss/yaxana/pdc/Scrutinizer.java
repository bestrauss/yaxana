package br.eng.strauss.yaxana.pdc;

import br.eng.strauss.yaxana.big.BigFloat;

/**
 * Static methods for the scrutinization of the exactness of results of arithmetic operations.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class Scrutinizer
{

   /**
    * Returns whether the result of the operation {@code r = x + y} is exact.
    * 
    * @param x
    *           the first operand.
    * @param y
    *           the second operand.
    * @param r
    *           the result.
    * @return whether the result is exact.
    */
   public static boolean addIsExact(final BigFloat x, final BigFloat y, final BigFloat r)
   {

      return r.sub(x).compareTo(y) == 0;
   }

   /**
    * Returns whether the result of the operation {@code r = x - y} is exact.
    * 
    * @param x
    *           the first operand.
    * @param y
    *           the second operand.
    * @param r
    *           the result.
    * @return whether the result is exact.
    */
   public static boolean subIsExact(final BigFloat x, final BigFloat y, final BigFloat r)
   {

      return r.add(y).compareTo(x) == 0;
   }

   /**
    * Returns whether the result of the operation {@code r = x * y} is exact.
    * 
    * @param x
    *           the first operand.
    * @param y
    *           the second operand.
    * @param r
    *           the result.
    * @return whether the result is exact.
    */
   public static boolean mulIsExact(final BigFloat x, final BigFloat y, final BigFloat r)
   {

      return x.mul(y).compareTo(r) == 0;
   }

   /**
    * Returns whether the result of the operation {@code r = x / y} is exact.
    * 
    * @param x
    *           the first operand.
    * @param y
    *           the second operand.
    * @param r
    *           the result.
    * @return whether the result is exact.
    */
   public static boolean divIsExact(final BigFloat x, final BigFloat y, final BigFloat r)
   {

      return r.mul(y).compareTo(x) == 0;
   }

   /**
    * Returns whether the result of the operation {@code r = root(x, n)} is exact.
    * 
    * @param x
    *           the first operand.
    * @param n
    *           the second operand.
    * @param r
    *           the result.
    * @return whether the result is exact.
    */
   public static boolean rootIsExact(final BigFloat x, final int n, final BigFloat r)
   {

      return r.pow(n).compareTo(x) == 0;
   }

   /**
    * Returns whether the result of the operation {@code r = pow(x, n)} is exact.
    * 
    * @param x
    *           the first operand.
    * @param n
    *           the second operand.
    * @param r
    *           the result.
    * @return whether the result is exact.
    */
   public static boolean powIsExact(final BigFloat x, final int n, final BigFloat r)
   {

      return x.pow(n).compareTo(r) == 0;
   }

   /**
    * Static methods only.
    */
   private Scrutinizer()
   {

   }
}

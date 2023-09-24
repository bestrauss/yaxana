package br.eng.strauss.yaxana.pdc;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.big.Rounder;

/**
 * Static methods for safe {@code double} operations.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class SafeDoubleOps
{

   /**
    * Returns the exact result of the operation {@code x + y} or {@code null} if a {@code double}
    * cannot represent the result exactly.
    * 
    * @param x
    *           the first operand.
    * @param y
    *           the second operand.
    * @return the exact result or {@code null}.
    */
   public static Double addOrNull(final double x, final double y)
   {

      try
      {
         final BigFloat bx = new BigFloat(x);
         final BigFloat by = new BigFloat(y);
         final BigFloat br = bx.add(by);
         if (Scrutinizer.addIsExact(bx, by, br))
         {
            final double dr = br.doubleValue();
            return br.equals(new BigFloat(dr)) ? dr : null;
         }
      }
      catch (final ArithmeticException e)
      {
      }
      return null;
   }

   /**
    * Returns the exact result of the operation {@code x - y} or {@code null} if a {@code double}
    * cannot represent the result exactly.
    * 
    * @param x
    *           the first operand.
    * @param y
    *           the second operand.
    * @return the exact result or {@code null}.
    */
   public static Double subOrNull(final double x, final double y)
   {

      try
      {
         final BigFloat bx = new BigFloat(x);
         final BigFloat by = new BigFloat(y);
         final BigFloat br = bx.sub(by);
         if (Scrutinizer.subIsExact(bx, by, br))
         {
            final double dr = br.doubleValue();
            return br.equals(new BigFloat(dr)) ? dr : null;
         }
      }
      catch (final ArithmeticException e)
      {
      }
      return null;
   }

   /**
    * Returns the exact result of the operation {@code x * y} or {@code null} if a {@code double}
    * cannot represent the result exactly.
    * 
    * @param x
    *           the first operand.
    * @param y
    *           the second operand.
    * @return the exact result or {@code null}.
    */
   public static Double mulOrNull(final double x, final double y)
   {

      try
      {
         final BigFloat bx = new BigFloat(x);
         final BigFloat by = new BigFloat(y);
         final BigFloat br = bx.mul(by);
         if (Scrutinizer.mulIsExact(bx, by, br))
         {
            final double dr = br.doubleValue();
            return br.equals(new BigFloat(dr)) ? dr : null;
         }
      }
      catch (final ArithmeticException e)
      {
      }
      return null;
   }

   /**
    * Returns the exact result of the operation {@code x / y} or {@code null} if a {@code double}
    * cannot represent the result exactly.
    * 
    * @param x
    *           the first operand.
    * @param y
    *           the second operand.
    * @return the exact result or {@code null}.
    */
   public static Double divOrNull(final double x, final double y)
   {

      try
      {
         final BigFloat bx = new BigFloat(x);
         final BigFloat by = new BigFloat(y);
         final BigFloat br = bx.div(by, Rounder.DOUBLE);
         if (Scrutinizer.divIsExact(bx, by, br))
         {
            final double dr = br.doubleValue();
            return br.equals(new BigFloat(dr)) ? dr : null;
         }
      }
      catch (final ArithmeticException e)
      {
      }
      return null;
   }

   /**
    * Returns the exact result of the operation {@code x^n} or {@code null} if a {@code double}
    * cannot represent the result exactly.
    * 
    * @param x
    *           the operand.
    * @param n
    *           the exponent.
    * @return the exact result or {@code null}.
    */
   public static Double powOrNull(final double x, final int n)
   {

      try
      {
         final BigFloat bx = new BigFloat(x);
         final BigFloat br = bx.pow(n);
         if (Scrutinizer.powIsExact(bx, n, br))
         {
            final double dr = br.doubleValue();
            return br.equals(new BigFloat(dr)) ? dr : null;
         }
      }
      catch (final ArithmeticException e)
      {
      }
      return null;
   }

   /**
    * Returns the exact result of the operation {@code x^{1/n}} or {@code null} if a {@code double}
    * cannot represent the result exactly.
    * 
    * @param x
    *           the operand.
    * @param n
    *           the exponent.
    * @return the exact result or {@code null}.
    */
   public static Double rootOrNull(final double x, final int n)
   {

      try
      {
         final BigFloat bx = new BigFloat(x);
         final BigFloat br = bx.root(n, ROUNDER);
         if (Scrutinizer.rootIsExact(bx, n, br))
         {
            final double dr = br.doubleValue();
            return br.equals(new BigFloat(dr)) ? dr : null;
         }
      }
      catch (final ArithmeticException e)
      {
      }
      return null;
   }

   /**
    * Static methods only.
    */
   private SafeDoubleOps()
   {

   }

   private static final Rounder ROUNDER = new Rounder(54);
}

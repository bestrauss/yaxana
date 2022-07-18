package br.eng.strauss.yaxana.pdc;

import static br.eng.strauss.yaxana.big.Rounder.DOUBLE;
import static br.eng.strauss.yaxana.pdc.ApproximationType.FRACTIONAL_DIGITS;
import static br.eng.strauss.yaxana.pdc.ApproximationType.SIGNIFICANT_DIGITS;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.addIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.divIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.mulIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.powIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.rootIsExact;
import static br.eng.strauss.yaxana.pdc.Scrutinizer.subIsExact;
import static java.lang.Math.max;
import static java.math.RoundingMode.CEILING;
import static java.math.RoundingMode.FLOOR;

import java.math.RoundingMode;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.big.Rounder;

/**
 * Precision driven computation of approximations.
 * <p>
 * This is the implementation of {@link Approximable#approximation(int)}.
 * <p>
 * When calculating an approximation for the value of an expression given an absolute or relative
 * precision, subexpressions generally must be evaluated with a higher precision to ensure the
 * resulting precision.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 * @see _ Chen Li, Sylvain Pion, Chee Yap: "Recent progress in exact geometric computation", section
 *      5.
 */
final class PDC
{

   /**
    * Ensures that the approximation of a given {@link Approximable} has a given minimum absolute or
    * relative precision.
    * 
    * @param a
    *           the approximable expression.
    * @param precision
    *           The requested minimum number of exact binary digits, i.e. the minimum number of
    *           binary digits that are digits of the true, exact value of expression.
    * @param approximationType
    *           The type of approximation.
    * @see Approximable#approximation(int)
    * @see Approximable#setApproximation(BigFloat, int)
    */
   public static void ensurePrecision(final Approximable<?> a, int precision,
         final ApproximationType approximationType)
   {

      boolean relative = approximationType == SIGNIFICANT_DIGITS;
      if (relative)
      {
         if (a.approximation() == null)
         {
            ensurePrecision(a, INITIAL_PRECISION, FRACTIONAL_DIGITS);
         }
         relative = false;
         final int msb = msbOfApprox(a);
         precision = max(1, precision - msb);
         if (msb < 0 && precision < -msb)
         {
            precision = -msb;
         }
      }
      if (a.precision() >= precision)
      {
         return;
      }
      if (a.left() != null && a.left().approximation() == null)
      {
         ensurePrecision(a.left(), INITIAL_PRECISION, FRACTIONAL_DIGITS);
      }
      if (a.right() != null && a.right().approximation() == null)
      {
         ensurePrecision(a.right(), INITIAL_PRECISION, FRACTIONAL_DIGITS);
      }
      switch (a.type())
      {
         case TERMINAL :
            a.setApproximation(a.approximation(), precision);
            return;
         case ADD :
            ensurePrecisionAdd(a, precision);
            break;
         case SUB :
            ensurePrecisionSub(a, precision);
            break;
         case MUL :
            ensurePrecisionMul(a, precision);
            break;
         case DIV :
            ensurePrecisionDiv(a, precision);
            break;
         case ROOT :
            ensurePrecisionRoot(a, precision);
            break;
         case ABS :
            ensurePrecisionAbs(a, precision);
            break;
         case NEG :
            ensurePrecisionNeg(a, precision);
            break;
         case POW :
            ensurePrecisionPow(a, precision);
            break;
      }
   }

   /**
    * Ensures absolute precision.
    */
   private static void ensurePrecision(final Approximable<?> a, final int precision)
   {

      ensurePrecision(a, precision, FRACTIONAL_DIGITS);
   }

   /**
    * Helper method for {@link #ensurePrecision(Approximable, int, ApproximationType)}.
    */
   private static void ensurePrecisionAdd(final Approximable<?> a, final int precision)
   {

      final Approximable<?> left = a.left();
      final Approximable<?> rite = a.right();
      final int ltPrec = precision + 2;
      final int rtPrec = precision + 2;
      final int opPrec = precision + 3 + max(0, max(msbOfApprox(left), msbOfApprox(rite)));
      ensurePrecision(left, ltPrec);
      ensurePrecision(rite, rtPrec);
      final Rounder rounder = new Rounder(opPrec, ROUNDING_MODE);
      final BigFloat approx = left.approximation().add(rite.approximation(), rounder);
      final boolean exact = addIsExact(left.approximation(), rite.approximation(), approx);
      setApproximation(a, approx, precision, exact);
   }

   /**
    * Helper method for {@link #ensurePrecision(Approximable, int, ApproximationType)}.
    */
   private static void ensurePrecisionSub(final Approximable<?> a, final int precision)
   {

      final Approximable<?> left = a.left();
      final Approximable<?> rite = a.right();
      final int ltPrec = precision + 2;
      final int rtPrec = precision + 2;
      ensurePrecision(left, ltPrec);
      ensurePrecision(rite, rtPrec);
      final int opPrec = precision + 3 + max(0, max(msbOfApprox(left), msbOfApprox(rite)));
      final Rounder rounder = new Rounder(opPrec, ROUNDING_MODE);
      final BigFloat approx = left.approximation().sub(rite.approximation(), rounder);
      final boolean exact = subIsExact(left.approximation(), rite.approximation(), approx);
      setApproximation(a, approx, precision, exact);
   }

   /**
    * Helper method for {@link #ensurePrecision(Approximable, int, ApproximationType)}.
    */
   private static void ensurePrecisionMul(final Approximable<?> a, final int precision)
   {

      final Approximable<?> left = a.left();
      final Approximable<?> rite = a.right();
      final int ltPrec = precision + 3 + max(0, msbOfApprox(rite));
      final int rtPrec = precision + 3 + max(0, msbOfApprox(left));
      ensurePrecision(left, ltPrec);
      ensurePrecision(rite, rtPrec);
      final int oom = msbOfApprox(rite) + msbOfApprox(left);
      final int opPrec = precision + 3 + max(0, oom);
      final Rounder rounder = new Rounder(opPrec, ROUNDING_MODE);
      final BigFloat approx = left.approximation().mul(rite.approximation(), rounder);
      final boolean exact = mulIsExact(left.approximation(), rite.approximation(), approx);
      setApproximation(a, approx, precision, exact);
   }

   /**
    * Helper method for {@link #ensurePrecision(Approximable, int, ApproximationType)}.
    */
   private static void ensurePrecisionDiv(final Approximable<?> a, final int precision)
   {

      final Approximable<?> left = a.left();
      final Approximable<?> rite = a.right();
      while (rite.approximation().abs().compareTo(rite.error()) < 0)
      {
         final int prec = max(24, 2 * rite.precision());
         ensurePrecision(rite, prec);
      }
      final BigFloat absRite = rite.approximation().abs();
      final BigFloat riteLower = absRite.sub(rite.error(), ROUNDER_DBL_FLOOR);
      final int ldRite = riteLower.signum() != 0 ? riteLower.abs().msb() : 0;
      final int leftPrec = precision + 2 + max(0, -ldRite);
      ensurePrecision(left, leftPrec);
      final int ldLeft = msbOfApprox(left) + 1;
      final int ritePrec = precision + 2 + max(0, ldLeft) + max(0, -2 * ldRite);
      ensurePrecision(rite, ritePrec);
      final int opPrec = precision + 2 + max(0, ldLeft) + max(0, -ldRite);
      final Rounder rounder = new Rounder(opPrec, ROUNDING_MODE);
      final BigFloat approx = left.approximation().div(rite.approximation(), rounder);
      final boolean exact = divIsExact(left.approximation(), rite.approximation(), approx);
      setApproximation(a, approx, precision, exact);
   }

   /**
    * Helper method for {@link #ensurePrecision(Approximable, int, ApproximationType)}.
    */
   private static void ensurePrecisionRoot(final Approximable<?> a, final int precision)
   {

      final int n = a.index();
      if (n < 2)
      {
         throw new UnsupportedOperationException("n < 2");
      }
      final Approximable<?> left = a.left();
      final int ldLeft = max(0, msbOfApprox(left));
      final int opPrec = precision + 2 + (ldLeft + n - 1) / n;
      final int ltPrec = n == 2 ? opPrec : precision + 3 + ldLeft;
      ensurePrecision(left, ltPrec);
      final BigFloat approx = left.approximation().root(n, new Rounder(opPrec, CEILING));
      final boolean exact = rootIsExact(left.approximation(), n, approx);
      setApproximation(a, approx, precision, exact);
   }

   /**
    * Helper method for {@link #ensurePrecision(Approximable, int, ApproximationType)}.
    */
   private static void ensurePrecisionPow(final Approximable<?> a, final int precision)
   {

      final int n = a.index();
      if (n != 2)
      {
         throw new UnsupportedOperationException("n != 2");
      }
      final Approximable<?> left = a.left();
      final int ltPrec = precision + 2 + max(0, msbOfApprox(left)) + 1;
      ensurePrecision(left, ltPrec);
      final int oom = 2 * msbOfApprox(left);
      final int opPrec = precision + 2 + max(0, oom) + 1;
      final Rounder rounder = new Rounder(opPrec, ROUNDING_MODE);
      final BigFloat approx = left.approximation().pow(n, rounder);
      final boolean exact = powIsExact(left.approximation(), n, approx);
      setApproximation(a, approx, precision, exact);
   }

   /**
    * Helper method for {@link #ensurePrecision(Approximable, int, ApproximationType)}.
    */
   private static void ensurePrecisionAbs(final Approximable<?> a, final int precision)
   {

      final Approximable<?> left = a.left();
      ensurePrecision(left, precision);
      final BigFloat approx = left.approximation().abs();
      final boolean exact = true;
      setApproximation(a, approx, precision, exact);
   }

   /**
    * Helper method for {@link #ensurePrecision(Approximable, int, ApproximationType)}.
    */
   private static void ensurePrecisionNeg(final Approximable<?> a, final int precision)
   {

      final Approximable<?> left = a.left();
      ensurePrecision(left, precision);
      final BigFloat approx = left.approximation().neg();
      final boolean exact = true;
      setApproximation(a, approx, precision, exact);
   }

   /**
    * Sets the approximation value and its precision, which may be {@link Integer#MAX_VALUE} if all
    * involved values are exact.
    * 
    * @param precision
    *           The new precision.
    * @param exact
    *           Whether the result of the operation is exact.
    */
   private static void setApproximation(final Approximable<?> a, final BigFloat approx,
         final int precision, final boolean exact)
   {

      if (exact && a.left().precision() == Integer.MAX_VALUE
            && (a.right() == null || a.right().precision() == Integer.MAX_VALUE))
      {
         a.setApproximation(approx, Integer.MAX_VALUE);
      }
      else
      {
         a.setApproximation(approx, precision);
      }
   }

   /**
    * Returns the {@link BigFloat#msb() the position of the most significant bit} of the absolute
    * value of the approximation of a given approximable expression or {@code 0} if the absolute
    * value is {@code 0}.
    * 
    * @param a
    *           the approximable expression.
    * @return see above.
    */
   private static int msbOfApprox(final Approximable<?> a)
   {

      final BigFloat approximation = a.approximation();
      return approximation.signum() != 0 ? approximation.abs().msb() : 0;
   }

   private PDC()
   {
   }

   /** Initial precision, used when approximations are not yet known. */
   private static final int INITIAL_PRECISION = 1;

   /** Rounder for denominator. */
   private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

   /** Rounder for denominator. */
   private static final Rounder ROUNDER_DBL_FLOOR = new Rounder(DOUBLE.getPrecision(), FLOOR);
}

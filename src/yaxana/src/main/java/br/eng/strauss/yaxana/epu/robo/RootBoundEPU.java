package br.eng.strauss.yaxana.epu.robo;

import static br.eng.strauss.yaxana.big.BigFloat.twoTo;
import static br.eng.strauss.yaxana.big.Rounder.DOUBLE;
import static java.lang.Math.max;
import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.UP;

import java.math.BigInteger;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.big.Rounder;
import br.eng.strauss.yaxana.epu.AbstractEPU;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.exc.PrecisionOverflowException;
import br.eng.strauss.yaxana.exc.UnreachedException;
import br.eng.strauss.yaxana.pdc.PDCTools;

/**
 * EPU implementation using the root bound approach.
 * <p>
 * The root bound approach computes bounds for the absolute values of the roots of the minimum monic
 * polynomials of two algebraic integers to determine the precision of an approximation needed to
 * safely recognize the signum of the exact value of an algebraic expression and then calculates
 * approximations with increasing precision up to the possibly needed one, to ascertain the signum
 * of the exact value.
 * <p>
 * This implementation specifically uses the {@code BMFSS[k], k = 2} root bound. See the papers
 * cited below.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 * @see <a><em>Christoph Burnikel et. al.:</em>
 *      "A Strong and Easily Computable Separation Bound for Arithmetic Expressions Involving Radicals"
 *      </a>
 * @see <a><em>Christoph Burnikel et. al.:</em>
 *      "A Separation Bound for Real Algebraic Expressions"</a>
 * @see <a><em>Sylvain Pion, Chee Yap:</em>
 *      "Constructive root bound for k-ary rational input numbers", Journal of Theoretical Computer
 *      Science (TCS), Elsevier, 2006, 369 (1-3), pp.361-376.</a>
 */
public final class RootBoundEPU extends AbstractEPU
{

   public RootBoundEPU()
   {

      this(false);
   }

   public RootBoundEPU(final boolean wellWorkedExpression)
   {

      this.wellWorkedExpression = wellWorkedExpression;
   }

   @Override
   protected int computeSignum()
   {

      final Algebraic value = this.operand;
      int precision = PDCTools.increment(value, 0);
      BigFloat approx = value.approximation(precision);
      for (int sufficientPrecision = -1;;)
      {
         precision = value.precision();
         if (precision == Integer.MAX_VALUE || approx.abs().compareTo(twoTo(-precision)) >= 0)
         {
            return approx.signum();
         }
         if (sufficientPrecision < 0)
         {
            sufficientPrecision = sufficientPrecision(value);
            if (sufficientPrecision == 0)
            {
               return PDCTools.setExactZero(value);
            }
            if (sufficientPrecision == Integer.MAX_VALUE)
            {
               return value.signum();
            }
         }
         if (precision >= sufficientPrecision)
         {
            if (approx.abs().compareTo(twoTo(-sufficientPrecision)) > 0)
            {
               return approx.signum();
            }
            return PDCTools.setExactZero(value);
         }
         precision = PDCTools.increment(value, precision);
         approx = value.approximation(precision);
      }
   }

   /**
    * Returns a precision which is suffices to guarantee that a zero approximation value of that
    * same precision signals a zero true value.
    * 
    * @return a precision which is suffices to guarantee that a zero approximation value of that
    *         same precision signals a zero true value.
    */
   public int sufficientPrecision()
   {

      return sufficientPrecision(this.operand);
   }

   public int sufficientPrecision(final Algebraic value)
   {

      ensureRootBoundParameters(value);
      if (value.precision() == Integer.MAX_VALUE)
      {
         return Integer.MAX_VALUE;
      }
      else
      {
         final BigFloat zeta = rootBound(value);
         return zeta.signum() != 0 ? max(1, 1 - zeta.msb()) : 0;
      }
   }

   public void ensureRootBoundParameters(final Algebraic a)
   {

      if (a.u == null)
      {
         final Algebraic left = a.left();
         final Algebraic rite = a.right();
         if (left != null && left.u == null)
         {
            ensureRootBoundParameters(left);
         }
         if (rite != null && rite.u == null)
         {
            ensureRootBoundParameters(rite);
         }
         switch (a.type())
         {
            default :
               throw new UnreachedException();
            case TERMINAL :
            {
               final BigFloat ap = a.approximation();
               final int m = ap.scale();
               a.vp = max(0, m);
               a.vn = max(0, -m);
               a.u = new BigFloat(BigInteger.ONE.max(ap.unscaledValue().abs())).round(MCU);
               a.l = BigFloat.ONE;
               a.D = 1;
               break;
            }
            case ADD :
            case SUB :
            {
               final int vpa = left.vp + rite.vn;
               final int vpb = left.vn + rite.vp;
               a.vp = vpa < vpb ? vpa : vpb;
               a.vn = left.vn + rite.vn;
               final BigFloat fa = twoTo(left.vp + rite.vn - a.vp);
               final BigFloat fb = twoTo(left.vn + rite.vp - a.vp);
               a.u = fa.mul(left.u.mul(rite.l, MCU)).add(fb.mul(rite.u.mul(left.l, MCU), MCU), MCU);
               a.l = left.l.mul(rite.l, MCD);
               mulDegree(a);
               break;
            }
            case MUL :
            {
               a.vp = left.vp + rite.vp;
               a.vn = left.vn + rite.vn;
               a.u = left.u.mul(rite.u, MCU);
               a.l = left.l.mul(rite.l, MCD);
               mulDegree(a);
               break;
            }
            case DIV :
            {
               a.vp = left.vp + rite.vn;
               a.vn = left.vn + rite.vp;
               a.u = left.u.mul(rite.l, MCU);
               a.l = left.l.mul(rite.u, MCD);
               mulDegree(a);
               break;
            }
            case POW :
            {
               final int n = a.index();
               a.vp = n * left.vp;
               a.vn = n * left.vn;
               a.u = left.u.pow(n, MCU);
               a.l = left.l.pow(n, MCD);
               a.D = left.D;
               break;
            }
            case ROOT :
            {
               BigFloat f = twoTo(left.vp - left.vn);
               final int n = a.index();
               if (f.mul(left.u).compareTo(left.l) >= 0)
               {
                  final int vs = left.vp + (n - 1) * left.vn;
                  a.vp = vs / n;
                  a.vn = left.vn;
                  f = twoTo(vs - n * a.vp);
                  a.u = f.mul(left.u, MCU).mul(left.l.pow(n - 1, MCU), MCU).sqrt(MCU);
                  a.l = left.l;
               }
               else
               {
                  final int vs = (n - 1) * left.vp + left.vn;
                  a.vp = left.vp;
                  a.vn = vs / n;
                  f = twoTo(vs - n * a.vn);
                  a.u = left.u;
                  a.l = f.mul(left.u.pow(n - 1, MCU), MCU).mul(left.l, MCU).sqrt(MCU);
               }
               a.D = n * left.D;
               break;
            }
            case NEG :
            case ABS :
            {
               a.vp = left.vp;
               a.vn = left.vn;
               a.u = left.u;
               a.l = left.l;
               a.D = left.D;
               break;
            }
         }
      }
   }

   public BigFloat rootBound(final Algebraic a)
   {

      try
      {
         if (a.u.signum() == 0 || a.l.signum() == 0)
         {
            return BigFloat.ZERO;
         }
         final Rounder rounder = MCD;
         final BigFloat nom = BigFloat.twoTo(a.vp - a.vn);
         final int exponent = this.wellWorkedExpression ? 1 : (int) (a.D - 1L);
         return nom.div(a.u.pow(exponent).mul(a.l), rounder);
      }
      catch (final ArithmeticException e)
      {
         throw new UnreachedException(e);
      }
   }

   private void mulDegree(final Algebraic a)
   {

      a.D = a.left().D * a.right().D;
      if (!wellWorkedExpression && a.D >= PrecisionOverflowException.MAX_PRECISION)
      {
         throw new PrecisionOverflowException(a.toString());
      }
   }

   protected static final Rounder MCU = new Rounder(DOUBLE.getPrecision(), UP);
   protected static final Rounder MCD = new Rounder(DOUBLE.getPrecision(), DOWN);

   private final boolean wellWorkedExpression;
}

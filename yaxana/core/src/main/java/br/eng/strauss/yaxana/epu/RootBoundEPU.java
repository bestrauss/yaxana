package br.eng.strauss.yaxana.epu;

import static br.eng.strauss.yaxana.big.BigFloat.twoTo;
import static br.eng.strauss.yaxana.big.Rounder.DOUBLE;
import static java.lang.Math.max;

import java.math.BigInteger;
import java.math.RoundingMode;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.big.Rounder;
import br.eng.strauss.yaxana.exc.UnreachedException;
import br.eng.strauss.yaxana.pdc.PDCTools;

/**
 * @author Burkhard Strauss
 * @since August 2017
 */
abstract class RootBoundEPU extends AbstractEPU
{

   protected RootBoundEPU()
   {

   }

   @Override
   protected final int computeSignum()
   {

      return computeSignum(this.operand);
   }

   protected int computeSignum(final Algebraic value)
   {

      int precision = PDCTools.increment(value, 0);
      BigFloat approx = value.approximation(precision);
      for (int sufficientPrecision = -1;;)
      {
         precision = value.precision();
         if (precision == Integer.MAX_VALUE)
         {
            return approx.signum();
         }
         if (approx.abs().compareTo(twoTo(-precision)) >= 0)
         {
            if (this.sufficientPrecision != null)
            {
               this.sufficientPrecision.accept(precision);
               this.sufficientPrecision = null;
            }
            if (sufficientPrecision >= 0 && approx.abs().compareTo(twoTo(-sufficientPrecision)) < 0)
            {
               throw new IllegalStateException(getClass().getSimpleName()
                     + " furnished a non sufficient precision of " + sufficientPrecision);
            }
            return approx.signum();
         }
         if (sufficientPrecision < 0)
         {
            sufficientPrecision = sufficientPrecision(value);
            if (this.sufficientPrecision != null)
            {
               this.sufficientPrecision.accept(sufficientPrecision);
               this.sufficientPrecision = null;
            }
            if (sufficientPrecision == 0)
            {
               return PDCTools.setExactZero(value);
            }
            if (sufficientPrecision == Integer.MAX_VALUE)
            {
               return approx.signum();
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
   protected final int sufficientPrecision()
   {

      return sufficientPrecision(this.operand);
   }

   protected int sufficientPrecision(final Algebraic value)
   {

      ensureRootBoundParameters(value);
      if (value.precision() == Integer.MAX_VALUE)
      {
         return Integer.MAX_VALUE;
      }
      else
      {
         final BigFloat zeta = lowerRootBound(value);
         return zeta.signum() != 0 ? max(1, 1 - zeta.msb()) : 0;
      }
   }

   private final void ensureRootBoundParameters(final Algebraic a)
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
               a.u = new BigFloat(BigInteger.ONE.max(ap.unscaledValue().abs())).round(UP);
               a.l = BigFloat.ONE;
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
               a.u = fa.mul(left.u.mul(rite.l, UP)).add(fb.mul(rite.u.mul(left.l, UP), UP), UP);
               a.l = left.l.mul(rite.l, DN);
               break;
            }
            case MUL :
            {
               a.vp = left.vp + rite.vp;
               a.vn = left.vn + rite.vn;
               a.u = left.u.mul(rite.u, UP);
               a.l = left.l.mul(rite.l, DN);
               break;
            }
            case DIV :
            {
               a.vp = left.vp + rite.vn;
               a.vn = left.vn + rite.vp;
               a.u = left.u.mul(rite.l, UP);
               a.l = left.l.mul(rite.u, DN);
               break;
            }
            case POW :
            {
               final int n = a.index();
               a.vp = n * left.vp;
               a.vn = n * left.vn;
               a.u = left.u.pow(n, UP);
               a.l = left.l.pow(n, DN);
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
                  a.u = f.mul(left.u, UP).mul(left.l.pow(n - 1, UP), UP).root(n, UP);
                  a.l = left.l;
               }
               else
               {
                  final int vs = (n - 1) * left.vp + left.vn;
                  a.vp = left.vp;
                  a.vn = vs / n;
                  f = twoTo(vs - n * a.vn);
                  a.u = left.u;
                  a.l = f.mul(left.u.pow(n - 1, DN), DN).mul(left.l, DN).root(n, DN);
               }
               break;
            }
            case NEG :
            case ABS :
            {
               a.vp = left.vp;
               a.vn = left.vn;
               a.u = left.u;
               a.l = left.l;
               break;
            }
         }
      }
   }

   protected BigFloat lowerRootBound(final Algebraic a)
   {

      final BigFloat nom = BigFloat.twoTo(a.vp - a.vn);
      return nom.div(a.u.pow(exponent(a)).mul(a.l), DN);
   }

   protected int exponent(final Algebraic a)
   {

      return 1;
   }

   protected static final Rounder UP = new Rounder(DOUBLE.getPrecision(), RoundingMode.UP);
   protected static final Rounder DN = new Rounder(DOUBLE.getPrecision(), RoundingMode.DOWN);
}

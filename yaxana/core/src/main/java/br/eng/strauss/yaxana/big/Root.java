package br.eng.strauss.yaxana.big;

import static br.eng.strauss.yaxana.big.BigFloat.ONE;
import static br.eng.strauss.yaxana.big.BigFloat.TWO;

import java.math.BigInteger;

import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.exc.NegativeRadicandException;

/**
 * Implementation of the sqrt/root operations of {@link BigFloat}.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
final class Root
{

   /**
    * Returns a new instance {@code sqrt(thiz)} rounded.
    * 
    * @param thiz
    *           the value.
    * @param rounder
    *           the rounder.
    * @return a new instance.
    */
   public static BigFloat sqrt(final BigFloat thiz, final Rounder rounder)
   {

      switch (thiz.signum())
      {
         case 0 :
            return BigFloat.ZERO;
         case -1 :
            throw new NegativeRadicandException();
      }
      if (thiz.isOne())
      {
         return ONE;
      }
      {
         final int n = 2;
         final int oom = thiz.msb();
         if (Math.abs(oom) >= n)
         {
            final int oomRoot = oom / n;
            final int oomRadi = n * oomRoot;
            return thiz.mulTwoTo(-oomRadi).sqrt(rounder).mulTwoTo(oomRoot);
         }
      }
      final int prec = rounder.getPrecision();
      final BigFloat HALF = new BigFloat(BigInteger.ONE, -1);
      final int maxPrecision = prec + 4 + thiz.precision();
      final BigFloat acceptableError = BigFloat.twoTo(-(prec + 2));
      BigFloat sqrt = thiz.mul(HALF, Rounder.SINGLE);
      int adaptivePrecision = 2;
      BigFloat previousSqrt;
      do
      {
         if (adaptivePrecision < maxPrecision)
         {
            adaptivePrecision *= 3;
            if (adaptivePrecision > maxPrecision)
            {
               adaptivePrecision = maxPrecision;
            }
         }
         final Rounder ctx = new Rounder(adaptivePrecision);
         previousSqrt = sqrt;
         sqrt = thiz.div(sqrt, ctx).add(sqrt, ctx).mul(HALF, ctx);
      }
      while (adaptivePrecision < maxPrecision
            || sqrt.sub(previousSqrt).abs().compareTo(acceptableError) > 0);
      return sqrt.round(rounder);
   }

   /**
    * Returns a new instance {@code n-th root(this)} rounded.
    * 
    * @param thiz
    *           the value.
    * @param n
    *           the index.
    * @param rounder
    *           the rounder.
    * @return a new instance.
    */
   public static BigFloat root(final BigFloat thiz, final int n, final Rounder rounder)
   {

      if (n == 0)
      {
         throw new DivisionByZeroException();
      }
      if (n < 0)
      {
         return BigFloat.ONE.div(thiz.root(-n, rounder), rounder);
      }
      if (n == 1)
      {
         return thiz;
      }
      if (n == 2)
      {
         return thiz.sqrt(rounder);
      }
      switch (thiz.signum())
      {
         case 0 :
            return BigFloat.ZERO;
         case -1 :
            if ((n & 1) == 0)
            {
               throw new NegativeRadicandException();
            }
            else
            {
               return thiz.neg().root(n, rounder).neg();
            }
      }
      if (thiz.isOne())
      {
         return ONE;
      }
      {
         final int oom = thiz.msb();
         if (Math.abs(oom) >= n)
         {
            final int oomRoot = oom / n;
            final int oomRadi = n * oomRoot;
            return thiz.mulTwoTo(-oomRadi).root(n, rounder).mulTwoTo(oomRoot);
         }
      }
      final int prec = rounder.getPrecision();
      final int maxPrecision = prec + 4 + thiz.precision();
      final BigFloat acceptableError = BigFloat.twoTo(-(prec + 1));
      final BigFloat bn = new BigFloat(n);
      final int nMinus1 = n - 1;
      BigFloat result = thiz.div(TWO, Rounder.SINGLE);
      int adaptivePrecision = 2;
      BigFloat step;
      do
      {
         adaptivePrecision = adaptivePrecision * 3;
         if (adaptivePrecision > maxPrecision)
         {
            adaptivePrecision = maxPrecision;
         }
         final Rounder ctx = new Rounder(adaptivePrecision);
         step = thiz.div(result.pow(nMinus1, ctx), ctx).sub(result, ctx).div(bn, ctx);
         result = result.add(step, ctx);
      }
      while (adaptivePrecision < maxPrecision || step.abs().compareTo(acceptableError) > 0);
      return result.round(rounder);
   }

   private Root()
   {
   }
}

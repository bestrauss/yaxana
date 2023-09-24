package br.eng.strauss.yaxana.epu;

import br.eng.strauss.yaxana.Type;
import br.eng.strauss.yaxana.exc.PrecisionOverflowException;

/**
 * EPU implementation as proposed in "Constructive root bound for k-ary rational input numbers".
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
final class BfmssEPU extends RootBoundEPU
{

   public BfmssEPU()
   {

   }

   @Override
   public int sufficientPrecision(final Algebraic value)
   {

      clearVisitedMarks(value);
      productOfIndices(value);
      return super.sufficientPrecision(value);
   }

   @Override
   protected int exponent(final Algebraic a)
   {

      return (int) (a.D - 1L);
   }

   void clearVisitedMarks(final Algebraic a)
   {

      a.D = Long.MIN_VALUE;
      final Algebraic left = a.left();
      if (left != null)
      {
         clearVisitedMarks(left);
      }
      final Algebraic rite = a.right();
      if (rite != null)
      {
         clearVisitedMarks(rite);
      }
   }

   long productOfIndices(final Algebraic a)
   {

      long product = 1L;
      if (a.D == Long.MIN_VALUE)
      {
         final Algebraic left = a.left();
         final Algebraic rite = a.right();
         if (left != null && left.D == Long.MIN_VALUE)
         {
            product *= productOfIndices(left);
         }
         if (rite != null && rite.D == Long.MIN_VALUE)
         {
            product *= productOfIndices(rite);
         }
         if (a.type() == Type.ROOT)
         {
            product *= a.index();
         }
      }
      if (product >= PrecisionOverflowException.MAX_PRECISION)
      {
         throw new PrecisionOverflowException(a.toString());
      }
      return a.D = product;
   }
}

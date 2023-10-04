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
   protected int exponent(final Algebraic value)
   {

      clearVisitedMarks(value);
      productOfIndices(value);
      return (int) (value.D - 1L);
   }

   static void clearVisitedMarks(final Algebraic value)
   {

      value.D = Long.MIN_VALUE;
      final Algebraic left = value.left();
      if (left != null)
      {
         clearVisitedMarks(left);
      }
      final Algebraic rite = value.right();
      if (rite != null)
      {
         clearVisitedMarks(rite);
      }
   }

   static long productOfIndices(final Algebraic value)
   {

      long product = 1L;
      if (value.D == Long.MIN_VALUE)
      {
         final Algebraic left = value.left();
         final Algebraic rite = value.right();
         if (left != null && left.D == Long.MIN_VALUE)
         {
            product *= productOfIndices(left);
         }
         if (rite != null && rite.D == Long.MIN_VALUE)
         {
            product *= productOfIndices(rite);
         }
         if (value.type() == Type.ROOT)
         {
            product *= value.index();
         }
      }
      if (product >= PrecisionOverflowException.MAX_PRECISION)
      {
         throw new PrecisionOverflowException(value.toString());
      }
      return value.D = product;
   }
}

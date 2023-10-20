package br.eng.strauss.yaxana.epu;

import static br.eng.strauss.yaxana.epu.Algebraic.ONE;

import br.eng.strauss.yaxana.pdc.PDCTools;

/**
 * Experimental EPU.
 * 
 * @author Burkhard Strauss
 * @since 07-2022
 */
final class ZvaaEPU extends RootBoundEPU
{

   @Override
   protected final int computeSignum(final Algebraic value)
   {

      if (value.isDivisionFree())
      {
         final Algebraic testValue = value.div(value.left().abs().add(value.right().abs()));
         final int signum = super.computeSignum(testValue);
         return signum == 0 ? PDCTools.setExactZero(value) : signum;
      }
      else
      {
         final Algebraic testValue = ONE.sub(value.right().div(value.left()).pow(2));
         return super.computeSignum(testValue) == 0 //
               ? PDCTools.setExactZero(value)
               : PDCTools.ensureFiniteApproximation(value);
      }
   }
}

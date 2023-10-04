package br.eng.strauss.yaxana.epu;

import static br.eng.strauss.yaxana.epu.Algebraic.ONE;

import br.eng.strauss.yaxana.pdc.PDCTools;

/**
 * EPU implementation as proposed in "Zum Vorzeichentest Algebraischer Ausdrücke".
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
      { // ZVAA
         final Algebraic testValue = value.div(value.left().abs().add(value.right().abs()));
         final int signum = super.computeSignum(testValue);
         return signum == 0 ? PDCTools.setExactZero(value) : signum;
      }
      else
      { // should be provable, we're testing T = 1 - (E_1/E_0)^2
         final Algebraic testValue = ONE.sub(value.right().div(value.left()).pow(2));
         return super.computeSignum(testValue) == 0 //
               ? PDCTools.setExactZero(value)
               : PDCTools.ensureFiniteApproximation(value);
      }
   }
}

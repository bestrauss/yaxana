package br.eng.strauss.yaxana.epu;

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
   protected final int computeSignum()
   {

      final Algebraic originalOperand = this.operand;
      try
      {
         final Algebraic val = this.operand;
         this.operand = val.left().sub(val.right()).div(val.left().abs().add(val.right().abs()));
         final int signum = super.computeSignum();
         return signum == 0 ? PDCTools.setExactZero(originalOperand)
               : PDCTools.ensureFiniteApproximation(originalOperand);
      }
      finally
      {
         this.operand = originalOperand;
      }
   }
}

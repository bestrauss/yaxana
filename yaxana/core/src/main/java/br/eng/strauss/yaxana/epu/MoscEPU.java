package br.eng.strauss.yaxana.epu;

import br.eng.strauss.yaxana.pdc.PDCTools;

/**
 * EPU implementation as proposed in "More on Sign Computation of Algebraic Expressions".
 * 
 * @author Burkhard Strauss
 * @since 07-2022
 */
final class MoscEPU extends RootBoundEPU
{

   @Override
   public int sufficientPrecision(final Algebraic value)
   {

      final Algebraic denom = this.operand.left().abs().add(this.operand.right().abs());
      PDCTools.ensureFiniteApproximation(denom);
      final int denomPrec = Math.max(0, -denom.approximation().msb());
      final Algebraic customOperand = this.operand.div(denom);
      return super.sufficientPrecision(customOperand) + denomPrec;
   }
}

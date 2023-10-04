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

      final Algebraic denom = value.left().abs().add(value.right().abs());
      PDCTools.ensureFiniteApproximation(denom);
      final int denomPrec = 1 - denom.approximation().msb();
      final int precision = super.sufficientPrecision(value.div(denom)) + denomPrec;
      return denomPrec >= 0 ? precision : (int) (1.65 * precision + 0.5);
   }
}

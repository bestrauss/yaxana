package br.eng.strauss.yaxana.epu.conj;

import br.eng.strauss.yaxana.Experimental;
import br.eng.strauss.yaxana.epu.AbstractEPU;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.yaxa.YaxanaEPU;
import br.eng.strauss.yaxana.pdc.PDCTools;

/**
 * EPU implementation which calculates the product of the values of all conjugate expressions using
 * {@code double} precision arithmetic.
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
@Experimental
public final class DoubleConjugatesEPU extends AbstractEPU
{

   @Override
   protected final int computeSignum()
   {

      final Algebraic value = this.operand;
      final Algebraic productOfConjugates = value.productOfConjugates();
      if (productOfConjugates.complexValue().real() < 0.5d)
      {
         return new YaxanaEPU().signum(value);
      }
      return PDCTools.ensureFiniteApproximation(value);
   }
}

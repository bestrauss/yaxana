package br.eng.strauss.yaxana.epu.yaxa;

import static br.eng.strauss.yaxana.big.BigFloat.ONE;
import static br.eng.strauss.yaxana.big.BigFloat.twoTo;

import br.eng.strauss.yaxana.Experimental;
import br.eng.strauss.yaxana.Type;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.AbstractEPU;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.zvaa.ZvaaEPU;
import br.eng.strauss.yaxana.pdc.PDCTools;

/**
 * EPU implementation even faster than {@link ZvaaEPU}.
 * <ul>
 * <li>passes all tests,
 * <li>but there is no mathematical proof that it works for all cases.
 * </ul>
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
@Experimental
public final class YaxanaEPU extends AbstractEPU
{

   @Override
   protected final int computeSignum()
   {

      final Algebraic value = this.operand;
      if (value.type() == Type.SUB)
      {
         value.approximation(1);
         final int precision = value.yaxanaPrecision();
         final BigFloat leftApprox = value.left().approximation().abs();
         final boolean bigValues = leftApprox.compareTo(ONE) > 0;
         final BigFloat testValue = bigValues ? value.approximation(precision)
               : ONE.sub(value.right().div(value.left()).approximation(precision));
         return testValue.abs().compareTo(twoTo(-precision)) < 0 ? PDCTools.setExactZero(value)
               : PDCTools.ensureFiniteApproximation(value);
      }
      throw new UnsupportedOperationException();
   }
}

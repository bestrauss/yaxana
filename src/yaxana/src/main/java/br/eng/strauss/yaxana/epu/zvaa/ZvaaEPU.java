package br.eng.strauss.yaxana.epu.zvaa;

import br.eng.strauss.yaxana.Type;
import br.eng.strauss.yaxana.epu.AbstractEPU;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.robo.RootBoundEPU;
import br.eng.strauss.yaxana.pdc.PDCTools;

/**
 * EPU implementation as proposed in "Zum Vorzeichentest Algebraischer Ausdrücke".
 * 
 * @author Burkhard Strauss
 * @since 07-2022
 */
public final class ZvaaEPU extends AbstractEPU
{

   @Override
   protected final int computeSignum()
   {

      final Algebraic value = this.operand;
      if (value.type() == Type.SUB)
      {
         final Algebraic testValue = value.left().sub(value.right())
               .div(value.left().abs().add(value.right().abs()));
         final int signum = new RootBoundEPU(true).signum(testValue);
         return signum == 0 ? PDCTools.setExactZero(value)
               : PDCTools.ensureFiniteApproximation(value);
      }
      throw new UnsupportedOperationException();
   }
}

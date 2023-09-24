package br.eng.strauss.yaxana.pdc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * @author Burkhard Strauß
 * @since 2023-09
 */
public final class PDCToolsTest
{

   @Test
   public void pimpCoverage()
   {

      final Algebraic a = new Algebraic("\\7P-200");
      final Algebraic b = Algebraic.ZERO;
      PDCTools.ensureFiniteApproximation(a, b);
      assertTrue(a.approximation().compareTo(BigFloat.ZERO) > 0);
      assertTrue(b.approximation().compareTo(BigFloat.ZERO) == 0);
   }
}

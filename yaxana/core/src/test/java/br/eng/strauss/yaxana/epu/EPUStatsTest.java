package br.eng.strauss.yaxana.epu;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.exc.IllegalExponentException;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauß
 * @since 2023-09
 */
public final class EPUStatsTest extends YaxanaTest
{

   @Test
   public void coverage()
   {

      assertThrows(IllegalExponentException.class, () -> {
         Robust.valueOf(2d).sqrt().pow(Robust.MAX_EXPONENT + 1).signum();
      });
      assertThrows(UnsupportedOperationException.class, () -> {
         final Algebraic a = new Algebraic(2D).sqrt();
         EPUStats.getInstance().signum(17, () -> a.signum());
      });
   }
}

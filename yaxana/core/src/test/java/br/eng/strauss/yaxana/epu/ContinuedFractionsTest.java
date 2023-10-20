package br.eng.strauss.yaxana.epu;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class ContinuedFractionsTest extends YaxanaTest
{

   @WithAlgorithms
   @Test
   public void testSqrtOfTwo()
   {

      final Robust desired = Robust.valueOf("\\2");
      Robust actual = Robust.valueOf("1/2");
      for (int k = 0; k < 100; k++)
      {
         actual = Robust.valueOf(2).add(Robust.ONE.div(actual));
      }
      actual = Robust.valueOf(1).add(Robust.ONE.div(actual));
      assertTrue(desired.sub(actual).abs().isLessThan(Robust.valueOf("1P-250")));
      format("%s\n", Math.sqrt(2d));
      format("%s\n", ((Algebraic) desired.toSyntaxTree()).approximation(100));
      format("%s\n", ((Algebraic) desired.sub(actual).abs().toSyntaxTree()).approximation(300));
   }
}

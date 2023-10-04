package br.eng.strauss.yaxana.epu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
@WithAlgorithms
public final class ContinuedFractionsTest extends YaxanaTest
{

   @Test
   public void testSqrtOfTwo()
   {

      final Robust desired = Robust.valueOf("\\2");
      Robust actual = Robust.valueOf("1/2");
      for (int k = 0; k < 50; k++)
      {
         actual = Robust.valueOf(2).add(Robust.ONE.div(actual));
      }
      actual = Robust.valueOf(1).add(Robust.ONE.div(actual));
      assertEquals(-1, desired.sub(actual).abs().sub(Robust.valueOf("1P-100")).signum());
      format("%s\n", Math.sqrt(2d));
      format("%s\n", ((Algebraic) desired.toSyntaxTree()).approximation(100));
      format("%s\n", ((Algebraic) desired.sub(actual).abs().toSyntaxTree()).approximation(150));
   }
}

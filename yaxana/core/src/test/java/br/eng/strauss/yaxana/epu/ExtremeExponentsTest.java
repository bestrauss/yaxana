package br.eng.strauss.yaxana.epu;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.tools.SampleAlgebraic;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * Diese Tests sind entstanden, als die Klasse PDC noch Probleme hatte.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class ExtremeExponentsTest extends YaxanaTest
{

   @WithAlgorithms
   @Test
   public void test()
   {

      test("\\1p-100", 4, 0);
      test("\\1p-101", 4, 0);
      test("\\1p-181", 4, 0);

      assertTrue(new Algebraic("\\1P-11").compareTo(new Algebraic("\\2*1P-6")) == 0);
      assertTrue(new Algebraic("\\1P-325").compareTo(new Algebraic("\\2*1P-163")) == 0);
      for (int k = 180; k <= 190; k++)
      {
         test("\\1p-" + k, 4, 0);
      }
      test("\\1p-190", 4, 0);
      test("\\1p-325", 4, 0);
      test("\\1237251p-325", 8, 0);
      test("\\1237251p-53", 16, 0);
      test("\\\\12372511234568764531445678764p-325", 8, 0);

      test("\\12372511234568764531445678764p-12325", 8, 0);
   }

   private void test(final String x, final int N, final int desiredSignum)
   {

      final Algebraic q = new Algebraic(x).sqrt();
      final Algebraic value = SampleAlgebraic.geometricSeries(q, N);
      assertTrue(value.signum() == desiredSignum);
   }
}

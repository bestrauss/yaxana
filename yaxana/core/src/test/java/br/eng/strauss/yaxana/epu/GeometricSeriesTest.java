package br.eng.strauss.yaxana.epu;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Algorithm;
import br.eng.strauss.yaxana.exc.PrecisionOverflowException;
import br.eng.strauss.yaxana.tools.SampleAlgebraic;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * Tests partial sums of the geometric series using all EPUs.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public class GeometricSeriesTest extends YaxanaTest
{

   @WithAlgorithms
   @Test
   public void testEPU()
   {

      test(new Algebraic("7").sqrt(), 4);
      test(new Algebraic("7").sqrt(), 1);
      test(new Algebraic("7").sqrt(), 2);
      test(new Algebraic("1p-1").sqrt(), 2);
      test(new Algebraic("1p-1").sqrt(), 8);
      test(new Algebraic(2d).sqrt(), 8);
   }

   private void test(final Algebraic q, final int N)
   {

      test(q, N, 0);
   }

   private void test(final Algebraic q, final int N, final int desiredSignum)
   {
      try
      {
         final Algebraic value = SampleAlgebraic.geometricSeries(q, N);
         if (value.signum() != desiredSignum)
         {
            format("EPU: %s\n", Algebraic.getAlgorithm());
            format("value: %s\n", value);
         }
         assertTrue(value.signum() == desiredSignum);
      }
      catch (final PrecisionOverflowException e)
      {
         if (Algebraic.getAlgorithm().equals(Algorithm.BFMSS2))
         {
            return;
         }
         fail();
      }
   }
}

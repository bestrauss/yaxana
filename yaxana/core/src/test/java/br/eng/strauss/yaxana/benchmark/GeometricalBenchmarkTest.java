package br.eng.strauss.yaxana.benchmark;

import static br.eng.strauss.yaxana.unittest.YaxanaSettings.STRESS_LEVEL;

import java.util.Random;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.epu.EPU;
import br.eng.strauss.yaxana.tools.SampleRobust;
import br.eng.strauss.yaxana.unittest.Benchmark;

/**
 * {@link EPU} benchmark, comparing EPUs.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class GeometricalBenchmarkTest extends BenchmarkTest
{

   @Benchmark(1)
   @Test
   public void test()
   {

      try
      {
         Robusts.setSimplification(false);
         run();
      }
      finally
      {
         Robusts.setSimplification(true);
      }
   }

   @Override
   protected Robust[] createNewLeftAndRightExpressions()
   {

      final int shift = random.nextInt(31);
      final boolean sign = random.nextBoolean();
      final Robust sci = Robust.valueOf(sign ? 1 << shift : 1d / (1 << shift));
      final Robust q = Robust.valueOf(random.nextDouble()).mul(sci).root(7);
      return SampleRobust.geometricSeriesArray(q, ORDER);
   }

   @Override
   protected int getLoopCount()
   {

      return STRESS_LEVEL > 0 ? STRESS_LEVEL * 20 : 10;
   }

   private static final int ORDER = 10;

   private final Random random = new Random(0L);
}

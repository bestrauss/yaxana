package br.eng.strauss.yaxana.benchmark;

import static br.eng.strauss.yaxana.Algorithm.BFMSS2;
import static br.eng.strauss.yaxana.Algorithm.YAXANA;
import static br.eng.strauss.yaxana.Algorithm.ZVAA;
import static br.eng.strauss.yaxana.Robust.ZERO;
import static org.junit.jupiter.api.Assertions.fail;

import br.eng.strauss.yaxana.Algorithm;
import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.EPU;
import br.eng.strauss.yaxana.pdc.PDCStats;
import br.eng.strauss.yaxana.tools.YaxanaTest;

/**
 * {@link EPU} benchmark, comparing EPUs.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
abstract class BenchmarkTest extends YaxanaTest
{

   public void run()
   {

      warmupJVM();
      test(getLoopCount());
   }

   private void warmupJVM()
   {

      format("Warming up the JVM ... ");
      final boolean quiet = YaxanaTest.quiet;
      try
      {
         YaxanaTest.quiet = true;
         for (final Algorithm algorithm : ALGORITHMS)
         {
            Algebraic.setAlgorithm(algorithm);
            Robusts.clearCache();
            test(2);
         }
      }
      finally
      {
         YaxanaTest.quiet = quiet;
      }
      format("done\n");
   }

   private void test(final int loopCount)
   {

      test(EPS_0, loopCount);
      test(EPS_1, loopCount);
      test(EPS_2, loopCount);
      test(EPS_3, loopCount);
   }

   private void test(final Robust epsilon, final int loopCount)
   {

      for (final Algorithm algorithm : ALGORITHMS)
      {
         Algebraic.setAlgorithm(algorithm);
         Robusts.clearCache();
         {
            format("%s: (epsilon=%s)\n", Algebraic.getAlgorithm(), epsilon);
            final long timeNs = testEPU(epsilon, loopCount);
            format("  total:  %8.2fms\n", 1E-6 * timeNs);
         }
      }
   }

   private long testEPU(final Robust epsilon, final int loopCount)
   {

      PDCStats.INSTANCE.set(new PDCStats());
      final long timeNs = testRun(epsilon, loopCount);
      format("  approx: %s\n", PDCStats.INSTANCE.get());
      return timeNs;
   }

   private long testRun(final Robust epsilon, final int loopCount)
   {

      long timeNs = 0L;
      for (int k = 0; k < loopCount; k++)
      {
         final Robust[] E = createNewLeftAndRightExpressions();
         final long now = System.nanoTime();
         final Robust E1 = E[0].add(epsilon);
         final Robust E2 = E[1];
         final int cmp = E1.compareTo(E2);
         timeNs += System.nanoTime() - now;
         if (cmp == 0 != (epsilon == ZERO))
         {
            final Algebraic a1 = (Algebraic) E1.toSyntaxTree();
            final Algebraic a2 = (Algebraic) E2.toSyntaxTree();
            a1.compareTo(a2);
            fail();
         }
      }
      return timeNs;
   }

   protected abstract Robust[] createNewLeftAndRightExpressions();

   protected abstract int getLoopCount();

   private static final Algorithm[] ALGORITHMS = { BFMSS2, ZVAA, YAXANA };

   private static final Robust EPS_0 = ZERO;
   private static final Robust EPS_1 = Robust.valueOf("1p-10");
   private static final Robust EPS_2 = Robust.valueOf("1p-100");
   private static final Robust EPS_3 = Robust.valueOf("1p-1000");
}

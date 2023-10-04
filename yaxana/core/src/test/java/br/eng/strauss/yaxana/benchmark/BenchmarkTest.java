package br.eng.strauss.yaxana.benchmark;

import static br.eng.strauss.yaxana.Robust.ZERO;
import static org.junit.jupiter.api.Assertions.fail;

import br.eng.strauss.yaxana.Algorithm;
import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.EPU;
import br.eng.strauss.yaxana.epu.EPUStats;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

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
         test(4);
      }
      finally
      {
         YaxanaTest.quiet = quiet;
      }
      format("done\n");
   }

   private void test(final int loopCount)
   {

      for (final Robust epsilon : createEpsilons())
      {
         test(epsilon, loopCount);
      }
   }

   private void test(final Robust epsilon, final int loopCount)
   {

      for (final Algorithm algorithm : Algorithm.getValuesForTest())
      {
         Algebraic.setAlgorithm(algorithm);
         Robusts.clearCache();
         {
            format("%-6s: (eps=%-7s) - ", Algebraic.getAlgorithm(), epsilon);
            testEPU(epsilon, loopCount);
         }
      }
   }

   private long testEPU(final Robust epsilon, final int loopCount)
   {

      EPUStats.getInstance().clear();
      final long timeNs = testRun(epsilon, loopCount);
      format("%s\n", EPUStats.getInstance());
      return timeNs;
   }

   private long testRun(final Robust epsilon, final int loopCount)
   {

      long timeNs = 0L;
      for (int k = 0; k < loopCount; k++)
      {
         final Robust[] E = createNewLeftAndRightExpressions(epsilon);
         final Robust E1 = E[0];
         final Robust E2 = E[1];
         final long now = System.nanoTime();
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

   protected Robust[] createEpsilons()
   {

      // @formatter:off
      return new Robust[] 
      { 
        ZERO, 
        Robust.valueOf("1p-10"), 
        Robust.valueOf("1p-100"),
        Robust.valueOf("1p-1000") 
      };
      // @formatter:on
   }

   protected Robust[] createNewLeftAndRightExpressions(final Robust epsilon)
   {

      final Robust[] E = createNewLeftAndRightExpressions();
      E[0] = E[0].add(epsilon);
      return E;
   }

   protected Robust[] createNewLeftAndRightExpressions()
   {

      return new Robust[0];
   }

   protected abstract int getLoopCount();
}

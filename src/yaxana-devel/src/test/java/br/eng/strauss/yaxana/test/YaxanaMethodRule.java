package br.eng.strauss.yaxana.test;

import static br.eng.strauss.yaxana.test.YaxanaDevelTest.SKIP_BENCHMARKS;

import org.junit.AssumptionViolatedException;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author Burkhard Strauss
 * @since May 2022
 */
public final class YaxanaMethodRule implements MethodRule
{

   @Override
   public Statement apply(final Statement base, final FrameworkMethod method, final Object target)
   {

      return new Statement()
      {
         @Override
         public void evaluate() throws Throwable
         {

            final String name = method.getMethod().getDeclaringClass().getName() + "."
                  + method.getName();
            if (SKIP_BENCHMARKS && method.getAnnotation(Benchmark.class) != null)
            {
               YaxanaDevelTest.format("method %s skipped\n", name);
               throw new AssumptionViolatedException("skipped (SKIP_BENCHMARKS is set)");
            }
            else
            {
               final long time = System.currentTimeMillis();
               try
               {
                  base.evaluate();
                  final float ms = 0.001f * (System.currentTimeMillis() - time);
                  YaxanaDevelTest.format("method %s took: %.3fs\n", name, ms);
               }
               catch (final Throwable t)
               {
                  final float ms = 0.001f * (System.currentTimeMillis() - time);
                  YaxanaDevelTest.format("method %s failed in: %.3fs\n", name, ms);
                  throw t;
               }
            }
         }
      };
   }
}

package br.eng.strauss.yaxana.tools;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.junit.AssumptionViolatedException;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.anno.Benchmark;
import br.eng.strauss.yaxana.anno.WithAllEPUs;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.EPU;
import br.eng.strauss.yaxana.epu.robo.RootBoundEPU;

/**
 * @author Burkhard Strauss
 * @since May 2022
 */
public final class YaxanaMethodRule implements MethodRule
{

   @Override
   public Statement apply(final Statement base, final FrameworkMethod frameworkMethod,
         final Object target)
   {

      return new Statement()
      {

         @Override
         public void evaluate() throws Throwable
         {

            final Method method = frameworkMethod.getMethod();
            final Class<?> clasz = method.getDeclaringClass();
            final String name = clasz.getName() + "." + method.getName();
            if (method.getAnnotation(Benchmark.class) != null
                  && method.getAnnotation(Benchmark.class).value() == 0)
            {
               YaxanaTest.format("method %s skipped\n", name);
               throw new AssumptionViolatedException("skipped (SKIP_BENCHMARKS is set)");
            }
            else if (clasz.getAnnotation(WithAllEPUs.class) != null
                  || method.getAnnotation(WithAllEPUs.class) != null)
            {
               for (final Supplier<EPU> epu : YaxanaTest.getEPUsToTest())
               {
                  Algebraic.setEPU(epu);
                  evaluate(name);
               }
            }
            else
            {
               Algebraic.setEPU(() -> new RootBoundEPU());
               evaluate(name);
            }
         }

         private void evaluate(final String name) throws Throwable
         {

            final String epuName = String.format(" [%s]", Algebraic.getEPUClassName());
            final long time = System.currentTimeMillis();
            try
            {
               base.evaluate();
               final float ms = 0.001f * (System.currentTimeMillis() - time);
               YaxanaTest.format("method %s took: %.3fs%s\n", name, ms, epuName);
            }
            catch (final Throwable t)
            {
               final float ms = 0.001f * (System.currentTimeMillis() - time);
               YaxanaTest.format("method %s failed in: %.3fs%s\n", name, ms, epuName);
               throw t;
            }
         }
      };
   }
}

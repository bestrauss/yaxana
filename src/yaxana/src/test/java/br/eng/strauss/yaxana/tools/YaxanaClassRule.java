package br.eng.strauss.yaxana.tools;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import br.eng.strauss.yaxana.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since May 2022
 */
public final class YaxanaClassRule implements TestRule
{

   @Override
   public Statement apply(final Statement base, final Description description)
   {

      return new Statement()
      {
         @Override
         public void evaluate() throws Throwable
         {

            final String methodName = description.getMethodName();
            assert methodName == null;

            final String name = description.getClassName();
            if (false)
            {
               YaxanaTest.format("class %s skipped\n", name);
               throw new AssumptionViolatedException("class skipped");
            }
            final long time = System.currentTimeMillis();
            base.evaluate();
            final float ms = 0.001f * (System.currentTimeMillis() - time);
            YaxanaTest.format("class %s took: %.3fs\n", name, ms);
         }
      };
   }

   static
   {
      ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
   }
}

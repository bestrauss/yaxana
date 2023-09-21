package br.eng.strauss.yaxana;

import static br.eng.strauss.yaxana.Algorithm.BFMSS2;
import static br.eng.strauss.yaxana.YaxanaSettings.SKIP_BENCHMARKS;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import br.eng.strauss.yaxana.anno.Benchmark;
import br.eng.strauss.yaxana.anno.WithAlgorithms;
import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class YaxanaExtension implements AfterAllCallback, BeforeAllCallback,
      AfterEachCallback, BeforeEachCallback, InvocationInterceptor
{

   @Override
   public void beforeAll(@SuppressWarnings("exports") final ExtensionContext context)
         throws Exception
   {

      YaxanaSettings.readPrefs();
      ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

      final Optional<Class<?>> clasz = context.getTestClass();
      if (clasz.isEmpty())
      {
         throw new IllegalStateException("no class given");
      }
      this.currentClassName = clasz.get().getName();
      this.startTime = System.currentTimeMillis();
   }

   @Override
   public void afterAll(@SuppressWarnings("exports") final ExtensionContext context)
         throws Exception
   {

      final float ms = 0.001f * (System.currentTimeMillis() - this.startTime);
      YaxanaTest.format("class %s took: %.3fs\n", this.currentClassName, ms);
   }

   @Override
   public void beforeEach(@SuppressWarnings("exports") final ExtensionContext context)
         throws Exception
   {

      Robusts.clearCache();
      maximumCacheSize = Robusts.getMaximumCacheSize();
   }

   @Override
   public void afterEach(@SuppressWarnings("exports") final ExtensionContext context)
         throws Exception
   {

      Robusts.clearCache();
      Robusts.setMaximumCacheSize(maximumCacheSize);
   }

   @Override
   public void interceptTestMethod(final Invocation<Void> invocation,
         final ReflectiveInvocationContext<Method> invocationContext,
         final @SuppressWarnings("exports") ExtensionContext extensionContext) throws Throwable
   {

      final Method method = invocationContext.getExecutable();
      final Class<?> clasz = method.getDeclaringClass();
      final String name = clasz.getName() + "." + method.getName();
      if (method.getAnnotation(Benchmark.class) != null
            && (SKIP_BENCHMARKS || method.getAnnotation(Benchmark.class).value() == 0))
      {
         YaxanaTest.format("method %s skipped\n", name);
         invocation.skip();
      }
      else if (method.getAnnotation(WithAlgorithms.class) != null)
      {
         evaluate(method.getAnnotation(WithAlgorithms.class).value(), invocationContext, invocation,
                  name);
      }
      else if (clasz.getAnnotation(WithAlgorithms.class) != null)
      {
         evaluate(clasz.getAnnotation(WithAlgorithms.class).value(), invocationContext, invocation,
                  name);
      }
      else
      {
         Algebraic.setAlgorithm(BFMSS2);
         evaluate(invocation, name);
      }
   }

   private void evaluate(final Algorithm[] algorithms,
         final ReflectiveInvocationContext<Method> invocationContext,
         final Invocation<Void> invocation, final String name) throws Throwable
   {

      // can't use an invocation twice, and neither zero times
      @SuppressWarnings("unchecked")
      final Invocation<Void> followUpInvocation = invocationContext instanceof Invocation
            ? (Invocation<Void>) invocationContext
            : invocation;
      boolean followUp = false;
      for (final Algorithm algorithm : algorithms)
      {
         Algebraic.setAlgorithm(algorithm);
         final Invocation<Void> i = followUp ? followUpInvocation : invocation;
         evaluate(i, name);
         followUp = true;
      }
   }

   private void evaluate(final Invocation<Void> invocation, final String name) throws Throwable
   {

      final String epuName = String.format(" [%s]", Algebraic.getAlgorithm());
      final long time = System.currentTimeMillis();
      try
      {
         invocation.proceed();
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

   private String currentClassName;

   private long startTime;

   private int maximumCacheSize;
}

package br.eng.strauss.yaxana;

import static java.util.Locale.US;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.EPU;
import br.eng.strauss.yaxana.epu.robo.RootBoundEPU;
import br.eng.strauss.yaxana.epu.yaxa.YaxanaEPU;
import br.eng.strauss.yaxana.epu.zvaa.ZvaaEPU;
import br.eng.strauss.yaxana.tools.YaxanaClassRule;
import br.eng.strauss.yaxana.tools.YaxanaMethodRule;

/**
 * Base class for all tests, benchmarks, and experiments.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
@RunWith(YaxanaRunner.class)
public abstract class YaxanaTest
{

   @SuppressWarnings("exports")
   @Rule
   public YaxanaMethodRule methodRule = new YaxanaMethodRule();

   @SuppressWarnings("exports")
   @ClassRule
   public static YaxanaClassRule classRule = new YaxanaClassRule();

   public static void format(final String format, final Object... args)
   {

      if (!quiet)
      {
         System.out.format(US, format, args);
      }
   }

   @SuppressWarnings("exports")
   public static Supplier<EPU>[] getEPUsToTest()
   {

      final List<Supplier<EPU>> list = new ArrayList<>();
      list.add(() -> new RootBoundEPU());
      list.add(() -> new ZvaaEPU());
      list.add(() -> new YaxanaEPU());
      // list.add(() -> new DoubleConjugatesEPU());
      @SuppressWarnings("unchecked")
      final Supplier<EPU>[] array = (Supplier<EPU>[]) Array.newInstance(Supplier.class,
                                                                        list.size());
      return list.toArray(array);
   }

   protected static void withAllEPUs(final Runnable runnable)
   {

      for (final Supplier<EPU> epu : getEPUsToTest())
      {
         Algebraic.setEPU(epu);
         runnable.run();
      }
   }

   protected static void rootBoundEPU()
   {

      Algebraic.setEPU(() -> new RootBoundEPU());
   }

   protected static void yaxanaEPU()
   {

      Algebraic.setEPU(() -> new YaxanaEPU());
   }

   @Before
   public void beforeYaxanaTest()
   {

      maximumCacheSize = Robusts.getMaximumCacheSize();
   }

   @After
   public void afterYaxanaTest()
   {

      Robusts.clearCache();
      Robusts.setMaximumCacheSize(maximumCacheSize);
   }

   private static int maximumCacheSize;

   public static boolean quiet = false;

   public static final int STRESS_LEVEL = 0;
}

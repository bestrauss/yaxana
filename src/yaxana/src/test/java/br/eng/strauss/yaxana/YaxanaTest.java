package br.eng.strauss.yaxana;

import static java.util.Locale.US;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

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

   @Before
   public void beforeYaxanaTest()
   {

      Robusts.clearCache();
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

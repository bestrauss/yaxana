package br.eng.strauss.yaxana;

import static java.util.Locale.US;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Base class for all tests, benchmarks, and experiments.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
@ExtendWith(YaxanaExtension.class)
public abstract class YaxanaTest
{

   public static void format(final String format, final Object... args)
   {

      if (!quiet)
      {
         System.out.format(US, format, args);
      }
   }

   public static boolean quiet = false;
}

package br.eng.strauss.yaxana.unittesttools;

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

   /**
    * Print a formatted string to the console.
    * 
    * @param format
    *           like in {@code String.format()}.
    * @param args
    *           like in {@code String.format()}.
    */
   public static void format(final String format, final Object... args)
   {

      if (!quiet)
      {
         System.out.format(US, format, args);
      }
   }

   /** Used by some tests to stop littering the console output. */
   public static boolean quiet = false;
}

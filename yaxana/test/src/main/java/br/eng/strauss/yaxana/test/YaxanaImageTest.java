package br.eng.strauss.yaxana.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Base class for tests.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public abstract class YaxanaImageTest
{

   public static void format(final String format, final Object... args)
   {

      System.out.format(Locale.US, format, args);
   }

   protected String getTestData(final String shortFileName)
         throws IllegalStateException, IOException
   {

      final StringBuffer sb = new StringBuffer();
      final Class<?> clasz = getClass();
      final String packageName = clasz.getPackage().getName().replace('.', '/');
      final String fileName = packageName + "/" + shortFileName;
      try (InputStream stream = clasz.getClassLoader().getResourceAsStream(fileName))
      {
         while (true)
         {
            final int c = stream.read();
            if (c < 0)
            {
               break;
            }
            sb.append((char) c);
         }
      }
      return sb.toString();
   }

   public static final int STRESS_LEVEL = 0;
   public static final boolean SKIP_BENCHMARKS = true;
}

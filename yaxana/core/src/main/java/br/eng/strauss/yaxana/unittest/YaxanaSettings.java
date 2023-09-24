package br.eng.strauss.yaxana.unittest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class YaxanaSettings
{

   /**
    * Read the developers preferences.
    */
   public static void readPrefs()
   {

      final File file = new File(".settings", "br.eng.strauss.yaxana.user.prefs");
      final String skipBenchmarksKey = "br.eng.strauss.yaxana.tests.skipBenchmarks";
      final String stressLevelKey = "br.eng.strauss.yaxana.tests.stressLevel";
      final Properties properties = new Properties();
      try (InputStream stream = new FileInputStream(file))
      {
         properties.load(stream);
         SKIP_BENCHMARKS = "true".equals(properties.get(skipBenchmarksKey));
         STRESS_LEVEL = Integer.valueOf(properties.get(stressLevelKey).toString());
         System.out.format("Prefs have been read from file %s\n", file);
      }
      catch (final Exception ignored)
      {
         properties.clear();
         properties.put(skipBenchmarksKey, String.valueOf(SKIP_BENCHMARKS));
         properties.put(stressLevelKey, String.valueOf(STRESS_LEVEL));
         try (OutputStream stream = new FileOutputStream(file))
         {
            file.getParentFile().mkdirs();
            properties.store(stream, null);
            System.out.format("Default prefs have been created. See file %s\n", file);
         }
         catch (final Exception e)
         {
            throw new IllegalStateException(
                  String.format("Failed to create default prefs. File %s\n", file), e);
         }
      }
      System.out.format("SKIP_BENCHMARKS = %s\n", SKIP_BENCHMARKS);
      System.out.format("STRESS_LEVEL    = %s\n", STRESS_LEVEL);
   }

   private YaxanaSettings()
   {
   }

   /** Set to true to skip tedious benchmarks. */
   public static boolean SKIP_BENCHMARKS = false;
   /** Set higher to increase test intensity. */
   public static int STRESS_LEVEL = 0;
}

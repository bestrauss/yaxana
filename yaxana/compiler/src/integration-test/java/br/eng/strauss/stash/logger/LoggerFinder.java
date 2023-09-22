package br.eng.strauss.stash.logger;

import static java.util.Locale.US;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.System.Logger;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Burkhard Strauss
 * @since 2020-11
 */
public final class LoggerFinder extends System.LoggerFinder
{

   @Override
   public Logger getLogger(final String name, final Module module)
   {

      return new DefaultLogger(name);
   }

   private static class DefaultLogger implements Logger
   {

      public DefaultLogger(final String name)
      {

         this.name = name;
      }

      @Override
      public String getName()
      {

         return name;
      }

      @Override
      public boolean isLoggable(final Level level)
      {

         if (this.name.startsWith("br.eng"))
         {
            return level.getSeverity() >= Level.DEBUG.getSeverity();
         }
         return level.getSeverity() >= Level.INFO.getSeverity();
      }

      @Override
      public void log(final Level level, final ResourceBundle bundle, final String msg,
            final Throwable thrown)
      {

         if (isLoggable(level))
         {
            if (thrown instanceof javax.net.ssl.SSLException)
            {
               return;
            }
            try (final StringWriter sw = new StringWriter();
                  final PrintWriter writer = new PrintWriter(sw, true))
            {
               thrown.printStackTrace(writer);
               log(level, bundle, "%s", sw.toString());
            }
            catch (final IOException e)
            {
               throw new IllegalStateException("unreached", e);
            }
         }
      }

      @Override
      public void log(final Level level, final ResourceBundle bundle, final String format,
            final Object... args)
      {

         if (isLoggable(level))
         {
            final StackTraceElement ste = new Exception().getStackTrace()[3];
            final String location = abbreviate(name) + ":" + ste.getLineNumber();
            @SuppressWarnings("resource")
            final PrintStream ps = level.ordinal() >= System.Logger.Level.WARNING.ordinal()
                  ? System.err
                  : System.out;
            ps.format("%s\n", getLoggingLineFor(level, location, format, args));
         }
      }

      private String getLoggingLineFor(final Level level, final String location,
            final String format, final Object... args)
      {

         try (Formatter f = new Formatter(US))
         {

            f.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL", new Date());
            f.format(" %5s", LEVELS.get(level));
            f.format(" %s", location);
            f.format(" - ");
            if (args != null)
            {
               f.format(format, args);
            }
            else
            {
               f.format("%s", format);
            }
            return f.toString();
         }
      }

      private final String name;
   }

   private static String abbreviate(final String className)
   {

      String abbreviation = ABBREVIATIONS.get(className);
      if (abbreviation == null)
      {
         final StringBuilder sb = new StringBuilder();
         boolean ignore = false;
         final int kLast = className.lastIndexOf('.');
         for (int k = 0, count = className.length(); k < count; k++)
         {
            final char c = className.charAt(k);
            if (k >= kLast)
            {
               sb.append(c);
            }
            else
            {
               if (Character.isAlphabetic(c))
               {
                  if (!ignore)
                  {
                     sb.append(c);
                     ignore = true;
                  }
               }
               else
               {
                  sb.append(c);
                  ignore = false;
               }
            }
         }
         ABBREVIATIONS.put(className, abbreviation = sb.toString());
      }
      return abbreviation;
   }

   private static final Map<String, String> ABBREVIATIONS = new HashMap<>();

   private static final Map<System.Logger.Level, String> LEVELS = new HashMap<>();
   static
   {
      LEVELS.put(System.Logger.Level.INFO, " INFO");
      LEVELS.put(System.Logger.Level.DEBUG, "DEBUG");
      LEVELS.put(System.Logger.Level.TRACE, "TRACE");
      LEVELS.put(System.Logger.Level.WARNING, " WARN");
      LEVELS.put(System.Logger.Level.ERROR, "ERROR");
   }
}

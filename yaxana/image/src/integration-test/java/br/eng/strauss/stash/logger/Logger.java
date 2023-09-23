package br.eng.strauss.stash.logger;

import java.lang.StackWalker.Option;
import java.util.Locale;

/**
 * The logger used by this module.
 * <p>
 * Output is forwarded to the {@link System.Logger}.
 *
 * @author Burkhard Strauss
 * @since 2020-11
 */
public final class Logger
{

   public static Logger getInstance()
   {

      final StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
      final String name = walker.getCallerClass().getName();
      return new Logger(System.getLogger(name));
   }

   private Logger(final java.lang.System.Logger logger)
   {

      this.logger = logger;
   }

   public void info(final String format, final Object... args)
   {

      this.logger.log(System.Logger.Level.INFO, String.format(Locale.US, format, args));
   }

   public void info(final Throwable e)
   {

      this.logger.log(System.Logger.Level.INFO, "", e);
   }

   public void debug(final String format, final Object... args)
   {

      this.logger.log(System.Logger.Level.DEBUG, String.format(Locale.US, format, args));
   }

   public void trace(final String format, final Object... args)
   {

      this.logger.log(System.Logger.Level.TRACE, String.format(Locale.US, format, args));
   }

   public void warn(final String format, final Object... args)
   {

      this.logger.log(System.Logger.Level.WARNING, String.format(Locale.US, format, args));
   }

   public void warn(final Throwable e)
   {

      this.logger.log(System.Logger.Level.WARNING, "", e);
   }

   public void error(final String format, final Object... args)
   {

      this.logger.log(System.Logger.Level.ERROR, String.format(Locale.US, format, args));
   }

   public void error(final Throwable e)
   {

      this.logger.log(System.Logger.Level.ERROR, "", e);
   }

   private final java.lang.System.Logger logger;
}

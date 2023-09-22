package br.eng.strauss.yaxana.exc;

/**
 * @author Burkhard Strauss
 * @since 2020-10
 */
public final class UnreachedException extends IllegalStateException
{

   /**
    * Returns a new instance.
    */
   public UnreachedException()
   {

      this(null, null);
   }

   /**
    * Returns a new instance.
    * 
    * @param message
    *           the message.
    */
   public UnreachedException(final String message)
   {

      this(message, null);
   }

   /**
    * Returns a new instance.
    *
    * @param cause
    *           the cause.
    */
   public UnreachedException(final Throwable cause)
   {

      this(cause != null ? cause.toString() : null, cause);
   }

   /**
    * Returns a new instance.
    *
    * @param message
    *           the message.
    * @param cause
    *           the cause.
    */
   public UnreachedException(final String message, final Throwable cause)
   {

      super(message != null ? MESSAGE + "\n" + message : MESSAGE, cause);
   }

   private static final long serialVersionUID = 1L;

   private static final String MESSAGE = "Program flow has reached a point classified as unreachable.";
}

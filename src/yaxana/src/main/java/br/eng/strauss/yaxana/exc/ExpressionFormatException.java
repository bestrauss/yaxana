package br.eng.strauss.yaxana.exc;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class ExpressionFormatException extends NumberFormatException
{

   /**
    * Returns a new instance.
    *
    * @param message
    *           the message.
    */
   public ExpressionFormatException(final String message)
   {

      this(message, null);
   }

   /**
    * Returns a new instance.
    *
    * @param message
    *           the message.
    * @param cause
    *           the cause.
    */
   public ExpressionFormatException(final String message, final Throwable cause)
   {

      super(message);
      super.initCause(cause);
   }

   private static final long serialVersionUID = 1L;
}

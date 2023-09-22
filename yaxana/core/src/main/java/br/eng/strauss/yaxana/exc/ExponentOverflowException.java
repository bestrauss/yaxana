package br.eng.strauss.yaxana.exc;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class ExponentOverflowException extends ArithmeticException
{

   /**
    * Returns a new instance.
    */
   public ExponentOverflowException()
   {

      super("exponent overflow");
   }

   private static final long serialVersionUID = 1L;
}

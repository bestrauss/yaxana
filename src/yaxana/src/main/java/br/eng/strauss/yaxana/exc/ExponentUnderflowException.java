package br.eng.strauss.yaxana.exc;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class ExponentUnderflowException extends ArithmeticException
{

   /**
    * Returns a new instance.
    */
   public ExponentUnderflowException()
   {

      super("exponent underflow");
   }

   private static final long serialVersionUID = 1L;
}

package br.eng.strauss.yaxana.exc;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class NonPositiveLogarithmArgumentException extends ArithmeticException
{

   /**
    * Returns a new instance.
    */
   public NonPositiveLogarithmArgumentException()
   {

      super("log(x) is not defined for x <= 0");
   }

   private static final long serialVersionUID = 1L;
}

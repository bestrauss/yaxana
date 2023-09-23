package br.eng.strauss.yaxana.exc;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class DivisionByZeroException extends ArithmeticException
{

   /**
    * Returns a new instance.
    */
   public DivisionByZeroException()
   {

      super("division by zero");
   }

   private static final long serialVersionUID = 1L;
}

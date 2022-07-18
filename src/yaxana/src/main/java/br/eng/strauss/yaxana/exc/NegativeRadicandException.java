package br.eng.strauss.yaxana.exc;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class NegativeRadicandException extends ArithmeticException
{

   /**
    * Returns a new instance.
    */
   public NegativeRadicandException()
   {

      super("n-th root(x) of x < 0 undefined for n even");
   }

   private static final long serialVersionUID = 1L;
}

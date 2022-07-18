package br.eng.strauss.yaxana.exc;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class NonTerminatingBinaryExpansionException extends ArithmeticException
{

   /**
    * Returns a new instance.
    */
   public NonTerminatingBinaryExpansionException()
   {

      super("division results in non-terminating binary expansion");
   }

   private static final long serialVersionUID = 1L;
}

package br.eng.strauss.yaxana.exc;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class RoundingNecessaryException extends ArithmeticException
{

   /**
    * Returns a new instance.
    */
   public RoundingNecessaryException()
   {

      super("rounding necessary");
   }

   private static final long serialVersionUID = 1L;
}

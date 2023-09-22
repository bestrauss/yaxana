package br.eng.strauss.yaxana.exc;

import static br.eng.strauss.yaxana.Robust.MAX_EXPONENT;
import static java.lang.String.format;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class IllegalExponentException extends ArithmeticException
{

   /**
    * Returns a new instance.
    *
    * @param n
    *           the exponent.
    */
   public IllegalExponentException(final int n)
   {

      super(format("illegal exponent (%d <= %d <= %d is false)", -MAX_EXPONENT, n, MAX_EXPONENT));
   }

   private static final long serialVersionUID = 1L;
}

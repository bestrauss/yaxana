package br.eng.strauss.yaxana.exc;

import static java.lang.String.format;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class PrecisionOverflowException extends ArithmeticException
{

   /**
    * Returns a new instance.
    *
    * @param expression
    *           the concerned expression.
    */
   public PrecisionOverflowException(final String expression)
   {

      super(format("maximum precision (%d) exceeded. `%s'", MAX_PRECISION, expression));
   }

   private static final long serialVersionUID = 1L;

   /** The max supported precision. */
   public static final long MAX_PRECISION = 0x4000_0000;
}

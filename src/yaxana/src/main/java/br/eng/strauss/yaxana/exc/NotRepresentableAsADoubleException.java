package br.eng.strauss.yaxana.exc;

import static java.lang.String.format;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public final class NotRepresentableAsADoubleException extends ArithmeticException
{

   /**
    * Returns a new instance.
    *
    * @param terminal
    *           the terminal that can't be represented as a double.
    */
   public NotRepresentableAsADoubleException(final String terminal)
   {

      super(format("terminal `%s' cannot be represented as a double", terminal));
   }

   private static final long serialVersionUID = 1L;
}

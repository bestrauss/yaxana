package br.eng.strauss.yaxana.epu;

import java.util.function.Consumer;

/**
 * Exact Processing Unit.
 * <p>
 * Interface to be implemented by algorithms which compute the signs of the exact values of
 * {@link Algebraic}s.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public abstract interface EPU
{

   /**
    * Sets the approximation/precision of a given operand such that the sign can be read from the
    * approximation, and returns {@code -1, 0, 1} in case the exact value of the operand is
    * negative, zero or positive.
    * 
    * @param operand
    *           the operand.
    * @return {@code -1, 0, 1} in case the exact value of the given operand is negative, zero or
    *         positive.
    */
   public default int signum(final Algebraic operand)
   {

      return signum(operand, null);
   }

   /**
    * Sets the approximation/precision of a given operand such that the sign can be read from the
    * approximation, and returns {@code -1, 0, 1} in case the exact value of the operand is
    * negative, zero or positive.
    * 
    * @param operand
    *           the operand.
    * @param sufficientPrecision
    *           if present, will be called with the precision deemed to be sufficient to safely read
    *           the sign from the approximated value.
    * @return {@code -1, 0, 1} in case the exact value of the given operand is negative, zero or
    *         positive.
    */
   public abstract int signum(Algebraic operand, Consumer<Integer> sufficientPrecision);
}

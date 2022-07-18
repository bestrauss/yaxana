package br.eng.strauss.yaxana.epu;

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
    * approximation/precision, and returns {@code -1, 0, 1} in case the exact value of the operand
    * is negative, zero or positive.
    * 
    * @param operand
    *           the operand.
    * @return {@code -1, 0, 1} in case the exact value of the given operand is negative, zero or
    *         positive.
    */
   public abstract int signum(Algebraic operand);
}

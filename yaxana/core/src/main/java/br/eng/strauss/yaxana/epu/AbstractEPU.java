package br.eng.strauss.yaxana.epu;

import br.eng.strauss.yaxana.Type;

/**
 * Base class for EPU implementations.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public abstract class AbstractEPU implements EPU
{

   /** The operand. */
   protected Algebraic operand;

   /** The signum or {@code null} if not yet calculated. */
   protected Integer signum;

   @Override
   public final int signum(final Algebraic operand)
   {

      if (this.operand != operand)
      {
         if (operand.type() == Type.ADD)
         {
            this.operand = operand.left().sub(operand.right().neg());
         }
         else if (operand.type() != Type.SUB)
         {
            throw new UnsupportedOperationException(
                  "the operand must be a difference of two expressions");
         }
         else
         {
            this.operand = operand;
         }
         this.signum = null;
      }
      if (this.signum == null)
      {
         this.signum = this.computeSignum();
      }
      if (this.operand != operand)
      {
         operand.setApproximation(this.operand.approximation(), this.operand.precision());
      }
      return this.signum;
   }

   /**
    * Computes and returns the signum of the exact value of the operand.
    * <p>
    * The operand is guaranteed to be of type {@code SUB}.
    * 
    * @return the signum of the exact value of the operand.
    */
   protected abstract int computeSignum();

   /**
    * Returns a string representation for debugging purposes.
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public final String toString()
   {

      return getClass().getSimpleName() + ":\n  "
            + (operand != null ? operand.toString() : "-no operand-");
   }
}

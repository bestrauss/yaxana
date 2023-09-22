package br.eng.strauss.yaxana.epu;

import br.eng.strauss.yaxana.Type;
import br.eng.strauss.yaxana.epu.zvaa.ZvaaEPU;

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
         this.operand = operand;
         this.signum = null;
      }
      if (this.signum == null)
      {
         if (operand.type() == Type.ADD)
         {
            final Algebraic difference = operand.left().sub(operand.right().neg());
            final ZvaaEPU epu = new ZvaaEPU();
            final int signum = epu.signum(difference);
            this.operand.setApproximation(difference.approximation(), difference.signum());
            return signum;
         }
         this.signum = this.computeSignum();
      }
      return this.signum;
   }

   /**
    * Computes and returns the signum of the exact value of the operand.
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

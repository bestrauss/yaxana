package br.eng.strauss.yaxana.epu;

import java.util.function.Consumer;

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

   /** Stats or {@code null}. */
   protected Consumer<Integer> sufficientPrecision;

   /** The signum or {@code null} if not yet calculated. */
   protected Integer signum;

   @Override
   public final int signum(final Algebraic operand, final Consumer<Integer> sufficientPrecision)
   {

      this.sufficientPrecision = sufficientPrecision;
      if (this.operand != operand)
      {
         if (operand.type() == Type.ADD)
         {
            return signum(operand.left().sub(operand.right().neg()), sufficientPrecision);
         }
         if (operand.type() != Type.SUB)
         {
            throw new UnsupportedOperationException("only subtraction supported");
         }
         this.operand = operand;
         this.signum = null;
      }
      if (this.signum == null)
      {
         this.signum = this.computeSignum();
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

package br.eng.strauss.yaxana;

import static br.eng.strauss.yaxana.Type.TERMINAL;
import static java.lang.System.arraycopy;

import java.util.Arrays;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.Cache;

/**
 * Immutable expression with {@code double} terminals concisely stored in reverse polish notation.
 * 
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public abstract sealed class ConciseNumber extends Number permits Robust
{

   protected ConciseNumber(final short[] operations, final double[] operands, final int hashCode)
   {

      this.operations = operations;
      this.operands = operands;
      this.hashCode = hashCode;
   }

   /**
    * Returns the number of nodes in the abstract syntax tree.
    * 
    * @return the number of nodes in the abstract syntax tree.
    */
   public final int noOfNodes()
   {

      return this.operations.length;
   }

   /**
    * Returns the number of non-terminal nodes in the abstract syntax tree.
    * 
    * @return the number of non-terminal nodes in the abstract syntax tree.
    */
   public final int noOfOperations()
   {

      return this.operations.length - this.operands.length;
   }

   /**
    * Returns the number of terminal nodes in the abstract syntax tree.
    * 
    * @return the number of terminal nodes in the abstract syntax tree.
    */
   public final int noOfOperands()
   {

      return this.operands.length;
   }

   /**
    * Returns whether the {@code other} is the same expression, consisting of the same operands and
    * operations, having the same abstract syntax tree as {@code this}.
    * <p>
    * To compare numeric values, use {@link Robust#isEqualTo(Robust)},
    * {@link Robust#isGreaterThan(Robust)}, etc.
    */
   @Override
   public final boolean equals(final Object object)
   {

      if (object != this)
      {
         if (object instanceof final ConciseNumber that)
         {
            if (this.hashCode == that.hashCode && Arrays.equals(this.operations, that.operations)
                  && Arrays.equals(this.operands, that.operands))
            {
               return true;
            }
         }
         return false;
      }
      return true;
   }

   @Override
   public final int hashCode()
   {

      return this.hashCode;
   }

   protected static Robust staticValueOf(final double value)
   {

      final Robust robust = Robust.valueOf(value);
      Cache.getInstance().clear();
      return robust;
   }

   protected static Robust valueOf(final double value, final boolean staticInit)
   {

      final int hashCode = 31 * (31 + TERMINAL_OPERATIONS_HASHCODE) + Double.hashCode(value);
      final Robust robust = valueOf(TERMINAL_OPERATIONS, new double[] { value }, hashCode, value,
                                    value, value, value == 0d, !staticInit);
      if (staticInit)
      {
         Cache.getInstance().clear();
      }
      else
      {

      }
      return robust;

   }

   protected static Robust valueOf(final short[] operations, final double[] operands,
         final int hashCode, final double value, final double lo, final double hi,
         final boolean mayBeZero, final boolean simplify)
   {

      final Cache cache = Cache.getInstance();
      final Robust key = new Robust(operations, operands, hashCode, 0d, 0d, 0d, false);
      Robust robust = cache.get(key);
      if (robust == null)
      {
         cache.put(robust = new Robust(operations, operands, hashCode, value, lo, hi, mayBeZero));
      }
      return simplify ? robust.simplified() : robust;
   }

   protected Robust newUnary(final Type type, final short exponent, final double value,
         final double lo, final double hi)
   {

      final int k = this.operations.length;
      final short[] operations = Arrays.copyOf(this.operations, k + 1);
      operations[k] = (short) (type.ordinal() + (exponent << 4));
      final int hashCode = 31 * this.hashCode() + operations[k];
      return Robust.valueOf(operations, this.operands.clone(), hashCode, value, lo, hi, false,
                            true);
   }

   protected Robust newBinary(final Type type, final Robust that, final double value,
         final double lo, final double hi, final boolean mayBeZero)
   {

      final int lenU = this.operations.length + that.operations.length;
      final int lenT = this.operands.length + that.operands.length;
      final short[] operations = Arrays.copyOf(this.operations, lenU + 1);
      final double[] operands = Arrays.copyOf(this.operands, lenT);
      arraycopy(that.operations, 0, operations, this.operations.length, that.operations.length);
      arraycopy(that.operands, 0, operands, this.operands.length, that.operands.length);
      operations[lenU] = (short) type.ordinal();
      final int hashCode = 31 * (31 * this.hashCode() + that.hashCode()) + type.ordinal();
      return Robust.valueOf(operations, operands, hashCode, value, lo, hi, mayBeZero, true);
   }

   protected Algebraic toAlgebraic()
   {

      final CachedStack<Algebraic> stack = new CachedStack<>();
      for (int kOperation = 0, kOperand = 0; kOperation < this.operations.length; kOperation++)
      {
         final int op = this.operations[kOperation];
         final Type type = Type.values()[op & 0xF];
         switch (type)
         {
            // @formatter:off
            case TERMINAL -> { stack.push(new Algebraic(this.operands[kOperand++])); }
            case NEG  -> { stack.push(stack.pop().neg()); }
            case ABS  -> { stack.push(stack.pop().abs()); }
            case POW  -> { stack.push(stack.pop().pow (op >> 4)); }
            case ROOT -> { stack.push(stack.pop().root(op >> 4)); }
            case ADD  -> { final Algebraic b = stack.pop(); stack.push(stack.pop().add(b)); }
            case SUB  -> { final Algebraic b = stack.pop(); stack.push(stack.pop().sub(b)); }
            case MUL  -> { final Algebraic b = stack.pop(); stack.push(stack.pop().mul(b)); }
            case DIV  -> { final Algebraic b = stack.pop(); stack.push(stack.pop().div(b)); }
            // @formatter:on
         }
      }
      return stack.pop();
   }

   protected static double root(final double x, final int n)
   {

      if (n == 2)
      {
         return Math.sqrt(x);
      }
      if ((n & 1) == 1)
      {
         return x < 0d ? -Math.pow(-x, 1d / n) : Math.pow(x, 1d / n);
      }
      return Math.pow(Math.abs(x), 1d / n);
   }

   private static final long serialVersionUID = BigFloat.serialVersionUID;

   /** The maximum exponent for pow and root operations. */
   public static final int MAX_EXPONENT = 0x07FF;

   protected static final short[] TERMINAL_OPERATIONS = new short[] { (short) TERMINAL.ordinal() };

   protected static final int TERMINAL_OPERATIONS_HASHCODE = Arrays.hashCode(TERMINAL_OPERATIONS);

   /** The operations. */
   protected final short[] operations;

   /** The operands. */
   protected final double[] operands;

   /** The precalculated hash code. */
   private final int hashCode;
}

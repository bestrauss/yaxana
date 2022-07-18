package br.eng.strauss.yaxana;

import static br.eng.strauss.yaxana.Type.TERMINAL;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.nextDown;
import static java.lang.Math.nextUp;
import static java.lang.System.arraycopy;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Stack;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.epu.Cache;
import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.exc.IllegalExponentException;
import br.eng.strauss.yaxana.exc.NotRepresentableAsADoubleException;
import br.eng.strauss.yaxana.exc.UnreachedException;
import br.eng.strauss.yaxana.io.Parser;

/**
 * Immutable, thread safe, ideally exact real algebraic {@link Number} intended to be used for
 * robust geometric computation.
 * <p>
 * Instances of this class represent exact algebraic numbers as
 * <ul>
 * <li>two arrays, a {@code short[]} and a {@code double[]}, storing an abstract syntax tree of an
 * expression with {@code double}-operands and operators
 * {@code add, sub, mul, div, neg, abs, pow, root} in reverse polish notation,
 * <li>plus two {@code double}s bracketing the exact value of the expression,
 * <li>plus a {@code double} approximation of the exact value of the expression, whose sign is the
 * true sign of the exact value.
 * </ul>
 * Memory usage:
 * <ul>
 * <li>one {@code short} per node of the abstract syntax tree of the expression ({@code pow}- and
 * {@code root}- exponents don't need extra storage),
 * <li>plus one {@code double} per terminal node in the abstract syntax tree of the expression,
 * <li>plus three {@code double}s,
 * <li>plus two pointers to the arrays, plus class-pointer, plus memory allocation and alignment
 * overhead.
 * </ul>
 * Calculations are done
 * <ul>
 * <li>using {@code double} interval arithmetic where possible,
 * <li>using the constructive root bound method else, constructing the zero separation bound using a
 * new (A.D. 2022) enhanced method described in "Zum-Vorzeichentest-Algebraischer-Ausdrücke" (see
 * link below).
 * </ul>
 * <p>
 * Instances
 * <ul>
 * <li>are immutable,
 * <li>thread safe,
 * <li>{@link Comparable},
 * <li>{@link Serializable}.
 * <li>{@link #hashCode}- and {@link #equals}-methods are fast, based on the abstract syntax tree.
 * <li>{@link #toString()} produces an ASCII expression, e.g. {@code \2+\3-\(5+2*\6)}, with
 * operators {@code +}, {@code -}, {@code *}, {@code /}, {@code ^}, and {@code \}, functions
 * {@code pow(x, n)}, {@code sqrt(x)}, and {@code root(x, n)}, which is accepted by
 * {@link #valueOf(String)}.
 * <li>Alternatively use {@link #valueOf(double)} and then {@link #add(Robust)},
 * {@link #sub(Robust)}, etc.
 * <li>{@link #signum()}-, {@link #compareTo(C)}-, {@link #isGreaterOrEqual(C)}-, etc. methods
 * return robust decisions based on the exact value of the expression.
 * </ul>
 * <p>
 * {@link Robust} does not implement {@link SyntaxTree}, but has a method {@link #toSyntaxTree()}
 * <p>
 * Alle exceptions thrown are {@link RuntimeException}s. Particularly {@link ArithmeticException}s
 * and {@link NumberFormatException}s are thrown in the notorious situations.
 * <p>
 * Please take a look at the useful inherited methods.
 * 
 * @author Burkhard Strauss
 * @since 05-2022
 * @see <a href=
 *      "https://github.com/bestrauss/yaxana/docs/Zum-Vorzeichentest-Algebraischer-Ausdruecke.pdf">Zum-Vorzeichentest-Algebraischer-Ausdrücke</a>.
 */
public final class Robust extends Number implements Expression<Robust>
{

   /**
    * Returns a new {@link Robust} instance parsed from a {@link String} representation of an
    * expression like e.g. {@code \2+\3-\(5+2*\6)}.
    * <p>
    * An expression is
    * <ul>
    * <li>a terminal like e.g. {@code 21.4E-1} or {@code 0x1.8P-2},
    * <li>a binary operation of expressions {@code a+b}, {@code a-b}, {@code a*b}, {@code a/b},
    * <li>a unary operation of an expression {@code -a}, {@code |a|},
    * <li>a function call {@code sqrt(a)}, {@code root(a, n)}, {@code pow(a, n)},
    * <li>{@code \a} as an abbreviation for {@code sqrt(a)}.
    * <li>Parentheses {@code (...)} control precedence of operators.
    * </ul>
    * <p>
    * For lexical details on terminals see {@link Robusts#terminalPattern()}.
    * 
    * @param expression
    *           a {@link String} representation of an expression.
    * @return a new {@link Robust} instance.
    * @throws ArithmeticException
    *            in case of decimal terminal values which can't be represented as binary
    *            {@code double}s exactly. Note that this is most often the case. In case of division
    *            by zero and other illegal operations. In case of exponent under- or overflows. In
    *            case of other limitations of the implementation.
    * @throws NumberFormatException
    *            in case of syntax errors.
    * @see #toString()
    */
   public static Robust valueOf(final String expression)
   {

      if (BigFloat.terminalPattern().matcher(expression).matches())
      {
         final BigFloat bigFloat = new BigFloat(expression);
         final double doubleValue = bigFloat.doubleValue();
         if (new BigFloat(doubleValue).equals(bigFloat))
         {
            return valueOf(doubleValue);
         }
         throw new NotRepresentableAsADoubleException(expression);
      }
      else
      {
         return new Parser<Robust>(ZERO, expression).expression();
      }
   }

   /**
    * Returns a new {@link Robust} instance, which is a terminal with a given rational value.
    * 
    * @param value
    *           the rational value.
    * @return a new instance, which is a terminal with the given rational value.
    */
   public static Robust valueOf(final double value)
   {

      return value != 0d ? new Robust(value) : ZERO;
   }

   /**
    * Returns a new {@link Robust} instance with a given abstract syntax tree.
    * 
    * @param st
    *           the abstract syntax tree.
    * @return a new instance with a given abstract syntax tree.
    */
   public static Robust valueOf(final SyntaxTree<?> st)
   {

      switch (st.type())
      {
         // @formatter:off
         case TERMINAL -> { return valueOf(st.doubleValue()); }
         case NEG  -> { return valueOf(st.left()).neg(); }
         case ABS  -> { return valueOf(st.left()).abs(); }
         case POW  -> { return valueOf(st.left()).pow(st.index()); }
         case ROOT -> { return valueOf(st.left()).root(st.index()); }
         case ADD  -> { return valueOf(st.left()).add(valueOf(st.right())); }
         case SUB  -> { return valueOf(st.left()).sub(valueOf(st.right())); }
         case MUL  -> { return valueOf(st.left()).mul(valueOf(st.right())); }
         case DIV  -> { return valueOf(st.left()).div(valueOf(st.right())); }
         // @formatter:on
      }
      throw new UnreachedException();
   }

   /**
    * Returns a {@code hashCode} precalculated on construction conforming the general contract.
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {

      return this.hashCode;
   }

   /**
    * Returns whether a given other object is a {@link Robust} which has an abstract syntax tree
    * that is identical to the abstract syntax tree of {@code this}.
    * <p>
    * For comparison of the <u>values</u> of {@link Robust} instances use
    * {@link #compareTo(Robust)}, {@link #isGreaterOrEqual(S)}, etc.
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(final Object other)
   {

      if (other != this)
      {
         if (other instanceof final Robust that)
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
   public int signum()
   {

      return this.value > 0d ? 1 : this.value < 0d ? -1 : 0;
   }

   @Override
   public int compareTo(final Robust that)
   {

      return this.sub(that).signum();
   }

   @Override
   public Robust add(final Robust that)
   {

      if (this.value == 0d)
      {
         return that;
      }
      if (that.value == 0d)
      {
         return this;
      }
      final double addLo = this.lo + that.lo;
      final double addHi = this.hi + that.hi;
      final double lo = nextDown(addLo);
      final double hi = nextUp(addHi);
      final double va = this.value + that.value;
      return newBinary(Type.ADD, that, va, lo, hi);
   }

   @Override
   public Robust sub(final Robust that)
   {

      if (this.value == 0d)
      {
         return that.neg();
      }
      if (that.value == 0d)
      {
         return this;
      }
      final double subLo = this.lo - that.hi;
      final double subHi = this.hi - that.lo;
      final double lo = nextDown(subLo);
      final double hi = nextUp(subHi);
      final double va = this.value - that.value;
      return newBinary(Type.SUB, that, va, lo, hi);
   }

   @Override
   public Robust mul(final Robust that)
   {

      if (this.value == 0d || that.value == 0d)
      {
         return ZERO;
      }
      final boolean twist = this.lo >= 0 != that.lo >= 0;
      final double mulLo = this.lo * (twist ? that.hi : that.lo);
      final double mulHi = this.hi * (twist ? that.lo : that.hi);
      final double sortedLo = mulLo < mulHi ? mulLo : mulHi;
      final double sortedHi = mulLo > mulHi ? mulLo : mulHi;
      final double lo = nextDown(sortedLo);
      final double hi = nextUp(sortedHi);
      final double va = this.value * that.value;
      return newBinary(Type.MUL, that, va, lo, hi);
   }

   @Override
   public Robust div(final Robust that)
   {

      if (that.value == 0d)
      {
         throw new DivisionByZeroException();
      }
      if (this.value == 0d)
      {
         return ZERO;
      }
      final boolean twist = this.lo >= 0 != that.lo >= 0;
      final double divLo = this.lo / (twist ? that.hi : that.lo);
      final double divHi = this.hi / (twist ? that.lo : that.hi);
      final double sortedLo = divLo < divHi ? divLo : divHi;
      final double sortedHi = divLo > divHi ? divLo : divHi;
      final double lo = nextDown(sortedLo);
      final double hi = nextUp(sortedHi);
      final double va = this.value / that.value;
      return newBinary(Type.DIV, that, va, lo, hi);
   }

   @Override
   public Robust neg()
   {

      if (this.value == 0d)
      {
         return ZERO;
      }
      final double lo = -this.hi;
      final double hi = -this.lo;
      final double va = -this.value;
      return newUnary(Type.NEG, (short) 0, va, lo, hi);
   }

   @Override
   public Robust abs()
   {

      if (this.value == 0d)
      {
         return ZERO;
      }
      if (this.value > 0d)
      {
         return this;
      }
      final double lo = -this.hi;
      final double hi = -this.lo;
      final double va = Math.abs(this.value);
      return newUnary(Type.ABS, (short) 0, va, lo, hi);
   }

   @Override
   public Robust pow(final int n)
   {

      if (this.value == 0d)
      {
         return n == 0 ? ONE : ZERO;
      }
      if (n < 0)
      {
         return ONE.div(pow(-n));
      }
      if (n == 0)
      {
         return ONE;
      }
      if (n == 1)
      {
         return this;
      }
      if (n > MAX_EXPONENT)
      {
         throw new IllegalExponentException(n);
      }
      final boolean twist = (n & 1) != 0 && this.value < 0d;
      final double powLo = twist ? Math.pow(this.hi, n) : Math.pow(this.lo, n);
      final double powHi = twist ? Math.pow(this.lo, n) : Math.pow(this.hi, n);
      final double lo = nextDown(powLo);
      final double hi = nextUp(powHi);
      final double va = Math.pow(this.value, n);
      return newUnary(Type.POW, (short) n, va, lo, hi);
   }

   @Override
   public Robust root(final int n)
   {

      if (n == 0)
      {
         throw new DivisionByZeroException();
      }
      if (this.value == 0d)
      {
         return ZERO;
      }
      if (n < 0)
      {
         return ONE.div(root(-n));
      }
      if (n == 1)
      {
         return this;
      }
      if (n > MAX_EXPONENT)
      {
         throw new IllegalExponentException(n);
      }
      final boolean twist = (n & 1) != 0 && this.value < 0d;
      final double rootLo = twist ? root(this.hi, n) : root(this.lo, n);
      final double rootHi = twist ? root(this.lo, n) : root(this.hi, n);
      final double lo = rootLo == 0d ? 0d : nextDown(rootLo);
      final double hi = rootHi == 0d ? 0d : nextUp(rootHi);
      final double va = root(this.value, n);
      return newUnary(Type.ROOT, (short) n, va, lo, hi);
   }

   @Override
   public Robust newTerminal(final String terminal)
   {

      return valueOf(terminal);
   }

   @Override
   public int index()
   {

      final int op = this.operations[this.operations.length - 1];
      final Type type = Type.values()[op & 0xF];
      if (type == Type.POW || type == Type.ROOT)
      {
         return op >> 4;
      }
      return (int) this.lo;
   }

   /**
    * Returns a friendly {@link String} representation, which is accepted by
    * {@link #valueOf(String)}.
    * <p>
    * For details see the {@link #valueOf(String)}.
    * 
    * @see java.lang.Object#toString()
    * @see #valueOf(String)
    */
   @Override
   public String toString()
   {

      return toSyntaxTree().toString();
   }

   /**
    * Returns a {@link SyntaxTree} whose structure corresponds to {@code this}.
    * 
    * @return a {@link SyntaxTree} whose structure corresponds to {@code this}.
    */
   public SyntaxTree<?> toSyntaxTree()
   {

      return toAlgebraic();
   }

   @Override
   public Type type()
   {

      return Type.values()[operations[this.operations.length - 1] & 0xF];
   }

   /**
    * Returns {@code (int)}{@link #doubleValue()}.
    * 
    * @see java.lang.Number#intValue()
    */
   @Override
   public int intValue()
   {

      return (int) this.value;
   }

   /**
    * Returns {@code (long)}{@link #doubleValue()}.
    * 
    * @see java.lang.Number#longValue()
    */
   @Override
   public long longValue()
   {

      return (long) this.value;
   }

   /**
    * Returns {@code (float)}{@link #doubleValue()}.
    * 
    * @see java.lang.Number#floatValue()
    */
   @Override
   public float floatValue()
   {

      return (float) this.value;
   }

   /**
    * Returns an approximation of the true value which has the sign of the true value and is in the
    * closed interval {@code [}{@link #lowerBound()}{@code ;}{@link #upperBound()}{@code ]}, where
    * upper and lower bounds always are both {@code >= 0} or both {@code <= 0}.
    * 
    * @see java.lang.Number#doubleValue()
    */
   @Override
   public double doubleValue()
   {

      return this.value;
   }

   /**
    * Returns the lower bound of a closed interval containing the true value of this.
    * <p>
    * Upper and lower bounds always are both {@code >= 0} or both {@code <= 0}.
    * 
    * @return the lower bound of a closed interval containing the true value of this.
    * @see #upperBound()
    */
   public double lowerBound()
   {

      return lo;
   }

   /**
    * Returns the upper bound of a closed interval containing the true value of this.
    * <p>
    * Upper and lower bounds always are both {@code >= 0} or both {@code <= 0}.
    * 
    * @return the upper bound of a closed interval containing the true value of this.
    * @see #lowerBound()
    */
   public double upperBound()
   {

      return hi;
   }

   /**
    * Returns the number of nodes in the abstract syntax tree.
    * 
    * @return the number of nodes in the abstract syntax tree.
    */
   public int size()
   {

      return this.operations.length;
   }

   /**
    * Returns the number of terminal nodes in the abstract syntax tree.
    * 
    * @return the number of terminal nodes in the abstract syntax tree.
    */
   public int terminalsSize()
   {

      return this.operands.length;
   }

   @Override
   public Robust one()
   {

      return ONE;
   }

   private Robust(final double value)
   {

      this(TERMINAL_OPERATIONS, new double[] { value }, value, value, value, hashCode(value));
   }

   private static final int hashCode(final double value)
   {

      return 31 * TERMINAL_OPERATIONS_HASHCODE + 31 + Double.hashCode(value);
   }

   private Robust(final short[] operations, final double[] operands, final double value,
         final double lo, final double hi, final int hashCode)
   {

      this.operations = operations;
      this.operands = operands;
      this.hashCode = hashCode;
      final int signum;
      if (lo <= 0d && hi >= 0d && lo != hi)
      {
         final Cache cache = Cache.getInstance();
         final Robust robust = cache.get(this);
         signum = robust != null ? robust.signum() : toAlgebraic().signum();
         cache.put(this);
      }
      else
      {
         signum = hi < 0d ? -1 : lo > 0d ? -1 : 0;
      }
      if (signum == 0)
      {
         this.value = 0d;
         this.lo = -0d;
         this.hi = 0d;
      }
      else if (lo <= 0 && 0 <= hi)
      {
         this.value = signum * (value == 0d ? nextUp(0d) : Math.abs(value));
         this.lo = min(signum > 0 ? 0d : lo, this.value);
         this.hi = max(signum < 0 ? 0d : hi, this.value);
      }
      else
      {
         this.value = value != 0 ? value : signum > 0 ? nextUp(value) : nextDown(value);
         this.lo = min(lo, value);
         this.hi = max(value, hi);
      }
   }

   private Robust newUnary(final Type type, final short exponent, final double value,
         final double lo, final double hi)
   {

      final int k = this.operations.length;
      final short[] operations = Arrays.copyOf(this.operations, k + 1);
      operations[k] = (short) (type.ordinal() + (exponent << 4));
      final int hashCode = 31 * this.hashCode + operations[k];
      return new Robust(operations, this.operands.clone(), value, lo, hi, hashCode);
   }

   private Robust newBinary(final Type type, final Robust that, final double value, final double lo,
         final double hi)
   {

      final int lenU = this.operations.length + that.operations.length;
      final int lenT = this.operands.length + that.operands.length;
      final short[] operations = Arrays.copyOf(this.operations, lenU + 1);
      final double[] operands = Arrays.copyOf(this.operands, lenT);
      arraycopy(that.operations, 0, operations, this.operations.length, that.operations.length);
      arraycopy(that.operands, 0, operands, this.operands.length, that.operands.length);
      operations[lenU] = (short) type.ordinal();
      final int hashCode = 31 * (31 * this.hashCode + that.hashCode) + type.ordinal();
      return new Robust(operations, operands, value, lo, hi, hashCode);
   }

   private Algebraic toAlgebraic()
   {

      final Stack<Algebraic> stack = new Stack<>();
      for (int kOperation = 0, kOperand = 0; kOperation < this.operations.length; kOperation++)
      {
         final int op = this.operations[kOperation];
         final Type type = Type.values()[op & 0xF];
         switch (type)
         {
            // @formatter:off
            case TERMINAL -> stack.push(new Algebraic(this.operands[kOperand++]));
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

   private static double root(final double x, final int n)
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

   private static final short[] TERMINAL_OPERATIONS = new short[] { (short) TERMINAL.ordinal() };

   private static final int TERMINAL_OPERATIONS_HASHCODE = Arrays.hashCode(TERMINAL_OPERATIONS);

   /** The operations (reverse polish notation). */
   private final short[] operations;

   /** The operands (reverse polish notation). */
   private final double[] operands;

   /** The precalculated hash code. */
   private final int hashCode;

   /** See {@link #lowerBound()}. */
   private final double lo;

   /** See {@link #upperBound()}. */
   private final double hi;

   /** See {@link #doubleValue()}. */
   private final double value;

   /** The number zero. */
   public static final Robust ZERO = new Robust(0d);

   /** The number one. */
   public static final Robust ONE = new Robust(1d);

   /** The maximum exponent for pow and root operations. */
   public static final int MAX_EXPONENT = 0x07FF;
}

package br.eng.strauss.yaxana;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.nextDown;
import static java.lang.Math.nextUp;

import java.io.Serializable;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.exc.IllegalExponentException;
import br.eng.strauss.yaxana.exc.NotRepresentableAsADoubleException;
import br.eng.strauss.yaxana.exc.UnreachedException;
import br.eng.strauss.yaxana.io.Parser;
import br.eng.strauss.yaxana.pdc.SafeDoubleOps;

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
 *      "https://raw.githubusercontent.com/bestrauss/yaxana/main/docs/Zum-Vorzeichentest-Algebraischer-Ausdruecke.pdf">Zum-Vorzeichentest-Algebraischer-Ausdrücke</a>.
 */
public final class Robust extends ConciseNumber implements Expression<Robust>
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

      final int hashCode = 31 * (31 + TERMINAL_OPERATIONS_HASHCODE) + Double.hashCode(value);
      return valueOf(TERMINAL_OPERATIONS, new double[] { value }, hashCode, value, value, value,
                     value == 0d);
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
         case NEG      -> { return valueOf(st.left()).neg(); }
         case ABS      -> { return valueOf(st.left()).abs(); }
         case POW      -> { return valueOf(st.left()).pow(st.index()); }
         case ROOT     -> { return valueOf(st.left()).root(st.index()); }
         case ADD      -> { return valueOf(st.left()).add(valueOf(st.right())); }
         case SUB      -> { return valueOf(st.left()).sub(valueOf(st.right())); }
         case MUL      -> { return valueOf(st.left()).mul(valueOf(st.right())); }
         case DIV      -> { return valueOf(st.left()).div(valueOf(st.right())); }
         // @formatter:on
      }
      throw new UnreachedException();
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
      if (simplify(addLo, addHi, that))
      {
         final Double value = SafeDoubleOps.addOrNull(this.value, that.value);
         if (value != null)
         {
            return valueOf(addLo);
         }
      }
      final double lo = nextDown(addLo);
      final double hi = nextUp(addHi);
      final double va = this.value + that.value;
      return newBinary(Type.ADD, that, va, lo, hi, true);
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
      if (simplify(subLo, subHi, that))
      {
         final Double value = SafeDoubleOps.subOrNull(this.value, that.value);
         if (value != null)
         {
            return valueOf(subLo);
         }
      }
      final double lo = nextDown(subLo);
      final double hi = nextUp(subHi);
      final double va = this.value - that.value;
      return newBinary(Type.SUB, that, va, lo, hi, true);
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
      if (simplify(mulLo, mulHi, that))
      {
         final Double value = SafeDoubleOps.mulOrNull(this.value, that.value);
         if (value != null)
         {
            return valueOf(mulLo);
         }
      }
      final double sortedLo = mulLo < mulHi ? mulLo : mulHi;
      final double sortedHi = mulLo > mulHi ? mulLo : mulHi;
      final double lo = sortedLo != 0d ? nextDown(sortedLo) : 0d;
      final double hi = sortedHi != 0d ? nextUp(sortedHi) : 0d;
      final double v = this.value * that.value;
      final double va = v != 0d ? v : twist ? nextDown(0d) : nextUp(0d);
      return newBinary(Type.MUL, that, va, lo, hi, false);
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
      if (simplify(divLo, divHi, that))
      {
         final Double value = SafeDoubleOps.divOrNull(this.value, that.value);
         if (value != null)
         {
            return valueOf(divLo);
         }
      }
      final double sortedLo = divLo < divHi ? divLo : divHi;
      final double sortedHi = divLo > divHi ? divLo : divHi;
      final double lo = nextDown(sortedLo);
      final double hi = nextUp(sortedHi);
      final double v = this.value / that.value;
      final double va = v != 0d ? v : twist ? nextDown(0d) : nextUp(0d);
      return newBinary(Type.DIV, that, va, lo, hi, false);
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
      final boolean negative = this.value < 0d;
      final boolean odd = (n & 1) != 0;
      final boolean twist = odd && negative;
      final double powLo = twist ? Math.pow(this.hi, n) : Math.pow(this.lo, n);
      final double powHi = twist ? Math.pow(this.lo, n) : Math.pow(this.hi, n);
      if (simplify(powLo, powHi, null))
      {
         final Double value = SafeDoubleOps.powOrNull(this.value, n);
         if (value != null)
         {
            return valueOf(powLo);
         }
      }
      final double lo = twist || powLo > 0d ? nextDown(powLo) : powLo;
      final double hi = !twist || powHi < 0d ? nextUp(powHi) : powHi;
      final double v = Math.pow(this.value, n);
      final double va = v != 0d ? v : twist ? nextDown(v) : nextUp(v);
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
      if (simplify(rootLo, rootHi, null))
      {
         final Double value = SafeDoubleOps.rootOrNull(this.value, n);
         if (value != null)
         {
            return valueOf(rootLo);
         }
      }
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

   @Override
   public Robust one()
   {

      return ONE;
   }

   protected Robust(final short[] operations, final double[] operands, final int hashCode,
         final double value, final double lo, final double hi, final boolean mayBeZero)
   {

      super(operations, operands, hashCode);
      final int signum;
      if (mayBeZero)
      {
         if (lo <= 0d && hi >= 0d && lo != hi)
         {
            signum = toAlgebraic().signum();
         }
         else
         {
            signum = hi < 0d ? -1 : lo > 0d ? 1 : 0;
         }
      }
      else
      {
         signum = value > 0d ? 1 : value < 0d ? -1 : 0;
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

   protected Robust simplified()
   {

      return simplification && this.lo == this.hi ? valueOf(this.value) : this;
   }

   private boolean simplify(final double lo, final double hi, final Robust that)
   {

      return simplification && Double.isFinite(lo) && lo == hi && this.lo == this.hi
            && (that == null || that.lo == that.hi);
   }

   private static final long serialVersionUID = BigFloat.serialVersionUID;

   /** Whether simplification is done. */
   protected static boolean simplification = true;

   /** See {@link #lowerBound()}. */
   private final double lo;

   /** See {@link #upperBound()}. */
   private final double hi;

   /** See {@link #doubleValue()}. */
   private final double value;

   /** The number zero. */
   public static final Robust ZERO = staticValueOf(0d);

   /** The number one. */
   public static final Robust ONE = staticValueOf(1d);
}

package br.eng.strauss.yaxana.epu;

import static br.eng.strauss.yaxana.Type.TERMINAL;

import java.util.Objects;
import java.util.function.Supplier;

import br.eng.strauss.yaxana.Algorithm;
import br.eng.strauss.yaxana.Expression;
import br.eng.strauss.yaxana.SyntaxTree;
import br.eng.strauss.yaxana.Type;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.big.Rounder;
import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.exc.UnreachedException;
import br.eng.strauss.yaxana.io.Parser;
import br.eng.strauss.yaxana.io.Stringifier;
import br.eng.strauss.yaxana.pdc.Approximable;

/**
 * Ideally exact algebraic {@link Number} intended to be used for the implementation of exact
 * geometric algorithms.
 * <p>
 * Instances of this class represent an algebraic number as an expression in form of an abstract
 * syntax tree (AST). The {@link #signum()} and {@link #compareTo(Algebraic)} methods return
 * decisions based on the exact value of the expression in finite time, although their exact decimal
 * or binary or whatever numeral representation may have an infinite number of digits.
 * <p>
 * Instances are not thread-safe.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class Algebraic
      implements Expression<Algebraic>, SyntaxTree<Algebraic>, Approximable<Algebraic>
{

   /**
    * Returns a new literal expression.
    * 
    * @param number
    *           the value.
    */
   public Algebraic(final double number)
   {

      this(new BigFloat(number));
   }

   /**
    * Returns a new expression.
    * 
    * @param expression
    *           An expression accepted by the {@link Parser}.
    * @throws NumberFormatException
    *            If {@code expression} is not accepted by the {@link Parser}.
    * @see Parser
    * @see Stringifier
    * @see #toString()
    */
   public Algebraic(final String expression) throws NumberFormatException
   {

      this(valueOf(expression, null));
   }

   /**
    * Returns a new value corresponding to a given expression.
    * 
    * @param expression
    *           the expression.
    * @param rounder
    *           rounder to be used or {@code null}
    * @return a new value corresponding to the given expression.
    */
   private static Algebraic valueOf(final String expression, final Rounder rounder)
   {

      final Algebraic that;
      if (BigFloat.terminalPattern().matcher(expression).matches())
      {
         that = new Algebraic(new BigFloat(expression, rounder));
      }
      else
      {
         that = new Parser<Algebraic>(ZERO, expression).expression();
      }
      return that;
   }

   /**
    * Returns a new literal expression.
    * 
    * @param number
    *           the value.
    */
   public Algebraic(final BigFloat number)
   {

      this(number, Integer.MAX_VALUE);
   }

   /**
    * Returns a new terminal expression.
    *
    * @param approximation
    *           The approximation.
    * @param precision
    *           The precision.
    */
   private Algebraic(final BigFloat approximation, final int precision)
   {

      this.type = Type.TERMINAL;
      this.approximation = approximation;
      this.precision = precision;
      this.left = null;
      this.right = null;
      this.hashCode = Objects.hash(this.approximation, this.precision);
   }

   /**
    * Returns a new binary or unary expression.
    *
    * @param type
    *           the type of expression;
    * @param left
    *           the left subexpression.
    * @param right
    *           the left subexpression or {@code null} in case of a unary expression.
    */
   public Algebraic(final Type type, final Algebraic left, final Algebraic right)
   {

      this.type = type;
      this.approximation = null;
      this.precision = 0;
      this.left = left;
      this.right = right;
      this.hashCode = Objects.hash(this.type, this.left, this.right);
   }

   /**
    * Returns a deep clone with cleared approximations/precisions.
    *
    * @param that
    *           The value to be cloned.
    */
   public Algebraic(final Algebraic that)
   {

      this.type = that.type;
      this.left = that.left != null ? new Algebraic(that.left) : null;
      this.right = that.right != null ? new Algebraic(that.right) : null;
      this.approximation = that.type == Type.TERMINAL ? that.approximation : null;
      this.precision = that.type == Type.TERMINAL ? Integer.MAX_VALUE : 0;
      this.hashCode = that.hashCode;
   }

   @Override
   public Algebraic newTerminal(final String literal)
   {

      return new Algebraic(literal);
   }

   @Override
   public Type type()
   {

      return type;
   }

   @Override
   public String terminal()
   {

      if (type == Type.TERMINAL)
      {
         return approximation().toString();
      }
      throw new IllegalArgumentException();
   }

   @Override
   public int index()
   {

      if (type == Type.POW || type == Type.ROOT)
      {
         return right.approximation.intValue();
      }
      return this.approximation.intValue();
   }

   @Override
   public Algebraic left()
   {

      return left;
   }

   @Override
   public Algebraic right()
   {

      return right;
   }

   @Override
   public Algebraic add(final Algebraic that)
   {

      return new Algebraic(Type.ADD, this, that);
   }

   @Override
   public Algebraic sub(final Algebraic that)
   {

      return new Algebraic(Type.SUB, this, that);
   }

   @Override
   public Algebraic neg()
   {

      return new Algebraic(Type.NEG, this, null);
   }

   @Override
   public Algebraic mul(final Algebraic that)
   {

      return new Algebraic(Type.MUL, this, that);
   }

   @Override
   public Algebraic div(final Algebraic that)
   {

      return new Algebraic(Type.DIV, this, that);
   }

   @Override
   public Algebraic pow(final int n)
   {

      if (n < 0)
      {
         return Algebraic.ONE.div(pow(-n));
      }
      switch (n)
      {
         case 0 :
            return ONE;
         case 1 :
            return this;
      }
      return new Algebraic(Type.POW, this, new Algebraic(new BigFloat(n)));
   }

   @Override
   public Algebraic root(final int n)
   {

      if (n < 0)
      {
         return Algebraic.ONE.div(root(-n));
      }
      switch (n)
      {
         case 0 :
            throw new DivisionByZeroException();
         case 1 :
            return this;
      }
      return new Algebraic(Type.ROOT, this, new Algebraic(new BigFloat(n)));
   }

   @Override
   public Algebraic abs()
   {

      return new Algebraic(Type.ABS, this, null);
   }

   @Override
   public int signum()
   {

      final Algorithm algorithm;
      synchronized (Algebraic.class)
      {
         algorithm = Algebraic.algorithm;
      }
      final Supplier<EPU> epu = switch (algorithm)
      {
         case BFMSS2 -> () -> new BfmssEPU();
         case ZVAA -> () -> new ZvaaEPU();
         case MOSC -> () -> new MoscEPU();
      };
      Approximable.super.ensureSignum(epu);
      return this.approximation.signum();
   }

   @Override
   public int compareTo(final Algebraic that)
   {

      if (this.type == TERMINAL && that.type == TERMINAL)
      {
         return this.approximation.compareTo(that.approximation);
      }
      return this.sub(that).signum();
   }

   /**
    * Tests whether {@code other} is {@code this} or another {@link Algebraic} and
    * {@link #astEquals(Algebraic)}.
    * <p>
    * Current approximations and their precisions are not considered during the test.
    * <p>
    * For exact comparison of algebraic number values see {@link #compareTo(Algebraic)}.
    * 
    * @param other
    *           The object to compare to.
    * @return {@code true} if {@code other} is {@code this} or another {@link Algebraic} and
    *         {@link #astEquals(Algebraic)}.
    * @see java.lang.Object#equals(java.lang.Object)
    * @see #compareTo(Algebraic)
    * @see #astEquals(Algebraic)
    */
   @Override
   public boolean equals(final Object other)
   {

      if (other != this)
      {
         if (other instanceof Algebraic)
         {
            final Algebraic that = (Algebraic) other;
            return this.astEquals(that);
         }
         return false;
      }
      return true;
   }

   /**
    * Deep test whether the abstract syntax trees of this {@link Algebraic} and that
    * {@link Algebraic} are equal.
    * 
    * @param that
    *           that {@link Algebraic}. Must not be {@code null}.
    * @return test whether the abstract syntax trees of this {@link Algebraic} and that
    *         {@link Algebraic} are equal.
    */
   public boolean astEquals(final Algebraic that)
   {

      if (this.hashCode == that.hashCode && this.type == that.type)
      {
         if (this.left != null && !this.left.astEquals(that.left))
         {
            return false;
         }
         if (this.right != null && !this.right.astEquals(that.right))
         {
            return false;
         }
         if (this.type == Type.TERMINAL)
         {
            return this.approximation.equals(that.approximation);
         }
         return true;
      }
      return false;
   }

   @Override
   public int hashCode()
   {

      return this.hashCode;
   }

   /**
    * Returns a representation accepted by the {@link Parser}.
    * 
    * @see java.lang.Object#toString()
    * @see Parser
    * @see Stringifier
    * @see #Algebraic(String)
    */
   @Override
   public String toString()
   {

      return new Stringifier<Algebraic>().stringify(this);
   }

   /**
    * Returns {@link #approximation()}{@code .doubleValue()}.
    * 
    * @return {@link #approximation()}{@code .doubleValue()}.
    */
   @Override
   public double doubleValue()
   {

      return this.approximation.doubleValue();
   }

   @Override
   public Algebraic one()
   {

      return Algebraic.ONE;
   }

   @Override
   public int precision()
   {

      return precision;
   }

   @Override
   public BigFloat approximation()
   {

      return approximation;
   }

   @Override
   public void setApproximation(final BigFloat approximation, final int precision)
   {

      this.approximation = approximation;
      this.precision = precision;
   }

   /**
    * Returns the algorithm being used for sign computation.
    * 
    * @return the algorithm being used for sign computation.
    * @see #setAlgorithm(Algorithm)
    */
   public static Algorithm getAlgorithm()
   {

      synchronized (Algebraic.class)
      {
         return algorithm;
      }
   }

   /**
    * Sets the algorithm to be used for sign computation.
    * 
    * @param algorithm
    *           the algorithm to be used for sign computation.
    * @see #getAlgorithm()
    */
   public static void setAlgorithm(final Algorithm algorithm)
   {

      synchronized (Algebraic.class)
      {
         Algebraic.algorithm = algorithm;
      }
   }

   /**
    * Returns an expression for the conjugate root of the structural polynomial with the greatest
    * absolute value.
    * <p>
    * The return value will have positive terminals and be free of {@code SUB}, {@code NEG} and
    * {@code ABS} operations.
    * 
    * @return see above.
    */
   public Algebraic maxConjugate()
   {

      // @formatter:off
      return switch (type)
      {
         case TERMINAL -> new Algebraic(approximation.abs());
         case ADD      -> left.maxConjugate().add(right.maxConjugate()); 
         case SUB      -> left.maxConjugate().add(right.maxConjugate()); 
         case MUL      -> left.maxConjugate().mul(right.maxConjugate()); 
         case DIV      -> left.maxConjugate().div(right.maxConjugate()); 
         case NEG      -> left.maxConjugate();                           
         case ABS      -> left.maxConjugate();                           
         case POW      -> left.maxConjugate().pow(index());              
         case ROOT     -> left.maxConjugate().root(index());             
      };
      // @formatter:on
   }

   public Algebraic minConjugate()
   {

      final Algebraic thiz = this.maxConjugate();
      // @formatter:off
      return switch (thiz.type)
      {
         case TERMINAL -> thiz;
         case ADD      -> thiz.left.minConjugate().sub(thiz.right.minConjugate()).abs(); 
         case MUL      -> thiz.left.minConjugate().mul(thiz.right.minConjugate()); 
         case DIV      -> thiz.left.minConjugate().div(thiz.right.minConjugate()); 
         case POW      -> thiz.left.minConjugate().pow(thiz.index());              
         case ROOT     -> thiz.left.minConjugate().root(thiz.index());             
         default -> throw new UnreachedException(); 
      };
      // @formatter:on
   }

   /**
    * Returns a new instance whose terminals are integers and which is a quotient of two division
    * free values.
    * 
    * @return see above.
    */
   public Algebraic toIntegerSingleDiv()
   {

      switch (type())
      {
         case TERMINAL ->
         {
            final int scale = approximation.scale();
            return scale < 0
                  ? new Algebraic(new BigFloat(approximation.unscaledValue()))
                        .div(new Algebraic(BigFloat.twoTo(-scale)))
                  : new Algebraic(approximation).div(Algebraic.ONE);
         }
         case ADD ->
         {
            final Algebraic a = left.toIntegerSingleDiv();
            final Algebraic b = right.toIntegerSingleDiv();
            return a.left.mul(b.right).add(a.right.mul(b.left)).div(a.right.mul(b.right));
         }
         case SUB ->
         {
            final Algebraic a = left.toIntegerSingleDiv();
            final Algebraic b = right.toIntegerSingleDiv();
            return a.left.mul(b.right).sub(a.right.mul(b.left)).div(a.right.mul(b.right));
         }
         case MUL ->
         {
            final Algebraic a = left.toIntegerSingleDiv();
            final Algebraic b = right.toIntegerSingleDiv();
            return a.left.mul(b.left).div(a.right.mul(b.right));
         }
         case DIV ->
         {
            final Algebraic a = left.toIntegerSingleDiv();
            final Algebraic b = right.toIntegerSingleDiv();
            return a.left.mul(b.right).div(a.right.mul(b.left));
         }
         case NEG ->
         {
            final Algebraic a = left.toIntegerSingleDiv();
            return a.left.div(a.right.neg());
         }
         case ABS ->
         {
            final Algebraic a = left.toIntegerSingleDiv();
            return a.left.abs().div(a.right.abs());
         }
         case POW ->
         {
            final Algebraic a = left.toIntegerSingleDiv();
            return a.left.pow(index()).div(a.right.pow(index()));
         }
         case ROOT ->
         {
            final Algebraic a = left.toIntegerSingleDiv();
            final int index = index();
            return index == 2 ? a.left.div(a.left.mul(a.right).root(index))
                  : a.left.div(a.left.pow(index - 1).mul(a.right).root(index));
         }
      }
      throw new UnreachedException();
   }

   /** The number {@code 0}. */
   public static final Algebraic ZERO = new Algebraic(0d);

   /** The number {@code 1}. */
   public static final Algebraic ONE = new Algebraic(1d);

   /** The number {@code -1}. */
   public static final Algebraic MINUSONE = new Algebraic(-1d);

   /** The number {@code 2}. */
   public static final Algebraic TWO = new Algebraic(2d);

   /** The number {@code 0.5}. */
   public static final Algebraic HALF = new Algebraic(0.5);

   /** The {@link Algorithm} being used for sign computation. */
   private static Algorithm algorithm = Algorithm.ZVAA;

   /** The type of expression represented by this {@link Algebraic}. */
   private final Type type;

   /** The left or the unary subexpression of this {@link Algebraic} or {@code null}. */
   private final Algebraic left;

   /** The right subexpression of this {@link Algebraic} or {@code null}. */
   private final Algebraic right;

   /** The hash code. */
   private final int hashCode;

   /** The current approximation of the value of this {@link Algebraic}. */
   private BigFloat approximation;

   /** The precision of the current approximation of the value of this {@link Algebraic}. */
   private int precision;

   /** BFMSS[2] parameter used by {@link EPU}-implementations. */
   public BigFloat u;

   /** BFMSS[2] parameter used by {@link EPU}-implementations. */
   public BigFloat l;

   /** BFMSS[2] parameter used by {@link EPU}-implementations. */
   public int vp;

   /** BFMSS[2] parameter used by {@link EPU}-implementations. */
   public int vn;

   /** BFMSS[2] parameter used by {@link EPU}-implementations. */
   public long D;
}

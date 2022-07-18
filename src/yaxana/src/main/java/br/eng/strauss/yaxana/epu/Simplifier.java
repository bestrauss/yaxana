package br.eng.strauss.yaxana.epu;

import static br.eng.strauss.yaxana.Type.TERMINAL;

import br.eng.strauss.yaxana.exc.NonTerminatingBinaryExpansionException;
import br.eng.strauss.yaxana.exc.UnreachedException;

/**
 * Simplification of {@link Algebraic}s.
 * 
 * @author Burkhard Strauss
 * @since 06-2022
 */
final class Simplifier
{

   public static Algebraic simplified(final Algebraic a)
   {

      switch (a.type())
      {
         // @formatter:off
         case TERMINAL -> { return terminal(a); } 
         case ADD      -> { return add(a); }
         case SUB      -> { return sub(a); }
         case MUL      -> { return mul(a); }
         case DIV      -> { return div(a); }
         case NEG      -> { return neg(a); }
         case ABS      -> { return abs(a); }
         case POW      -> { return pow(a); }
         case ROOT     -> { return root(a); }
         // @formatter:on
      }
      throw new UnreachedException();
   }

   private static Algebraic terminal(final Algebraic a)
   {

      return new Algebraic(a.approximation());
   }

   private static Algebraic add(final Algebraic a)
   {

      final Algebraic left = simplified(a.left());
      final Algebraic rite = simplified(a.right());
      return left.type() == TERMINAL && rite.type() == TERMINAL
            ? new Algebraic(left.approximation().add(rite.approximation()))
            : left.add(rite);
   }

   private static Algebraic sub(final Algebraic a)
   {

      final Algebraic left = simplified(a.left());
      final Algebraic rite = simplified(a.right());
      return left.type() == TERMINAL && rite.type() == TERMINAL
            ? new Algebraic(left.approximation().sub(rite.approximation()))
            : left.sub(rite);
   }

   private static Algebraic mul(final Algebraic a)
   {

      final Algebraic left = simplified(a.left());
      final Algebraic rite = simplified(a.right());
      return left.type() == TERMINAL && rite.type() == TERMINAL
            ? new Algebraic(left.approximation().mul(rite.approximation()))
            : left.mul(rite);
   }

   private static Algebraic div(final Algebraic a)
   {

      final Algebraic left = simplified(a.left());
      final Algebraic rite = simplified(a.right());
      try
      {
         return left.type() == TERMINAL && rite.type() == TERMINAL
               ? new Algebraic(left.approximation().div(rite.approximation()))
               : left.div(rite);
      }
      catch (final NonTerminatingBinaryExpansionException e)
      {
         // TODO coverage
         return left.div(rite);
      }
   }

   private static Algebraic neg(final Algebraic a)
   {

      final Algebraic left = simplified(a.left());
      switch (left.type())
      {
         // @formatter:off
         case TERMINAL -> { return new Algebraic(left.approximation().neg()); }
         case NEG -> { return left.left(); }
         default -> { return left.neg(); }
         // @formatter:on
      }
   }

   private static Algebraic abs(final Algebraic a)
   {

      final Algebraic left = simplified(a.left());
      switch (left.type())
      {
         // @formatter:off
         case TERMINAL -> { return new Algebraic(left.approximation().abs()); }
         case NEG -> { return left.left().abs(); }
         case ROOT -> { return left; }
         default -> { return left.abs(); }
         // @formatter:on
      }
   }

   private static Algebraic pow(final Algebraic a)
   {

      final Algebraic left = simplified(a.left());
      switch (left.type())
      {
         // @formatter:off
         case TERMINAL -> { return new Algebraic(left.approximation().pow(a.index())); }
         case ROOT     -> { return powRoot(left, a.index(), left.index()); }
         default       -> { return left.pow(a.index()); }
         // @formatter:on
      }
   }

   private static Algebraic root(final Algebraic a)
   {

      final Algebraic left = simplified(a.left());
      switch (left.type())
      {
         // @formatter:off
         case POW -> { return powRoot(left, left.index(), a.index()); }
         default  -> { return left.root(a.index()); }
         // @formatter:on
      }
   }

   private static Algebraic powRoot(final Algebraic a, final int nom, final int denom)
   {

      // TODO
      return a.pow(nom).root(denom);
   }
}

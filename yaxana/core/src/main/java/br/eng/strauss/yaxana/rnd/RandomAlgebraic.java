package br.eng.strauss.yaxana.rnd;

import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * Pseudo random generator for {@link Algebraic}s.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class RandomAlgebraic
{

   public RandomAlgebraic(final int maxBinaryDigits, final int maxScale, final int maxDepth)
   {

      this.randomRobust = new RandomRobust(maxBinaryDigits, maxScale, maxDepth);
   }

   public Algebraic next()
   {

      return (Algebraic) this.randomRobust.next().toSyntaxTree();
   }

   private final RandomRobust randomRobust;
}

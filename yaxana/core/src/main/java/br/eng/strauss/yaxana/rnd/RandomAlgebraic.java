package br.eng.strauss.yaxana.rnd;

import static br.eng.strauss.yaxana.epu.Algebraic.ZERO;

import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * Pseudo random generator for {@link Algebraic}s.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class RandomAlgebraic extends RandomExpression<Algebraic>
{

   public RandomAlgebraic(final int maxBinaryDigits, final int maxScale, final int maxDepth)
   {

      super(ZERO, maxBinaryDigits, maxScale, maxDepth);
   }
}

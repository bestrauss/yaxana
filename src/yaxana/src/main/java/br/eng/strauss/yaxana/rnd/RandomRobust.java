package br.eng.strauss.yaxana.rnd;

import static br.eng.strauss.yaxana.Robust.ZERO;

import br.eng.strauss.yaxana.Robust;

/**
 * Pseudo random generator for {@link Robust}s.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class RandomRobust extends RandomExpression<Robust>
{

   public RandomRobust(final int maxDepth)
   {

      super(ZERO, 0, 0, maxDepth);
   }
}

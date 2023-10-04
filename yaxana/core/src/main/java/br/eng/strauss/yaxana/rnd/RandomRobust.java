package br.eng.strauss.yaxana.rnd;

import static br.eng.strauss.yaxana.Robust.ZERO;
import static br.eng.strauss.yaxana.Type.ADD;
import static br.eng.strauss.yaxana.Type.SUB;

import java.util.Arrays;
import java.util.Random;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Type;
import br.eng.strauss.yaxana.exc.UnreachedException;

/**
 * Pseudo random generator for {@link Robust}s.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class RandomRobust
{

   public RandomRobust(final int maxDepth)
   {

      this(0, 0, maxDepth);
   }

   public RandomRobust(final int maxBinaryDigits, final int maxScale, final int maxDepth)
   {

      this.random = new Random(0L);
      this.randomBigFloat = maxBinaryDigits > 0 && maxScale > 0
            ? new RandomBigFloat(maxBinaryDigits, maxScale)
            : null;
      this.maxDepth = maxDepth;
   }

   public Robust next()
   {

      return next(this.random.nextInt(this.maxDepth));
   }

   private Robust next(final int depth)
   {

      if (depth == 0)
      {
         final String terminal;
         if (this.randomBigFloat != null)
         {
            terminal = this.randomBigFloat.next().toHexString();
         }
         else
         {
            terminal = Double.toHexString(this.random.nextDouble());
         }
         return ZERO.newTerminal(terminal);
      }
      final Type type = TYPES[this.random.nextInt(TYPES.length)];
      final int dl = depth - 1;
      final int dr = depth - 1;
      switch (type)
      {
         default :
            throw new UnreachedException();
         case ADD :
            return next(dl).add(next(dr));
         case SUB :
            return next(dl).sub(next(dr));
         case MUL :
            return next(dl).mul(next(dr));
         case DIV :
         {
            final Robust expression = next(dr);
            if (expression.type() != SUB && expression.type() != ADD || expression.signum() == 0)
            {
               return next(dl);
            }
            return next(dl).div(expression);
         }
         case POW :
            return next(dl).pow(2);
         case ROOT :
         {
            Robust expression = next(dl);
            if (!(expression.type() == Type.TERMINAL && expression.signum() >= 0)
                  && expression.type() != Type.ABS && expression.type() != Type.ROOT)
            {
               expression = expression.abs();
            }
            return expression.sqrt();
         }
         case NEG :
            return next(dl).neg();
         case ABS :
            return next(dl).abs();
      }
   }

   private static final Type[] TYPES = Arrays.copyOfRange(Type.values(), 1, Type.values().length);

   private final Random random;

   private final RandomBigFloat randomBigFloat;

   private final int maxDepth;
}

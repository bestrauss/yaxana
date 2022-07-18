package br.eng.strauss.yaxana.rnd;

import java.math.BigInteger;
import java.util.Random;

import br.eng.strauss.yaxana.big.BigFloat;

/**
 * Random {@link BigFloat} generator.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class RandomBigFloat
{

   private final Random random;

   private final int maxBinaryDigits;

   private final int maxScale;

   public RandomBigFloat(final int maxBinaryDigits, final int maxScale)
   {

      this(new Random(0), maxBinaryDigits, maxScale);
   }

   public RandomBigFloat(final Random random, final int maxBinaryDigits, final int maxScale)
   {

      this.random = random;
      this.maxBinaryDigits = maxBinaryDigits;
      this.maxScale = maxScale;
   }

   public BigFloat next()
   {

      BigInteger unscaledValue = new BigInteger(maxBinaryDigits, random);
      if (random.nextBoolean())
      {
         unscaledValue = unscaledValue.negate();
      }
      int scale = random.nextInt(maxScale + 1);
      if (random.nextBoolean())
      {
         scale = -scale;
      }
      return new BigFloat(unscaledValue, scale);
   }
}

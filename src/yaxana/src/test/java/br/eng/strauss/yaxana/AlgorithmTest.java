package br.eng.strauss.yaxana;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Burkhard Strauss
 * @since 07-2022
 */
public final class AlgorithmTest extends YaxanaTest
{

   @Test
   public void test()
   {

      for (final Algorithm algorithm : Algorithm.values())
      {
         algorithm.setCurrent();
         assertEquals(algorithm, Algorithm.getCurrent());
      }
   }
}
package br.eng.strauss.yaxana;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.tools.YaxanaTest;

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

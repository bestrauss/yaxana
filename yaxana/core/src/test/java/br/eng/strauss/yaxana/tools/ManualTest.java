package br.eng.strauss.yaxana.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Algorithm;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class ManualTest extends YaxanaTest
{

   @WithAlgorithms({ Algorithm.BFMSS2, Algorithm.YAXANA })
   @Test
   public void crucialTest()
   {

      assertEquals(1, 1);
   }
}

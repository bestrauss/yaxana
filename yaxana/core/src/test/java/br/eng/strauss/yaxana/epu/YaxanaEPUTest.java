package br.eng.strauss.yaxana.epu;

import static br.eng.strauss.yaxana.epu.Algebraic.ONE;
import static br.eng.strauss.yaxana.epu.Algebraic.ZERO;
import static br.eng.strauss.yaxana.epu.YaxanaEPU.flatLength;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Algorithm;
import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.unittest.WithAlgorithms;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-10
 */
public final class YaxanaEPUTest extends YaxanaTest
{

   @WithAlgorithms(Algorithm.YAXANA)
   @Test
   public void test()
   {

      {
         final Algebraic a = new Algebraic("\\1234567890123456789012345");
         final Algebraic b = new Algebraic("\\1234567890123456789012345");
         final Algebraic c = new Algebraic("\\1234567890123456789012346");
         assertTrue(a.isEqualTo(a));
         assertTrue(a.isEqualTo(b));
         assertTrue(a.isLessThan(c));
      }
      {
         final Algebraic a = new Algebraic("\\1234567890000000000000000");
         final Algebraic b = new Algebraic("\\1234567890000000000000000");
         final Algebraic c = new Algebraic("\\1234567890000000000000001");
         assertTrue(a.isEqualTo(a));
         assertTrue(a.isEqualTo(b));
         assertTrue(a.isLessThan(c));
      }
      {
         final Algebraic a = new Algebraic("\\1234567890000000000000000P-1000");
         final Algebraic b = new Algebraic("\\1234567890000000000000000P-1000");
         final Algebraic c = new Algebraic("\\1234567890000000000000001P-1000");
         assertTrue(a.isEqualTo(a));
         assertTrue(a.isEqualTo(b));
         assertTrue(a.isLessThan(c));
      }
   }

   @Test
   public void testFlatLength()
   {

      assertEquals(0, flatLength(ZERO.approximation()));
      assertEquals(1, flatLength(ONE.approximation()));
      assertEquals(1, flatLength(new BigFloat(-1d)));
      assertEquals(13, flatLength(new BigFloat(0x1000)));
      assertEquals(13, flatLength(new BigFloat(0x1FFF)));
      assertEquals(1, flatLength(new BigFloat(0x1P-1)));
      assertEquals(2, flatLength(new BigFloat(0x1P-2)));
      assertEquals(10, flatLength(new BigFloat(0x1P-10)));
      assertEquals(10, flatLength(new BigFloat(0x3P-10)));
      assertEquals(10, flatLength(new BigFloat(0xFFP-10)));
   }
}

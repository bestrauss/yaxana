package br.eng.strauss.yaxana.pdc;

import static br.eng.strauss.yaxana.pdc.Ld.ceilOfLdOfAbsOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class LdTest
{

   @Test
   public void testCeilOfLog2OfAbsOf()
   {

      assertEquals(0, ceilOfLdOfAbsOf(0));
      assertEquals(0, ceilOfLdOfAbsOf(1));
      assertEquals(1, ceilOfLdOfAbsOf(2));
      assertEquals(2, ceilOfLdOfAbsOf(3));
      assertEquals(2, ceilOfLdOfAbsOf(4));
      assertEquals(3, ceilOfLdOfAbsOf(5));
      assertEquals(3, ceilOfLdOfAbsOf(6));
      assertEquals(3, ceilOfLdOfAbsOf(7));
      assertEquals(3, ceilOfLdOfAbsOf(8));
      assertEquals(4, ceilOfLdOfAbsOf(9));

      assertEquals(4, ceilOfLdOfAbsOf(15));
      assertEquals(4, ceilOfLdOfAbsOf(16));
      assertEquals(5, ceilOfLdOfAbsOf(17));

      assertEquals(0, ceilOfLdOfAbsOf(-0));
      assertEquals(0, ceilOfLdOfAbsOf(-1));
      assertEquals(1, ceilOfLdOfAbsOf(-2));
      assertEquals(2, ceilOfLdOfAbsOf(-3));
      assertEquals(2, ceilOfLdOfAbsOf(-4));
      assertEquals(3, ceilOfLdOfAbsOf(-5));
      assertEquals(3, ceilOfLdOfAbsOf(-6));
      assertEquals(3, ceilOfLdOfAbsOf(-7));
      assertEquals(3, ceilOfLdOfAbsOf(-8));
      assertEquals(4, ceilOfLdOfAbsOf(-9));
   }
}

package br.eng.strauss.yaxana;

import static br.eng.strauss.yaxana.Robust.ONE;
import static br.eng.strauss.yaxana.Robust.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.tools.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class ComparisonTest extends YaxanaTest
{

   @Test
   public void test()
   {

      assertEquals(true, ZERO.isZero());
      assertEquals(false, ONE.isZero());
      assertEquals(false, ONE.neg().isZero());

      assertEquals(false, ZERO.isPositive());
      assertEquals(true, ONE.isPositive());
      assertEquals(false, ONE.neg().isPositive());

      assertEquals(false, ZERO.isNegative());
      assertEquals(false, ONE.isNegative());
      assertEquals(true, ONE.neg().isNegative());

      assertEquals(true, ZERO.isEqualTo(ZERO));
      assertEquals(false, ZERO.isEqualTo(ONE));
      assertEquals(false, ZERO.isEqualTo(ONE.neg()));
      assertEquals(false, ONE.isEqualTo(ZERO));
      assertEquals(true, ONE.isEqualTo(ONE));
      assertEquals(false, ONE.isEqualTo(ONE.neg()));
      assertEquals(false, ONE.neg().isEqualTo(ZERO));
      assertEquals(false, ONE.neg().isEqualTo(ONE));
      assertEquals(true, ONE.neg().isEqualTo(ONE.neg()));

      assertEquals(false, ZERO.isGreaterThan(ZERO));
      assertEquals(false, ZERO.isGreaterThan(ONE));
      assertEquals(true, ZERO.isGreaterThan(ONE.neg()));
      assertEquals(true, ONE.isGreaterThan(ZERO));
      assertEquals(false, ONE.isGreaterThan(ONE));
      assertEquals(true, ONE.isGreaterThan(ONE.neg()));
      assertEquals(false, ONE.neg().isGreaterThan(ZERO));
      assertEquals(false, ONE.neg().isGreaterThan(ONE));
      assertEquals(false, ONE.neg().isGreaterThan(ONE.neg()));

      assertEquals(true, ZERO.isGreaterOrEqual(ZERO));
      assertEquals(false, ZERO.isGreaterOrEqual(ONE));
      assertEquals(true, ZERO.isGreaterOrEqual(ONE.neg()));
      assertEquals(true, ONE.isGreaterOrEqual(ZERO));
      assertEquals(true, ONE.isGreaterOrEqual(ONE));
      assertEquals(true, ONE.isGreaterOrEqual(ONE.neg()));
      assertEquals(false, ONE.neg().isGreaterOrEqual(ZERO));
      assertEquals(false, ONE.neg().isGreaterOrEqual(ONE));
      assertEquals(true, ONE.neg().isGreaterOrEqual(ONE.neg()));

      assertEquals(false, ZERO.isLessThan(ZERO));
      assertEquals(true, ZERO.isLessThan(ONE));
      assertEquals(false, ZERO.isLessThan(ONE.neg()));
      assertEquals(false, ONE.isLessThan(ZERO));
      assertEquals(false, ONE.isLessThan(ONE));
      assertEquals(false, ONE.isLessThan(ONE.neg()));
      assertEquals(true, ONE.neg().isLessThan(ZERO));
      assertEquals(true, ONE.neg().isLessThan(ONE));
      assertEquals(false, ONE.neg().isLessThan(ONE.neg()));

      assertEquals(true, ZERO.isLessOrEqual(ZERO));
      assertEquals(true, ZERO.isLessOrEqual(ONE));
      assertEquals(false, ZERO.isLessOrEqual(ONE.neg()));
      assertEquals(false, ONE.isLessOrEqual(ZERO));
      assertEquals(true, ONE.isLessOrEqual(ONE));
      assertEquals(false, ONE.isLessOrEqual(ONE.neg()));
      assertEquals(true, ONE.neg().isLessOrEqual(ZERO));
      assertEquals(true, ONE.neg().isLessOrEqual(ONE));
      assertEquals(true, ONE.neg().isLessOrEqual(ONE.neg()));
   }
}

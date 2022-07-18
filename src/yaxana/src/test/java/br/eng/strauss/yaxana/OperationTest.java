package br.eng.strauss.yaxana;

import static java.lang.Math.PI;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class OperationTest extends YaxanaTest
{

   @Test
   public void test()
   {

      assertEquals(PI * PI, Robust.valueOf(PI).sqr().doubleValue(), 0d);
      assertEquals(Math.sqrt(PI), Robust.valueOf(PI).sqrt().doubleValue(), 0d);
      assertEquals(1d / PI, Robust.valueOf(PI).inv().doubleValue(), 0d);
   }
}

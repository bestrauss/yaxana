package br.eng.strauss.yaxana;

import static br.eng.strauss.yaxana.Robust.ONE;
import static br.eng.strauss.yaxana.Robust.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class RobustSimplificationTest extends YaxanaTest
{

   @Test
   public void testValueOf()
   {

      assertEquals(ZERO, Robust.valueOf(0d));
      assertEquals(ZERO, Robust.valueOf("0"));
   }

   @Test
   public void testAdd()
   {

      assertEquals(value, value.add(ZERO));
      assertEquals(value, ZERO.add(value));
   }

   @Test
   public void testSub()
   {

      assertEquals(value, value.sub(ZERO));
      assertEquals(value.neg(), ZERO.sub(value));
   }

   @Test
   public void testMul()
   {

      assertEquals(ZERO, value.mul(ZERO));
      assertEquals(ZERO, ZERO.mul(value));
   }

   @Test
   public void testDiv()
   {

      try
      {
         value.div(ZERO);
         fail();
      }
      catch (final DivisionByZeroException e)
      {
      }
      assertEquals(ZERO, ZERO.div(value));
   }

   @Test
   public void testNeg()
   {

      assertEquals(ZERO, ZERO.neg());
   }

   @Test
   public void testAbs()
   {

      assertEquals(ZERO, ZERO.abs());
      assertEquals(value, value.abs());
   }

   @Test
   public void testPow()
   {

      assertEquals(ONE, ZERO.pow(0));
      assertEquals(ZERO, ZERO.pow(1));
      assertEquals(ONE.div(value), value.pow(-1));
      assertEquals(ONE, value.pow(0));
      assertEquals(ONE, ONE.pow(0));
   }

   @Test
   public void testRoot()
   {

      try
      {
         value.root(0);
         fail();
      }
      catch (final DivisionByZeroException e)
      {
      }
      assertEquals(ZERO, ZERO.root(1));
      assertEquals(ONE.div(value), value.root(-1));

      assertEquals(ONE, ZERO.pow(0));
      assertEquals(ONE.div(value), value.pow(-1));
      assertEquals(ONE, value.pow(0));
      assertEquals(ONE, ONE.pow(0));
   }

   @Test
   public void testExample()
   {

      final String input = "-1*1/\\3*1";
      final String desired = "-1/\\3*1";
      final String actual = Robust.valueOf(input).toString();
      assertEquals(desired, actual);
   }

   private final Robust value = Robust.valueOf("\\2+\\3");
}

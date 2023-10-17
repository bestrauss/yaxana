package br.eng.strauss.yaxana;

import static br.eng.strauss.yaxana.Robust.ONE;
import static br.eng.strauss.yaxana.Robust.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.exc.DivisionByZeroException;
import br.eng.strauss.yaxana.exc.NegativeRadicandException;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

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

      {
         final String input = "-1*1/\\3*1";
         final String desired = "-1/\\3";
         final String actual = Robust.valueOf(input).toString();
         assertEquals(desired, actual);
      }
      {
         final String input = "-1/1";
         final String desired = "-1";
         final String actual = Robust.valueOf(input).toString();
         assertEquals(desired, actual);
      }
      {
         final Robust desired = Robust.MINUS_ONE;
         final Robust actual = Robust.valueOf(1d).neg();
         assertTrue(desired == actual);
      }
      {
         final Robust desired = Robust.MINUS_ONE;
         final Robust actual = Robust.valueOf(-1d).root(3);
         assertTrue(desired == actual);
      }
      {
         assertThrowsExactly(NegativeRadicandException.class, () -> Robust.valueOf(-1d).sqrt());
      }
   }

   @Test
   public void testExample2()
   {

      final String input = "root(2^32-1, 32)-2";
      final String simplified = "root(4294967295, 32)-2";
      try
      {
         Robusts.setSimplification(false);
         final Robust r = Robust.valueOf(input);
         assertEquals(input, r.toString());
      }
      finally
      {
         Robusts.setSimplification(true);
      }
      {
         assertEquals(true, Robust.simplification);
         final Robust r = Robust.valueOf(input);
         assertEquals(simplified, r.toString());
      }
   }

   private final Robust value = Robust.valueOf("\\2+\\3");
}

package br.eng.strauss.yaxana.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.epu.Algebraic;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class StringifierTest extends YaxanaTest
{

   @Test
   public void test()
   {

      final Algebraic a = new Algebraic(2);
      final Algebraic b = new Algebraic(3);
      final Algebraic c = new Algebraic(5);
      final Algebraic d = new Algebraic(7);
      {
         final Algebraic value = a.add(b).add(c);
         assertEquals("2+3+5", value.toString());
         assertIO(value);
         assertEquals("10", value.approximation(1).toString());
         assertEquals(new Algebraic("(2+3)+5"), new Algebraic("2+3+5"));
      }
      {
         final Algebraic value = a.add(b.add(c));
         assertEquals("2+(3+5)", value.toString());
         assertIO(value);
         assertEquals("10", value.approximation(1).toString());
      }
      {
         final Algebraic value = a.add(b.add(c.add(d)));
         assertEquals("2+(3+(5+7))", value.toString());
         assertIO(value);
         assertEquals("17", value.approximation(1).toString());
      }
      {
         final Algebraic value = a.sub(b).sub(c);
         assertEquals("2-3-5", value.toString());
         assertIO(value);
         assertEquals("-6", value.approximation(1).toString());
      }
      {
         final Algebraic value = a.sub(b.sub(c));
         assertEquals("2-(3-5)", value.toString());
         assertIO(value);
         assertEquals("4", value.approximation(1).toString());
      }
   }

   @Test
   public void testExample()
   {

      final String input = "-1*(1/\\3)*1";
      {
         final String desired = "-1*1/\\3*1";
         final Algebraic value = new Algebraic(input);
         assertEquals(desired, value.toString());
      }
      {
         final String desired = "-(1/\\3)";
         final Robust value = Robust.valueOf(input);
         assertEquals(desired, value.toString());
      }
   }

   private static void assertIO(final Algebraic a)
   {

      final Algebraic b = new Algebraic(a.toString());
      assertTrue(a.astEquals(b));
   }
}

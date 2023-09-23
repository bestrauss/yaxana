package br.eng.strauss.yaxana;

import static br.eng.strauss.yaxana.Robust.ONE;
import static br.eng.strauss.yaxana.Robust.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.tools.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class RobustHashcodeEqualsTest extends YaxanaTest
{

   @Test
   public void test()
   {

      final int count = 68_000;
      final Map<Integer, Robust> map = new HashMap<>();
      final Supplier<Robust> sequence = Robusts.randomSequence(10);
      for (int k = 0; k < count; k++)
      {
         final Robust value = sequence.get();
         final int hashCode = value.hashCode();
         if (map.containsKey(hashCode))
         {
            format("%s\n", map.get(hashCode));
            format("%s\n", value);
            assertFalse(value.equals(map.get(hashCode)));
         }
         map.put(hashCode, value);
      }
      assertEquals(count, map.size());
   }

   @Test
   public void test_equals()
   {

      assertFalse(Robust.valueOf(0d).equals(null));
      assertFalse(Robust.valueOf(1d).equals(new Object()));
      assertTrue(Robust.valueOf(1d).equals(ONE));
      assertTrue(Robust.valueOf(1d).hashCode() == ONE.hashCode());
      assertFalse(Robust.valueOf(1d).equals(ZERO));
      {
         final Robust a = Robust.valueOf("\\2+\\3+\\(5+2*\\6)");
         assertEquals(a, a);
      }
      assertNotEquals(Robust.valueOf("\\2+\\3"), Robust.valueOf("\\3+\\2"));
   }

   @Test
   public void testHashCode()
   {

      {
         final Robust a = Robust.ZERO;
         final Robust b = Robust.ZERO;
         assertEquals(a.hashCode(), b.hashCode());
      }
      {
         final Robust a = Robust.ONE;
         final Robust b = Robust.ONE;
         assertEquals(a.hashCode(), b.hashCode());
      }
      {
         final Robust a = Robust.ZERO;
         final Robust b = Robust.ONE;
         assertNotEquals(a.hashCode(), b.hashCode());
      }
      {
         final Robust a = Robust.valueOf("\\2+\\3+\\(5+2*\\6)");
         final Robust b = Robust.valueOf("\\2+\\3+\\(5+2*\\6)");
         assertEquals(a.hashCode(), b.hashCode());
      }
      {
         final Robust a = Robust.valueOf("\\2+\\3+\\(5+2*\\6)");
         final Robust b = Robust.valueOf("\\2+\\3+\\(5+2*6)");
         assertNotEquals(a.hashCode(), b.hashCode());
      }
   }
}

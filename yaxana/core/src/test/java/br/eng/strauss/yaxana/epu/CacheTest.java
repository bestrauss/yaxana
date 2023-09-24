package br.eng.strauss.yaxana.epu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.Robusts;
import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class CacheTest extends YaxanaTest
{

   @Test
   public void testMaximumSize()
   {

      final Cache cache = Cache.getInstance();
      assertEquals(1_000_000, cache.getMaximumSize());
      final int size = 1024;
      cache.setMaximumSize(size);
      assertEquals(size, cache.getMaximumSize());
   }

   @Test
   public void testPut()
   {

      final Cache cache = Cache.getInstance();
      assertEquals(0, cache.noOfEntries());
      final Robust[] array = new Robust[1000];
      final Supplier<Robust> sequence = Robusts.randomSequence(10);
      int size = 0;
      for (int k = 0; k < array.length; k++)
      {
         array[k] = sequence.get();
         if (k >= array.length / 2)
         {
            size += array[k].noOfNodes();
         }
      }
      Robusts.setMaximumCacheSize(size);
      assertEquals(1924, cache.noOfEntries());
      for (int k = 0; k < array.length; k++)
      {
         cache.put(array[k]);
      }
      for (int k = 0; k < array.length / 2; k++)
      {
         assertNull(cache.get(array[k]));
      }
      for (int k = array.length / 2; k < array.length; k++)
      {
         assertTrue(array[k] == cache.get(array[k]));
      }
   }

   @Test
   public void testGet()
   {

      final Cache cache = Cache.getInstance();
      final Robust[] array = new Robust[20];
      int maxSize = 0;
      final Supplier<Robust> sequence = Robusts.randomSequence(10);
      for (int k = 0; k < array.length; k++)
      {
         array[k] = sequence.get();
         maxSize += array[k].noOfNodes();
      }
      Robusts.setMaximumCacheSize(maxSize / 2);
      for (int k = 0; k < array.length; k++)
      {
         cache.put(array[k]);
      }
      final Robust mySpecialRobust = array[1];
      cache.put(mySpecialRobust);
      for (int k = 1; k < array.length; k++)
      {
         cache.put(array[k]);
         cache.get(mySpecialRobust);
      }
      assertTrue(mySpecialRobust == cache.get(mySpecialRobust));
   }
}

package br.eng.strauss.yaxana;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class RobustsTest extends YaxanaTest
{

   @Test
   public void testSetGetMaximumCacheSize()
   {

      Robusts.setMaximumCacheSize(24);
      assertEquals(24, Robusts.getMaximumCacheSize());
   }

   @Test
   public void testVintageMode()
   {

      Robusts.setVintageMode(true);
      Robusts.setVintageMode(false);
   }
}

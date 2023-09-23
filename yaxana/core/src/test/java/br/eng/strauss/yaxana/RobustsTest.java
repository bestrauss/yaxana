package br.eng.strauss.yaxana;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

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
}

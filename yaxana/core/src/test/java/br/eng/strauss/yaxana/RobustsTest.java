package br.eng.strauss.yaxana;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Formatter;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.unittest.YaxanaTest;

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
   public void testPrintCache()
   {

      Robusts.printCache(" ", null);
      try (Formatter f = new Formatter())
      {
         Robusts.printCache(" ", f);
      }
   }

   @Test
   public void testTerminalPattern()
   {

      final String pattern = Robusts.terminalPattern().pattern();
      assertEquals("((-?0x[_0-9a-f]+([.][_0-9a-f]*)?|-?0x[_0-9a-f]*[.]([_0-9a-f]+)?)(([ep])([+-]?[0-9]+))?|(-?[_0-9]+([.][_0-9]*)?|-?[_0-9]*[.]([_0-9]+)?)(([ep])([+-]?[0-9]+))?)",
                   pattern);
   }
}

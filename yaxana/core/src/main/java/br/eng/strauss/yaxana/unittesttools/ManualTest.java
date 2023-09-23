package br.eng.strauss.yaxana.unittesttools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.big.Rounder;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class ManualTest extends YaxanaTest
{

   /** Test for transient experiments. */
   @Test
   public void test()
   {

      final Rounder rounder = new Rounder(8);
      final BigFloat value = new BigFloat(0xF).add(new BigFloat(0xFP-4), rounder);
      assertEquals(new BigFloat(0x1.FEP+3), value);
   }
}

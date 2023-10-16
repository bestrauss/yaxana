package br.eng.strauss.yaxana;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.MathContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class DivisionIntervalBugFixTest extends YaxanaTest
{

   @BeforeEach
   public void beforeEach()
   {

      Robust.assertSaneIntervals = true;
   }

   @AfterEach
   public void afterEach()
   {

      Robust.assertSaneIntervals = false;
   }

   @Test
   public void testIntervalProblem()
   {

      final String syb = "-0x1.34";
      final String syp = """
            ((-0x1.34+-0x1.AEDCP+2/0x1.7402P+3*-0x1.BP+1+1/\\0x1.7402P+3*-0x1.BP+1-(-0x1.34+-0x1.AEDCP+2/0x1.7402P+3*-0x1.BP+1))
              *(0x1.61P+2-(0x1.61P+2+-0x1.AEDCP+2/0x1.7402P+3*0x1.FP-2))
              -(0x1.61P+2+-0x1.AEDCP+2/0x1.7402P+3*0x1.FP-2+1/\\0x1.7402P+3*0x1.FP-2-(0x1.61P+2+-0x1.AEDCP+2/0x1.7402P+3*0x1.FP-2))
            *-(-0x1.34+-0x1.AEDCP+2/0x1.7402P+3*-0x1.BP+1))/(1/0x1.7C*0x1.7C*(0x1.61P+2+-0x1.AEDCP+2/0x1.7402P+3*0x1.FP-2+1
              /\\0x1.7402P+3*0x1.FP-2-(0x1.61P+2+-0x1.AEDCP+2/0x1.7402P+3*0x1.FP-2)))
             /0x1.7C*0x1.7C
            """;
      final Robust ryb = Robust.valueOf(syb);
      final Robust ryp = Robust.valueOf(syp);
      // ryb und ryp sind gleich, es wird aber nicht richtig getestet, weil das Interval von ryp
      // den wahren Wert nicht enthält.
      final MathContext mc = new MathContext(128);
      System.out.format("b.y = %s\n", ryb.toBigFloat(330).toString(mc));
      System.out.format("p.y = %s\n", ryp.toBigFloat(330).toString(mc));
      assertTrue(ryb.isEqualTo(ryp));
   }
}

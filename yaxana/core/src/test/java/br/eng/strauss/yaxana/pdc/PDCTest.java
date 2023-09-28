package br.eng.strauss.yaxana.pdc;

import static br.eng.strauss.yaxana.pdc.ApproximationType.FRACTIONAL_DIGITS;
import static br.eng.strauss.yaxana.pdc.ApproximationType.SIGNIFICANT_DIGITS;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.big.BigFloat;
import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * @author Burkhard Strauß
 * @since 2023-09
 */
public final class PDCTest
{

   @Test
   public void pimpCoverage()
   {

      final String value = "\\7P-200";
      BigFloat approx;
      {
         final Algebraic a = new Algebraic(value);
         PDC.ensurePrecision(a, 52, SIGNIFICANT_DIGITS);
         approx = a.approximation();
      }
      {
         final Algebraic a = new Algebraic(value);
         PDC.ensurePrecision(a, 1, SIGNIFICANT_DIGITS);
         assertTrue(a.approximation().compareTo(BigFloat.ZERO) > 0);
      }
      {
         final Algebraic a = new Algebraic(value);
         PDC.ensurePrecision(a, 1, FRACTIONAL_DIGITS);
         assertTrue(a.approximation().compareTo(approx.mul(BigFloat.twoTo(-97))) > 0);
      }
   }
}

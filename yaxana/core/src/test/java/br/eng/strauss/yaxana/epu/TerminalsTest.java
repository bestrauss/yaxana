package br.eng.strauss.yaxana.epu;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.unittesttools.WithAlgorithms;
import br.eng.strauss.yaxana.unittesttools.YaxanaTest;

/**
 * Tests constant values using all EPUs.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class TerminalsTest extends YaxanaTest
{

   @WithAlgorithms
   @Test
   public void test()
   {

      test(new Algebraic("0"), 0);
      test(new Algebraic("1"), 1);
      test(new Algebraic("-1"), -1);
      test(new Algebraic("1p-1"), 1);
      test(new Algebraic("-1p-1"), -1);
      test(new Algebraic("1p+1"), 1);
      test(new Algebraic("-1p+1"), -1);
      test(new Algebraic("1p-100"), 1);
      test(new Algebraic("-1p-100"), -1);
      test(new Algebraic("1p+100"), 1);
      test(new Algebraic("-1p+100"), -1);
      test(new Algebraic("19015734"), 1);
      test(new Algebraic("-19015734"), -1);
   }

   private void test(final Algebraic q, final int signum)
   {

      assertTrue(q.signum() == signum);
   }
}

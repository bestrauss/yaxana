package br.eng.strauss.yaxana.epu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.anno.WithAllEPUs;

/**
 * Tests constant values using all EPUs.
 * 
 * @author Burkhard Strauss
 * @since August 2017
 */
public final class TerminalsTest extends YaxanaTest
{

   @WithAllEPUs
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

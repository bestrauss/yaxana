package br.eng.strauss.yaxana.epu.conj;

import static br.eng.strauss.yaxana.tools.YaxanaTest.format;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.epu.Algebraic;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class ConjugatesOfAnExpressionTest
{

   @Test
   public void test()
   {

      test("(\\999-\\998)-(\\998-\\997)");
      test("(\\999-\\998)-(\\999-\\998)");
      test("\\2+\\3-\\(5+2*\\7)");
      test("\\2+\\3-\\(5+2*\\6)");
      test("\\2+\\3-\\(5+2*\\5)");
      test("\\2+\\3+\\5");
      test("\\2+\\3");
      test("\\2-\\3");
      test("\\3-\\5");
      test("\\999999-\\999998");
      test("(\\999999-\\999998)-(\\999998-\\999997)");
   }

   private static void test(final String expression)
   {

      final Algebraic value = new Algebraic(expression);
      final Algebraic productOfConjugates = value.productOfConjugates();
      // format("%s -> %s\n", value, productOfConjugates.approximation(2));
      format("%s -> %s\n", value, productOfConjugates.complexValue());
   }
}

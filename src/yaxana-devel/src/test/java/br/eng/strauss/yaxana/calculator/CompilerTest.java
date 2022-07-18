package br.eng.strauss.yaxana.calculator;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import br.eng.strauss.yaxana.Robust;
import br.eng.strauss.yaxana.test.YaxanaDevelTest;

/**
 * Tests for {@link Compiler}.
 * 
 * @author Burkhard Strauss
 * @since July 2017
 */
public class CompilerTest extends YaxanaDevelTest
{

   @Test
   public void test() throws Exception
   {

      String source = "";
      source += "y=3*(2+7)-3/\\7";
      source += "z=x+y";
      final Compiler<Robust> compiler = new Compiler<>(Robust.ZERO, source);
      final Class<Function<Robust>> clasz = compiler.compile(Robust.class);
      final Function<Robust> function = clasz.getDeclaredConstructor().newInstance();
      final Map<String, Robust> map = new HashMap<>();
      map.put("x", Robust.valueOf(10));
      function.value(map);
      format("x = %s\n", map.get("x"));
      format("y = %s\n", map.get("y"));
      format("z = %s\n", map.get("z"));
   }

   @Test
   public void bug2022T0000() throws Exception
   {

      test("a = 2^-100", "1/2^100");
      test("a = --100", "--100");
   }

   private void test(final String source, final String desired) throws Exception
   {

      final Compiler<Robust> compiler = new Compiler<>(Robust.ZERO, source);
      final Class<Function<Robust>> clasz = compiler.compile(Robust.class);
      final Function<Robust> function = clasz.getDeclaredConstructor().newInstance();
      final Map<String, Robust> map = new HashMap<>();
      function.value(map);
      final String actual = map.get("a").toString();
      assertEquals(desired, actual);
   }
}
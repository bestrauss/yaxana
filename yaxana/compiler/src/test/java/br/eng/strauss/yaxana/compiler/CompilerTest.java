package br.eng.strauss.yaxana.compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.Robust;

/**
 * @author Burkhard Strauss
 * @since July 2017
 */
public final class CompilerTest
{

   @Test
   public void test() throws Exception
   {

      final String sx = "10";
      final String sy = "3*(2+7)-3/\\7";
      final String sz = "x+y";
      final String source = String.format("y=%s\nz=%s\n", sy, sz);
      final Compiler<Robust> compiler = new Compiler<>(Robust.ZERO, source);
      final Class<Function<Robust>> clasz = compiler.compile(Robust.class);
      final Function<Robust> function = clasz.getDeclaredConstructor().newInstance();
      final Map<String, Robust> map = new HashMap<>();
      map.put("x", Robust.valueOf(sx));
      function.value(map);
      System.out.format("x = %s\n", map.get("x"));
      System.out.format("y = %s\n", map.get("y"));
      System.out.format("z = %s\n", map.get("z"));
      assertEquals(Robust.valueOf(sx), map.get("x"));
      assertEquals(Robust.valueOf("27-3/\\7"), map.get("y"));
      assertEquals(Robust.valueOf("10+(27-3/\\7)"), map.get("z"));
   }

   @Test
   public void bug2022T0000() throws Exception
   {

      test("a = 2^-100", "1P-100");
      test("a = --100", "100");
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
package br.eng.strauss.yaxana.math;

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.unittest.YaxanaTest;

/**
 * @author Burkhard Strauﬂ
 * @since 2023-09
 */
public final class ComplexTest extends YaxanaTest
{

   @Test
   public void test_Complex()
   {

      assertEquals(new Complex(0d, 0d), new Complex());
   }

   @Test
   public void test_Complex_d()
   {

      assertEquals(new Complex(1d, 0d), new Complex(1d));
   }

   @Test
   public void test_Complex_dd()
   {

      assertEquals(1d, new Complex(1d, 2d).real());
      assertEquals(2d, new Complex(1d, 2d).imag());
   }

   @Test
   public void test_Complex_Complex()
   {

      assertEquals(new Complex(2d, 3d), new Complex(new Complex(2d, 3d)));
   }

   @Test
   public void test_newArray()
   {

      final Complex[] array = Complex.newArray(5);
      assertEquals(5, array.length);
      for (int k = 0; k < 5; k++)
      {
         assertEquals(Complex.ZERO, array[k]);
      }
   }

   @Test
   public void test_hashCode()
   {

      assertEquals(0, new Complex().hashCode());
      final Set<Integer> set = new HashSet<>();
      for (int k = 0; k < 1000; k++)
      {
         set.add(newComplex().hashCode());
      }
      assertEquals(1000, set.size());
   }

   @Test
   public void test_equals()
   {

      final Complex c = new Complex(PI, E);
      assertEquals(c, c);
      assertEquals(c, new Complex(PI, E));
      assertNotEquals(c, new Complex(E, PI));
      assertNotEquals(c, new Complex(E, 0d));
      assertNotEquals(c, new Complex(0d, PI));
      assertNotEquals(c, new Complex());
      assertNotEquals(c, PI);
   }

   private Complex newComplex()
   {

      return new Complex(random.nextDouble(), random.nextDouble());
   }

   @BeforeEach
   public void beforeEach()
   {

      random = new Random();
   }

   private Random random;
}

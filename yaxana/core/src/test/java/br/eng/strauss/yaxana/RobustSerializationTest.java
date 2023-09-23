package br.eng.strauss.yaxana;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

import br.eng.strauss.yaxana.tools.YaxanaTest;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class RobustSerializationTest extends YaxanaTest
{

   @Test
   public void test() throws IOException, ClassNotFoundException
   {

      final Robust desiredValue = Robust.valueOf("\\2+\\3+\\(5+2*\\6)");
      final byte[] bytes;
      try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos))
      {
         oos.writeObject(desiredValue);
         bytes = baos.toByteArray();
      }
      final Robust actualValue;
      try (final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            final ObjectInputStream ois = new ObjectInputStream(bais))
      {
         actualValue = (Robust) ois.readObject();
      }
      final SyntaxTree<?> desiredTree = desiredValue.toSyntaxTree();
      final SyntaxTree<?> actualTree = actualValue.toSyntaxTree();
      assertEquals(desiredTree, actualTree);
   }
}

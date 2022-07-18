package br.eng.strauss.yaxana.anno;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import br.eng.strauss.yaxana.YaxanaTest;
import br.eng.strauss.yaxana.tools.ResourceBase;
import br.eng.strauss.yaxana.tools.StringTestResource;

/**
 * Marker for {@code @Test}-methods of {@link YaxanaTest}s which store desired values in files in
 * {@code src/test/resources} and use one of the interfaces based on {@link ResourceBase} to access
 * these files.
 * <p>
 * To generate the file instead of running the test, the developer temporarily sets {@code value} to
 * a non-zero value.
 * 
 * @author Burkhard Strauߟ
 * @since 04-2022
 * @see StringTestResource
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface GenerateTestResource {

   int value();
}

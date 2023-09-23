package br.eng.strauss.yaxana.test;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marker for {@code @Test}-methods of {@link YaxanaResourceTest}s which store desired values in files
 * in {@code src/test/resources} and use one of the interfaces based on {@link ResourceBase} to
 * access these files.
 * <p>
 * To generate the file instead of running the test, the developer temporarily sets {@code value} to
 * a non-zero value.
 * 
 * @author Burkhard Strauﬂ
 * @since 04-2022
 * @see ImageTestResource
 * @see StringTestResource
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface GenerateTestResource {

   int value();
}

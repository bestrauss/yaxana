/**
 * Yaxana - Compiler.
 * <p>
 * Compiles expressions into byte code.
 *
 * @author Burkhard Strauss
 * @since 09-2023
 */
module br.eng.strauss.yaxana.compiler
{

   requires static org.junit.jupiter.api;

   requires transitive br.eng.strauss.yaxana;
   requires transitive jasmin;

   exports br.eng.strauss.yaxana.compiler;
}
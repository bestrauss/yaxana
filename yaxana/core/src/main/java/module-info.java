/**
 * Yaxana - Yet another exact algebraic numbers api.
 * <p>
 * Provides {@code class} {@code Robust} {@code extends Number} for robust geometric computation.
 *
 * @author Burkhard Strauss
 * @since 03-2017
 */
module br.eng.strauss.yaxana
{

   requires static org.junit.jupiter.api;

   exports br.eng.strauss.yaxana;
   exports br.eng.strauss.yaxana.big;
   exports br.eng.strauss.yaxana.exc;

   exports br.eng.strauss.yaxana.unittest;
}
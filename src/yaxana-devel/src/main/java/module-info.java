/**
 * Yaxana-Devel - development tools for Yaxana.
 *
 * @author Burkhard Strauss
 * @since 2017-03
 */
module br.eng.strauss.yaxana.devel
{

   requires java.base;
   requires java.desktop;
   requires jasmin;
   requires br.eng.strauss.yaxana;

   exports br.eng.strauss.yaxana.calculator;
}
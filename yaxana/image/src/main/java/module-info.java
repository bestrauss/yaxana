/**
 * Yaxana - Creation of images depicting syntax trees of expressions.
 *
 * @author Burkhard Strauss
 * @since 09-2023
 */
module br.eng.strauss.yaxana.image
{

   requires static br.eng.strauss.yaxana.test;

   requires transitive java.desktop;
   requires transitive br.eng.strauss.yaxana;

   exports br.eng.strauss.yaxana.image;
}
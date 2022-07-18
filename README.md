# yaxana

Yet another exact algebraic numbers API, a Java 17 module.

 * `class Robust extends java.lang.Number`
   Immutable, thread safe, ideally exact real algebraic number type
   intended to be used for robust, exact geometric computations. 
 * Uses `double` interval arithmetic, where possible.
 * Uses a constructive root bound method
   massively sped-up by using special well-worked test expressions E'
   with `sgn` E = `sgn` E', where needed.
 * For usage see javadoc (in `./jars`) of `class br.eng.strauss.yaxana.Robust`.
 * For examples see `src/yaxana/src/test/java/br.eng.strauss.yaxana.examples`
 * For theory see: Burkhard E. Strauss: [Zum-Vorzeichentest-Algebraischer-Ausdrücke](https://github.com/bestrauss/yaxana/blob/main/docs/Zum-Vorzeichentest-Algebraischer-Ausdruecke.pdf)
 * Stable versions (binary, sources, and javadoc) will be available in `./jars`
   as soon as the first beta test version is published.


## Free Software

 * Free software: no copyright, no copyleft, no warranty.


## Building

 * clone this repository and call `gradlew build` to build.
 * call `gradlew ec` for eclipse classpath


## History

 * July 14, 2022, Version V0.6.6 - first alpha test version
    - >95% unit test coverage.
    - root/pow don't support |exponent|>2 yet 
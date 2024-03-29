# yaxana

Yet another exact algebraic numbers API, a Java 17 module.

 * `class Robust extends java.lang.Number`
 * Immutable, thread safe, ideally exact real algebraic number type
   to be used for e.g. robust, exact geometric computations. 
 * Uses `double` interval arithmetic, where possible, and a constructive root bound method, else.
 * Modifies the [BFMSS[2]](https://www.google.com/search?q="BFMSS"+"constructive+root+bound") 
   constructive root bound method to be essentially equally efficient for low degree expressions
   and massively more efficient for most typical high degree expressions.
   Makes the constructive root bound method feasible for high degree expressions in the first place.
 * For theory see: [Burkhard E. Strauss: Zum-Vorzeichentest-Algebraischer-Ausdr�cke](https://raw.githubusercontent.com/bestrauss/yaxana/main/docs/Zum-Vorzeichentest-Algebraischer-Ausdruecke.pdf)
 * For usage see javadoc of [class Robust](https://github.com/bestrauss/yaxana/blob/main/yaxana/core/src/main/java/br/eng/strauss/yaxana/Robust.java).
 * For examples see [ExampleTest.java](https://github.com/bestrauss/yaxana/blob/main/yaxana/core/src/test/java/br/eng/strauss/yaxana/examples/ExampleTest.java)
 * Stable versions (binary, sources, and javadoc) will be available in `./jars`
   as soon as the first beta test version is published.


## Free Software

 * Free software: no copyright, no copyleft, no warranty.


## Building

 * (fork and) clone this repository 
 * use `gradlew build` to build jars (including `*-sources.jar` and `*-javadoc.jar`)
 * use `gradlew eclipse` to generate eclipse configuration files

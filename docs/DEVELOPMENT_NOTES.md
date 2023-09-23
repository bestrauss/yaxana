## Quality checks

 * `eclipseClean eclipse`
 * `clean build`
 * no javadoc warnings
 * view the javadoc
 * no TODOs in sources
 * squash commits, commit message equals branch name, Yax-12345-title-of-ticket



## Unit tests

 * In project `yaxana-core` a file  `br.eng.strauss.yaxana.user.prefs` 
   allows for configurations. The file is generated in case it is not present and valid.



## Eclipse

 * Eclipse [2023-09 (4.29.0)] has a bug and doesn't run tests out of the box.
   Workaround: Additional VM-args in every Run-/Debug-Configuration:
   ```
    --add-exports=org.junit.platform.commons/org.junit.platform.commons.util=ALL-UNNAMED
    --add-exports=org.junit.platform.commons/org.junit.platform.commons.logging=ALL-UNNAMED
   ```
   See also: [edvpfau.de](https://www.edvpfau.de/junit5-debugging-mit-eclipse-und-gradle/)
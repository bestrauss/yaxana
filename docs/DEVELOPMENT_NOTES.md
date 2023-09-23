## Eclipse

 * Eclipse [2023-09 (4.29.0)] has a bug and doesn't run tests out of the box.
   Workaround: Additional VM-args in every Run-/Debug-Configuration:
   ```
    --add-exports=org.junit.platform.commons/org.junit.platform.commons.util=ALL-UNNAMED
    --add-exports=org.junit.platform.commons/org.junit.platform.commons.logging=ALL-UNNAMED
   ```
   See also: [edvpfau.de](https://www.edvpfau.de/junit5-debugging-mit-eclipse-und-gradle/)
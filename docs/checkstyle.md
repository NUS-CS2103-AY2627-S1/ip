# Check Java coding style

Use Java 25 for Gradle, as for the rest of this project. On this Windows machine:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-25.0.4.1'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\gradlew.bat checkstyleMain checkstyleTest
```

Adjust the JDK path on other machines. On macOS/Linux use `./gradlew` instead.
To run the style checks together with all unit tests and packaging:

```powershell
.\gradlew.bat clean check build
```

Gradle's Checkstyle plugin attaches both source sets to `check`, which also runs
the tests. Errors and warnings both fail the build; fix the reported source
instead of weakening rules. HTML reports are in
`build/reports/checkstyle/main.html` and `build/reports/checkstyle/test.html`;
the corresponding XML files support automated inspection.

## Rule provenance

The [course Checkstyle tutorial](https://se-education.org/guides/tutorials/checkstyle.html)
recommends the AddressBook Level 3 configuration for the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Both checked-in configuration files were copied unchanged on 2026-10-06 from:

- [AB3 checkstyle.xml](https://github.com/se-edu/addressbook-level3/blob/master/config/checkstyle/checkstyle.xml)
- [AB3 suppressions.xml](https://github.com/se-edu/addressbook-level3/blob/master/config/checkstyle/suppressions.xml)

These local files are the reproducible rule set; builds do not fetch the latest
AB3 rules. They cover naming, imports, braces, indentation, whitespace,
120-character lines, and Javadoc. The only suppressions are AB3's existing
test-file exceptions for `JavadocType` and `MissingJavadocMethodCheck`.
No project-specific suppressions were added.

Checkstyle is pinned to **14.3.0**. The
[official compatibility information](https://checkstyle.org/#JRE_and_JDK)
supports JDK 21 and later for Checkstyle 14.x and Java 25 language features.
Use the same version and local configuration if enabling Checkstyle in an IDE.

## Verification

The initial integration passes checks on all 12 production and 5 test source
files and all 36 unit tests. A separate ignored scratch fixture with an opening
brace on the wrong line was rejected with `LeftCurly`, confirming that even a
warning fails the style gate. The fixture is not part of the application or its
test sources.

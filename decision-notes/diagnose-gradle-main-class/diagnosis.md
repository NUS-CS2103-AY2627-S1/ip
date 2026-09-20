# Diagnose Gradle IDE run failure

## Diagnosis

The pasted output does not contain an IDE or Gradle error; it contains output
from unrelated Git-Mastery exercises. The repository configuration does expose
the likely issue:

```groovy
application {
    mainClass = 'seedu.duke.Duke'
}
```

The actual application entry point is `src/main/java/Duchess.java`, which
declares the default-package class `Duchess`, not `seedu.duke.Duke`.

Running the exact application task confirmed:

```text
Error: Could not find or load main class seedu.duke.Duke
Caused by: java.lang.ClassNotFoundException: seedu.duke.Duke
```

## Recommended fix

If the project should keep its current structure, change the Gradle
configuration to:

```groovy
application {
    mainClass = 'Duchess'
}
```

Then refresh the Gradle project in IntelliJ and run the Gradle `run` task again.
An IntelliJ Application run configuration should likewise use `Duchess` as
its main class.

## Alternative fix

To keep `seedu.duke.Duke`, the application would need a larger migration:
rename the entry-point class to `Duke`, add the `seedu.duke` package, move source
files into the matching directory, and update references. That is not the
minimal fix for this repository.

## Verification

- `./gradlew test` succeeds under Java 25.0.3, but reports `test NO-SOURCE`
  because no Gradle test sources exist.
- `./gradlew run` fails only because the configured main class does not exist.
- No existing project files were modified while recording this diagnosis.

## Suggested commit message

```text
Fix Gradle application main class
```


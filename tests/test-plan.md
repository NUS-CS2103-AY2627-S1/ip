# C-DetectDuplicates acceptance checks

Run `gradlew check shadowJar` using Java 25. For CLI checks, run
`java -cp <absolute-path-to-build/libs/orbit.jar> orbit.Orbit` in a fresh temporary
directory so that the test does not touch your normal task data.

1. Add `todo Read book`, mark it done with `mark 1`, then add
   `deadline Report /by 2026-09-15` and
   `event Meeting /from Mon 2pm /to Mon 3pm`. Exit with `bye`.
2. Record the hash of `data/orbit.txt` and restart the CLI in the same directory.
3. Enter `todo read BOOK`, `deadline REPORT /by 2026-09-15`, and
   `event meeting /from MON 2PM /to MON 3PM`.
   Each must report `OOPS!!! This task already exists. Use list to find it, or unmark it to reopen it.`
4. Confirm `list` still contains three tasks and the saved file hash is unchanged.
5. Add `deadline Report /by 2026-09-16`,
   `event Meeting /from Mon 2pm /to Mon 4pm`, and `todo Report`.
   All must succeed because their date, end time, or type differs.
6. Run `delete 1`, then `todo Read book`. Re-adding must succeed; six tasks remain.

Automated unit tests additionally cover surrounding whitespace, significant
internal whitespace, both event endpoints, distinct descriptions, and comparison
in both directions across task types. Existing duplicate records are preserved
when loading; only new duplicate additions are rejected.

Validation: all 10 JUnit tests and both Checkstyle tasks passed. Packaged CLI
steps above passed, including the saved-file hash comparison across restart.
The GUI uses the same `Orbit.getResponse` addition path; GUI interaction was not
separately exercised.

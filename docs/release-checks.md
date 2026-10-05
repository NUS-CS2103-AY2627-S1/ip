# Release Checks: v0.3

Checked on 18 September 2026 against the course's
[iP grading criteria](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-grading.html) and
[final submission instructions](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w6.html).
This is an evidence checklist, not a guarantee of a grade.

## Implementation and Documentation

- Level-0 through Level-10 tags and the required A-increment tags are present on the remote.
- Task inheritance, separate parser/storage/UI classes, exception handling, assertions, and public Javadocs exist.
- JUnit covers parsing, storage, task operations, date validation, and GUI response logic.
- New regression tests cover pipe-character rejection, corrupt-file protection, failed-save rollback,
  recurrence persistence and duplicate prevention, calendar boundaries, editing, and completed reminders.
- A-BetterGui, A-Personality, A-MoreErrorHandling, and A-MoreTesting are implemented and tagged.
- BCD-Extension is tagged; sorting, recurrence, and reminders are implemented and documented.
- The product guide covers all commands, date formats, persistence, errors, and recurrence semantics.
- GitHub Pages is configured for master/docs; docs/Ui.png contains one complete app window.
- The release contains one Gradle-built fat JAR, not multiple platform-specific assets.

## Smoke Testing

- macOS 26.6.2, Apple Silicon, plain Temurin Java 25.0.3 (without bundled JavaFX): launch, task creation,
  description editing, recurrence, invalid input, Enter/Send, and restart persistence passed.
- Intel Mac, Windows, and Linux execution of v0.3 has not been verified locally. Their native libraries are
  bundled. Windows and Linux testers reported successful launch of v0.2 in forum issue #403.
- Native Mac libraries are stored by architecture and extracted at startup. The loader integration was checked
  against [OpenJFX NativeLibLoader](https://github.com/openjdk/jfx/blob/jfx17/modules/javafx.graphics/src/main/java/com/sun/glass/utils/NativeLibLoader.java).

## Project Management Limits

- Recent commit subjects use imperative, capitalized summaries; release fixes include explanatory bodies.
- Repository history shows work in weeks 3, 4, 5, and 6 (24 August through 18 September).
- Historical submission timeliness, required peer reviews, Git-Mastery activities, and the student's exact
  dashboard row still require confirmation against course records. Existing history has not been rewritten.
- Full marks require all grading bars, including project management. Passing tests alone cannot establish that.

## Feedback Addressed

See [forum issue #403](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/403).
Thanks to DhruvBawa and MinhHoangLeNUS for identifying persistence, recurrence, greeting, and editing issues,
and zxtay3 for macOS smoke testing.

# A-MoreTesting plan and coverage map

## Automated checks

Run `./gradlew check` (Windows: `.\gradlew.bat check`) with Java 25.
JUnit dynamic tests give each data-driven scenario a separate result. Each scenario creates its own tasks
and temporary save file, so a failure cannot hide later cases or affect personal data.

| Area | Suite | Additional checks |
| --- | --- | --- |
| Task positions | `TaskListBoundaryTest` | First/middle/last get and delete; invalid positions; empty list; renumbering |
| Date searches | `TaskListBoundaryTest` | Start/end boundaries, adjacent excluded dates, same-day events, multiple matches |
| Restart workflows | `PersistenceWorkflowTest` | Add/mark/unmark/delete across restarts; mixed task fields; delete all; reuse shifted numbers |
| Malformed records | `StorageCorruptionTest` | Unknown type, bad status, missing fields, blank lines; no overwrite; repair/restart |
| Save recovery | `SaveRecoveryTest` | Failed mutation then successful retry; exactly one change; no temporary-file leftovers |
| Save advice | `SaveRecoveryTest` | Permission denial, unsupported atomic replacement, generic failure, original exception cause |
| Task-number input | `TaskNumberTest` | Missing, decimal, text, negative, zero, out-of-range and overflowing numbers |
| Text and languages | `TextSearchTest` | Multiword search, literal punctuation, Chinese, emoji, English/Chinese Java locales |
| Response facts | `ResponseAccuracyTest` | Task descriptions/counts, addition singular/plural, empty results, error recovery |
| Existing error scenarios | `MoreErrorHandlingTest` | Invalid command cases now report independently |

Response tests focus on factual details and recovery instructions. They do not freeze every motivational sentence.
Injected I/O exceptions are deterministic; they do not change real file permissions or fill the user's disk.

## Manual environment checklist

These items remain unchecked until performed on the stated environment. Java locale tests do not substitute
for changing the OS language or checking fonts and input methods on a real desktop.

| Environment | Procedure | Expected outcome | Result |
| --- | --- | --- | --- |
| Windows / macOS / Linux | Record OS and Java version; launch the packaged JAR; add, search, mark and restart | App opens and saved tasks survive; controls remain usable | Not run for this increment |
| 100%, 125%, 150% scaling | At each scale, resize to the minimum window and maximize | Commands, error text, dates, buttons and clear control remain readable and reachable | Not run |
| Small and large displays | Try 1366×768 and 1920×1080 where available | Window fits usable screen area; history scrolls; input remains accessible | Not run |
| Long content | Add a long description and many valid tags; narrow the window | Text wraps without overlapping controls; full task remains accessible | Not run |
| Keyboard | Navigate with Tab/Shift+Tab; submit with Enter; correct a missing date; activate clear | Logical focus order; error draft stays editable; clear affects only the draft | Not run |
| English / Chinese OS | Enter Chinese with an input method; add emoji; save, restart and search | Text survives unchanged and is legible; ISO date input still works | Not run |
| Error presentation | Invalid date in Add task; invalid inline tag; invalid typed command | Form/editor preserves input; typed error hides board; View tasks restores it | Not run |

## CI and interpreting results

The existing CI workflow runs Gradle checks on Ubuntu, macOS, and Windows. Inspect each actual job result after
pushing; a local pass does not establish a pass on the other operating systems. The GUI suite skips itself when
there is no display. Always report skipped tests separately.

The coverage map above describes scenarios, not an instrumented line/branch coverage percentage. No coverage
percentage or claim of complete code coverage should be inferred from the test count.

# iP review through A-Personality

Scope: Weeks 2–5 and Week 6 through A-Personality only. Later Week 6
increments, final submission, and Weeks 7–8 are outside this review.

## Requirement review

| Requirements | Code evidence and action |
| --- | --- |
| Week 2: Levels 0–2, naming, greeting, command loop, add/list | Duchess name and command loop exist. Early echo-only behavior is superseded by task commands. Console regression cases cover the current behavior. |
| Week 2: Level 3, A-Classes | Task encapsulates status; mark/unmark update it. Covered by Task, TaskList, and command tests. |
| Week 2: Level 4, A-Inheritance | Todo, Deadline, Event extend Task. Gap corrected: events now support separate start/end dates. |
| Week 2: Level 5, A-Exceptions | Parser rejects malformed input using DuchessException; Duchess returns errors. Added range validation for the new syntax. |
| Week 2: Level 6, A-Collections, A-Enums | TaskList uses ArrayList; delete and reindexing are tested; TaskType supplies task icons. |
| Week 3: Levels 7–8 | Storage automatically saves changes and loads tasks, creates missing directories, and uses relative paths. Deadline already uses LocalDate. New event ranges now use LocalDate too, with a different display format. Persistence tests cover both range and legacy events. |
| Week 3: A-MoreOOP, A-Packages | Ui, Storage, Parser, TaskList and task subclasses occupy suitable duchess packages. |
| Week 3: A-Gradle, A-JUnit, A-Jar | Gradle builds, runs tests, and creates a fat JAR. Java 25 JUnit tests exercise the core parsing, task, statistics, storage, and command behavior. |
| Week 3: A-JavaDoc, A-CodingStandard | Public application classes/methods are documented. Changed code reviewed against the project skill and checked with Checkstyle. |
| Week 4: A-CheckStyle, Level 10, A-Varargs | Checkstyle and JavaFX Launcher exist. TaskList and ConversationView use varargs. Corrected the GUI help implementation to populate the reusable FXML dialog with the shared response. Fixed GUI bye to exit. |
| Week 5: A-Assertions, A-CodeQuality, A-Streams | Assertions document internal invariants; streams serve loading, searching, and statistics. Removed duplicated GUI help content instead of adding another command table. |
| Week 5: A-CI and B/C/D extension | A cross-platform GitHub Actions workflow exists. C-Statistics implements completion totals and seven-day counts, with clock-based tests. Local tests do not establish that remote CI succeeded. |
| Week 6: A-BetterGui | Distinct user/bot styling and error highlighting exist. Corrected CSS specificity for response colors, FXML status classes, system fonts, and wrapping shortcut layout. Tested loaded FXML at 420 and 800 pixels wide. |
| Week 6: A-Personality | Retained Duchess's squad-room branding, greeting, help voice and aliases. Replaced the remaining Court status wording with Squad. |

## Design decisions

- FXML defines the reusable view. Java loads instances, supplies changing
  response text, chooses response styles, and handles actions. Runtime content
  does not require recreating the fixed layout in Java.
- Canonical events use `event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd`.
  Ranges are inclusive and same-day events are allowed, matching date precision.
- Legacy `/at` commands and saved event text remain supported. Missing dates
  are not guessed. New ranges use a six-field record; existing four/five-field
  event records retain their interpretation and completion timestamps.
- Existing documentation and help were updated for changed behavior. No new
  website, release publication, or later optional increment was undertaken.

## Verification and limits

- 61 JUnit tests pass under Zulu Java 25.0.3.fx-zulu.
- All 20 console UI-plan cases pass, including valid and invalid event ranges.
- Checkstyle passes for production and test code; Gradle shadowJar succeeds.
- A temporary JavaFX integration check loads the real FXML, fires Help and
  composer actions, checks current task responses and error styling, measures
  message layout at narrow/wide sizes, and invokes bye. Rendered snapshots
  were visually inspected. Desktop-control selection of the Java process was
  unavailable, so this is not a claim of a manual mouse/keyboard test.
- Git branches, tags, and local history were inspected. Historical remote PR
  reviews, prescribed parallel-PR workflows, tutorial completion, and weekly
  timing cannot be established by passing code tests. No history was rewritten,
  and no commits, tags, merges, or pushes were made.
- Cross-platform execution is not proven by local macOS tests.

## Sources

- [Week 2](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w2.html)
- [Week 3](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w3.html)
- [Week 4](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w4.html)
- [Week 5](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w5.html)
- [Week 6](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w6.html)
- [JavaFX Part 4](https://se-education.org/guides/tutorials/javaFxPart4.html)

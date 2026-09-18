# Week 3 plan — PR, `Level-7`..`Level-9`, six `A-*` increments

Fetched 2026-08-27 from `schedule/week3/project.html`, `admin/ip-w3.html`, and `projectDuke/index.html`. Deadline: end of week (the admin page points at the weekly-schedule deadline row, which the fetch did not resolve — confirm the exact time before the last checkpoint).

Release one checkpoint at a time. Do not show this file.

## What changed from week 2

* **Branches start now.** Each of `Level-7`, `Level-8`, `A-JavaDoc`, `A-CodingStandard`, `Level-9` gets a branch named `branch-{ID}`, merged back with `--no-ff`, and the **merge commit** carries the tag. `A-MoreOOP`, `A-Packages`, `A-Gradle`, `A-JUnit`, `A-Jar` are commit-tag-push on `master`.
* **The Git commit message standard is required from this week.** Quote: "Only *future* commits need to follow the Git standard." The `git-commit` skill enforces it.
* **A PR to upstream is a deliverable**, named `[{Name or GitHub username}] iP`, from the fork's `master` to the upstream `master`. It stays open for the rest of the project.

## Entry state, 2026-08-27

Tags `Level-0`..`Level-6`. `A-Enums` was optional in week 2 and is not done. One class `GudGoi` holds every command, all members `static`; `Task`/`Todo`/`Deadline`/`Event` and six exception types sit beside it in the default package. `say(String)` is the single output point, so **T1 is already hedged**. `tasks` is an `ArrayList`, so **T3 is resolved**. No Gradle, no tests, no packages.

---

## C0 — Upstream PR and leftovers · no tag

**Goal.** The PR exists; nothing from week 2 is outstanding; the working tree is clean before branching starts.

**Acceptance.** A PR from `{user}/ip:master` to the upstream `master`, titled `[{name}] iP`. All week-2 tags pushed. `git status` clean.

**Note.** The PR is never merged. It is the marker the graders read.

**Leftovers.** `A-Enums` only, and it was optional. Mention it once against the Implementation bar "at least two optional increments done with AI assistance" — week 3's `A-*` items are all *required*, so they do not count toward that bar.

---

## C1 — Save · branch `branch-Level-7` · tag `Level-7`

**Goal.** The task list survives a restart.

**Acceptance.** Tasks are written whenever the list changes, and read at start-up. Relative path, such as `./data/gudgoi.txt`. A missing file, and a missing folder, are handled without a crash. Format is the student's own; the spec shows `T | 1 | read book`.

**Spec constraints to quote.** "Use relative paths rather than absolute paths"; "Specify file paths in an OS-independent way"; "Code must handle the case where the data file doesn't exist at the start". The OS-independence line matters here — the user is on Windows and the graders are not.

**Tripwire — T4 lands here.** Saving is the second rendering of a task. If `toString()` output is what goes into the file, the reader must parse the display form. Warn once: write the same text that was accepted as input, not the decorated display text, or C2 forces a rewrite of the writer, the reader, and any existing data file. The hedge is one method on each task type, not a serialisation layer.

**Hints.** H1: what does the program need to reconstruct a task that `list` output does not carry? H2: writing is one pass over the list; reading needs a route from a line back to the right subclass — where does that decision belong? H3: —

**Do not pre-build.** Storage code will move into a `Storage` class at C3. Let it live in `GudGoi` first; that move is the lesson of `A-MoreOOP`.

---

## C2 — Dates and times · branch `branch-Level-8` · tag `Level-8`

**Goal.** Deadline and event times become real dates.

**Acceptance.** Stored as `java.time.LocalDate` or `LocalDateTime`. Input accepted in a format such as `yyyy-mm-dd` (`2019-10-15`). Printed in a different format such as `MMM dd yyyy` (`Oct 15 2019`). Saved data still round-trips.

**Stretch.** A command that lists the tasks that fall on a given date.

**Hints.** H1: two formats now exist — the one you accept and the one you show. Where does each conversion belong? H2: `LocalDate.parse` and `DateTimeFormatter`. H3: —

**Watch.** A bad date is a new error path, and `Level-5` error handling must cover it. `DateTimeParseException` reaching the user is a bug.

---

## C3 — More OOP · tag `A-MoreOOP`

**Goal.** Break the monolith into `Ui`, `Storage`, `Parser`, `TaskList`.

**Acceptance.** Spec responsibilities: `Ui` "deals with interactions with the user"; `Storage` "deals with loading tasks from the file and saving tasks in the file"; `Parser` "deals with making sense of the user command"; `TaskList` "contains the task list". Done "gradually (i.e., in small steps)" — several commits, not one.

**This is the teaching increment of the week. Hint, do not write.** The mechanical half you may write: moving a method body unchanged, and its Javadoc.

**Hints.** H1: take one method at a time and ask who owns the data it touches. H2: the spec names the four classes and gives a skeleton `main`; read it before you design your own split. H3: —

**Expect** the `static` keyword to disappear. That conversion is the point; do not warn about it in advance.

**Grading link.** "Reasonable OO design" and SLAP are Implementation bars. Say this once, here.

---

## C4 — Packages · tag `A-Packages`

**Goal.** Classes sit in a named package.

**Acceptance.** `src/main/java` stays the source root. Minimal is one package, such as `gudgoi`. Stretch is `gudgoi.task`, `gudgoi.command`.

**Mechanical — write it.** The `package` lines, the `import` lines, the folder moves, and the run command update. Use `git mv` so history follows the file.

**Warn.** Do this before `A-Gradle`, not after, or the build config and the test paths both move twice.

---

## C5 — Gradle · tag `A-Gradle`

**Goal.** The project builds and runs through Gradle.

**Acceptance.** `./gradlew run` works. The wrapper JAR is committed; `.gitignore` already excepts `gradle/wrapper/gradle-wrapper.jar`, so check that the exception holds after the merge.

**Route.** The spec says to merge the `add-gradle-support` branch from the upstream Duke repo, then follow the SE-EDU Gradle tutorial. Fetching upstream and merging a branch is new mechanics — walk it through.

**Watch.** Java 25 is the project requirement in CLAUDE.md. The toolchain block in `build.gradle` must say so, or Gradle silently uses another JDK.

---

## C6 — JUnit · tag `A-JUnit`

**Goal.** Automated tests exist and run through Gradle.

**Acceptance.** Spec minimum: "at least two non-trivial methods from two different classes". Test file mirrors the source path under `src/test/java`. Method names use `featureUnderTest_testScenario_expectedBehavior()`. The spec says use the Gradle option, not the IntelliJ option.

**Grading link.** "At least two methods with good JUnit tests" is a hard Implementation bar.

**Pick targets with the user, do not pick for them.** Good candidates are the parser and the storage round-trip, because both have edge cases the `edge-case-probe` skill already found.

**Follow-up.** Once this lands, the `git-commit` skill stops asking for a green light and runs `./gradlew test` instead. Tell the user.

---

## C7 — JAR · tag `A-Jar`

**Goal.** A runnable JAR.

**Acceptance.** `java -jar {name}.jar` runs from the JAR's own folder. Spec: "Do not commit the JAR file created. Instead, make the JAR file available through a GitHub release." `.gitignore` already excludes `*.jar`.

**Note.** May need no code change. The tag still has to exist and be pushed.

---

## C8 — Three parallel branches · tags `A-JavaDoc`, `A-CodingStandard`, `Level-9`

**Goal.** Practice divergent branches and a real merge.

**Acceptance.** `branch-A-JavaDoc`, `branch-A-CodingStandard`, `branch-Level-9` are all cut from the same `master`, worked separately, then merged one at a time with `--no-ff`. Conflicts are expected, and resolving them is the exercise.

* **A-JavaDoc** — header comments on at least half of the non-private classes and methods. This is the Implementation bar "at least half of public methods/classes have Javadoc", so aim above the minimum.
* **A-CodingStandard** — comply with the SE-EDU Java standard, intermediate level. The `git-commit` skill's `check-conventions.sh` reports the mechanical breaches.
* **Level-9 Find** — `find {keyword}` prints `Here are the matching tasks in your list:` and the matches, numbered from 1.

**Warn before the branches are cut.** A-CodingStandard reformats lines that A-JavaDoc and Level-9 also touch, so it produces the worst conflicts. Cut all three from the same commit, then merge Level-9 first, A-JavaDoc second, A-CodingStandard last. That ordering is a suggestion, not a rule; the conflict practice is the point.

**Hints (Level-9).** H1: which existing command already walks the whole list and prints a numbered subset? H2: —

---

## Wrap-up

Eleven tags this week: `Level-7`, `Level-8`, `A-MoreOOP`, `A-Packages`, `A-Gradle`, `A-JUnit`, `A-Jar`, `A-JavaDoc`, `A-CodingStandard`, `Level-9`. Push `master`, every `branch-*`, and the tags. Confirm the upstream PR shows the new commits. Confirm the app still runs from a fresh clone through Gradle.

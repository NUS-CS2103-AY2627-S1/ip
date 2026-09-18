# Week 6 plan — final week

Fetched 2026-09-14 from:

* <https://nus-cs2103-ay2627-s1.github.io/website/schedule/week6/project.html>
* <https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w6.html>
* <https://nus-cs2103-ay2627-s1.github.io/website/projectDuke/index.html>

**Deadline: Fri 18 Sep 2026, 2359. This is the final submission.** Four days from
the fetch date.

## Deliverables, verbatim headings

1. "Add Increments: `A-BetterGui`, `A-Personality`, `A-MoreErrorHandling`,
   `A-MoreTesting`" — do **at least two** of the four.
2. "Finalize the features" — handle common errors, the product name must not be
   `Duke`, credit all reused code.
3. "Set up a product website" — `A-UserGuide`. `docs/Ui.png` (that exact name),
   a real User Guide in `docs/README.md`, GitHub Pages on branch `master`,
   folder `/docs`.
4. "Submit the final version" — `A-Release`. Fat JAR from Gradle, smoke test it,
   GitHub release with the JAR as the only asset.

## State of the repository at fetch time

Done: `Level-0`..`Level-10`, `A-MoreOOP`, `A-Packages`, `A-Gradle`, `A-JUnit`,
`A-Jar`, `A-JavaDoc`, `A-CodingStandard`, `A-Varargs`, `A-Checkstyle`,
`A-FullCommitMessage`, `A-Assertions`, `A-CodeQuality`, `A-Streams`,
`BCD-Extension` (D-Trivia). CI runs from `.github/workflows/gradle.yml` but
carries no `A-CI` tag.

Not done: every week 6 item. `docs/README.md` is still the unedited template.
`docs/Ui.png` is absent. No GitHub release exists.

Name check passes already: the product is `GudGoi`, package `gudgoi`, JAR
`gudgoi.jar`.

## Checkpoints — release one at a time

### C1 — Pick the two increments, then do `A-Personality` first
`A-Personality` is the cheapest of the four and it feeds the other three: the
name and the phrases it fixes are the text that `A-BetterGui` styles and that the
User Guide quotes. Doing it first stops the User Guide being rewritten twice.
The avatars are still `DaDuke.jpg` and `DaUser.jpg` from the tutorial — the
picture and the credit line in `README.md` both move together.

### C2 — Second increment
`A-BetterGui` pairs naturally with C1 and its minimal bar is one item from the
list i–vi. Example (ii), a different format for errors, is worth more than
cosmetic padding, and it needs `getResponse` to report that a reply was an error —
a small change in `GudGoi.handle`, not a new layer.

`A-MoreErrorHandling` is the alternative. Real gaps in this codebase: duplicate
`/by` in one command, a `/from` later than `/to` is already caught but a
zero-length event is not, blank descriptions after trimming, an unreadable
`data/` folder, and a duplicate task.

`A-MoreTesting` is the third. `GudGoi.getResponse` is testable end to end and no
test touches it yet.

### C3 — `A-UserGuide`
`Ui.png` is a screenshot of the running window, saved at `docs/Ui.png`. The guide
states the chatbot name at the top and covers every command: `todo`, `deadline`,
`event`, `list`, `mark`, `unmark`, `delete`, `find`, the card commands, `bye`.
Benchmark given by the course is the AB3 Features section.
Pages: Settings → Pages → branch `master`, folder `/docs`. Check the rendered URL,
not the GitHub file preview.

### C4 — `A-Release`
`./gradlew clean shadowJar`, then a smoke test of `build/libs/gudgoi.jar` from an
empty folder — the JAR must create `data/` itself. Tag `A-Release`, push, then a
GitHub release `v0.2` with exactly one asset, `gudgoi.jar`.

### C5 — Grading sweep before the deadline
Re-check the bars that are verified at the end, from `roadmap.md`:

* last 5 commits comply with the commit message convention;
* at least two optional increments done with AI assistance, recorded in
  `README.md`;
* at least half of public methods and classes carry Javadoc;
* at least two methods with good JUnit tests.

## Tripwires for this week

**T6 — The User Guide written before the personality lands.** `A-Personality`
changes the chatbot name and its reply text. A guide written first has to be
re-checked line by line. Order C1 before C3.

**T7 — The screenshot taken before the GUI work.** Same cause. `Ui.png` shows the
window, so take it after `A-BetterGui`.

**T8 — A release built from an untested JAR.** The JAR runs with a different
working folder from `gradlew run`, so a save-path fault shows only in the smoke
test. Smoke test before the release, not after.

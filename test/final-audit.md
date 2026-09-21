# Final iP audit — Gronk

Reviewed on 17 September 2026 against the supplied grading bars and the current
[Week 2](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w2.html),
[Week 3](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w3.html),
[Week 4](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w4.html),
[Week 5](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w5.html), and
[Week 6](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w6.html) instructions.

## Current conclusion

The reviewed implementation and documentation satisfy the checked feature, OOP,
Javadoc, testing, and coding-quality bars. The product website and screenshot are
published and were verified on 18 September 2026. Publication is **not complete**:
the latest release still contains the old CLI JAR. Consequently this is not yet
evidence of a complete final submission or a guaranteed 15/15. Final dashboard
status remains unverified. Historical weekly credit is determined by the course.

## Deliverable evidence

| Requirement | Evidence/status |
| --- | --- |
| Levels 0–6 and enums | Gronk branding, greeting/exit, list, completion, four task types, validation, deletion, `TaskType` enum |
| Levels 7–9 | Relative-path persistence with recovery, `java.time` dates, case-insensitive search |
| Level 10 GUI | Real FXML/CSS interface, Enter/Send, scrolling, task processing, explicit error cards, resizable window |
| More OOP and packages | Separate parser, storage, UI, task, and command packages; Task and Command inheritance |
| Gradle, JAR, and JUnit | Java 25 build; dependency-inclusive executable JAR; 82 passing tests |
| Javadoc | Compiler syntax-tree audit found explicit Javadoc on 106/114 public classes, methods, and constructors (93%); remaining overrides inherit behavior |
| Coding standard / quality | Main and test Checkstyle passed; affected code reviewed for naming, braces, line length, documentation, and method responsibilities |
| Assertions and exceptions | Internal invariants use assertions; parsing/storage errors use checked `TopazException` and actionable UI responses |
| BCD extension | Fixed-duration tasks support positive hours/minutes, persistence, finding, marking, and deletion |
| At least two AI-assisted optional increments | Existing BetterGui, Personality, and MoreErrorHandling implementations are present and tagged. This finalization used Codex for further BetterGui changes (minimum size, focus, prompt) and MoreTesting (search/GUI regressions and packaged-app checks) |
| User guide | Replaced template with installation, all commands, examples, numbering rules, date/duration formats, errors, backup/recovery, and troubleshooting |
| Product screenshot | `docs/Ui.png` is a real single-window capture, with Gronk title and full input controls |
| Website / release | Live Pages guide and screenshot verified on desktop and mobile; new GUI release pending |

The required implementation increments were reviewed individually; optional and
if-applicable increments such as A-CI and A-Varargs are not treated as missing
mandatory features. A final submitted-deliverable percentage should only be
claimed after the website, release, and course dashboard have been checked.

## Fixes made in this finalization

- Preserve original list numbers in search results. Previously, `find` could show
  task 2 as result 1, inviting a subsequent `mark 1` or `delete 1` on the wrong task.
- Explain empty lists and searches with no matches.
- Keep the input usable at a minimum window size, restore typing focus after Send,
  and show a concrete example in the input prompt.
- Add regression tests covering search-then-mutate across restarts, empty states,
  real GUI Enter/Send handlers, error recovery, persistence, and farewell controls.
- Finish the user guide, product screenshot, site configuration, and credits.

## Verification results

Environment: Windows, Temurin Java **25.0.4**. Production task data was not used.

- `gradlew.bat clean test checkstyleMain checkstyleTest shadowJar` passed as the
  baseline. After changes, `gradlew.bat test checkstyleMain checkstyleTest shadowJar`
  passed again: **82 tests, 0 failures, 0 errors, 0 skipped**.
- The project `test-ui` skill runner passed **17/17** normal CLI sessions against
  the final JAR, using `build/ui-test-data.txt` as a disposable save file.
- The same runner passed **1/1** corrupt-storage startup session. The deliberately
  corrupt file remained byte-for-byte unchanged.
- A packaged-GUI harness using only the copied release JAR loaded the real window,
  checked the Gronk title, submitted commands, checked persistence, resized to
  360 × 400, and captured the full window. All checks passed.
- The exact `java -jar topaz.jar` launch also opened a responsive window titled Gronk from a separate folder.
- Local HTML preview contained two valid tables, no broken images or placeholders,
  and no page overflow at a 390-pixel viewport. Screenshot and mobile preview were
  visually inspected. The published Jekyll page was subsequently verified on
  18 September, as recorded below.
- `git diff --check` passed.

Packaged artifact: `build/libs/topaz.jar` (12,884,560 bytes).
SHA-256: `509B895D6DC80A79817F3E09A075F5312A80C9B7508696615B066ADFA355C118`.

The high-value JUnit coverage includes parsing/validation, dates, all persisted
task types, malformed records, atomic-save failure handling, mutation rollback,
collection operations, command processing, and GUI rendering/controller behavior.
No instrumented method-coverage percentage was measured.

Remaining test limits: macOS/Linux runtime smoke tests have not been performed;
bundled JavaFX natives are x64, so ARM needs a matching runtime/build. Disk-full
and abrupt power-loss behavior are not directly simulated. JavaFX 17 emits
compatibility/deprecation warnings on Java 25, but the tested GUI runs.

## Project-management evidence

- Local dated commits exist in all five iP weeks: August 21, August 28–30,
  September 4, September 11–12, and September 16. Commit dates do not independently
  prove that the work was pushed before each deadline.
- The last five existing subjects follow the required capitalized, imperative,
  no-final-period, maximum-72-character convention. No history was rewritten.
- The upstream [iP PR #475](https://github.com/NUS-CS2103-AY2627-S1/ip/pull/475)
  exists and its description contains Markdown formatting. It still calls the
  product Topaz and should be refreshed to describe Gronk when publishing.
- Two peer reviews are verified on September 3, 2026 (before the Week 4 deadline):
  [PR #407](https://github.com/NUS-CS2103-AY2627-S1/ip/pull/407#pullrequestreview-5103525891)
  and [PR #61](https://github.com/NUS-CS2103-AY2627-S1/ip/pull/61#pullrequestreview-5103830556).
- The GitHub API confirms the published `Level-10` tag, now also fetched locally.
  No conflicting replacement was created. Other implementation tags
  including BCD-Extension and the three existing optional increments are published.
- The dashboard identifies students by masked IDs, not GitHub usernames. The
  individual row could not be identified without the user's masked ID. Check the
  [iP progress dashboard](https://nus-cs2103-ay2627-s1.github.io/dashboards/contents/ip-progress.html)
  after publishing, particularly Git Standard and final deliverables.

## Final publication steps

1. **Done:** the user explicitly authorized committing and pushing. Changes were
   pushed to `master` as `a7f749b`, `3c9387d`, and `651c0a5`.
2. **Done:** lightweight tags A-MoreTesting and A-UserGuide were created at their
   completed commits and pushed. Fetching also restored the existing Level-10
   tag locally without changing the published tag.
3. **Done:** GitHub Pages uses `master` and `/docs`, with HTTPS enforced. The
   actual rendered guide and `https://aston-ish.github.io/ip/Ui.png` were checked.
4. Publish a new release (suggested `v0.2`) with exactly the verified `topaz.jar`
   asset. Release notes are prepared in `build/release-notes.md`. The current
   August 30 release asset is only 22,221 bytes and predates the GUI.
5. Confirm the release is publicly accessible and the final dashboard is green.
   Obtain a teammate smoke test on other operating systems where available.

The course's final-submission deadline is September 18, 2026 at 23:59 Singapore
time, as listed in the linked Week 6 instructions.

## Requested submission steps 1–3

After the user authorized the push, Java 25.0.4 was reconfirmed and
`gradlew.bat clean shadowJar test checkstyleMain checkstyleTest` passed. All 82
JUnit tests passed with no skips, and both Checkstyle tasks passed. The rebuilt
JAR has the same size and SHA-256 recorded above.

The fresh JAR was copied into a newly created empty folder and launched using
`java -jar topaz.jar`. Its window title was Gronk and Windows reported it as
responding. Only the smoke-test instance was closed afterward. The manifest,
Java 25 class version, FXML/CSS resources, and Windows/Linux/macOS native library
entries were verified inside the JAR. Bundling libraries is not a substitute for
running the app on each operating system.

The rebuilt artifact passed all 17 regular CLI sessions and the corrupt-file
startup session. A further two-process check added all four task types in a fresh
folder, marked a deadline, restarted, and verified restored completion and search
numbers. Reading/searching left the saved bytes unchanged.

The last five pushed commit subjects were reviewed for meaningful imperative
wording, capitalization, length, and punctuation. The exact green Git Standard
dashboard indicator remains unverified until the user's masked row is identified.
The dashboard states that its automated results refresh daily.

Step 2 and the local part of step 3 are complete. Step 1 still flags the old
released JAR and unverified dashboard; a full-mark final submission cannot yet
be confirmed. Teammate/other-OS testing awaits recipient details and
their results. A short [smoke-test checklist](smoke-test.md) is ready to accompany
the JAR. No new GitHub release was created, in line with deferring step 4.

## Published website verification — 18 September 2026

- The [product website](https://aston-ish.github.io/ip/) returns HTTP 200 and
  identifies the product as **Gronk User Guide**. GitHub Pages builds from
  `master:/docs`; the deployment at commit `70898c6` succeeded.
- `docs/Ui.png` is a real 886 × 573 capture of one complete GUI window. It shows
  the Gronk title, all four task types, completed tasks, and the input controls.
  The [public screenshot](https://aston-ish.github.io/ip/Ui.png) returns HTTP 200
  and its bytes match the repository file exactly.
- The course showcase loads and decodes the screenshot in the `aston-ish` entry
  under **CS2103T-W08-4** after expanding the tutorial group.
- The published guide has two rendered tables and covers all ten commands,
  supported date/duration formats, search numbering, validation, persistence,
  recovery, and launch troubleshooting. Desktop and mobile views were checked;
  the page has no horizontal overflow at a 390-pixel viewport.
- Installation instructions explicitly distinguish the available console
  release from the current GUI build, with a working source-build link.
- The first Pages deployment exposed a broken tracked local-skill submodule
  reference. Removing that reference from Git fixed deployment; the local skill
  files were preserved. This did not change application code or the tested JAR.


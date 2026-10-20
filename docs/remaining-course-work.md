# Delivery and personal checks

## Verified implementation and process

- [x] JavaFX GUI using FXML, packaged JavaFX dependencies, shared command backend,
  and `stats` (C-Statistics). The separate tutorial also reaches Part 4.
- [x] Java 25 tests, Checkstyle, and GUI interaction checks pass. Console regression:
  46 commands across five cases. JUnit: 55 tests, including save rollback and reload.
- [x] Three independent branches prepared before merging any PR:
  [assertions #3](https://github.com/sheeppocket/ip/pull/3),
  [code quality #4](https://github.com/sheeppocket/ip/pull/4), and
  [streams #5](https://github.com/sheeppocket/ip/pull/5).
  Each was merged on GitHub with a merge commit, pulled and tagged; remaining
  branches incorporated master, passed checks, and were pushed between merges.
- [x] Remote milestone tags include `Level-10`, `BCD-Extension`, `A-Assertions`,
  `A-CodeQuality`, `A-Streams`, `A-CI`, `A-CheckStyle`, `A-BetterGui`,
  `A-MoreErrorHandling`, and `A-MoreTesting`. `A-FullCommitMessage` points to
  `15a56f5`; commit bodies explain the changes and rationale.
- [x] [Published guide](https://sheeppocket.github.io/ip/) and `Ui.png` return
  HTTP 200 without authentication. The real full-window image was visually checked.
- [x] Existing [course PR #589](https://github.com/NUS-CS2103-AY2627-S1/ip/pull/589)
  describes the current app and includes the required Markdown elements.
- [x] [Yapper 1.0](https://github.com/sheeppocket/ip/releases/tag/v1.0) contains one public JAR asset.
  Its unauthenticated download matches the tested build, passes GUI interactions,
  and opens via `java -jar yapper.jar` in an isolated folder. Linux CI also
  passes the GUI smoke test under Xvfb. See [verification details](verification.md).

## Only checks requiring the student's participation

- [ ] Confirm no teammate chose **C-Statistics**. If there is a clash, change
  the extension choice rather than claiming team coordination has happened.
- [ ] Ask a teammate on another OS to test the release. Native libraries target
  Windows, Linux, and macOS x64; ARM64 is not bundled.
- [ ] Confirm assigned Week 4 peer reviews and Week 8 Canvas evaluations.
  Historical participation and deadlines cannot be recreated by code changes.
- [ ] Review the AI-assisted work and the course dashboard. The course also
  encourages personally practising full commit messages and Markdown.

The final submission is the published JAR, user guide, and code; there is no
separate final Canvas upload. This checklist does not claim historical grading
or participation requirements were satisfied.

Sources: [Week 5](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w5.html),
[Week 6](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w6.html),
[Week 8](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w8.html), and
[grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-grading.html).

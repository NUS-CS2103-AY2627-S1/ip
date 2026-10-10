# Final submission validation — v1.1

## Completed checks

- Java 25 build: `gradlew.bat check shadowJar` passes.
- 20 JUnit tests and Checkstyle pass.
- `build/libs/zsiggy.jar` is the single fat JAR for the v1.1 release.
- Packaged CLI smoke test: creation, mark, find, validation, save, and new-process reload passed.
- Windows JAR startup produced a Zsiggy window without a startup exception.
- Mac Intel and Apple Silicon native headers verified; launcher extraction selected seven matching libraries for each.
- BetterGui, Personality, MoreErrorHandling, and UserGuide milestone tags already exist.
- Existing screenshot is exactly `docs/Ui.png` and includes the full window and title.
- User Guide covers Java 25 startup, every command, restrictions, duplicates, persistence, and recovery.
- GitHub Pages uses master /docs; the existing public guide and screenshot were accessible.

## Release procedure

The user approved committing, pushing, tagging, and publishing v1.1.
Publish one JAR asset, `build/libs/zsiggy.jar`, using `tests/release-notes-v1.1.md`.
Add lightweight `A-MoreTesting`, `A-Release`, and `v1.1` tags; preserve earlier milestone tags.
Verify the public release asset, updated Pages content, and GitHub CI after pushing.

## Remaining verification limits

- Interactive GUI checks in `tests/test-plan.md` were not completed: desktop-control approval timed out.
- `docs/Ui.png` is a valid full-window screenshot but predates the new spacing and error styling.
- Fresh Mac/Linux launches of v1.1 remain unverified; forum smoke-test reports cover v1.0.
- Historical weekly deliverables, peer reviews, and the Git Standard dashboard indicator
  need confirmation on the course dashboard. Successful local checks do not establish full marks.

Sources:
- https://nus-cs2103-ay2627-s1.github.io/website/schedule/week6/project.html
- https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-grading.html
- https://github.com/NUS-CS2103-AY2627-S1/forum/issues/443

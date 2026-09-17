---
name: grading-guideline
description: >-
  Reviews Mouse iP work against CS2103T AY2627-S1 iP grading bars
  (implementation, project management, documentation).
  Use when reviewing code changes, PRs, increments, Week 5-8 iP work,
  or when the user asks if the iP is ready to submit or will get full marks.
---

# iP grading guideline

Source of truth: [CS2103T iP Grading](https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-grading.html)

iP is formative S/U-style: full `15` marks only if **every** bar below is met.
Missing any bar drops the score below half.

When reviewing changes, map each diff to these bars.
Do not invent extra bars.
Optional and if-applicable increments do not count toward the 90% deliverables figure.

Also apply project skills:

- `.cursor/skills/seedu-java-coding-standard/SKILL.md` for Java
- `.cursor/skills/seedu-git-standard/SKILL.md` for commits and branch names

## Review output

Lead with a grade risk: **on track for 15** or **below a bar**.

Then list:

1. Bars this change helps
2. Bars still unmet in the repo
3. Blocking issues (must fix)
4. Non-blocking notes

Do not treat style nits as grading failures unless they are blatant Java/Git convention breaks.

## Implementation (`10`)

Check evidence in the repo, not just the student's claim.

- More than 90% of required (non-optional) deliverables exist in the final iP.
  Minimal versions count.
- GUI is at least JavaFX tutorial part 4 (chat window, input, send, dialog bubbles).
  Part 5 extras are not required.
- At least two optional increments used AI assistance (Week 6 instruction).
- No major bugs in core commands (`todo`, `deadline`, `event`, `list`, `mark`, `unmark`, `delete`, persistence if required by completed levels).
- Reasonable OOP: inheritance plus sensible classes such as `Ui`, `Storage`, `Parser`, `ToDo`, `Deadline`, `Event`.
  Flag a missing `Storage` class if tasks still die when the app exits.
- At least half of public methods/classes have Javadoc.
- Reasonable quality: no blatant Java/Git convention violations, no chunks of commented-out code, no very long or deeply nested methods (SLAP).
- Some errors handled with exceptions (`MouseException` or similar).
- At least two methods have real JUnit tests (not dummy `assertEquals(2, 2)`).

## Project management (`2`)

- Some deliverables submitted in at least 4 of iP weeks 2-6.
- Other specified requirements (GitHub increments, peer reviews) in at least 4 weeks.
- Last 5 iP commit **subjects** follow SE-EDU Git convention (imperative, capitalized, no trailing period, ≤72 chars).
  If not, add small new commits.
  Do not rewrite history or force-push to green the tag.

## Documentation (`3`)

Product website / `docs/README.md` user guide:

- Enough guidance for a new user
- All non-trivial features covered
- No major formatting errors in the published GitHub Pages view

## After a code change

1. Run `./gradlew test checkstyleMain checkstyleTest` with Java 25 (`25.0.3.fx-zulu`).
2. If behavior changed, update JUnit tests and CLI UI tests if those files exist.
3. Prefer one increment per commit.
  Non-trivial commits this week should have a WHY-focused body (SE-EDU full message).
4. Re-check the bars this increment claimed to satisfy.

## Additional resources

- Full bar text and scoring notes: [reference.md](reference.md)
- Course page: https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-grading.html

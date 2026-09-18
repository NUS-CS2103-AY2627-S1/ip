# iP roadmap

Fetched 2026-08-20 from the AY26/27 S1 course website. Weeks 2 and 3 re-fetched 2026-08-27. Week 6 re-fetched 2026-09-14; see `week6-plan.md`. Later weeks are subject to change — re-fetch before relying on them.

## Week arc

| Week | Deliverables | Deadline |
|---|---|---|
| 2 | `Level-0`..`Level-6`, `A-Enums`. Fork, clone, IDE setup. Tag each increment. | **Fri 21 Aug 1600** |
| 3 | PR to upstream. `Level-7` (save), `Level-8` (dates). `A-MoreOOP`, `A-Packages`, `A-Gradle`, `A-JUnit`, `A-Jar`. Then `A-JavaDoc`, `A-CodingStandard`, `Level-9` (find) as parallel branches, for merge practice. Git standard required from this week. | end of week |
| 4 | GFMD in the PR description. Two peer PR reviews (4 participation points). `Level-10` (JavaFX GUI), `A-CheckStyle` (opt), `A-Varargs`. | reviews **Fri 4 Sep 1600** |
| 5 | Fat JAR via Gradle shadow. Full commit message bodies. `A-Assertions`, `A-CodeQuality`, `A-Streams`, `A-CI` (opt). One extension from category B, C, or D. | end of week |
| 6 | At least two of `A-BetterGui`, `A-Personality`, `A-MoreErrorHandling`, `A-MoreTesting`. Product website with `Ui.png` + User Guide. JAR + GitHub release. **Final submission.** | **Fri 18 Sep 2359** |
| 7 | Optional: AI features branch, further polish. | — |

Week 2 mechanics: work directly on `master`, no branches. Tag the finished commit of each increment with the exact increment ID (`Level-2`, `A-Enums`). Push code **and** tags. Late submission allowed within one week without penalty. `A-Enums` was **optional**; `Level-0`..`Level-6` were required.

Week 3 mechanics (fetched 2026-08-27): branches start. `Level-7`, `Level-8`, `A-JavaDoc`, `A-CodingStandard`, and `Level-9` each get a `branch-{ID}` branch, merged back with `--no-ff`, and the **merge commit** carries the tag. `A-MoreOOP`, `A-Packages`, `A-Gradle`, `A-JUnit`, `A-Jar` are done on `master`. Push `master`, the branches, and the tags. A PR from the fork's `master` to the upstream `master`, named `[{name or username}] iP`, is a deliverable and stays open. The Git commit message standard is required from this week; only future commits are checked. Details in `week3-plan.md`.

## Grading — 15 marks, pass/fail cliff

**You get full marks or less than half.** Falling below any single bar drops the whole component under 7.5. Treat every bar as mandatory.

**Implementation [10]** — >90% of deliverables; GUI at least as advanced as JavaFX tutorial part 4; **at least two optional increments done with AI assistance**; no major bugs; reasonable OO design with inheritance; **at least half of public methods/classes have Javadoc**; coding standard + SLAP; exception handling; **at least two methods with good JUnit tests**.

**Project management [2]** — deliverables in ≥4 of the 5 iP weeks (2–6); requirements followed in ≥4 weeks; **last 5 commits comply with the Git commit message convention**.

**Documentation [3]** — enough guidance for all non-trivial features; no major formatting errors.

Non-obvious bars worth tracking early: the AI-assisted increments, the Javadoc ratio, the two JUnit tests, and the last-5-commits rule (it is checked at the end, so message quality matters most near the deadline).

## Design tripwires

Only raise these when the user is about to make the choice. Give the hedge, not an architecture.

**T1 — Output goes to stdout everywhere.** Collides with `Level-10` (week 4), which replaces the console with JavaFX. Fixing it later means touching every print site. Hedge: send all user-facing text through one method from the start. One method, not a UI layer.

**T2 — Command parsing scattered across branches.** Collides with `A-Enums` (this week) and `Level-8` (week 3), which reparses date arguments. Hedge: work out the command word in one place.

**T3 — Fixed-size array for the task list.** The Level-2 spec permits `String[100]`, but `Level-6` (delete, same week) makes it painful — deletion means shifting elements by hand. `A-Collections` is the intended fix. Because Level-6 lands hours after Level-2, raise this at Level-2 and let the user choose: feel the pain deliberately, or go straight to `ArrayList`.

**T4 — Display formatting entangled with task data.** Collides with `Level-7` (save, week 3), which needs a second serialisation of the same objects. Hedge: keep "how a task prints" separate from "what a task is". Low cost, no new abstraction.

**T5 — Dates stored as free text.** Correct for now — Level-4 explicitly says dates may be strings. `Level-8` converts them. Do **not** pre-warn; this is intended pain. Listed here only so it is not mistaken for a tripwire.

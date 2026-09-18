# Handoff

Context for an assistant picking this project up fresh. Most of what matters is
already in the repo — this file covers only what is not.

## Read these first, in this order

| File | What it gives you |
| --- | --- |
| `AGENTS.md` | project rules, and the **mandatory** testing workflow after every code change. Loaded automatically. |
| `NOTES.md` | the full technical story: architecture, every increment, the theory, design decisions and why they were made, known limitations |
| `docs/README.md` | the user guide — every command with real expected output |
| `test/ui-test-plan.md` | all 31 text-UI cases |

`NOTES.md` is the important one. Do not restate it here; keep it up to date
instead.

## Who you are working with

A CS2103/T student doing the individual project. Intermediate Java and OOP,
comfortable in IntelliJ, but had not used Java in a while when this started.

**Explain Java at a basics level.** Whenever a language feature beyond plain
classes and methods comes up, say explicitly that it is a Java keyword rather
than something invented for this project, name the version that introduced it,
and explain what it does *before* using it in a plan. This came up repeatedly:
`record` and `yield` were used in a plan without being introduced, and it was
not clear whether they were Java features or custom code.

Demonstrating beats describing. Running `javap` on a compiled record, or showing
`split` misbehaving in `jshell`, landed far better than any explanation did.

## How this project is worked on

**Plan, then code, then change.** The preferred rhythm is: describe the change
in plain English → show the exact code → wait for an explicit go-ahead → make
the change. Skipping straight to editing after an ambiguous "let's move on" has
gone wrong before.

**Push back and be corrected.** Several designs here came from the student, not
the assistant — the `toStorable()` split, saving unconditionally instead of
tracking which commands mutate, and the `TaskDate` wrapper class were all their
calls, and all better than what was first proposed. When they question a design,
take it seriously rather than defending the first answer.

**Report honestly.** If a test fails, show it. If a step was skipped, say which.
If you broke something, say so plainly and fix it.

**Commit messages: one line, four words maximum, no body.** Capitalised
imperative, no full stop — `Add find command`, `Extract Ui class`. This overrides
the longer style `AGENTS.md` asks for. The course requires the capitalised
imperative form from week 3 onward, and past messages should be left alone.

**Git ownership varies.** Early on the student ran every git command themselves.
Later they asked the assistant to commit, tag and push. Ask which they want
rather than assuming.

**Every skill must work in Claude Code and Codex.** One real copy in
`.codex/skills/<name>/`, symlinked into `.claude/skills/`. Never add a skill only
one of them can run. This is also written into `AGENTS.md`.

## Where the project stands

Everything through Level-9 is done, merged and pushed:

- Level-0 … Level-9
- A-Enums (tagged `enums`), A-MoreOOP, A-Packages, A-Gradle, A-JUnit, A-Jar,
  A-JavaDoc, A-CodingStandard

Every increment is on its own branch, merged with `--no-ff`, and tagged on the
merge commit. All branches are kept — the course requires it.

Green as of the last run: **31 text-UI cases**, **38 JUnit tests**, **zero
Javadoc warnings**, clean Gradle build, working standalone JAR.

## What is left

1. **The GUI.** JavaFX, at least as complete as Part 4 of the tutorial. The
   largest remaining piece, roughly 5–9 hours, and required for the final
   submission. The JDK in use is Zulu **FX**, so JavaFX is already installed and
   the usual module-path setup problems do not apply.
2. **A product screenshot** in `docs/README.md`, once there is a GUI.
3. **Confirm the pull request exists** from the student's fork to
   `NUS-CS2103-AY2627-S1/ip`, titled `[Kenil Gandhi] iP`. It is a week-3
   deliverable and it is unclear whether it was ever created. It has to be done
   in the browser.
4. Optionally, two stretch goals that were skipped on purpose: `*Command` classes
   for A-MoreOOP, and a "what is due on this date" command for Level-8. The
   second is about 80% the same work as `find`, so it is cheap now.

## Gotchas that have already caused problems

- **`./gradlew run` executes in the project root** and overwrites the real
  `data/Kenbot.txt`. Run experiments from a scratch folder using the JAR.
- **IntelliJ run configurations go stale** after a package move. A config
  created before A-Packages still says `Kenbot` and fails with
  `ClassNotFoundException`; it needs to be `kenbot.Kenbot`.
- **Forgetting `--no-ff`** on a merge produces a fast-forward and loses the
  branch shape the course checks for. It happened once and had to be undone with
  `git reset --hard` followed by a proper merge.
- **`data/Kenbot.txt` is gitignored**, so a fresh clone has no `data/` folder at
  all. `Storage` creating it is load-bearing, not defensive.
- **The tag for A-Enums is `enums`**, not `A-Enums`. Renaming was discussed and
  never done.

## Before you change any code

`AGENTS.md` requires two steps after **every** change under `src/main/java`:
update `test/ui-test-plan.md` if the console output changed, then run the tests:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

Use Java 25. Also run `./gradlew test` for the JUnit suite. If a change cannot
alter console output — a pure refactor, a Javadoc edit — say so explicitly
rather than skipping the step silently.

---

*This file is only for continuing the work. Safe to delete before final
submission.*

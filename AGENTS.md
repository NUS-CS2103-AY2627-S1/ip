# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate experience in OOP programming like Java, foundational experience in Javascript, Basic knowledge in python but no knowledge on programming in software engineering
* IDE and level of expertise: Intellij IDEA, intermediate expertise

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Skills must work in both Claude Code and Codex

This project is worked on with Claude Code today and may be worked on with
Codex later, so the two must have identical functionality. Never add a skill
that only one of them can run.

Keep exactly one real copy of every skill, in `.codex/skills/<skill-name>/`,
and make it visible to Claude Code with a relative symlink:

```bash
ln -s ../../.codex/skills/<skill-name> .claude/skills/<skill-name>
```

A symlink is used instead of a second copy so the two can never drift apart:
there is one set of files, one place to edit, and one thing to review. Always
edit the real files under `.codex/`; never edit through the `.claude/` path as
if it were separate.

When adding or changing a skill:

* Put the skill's own files in `.codex/skills/<skill-name>/`.
* Add the symlink under `.claude/skills/` and commit it, so a fresh clone gets
  both. `.gitignore` already re-includes `.claude/skills/` for this reason.
* Write any command in a skill or in this file using its `.codex/` path, so the
  command works no matter which agent runs it.
* Check that the skill actually runs from both paths before reporting it done.

# Testing workflow after code changes

Kenbot is a console program, so every change to its behavior must be checked
against the text-UI test plan before the change is reported as done. Follow
these two steps after **each** code update to `src/main/java`, without waiting
to be asked.

## 1. Update the test plan if needed

`test/ui-test-plan.md` is the single record of the text-UI test cases. Update it
whenever a code change affects what the program prints or accepts:

* **New or changed command** - add a test case, or update the affected one.
* **Changed wording, spacing, or formatting of any output** - update the
  expected output of every test case that shows it, and update the `Greeting`
  section if the banner or welcome message changed.
* **Newly handled error** - add a test case for it, and remove the matching
  entry from the plan's "Not yet covered" section.

Write the expected output from what the program *should* print. Never paste in
what it currently prints to make a failing test pass: that turns a bug into a
rule. If a test fails because the intended behavior genuinely changed, update
the expected output and say so explicitly.

No test plan update is needed for a change that cannot alter console output,
such as a pure refactoring, a Javadoc-only edit, or a rename of a private field.
Say which of these applies rather than silently skipping the step.

## 2. Run the text-UI tests

Invoke the `test-ui` skill. It is available to both Claude Code and Codex, and
any agent can also run it directly from the repository root:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

Use Java 25 (see "Java version" below). The runner compiles the sources itself,
so no separate build step is needed.

Report the outcome honestly:

* **All cases passed** - say so, and include the console session.
* **A case failed** - the runner stops at the first failure and prints the
  expected output, the actual output, and the difference. Show that report, fix
  the cause in `src/main/java`, and run the plan again. Do not describe the
  change as working until the full plan passes.
* **Not run** - if the tests could not be run at all, say that plainly instead
  of implying the change was verified.

# iP grading criteria

When helping with this individual project, keep the following grading requirements in mind. Mention relevant risks or improvements when reviewing code, planning work, or suggesting changes, but do not let this rubric override the user's explicit request.

## Implementation

For full marks, the final submission should satisfy all relevant requirements, so bear all these in mind while recommending code changes at all times.

* More than 90% of required deliverables completed. Requirements labelled optional or if-applicable do not count in this percentage.
* A fit-for-purpose GUI at least as complete as Part 4 of the JavaFX tutorial.
* At least two optional increments completed with AI assistance, as required by the course.
* No major bugs.
* Reasonable object-oriented design, including some inheritance and sensible division of responsibilities into classes, e.g e.g., Ui, Storage, Parser, Todo, Deadline, Event, etc
* Javadoc comments on at least half of public classes and public methods.
* Reasonable code quality: follows Java and Git conventions, has no unnecessary commented-out code, and uses small, focused methods without deep nesting.
* Some errors handled using exceptions.
* Good JUnit tests for at least two methods.

## Project management

* Submit some deliverables in at least four of the five iP weeks, from Week 2 to Week 6.
* Follow course requirements such as using Git/GitHub for each increment and completing peer reviews in at least four weeks.
* The final five iP commits must follow the course's required Git commit-message subject convention. Do not rewrite past commits merely to meet this requirement; make further legitimate, small commits when appropriate.

## Documentation

* The product website and user guide provide enough guidance for every non-trivial feature.
* The published documentation has no major formatting errors.
* Final grading is mainly a manual review; automated checks are progress indicators, not the only standard.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Coding standard

All Java code in this repository follows the SE-EDU Java coding standard
(intermediate level). The rules are recorded in the `seedu-java-coding-standard`
skill; read it before writing or changing any Java code, and apply the rules as
the code is written rather than tidying up afterwards.

Use the same skill when reviewing Java code, including someone else's pull
request. Honour the standard's exemptions: getters, setters, overridden methods
and test methods do not need header comments, so their absence is not a
violation.

## Git

All commit messages and branch names follow the SE-EDU Git conventions,
recorded in the `seedu-git-standard` skill. Read it before proposing a commit
message. In short: an imperative, capitalised subject with no full stop, kept
well under 72 characters.

This project prefers a single-line subject of at most four words, with no body,
which is stricter than the standard and stays compliant with it. Add a body
only when the subject genuinely cannot carry the reason on its own.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.


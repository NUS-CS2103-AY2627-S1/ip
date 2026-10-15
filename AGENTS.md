# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [Beginner]
* IDE and level of expertise: [Beginner]

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java code in this project, under `src/main/java` and `src/test/java` alike,
must follow the SE-EDU Java coding standard (intermediate level):
https://se-education.org/guides/conventions/java/intermediate.html

Invoke the `seedu-java-coding-standard` skill for the full rules before writing
or reviewing Java code. Apply them as you write, not as a clean-up pass
afterwards. Before reporting a code change as done, run the checker:

```bash
py .claude/skills/seedu-java-coding-standard/scripts/check-style.py
```

It exits non-zero on any violation. A clean run does not prove full compliance,
since naming and comment rules still need a human read, but a dirty run always
means something must be fixed.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Do not commit or push unless explicitly asked.

All commits must follow the SE-EDU Git conventions:
https://se-education.org/guides/conventions/git.html

Invoke the `seedu-git-standard` skill for the full rules before proposing or
writing any commit message, or naming a branch. In short:

* Subject line in imperative mood, capitalized, no trailing full stop, 50
  characters preferred and 72 at most.
* Keep commit messages to the subject line unless the user asks for a body.
* One commit per standalone change. Keep source changes separate from changes
  to agent files such as this one or anything under `.claude/skills/`.
* Branch names in kebab case, except where the course dictates the name for a
  graded increment.

Use lightweight tags unless the user requests an annotated tag. Increment tags
go on the merge commit on `master`, not on the feature branch tip.

## Testing

There are two test suites, and both must be kept green.

### UI tests

`test/ui-test-plan.md` drives the whole program through a scripted console
session. After each code update (a change to any file under `src/main/java`),
before reporting the task as done:

1. Update `test/ui-test-plan.md` if the change affects console output —
   added/changed/removed commands, changed message wording, or changed
   formatting. Get expected output by running the program with the new
   input first and copying its real output; don't hand-type a guess.
2. Invoke the `test-ui` skill to run the plan and confirm it passes. If it
   fails, treat that as a bug to fix (in the code or the plan, whichever is
   wrong) before considering the update done.

### JUnit tests

JUnit 5 tests live under `src/test/java`, mirroring the package and class
being tested: `ff15.task.Todo` is tested by `ff15.task.TodoTest` in
`src/test/java/ff15/task/TodoTest.java`. Run them with `./gradlew test`.

Name test methods `featureUnderTest_testScenario_expectedBehavior()`, e.g.
`parse_malformedDate_throwsException()` or `get_onePastTheEnd_throwsException()`.

**Coverage target: the top ~50% highest-value methods.** Value here means
complex, core, or critical logic — parsing, date arithmetic, index handling,
and save-file round-tripping — rather than one-line getters, constructors, or
`Ui` printing (which the UI test plan already covers end to end).

**JUnit tests must be updated after each code change to stay at that target.**
Concretely, when a change under `src/main/java`:

* adds a non-trivial method, add tests for it in the matching `*Test` class;
* changes what an existing tested method does, update its tests to match the
  new intended behaviour rather than deleting the failing assertions;
* adds a new class holding real logic, add the matching `*Test` class;
* fixes a bug, add the test case that would have caught it.

A change is not done until `./gradlew test` passes.

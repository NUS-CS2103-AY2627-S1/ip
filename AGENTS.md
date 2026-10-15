# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: decent, not the best
* IDE and level of expertise: amateur

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Coding standard

All Java code in this project — in `src/main/java` and `src/test/java` alike —
must follow the SE-EDU Java coding standard, basic and intermediate rules. The
rules are written out in the `seedu-java-coding-standard` skill.

**Invoke that skill before writing or changing any Java file**, and before
answering any question about naming, layout or comment style here. Do not work
from memory of what Java style usually looks like: this standard differs from
common practice in places, notably American spelling in comments, `case` labels
indented inside `switch`, and wrapped lines indented 8 spaces rather than 4.

New code must comply when it is written, not be tidied up afterwards. When
editing an existing file, bring the lines you touch into line with the standard,
but leave lines you had no other reason to change alone, so that a real change
is not buried in reformatting.

## Testing after code changes

The project is tested at two levels, and both must pass before any change is
reported as done. They catch different faults and neither replaces the other:

* **JUnit tests** in `src/test/java`, run with `./gradlew test`. These call
  methods directly and check what they return or throw. A failure names the
  method and the scenario, so it says what broke and roughly where.
* **Text UI tests** described in `test/ui-test-plan.md`, run by the `test-ui`
  skill. These start the whole program, type commands into it and compare the
  entire console session. They catch what JUnit structurally cannot see:
  wording, layout, the flow of the conversation, and whether the task list
  survives a restart.

Note that `test/` and `src/test/java` are unrelated. Gradle only ever compiles
`src/test/java`; the top-level `test/` folder holds the UI test plan and is
invisible to it.

### JUnit coverage target

Aim to cover the **top ~50% highest-value methods** by JUnit tests: the complex,
core and critical ones, where "high value" means a bug would be costly, silent,
or hard to trace. Data handling, parsing, and any arithmetic on what the user
sees rank above thin wrappers, getters and printing code. Methods already
covered thoroughly end to end by the UI test plan rank lower, since testing them
again in isolation adds little.

**Keep the JUnit tests up to date with every code change**, so that target keeps
holding rather than decaying:

* A new method that falls in the top half needs tests in the same change.
* A changed method needs its tests updated in the same change — including
  deliberate behavior changes, where the test is what records the new intent.
* A removed or renamed method needs its tests removed or renamed with it.

Tests mirror the package of the class under test, e.g. `billy.task.TaskList` is
tested by `src/test/java/billy/task/TaskListTest.java`. Name test methods
`featureUnderTest_testScenario_expectedBehavior`, e.g.
`get_taskInEmptyList_exceptionThrown`.

Write tests that would actually fail if the method were wrong. A test that only
restates what the code already does proves nothing and quietly locks in bugs.

### The routine

After every change to the code, before reporting the work as done:

1. Add or update the JUnit tests as described above. Skip this step only when
   nothing in the top half of the codebase was touched, and say so.
2. Update `test/ui-test-plan.md` if the change affects the text UI. Add cases for
   new commands or behavior, and update the expected output of existing cases
   whose output the change deliberately alters. Skip this step only when the
   change cannot affect what the user sees on the console, and say so.
3. Run `./gradlew test`, then invoke the `test-ui` skill to run the UI cases.
4. Show the user both test session transcripts, and report how many tests and
   cases passed. Tool output is not reliably visible to the user, so save the
   transcripts to files and send them rather than assuming they can see the
   console output.

Never report a change as working without having run both suites. If something
fails, fix the cause rather than editing the expectation to match — unless the
new behavior is genuinely intended, in which case update the test or the plan
and say which side was changed and why.

## Git

All commits, branch names and tags in this project must follow the SE-EDU Git
conventions. The rules are written out in the `seedu-git-standard` skill.

**Invoke that skill before writing or proposing any commit message**, and before
naming a branch or creating a tag. Write the message from the skill's rules
rather than from habit: the conventions are stricter than usual about the
50-character subject line and about saying what changed and why rather than how.

Commits here are subject-only, which the standard allows. Propose a single
`git commit -m "Subject line"` by default, and carry the reasoning in the
subject rather than in a body. Add a body only where the subject genuinely
cannot carry it — a change that looks wrong without an explanation, an obvious
alternative that was rejected, or a rule deliberately not followed — and say why
that commit earns one.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.

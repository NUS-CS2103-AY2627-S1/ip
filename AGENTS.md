# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Low
* IDE and level of expertise: Low

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

## Console UI testing

After each code update, review `test/ui-test-plan.md` and update its test cases whenever the console UI behaviour changes. Then invoke the project `test-ui` skill to run the recorded UI tests. Do this before handing the change back to the user; if a test fails, report the failure rather than treating the code update as verified.

## JUnit test coverage

Focus JUnit tests on approximately the highest-value 50% of methods, prioritizing complex, core,
or critical business logic. After each code change, update the JUnit tests for the affected
behaviour and run them with Gradle so the test suite continues to meet this coverage target.

## Java coding standard

Follow the project skill `seedu-java-coding-standard` in
`.codex/skills/seedu-java-coding-standard/SKILL.md` for all Java code. Review it before adding or
changing Java source or tests, and keep new code compliant with the SE-EDU basic and intermediate
Java coding standard.

## Code quality self-check

An automated course script reports style problems in this repository, and it
checks more than Checkstyle does. Before handing any change back, run
`./gradlew checkstyleMain checkstyleTest` and then follow the project skill
`code-quality-self-check` in `.codex/skills/code-quality-self-check/SKILL.md`.
It covers the aspects Checkstyle cannot enforce: predicate naming for booleans,
class size, method length, the Single Level of Abstraction Principle, dead code,
informative header comments, binary files in the repository, and commit message
wrapping.

Design code so that every statement in a method sits at the same level of
abstraction. A method either orchestrates named steps or performs one step, not
both. The `seedu-java-coding-standard` skill states the rule and shows a worked
example.

### How the main classes divide the work

Keep new code in the class that owns the responsibility, rather than adding it
to `SevenSix`.

* `SevenSix` connects parsed commands to the session executor, without parsing or wording.
* `Command` implementations own individual actions; related task types share `AddCommand`.
* `StatusCommand` shares the workflow for marking and reopening tasks.
* `CommandExecutor` coordinates persistence and rollback for all task-changing commands.
* `Parser` owns every rule about the shape of a command, and turns command text
  into command objects with typed tasks, keywords, and task numbers.
* `Ui` formats user-visible responses, and `HelpText` owns the command reference.
* `ConsoleUi` drives the console loop and prints response blocks.
* `UndoHistory` keeps a stack of task states and action details for session undo.
* `TaskSnapshot` and `UndoEntry` isolate saved state from later task mutations.
* `TaskList` and `TaskStorage` own the in-memory list and its persistence.

## Git commit message standard

Follow the project skill `seedu-git-standard` in `.codex/skills/seedu-git-standard/SKILL.md` for all
future commits. Use its subject and body rules whenever creating or recommending a commit message.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

When recommending interactive staging with `git add -p` or `git commit -p`, always tell the user the exact key to enter for each hunk in order, based on the requested commit split. Explain when a hunk should be edited with `e`, and identify which changes to keep or remove in the editor. Do not make the user infer the staging sequence from a general explanation of `y`, `n`, or `e`.

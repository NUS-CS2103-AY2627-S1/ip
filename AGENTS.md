# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: intermediate
* IDE and level of expertise: intelliJ, used for 6 months

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Coding standard

All Java code in this project **must** follow the SE-EDU Java coding standard (Basic + Intermediate rules).
Before writing, reviewing, or refactoring any Java code, read and apply the project-specific `seedu-java-coding-standard` skill.
Key points (full rules are in the skill):
- No wildcard (`.*`) imports — always import classes explicitly.
- No `public` non-constant instance fields — use accessors.
- Every loop and conditional body must be wrapped in `{ }`, even single-liners.
- Javadoc on every public class and non-trivial public method (except getters/setters, overrides with identical semantics, and test methods).
- K&R (Egyptian) brace style; 4-space indentation; line limit 120 chars (soft 110).
- Test method names follow `featureUnderTest_testScenario_expectedBehavior()`.

## Java version:

Ensure that Java 25 is used when running the application or build tasks.

## UI regression testing:

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change adds, removes, or changes observable command-line UI behavior. Each affected behavior should have a test case with its aim, inputs, and expected output.
2. Invoke the project-specific `test-ui` skill to run the plan:

   ```powershell
   py .codex/skills/test-ui/scripts/run-ui-tests.py test/ui-test-plan.md
   ```

   Include the resulting console input/output record in the handoff. If a test fails, stop the test session immediately and report the actual and expected outputs; do not continue to later cases.

## JUnit testing

**Coverage target:** the top ~50% of methods by value — prioritising complex, core, or critical business logic (e.g. parsing, date handling, storage serialisation) over trivial getters and one-liners.

When deciding what to test, rank candidate methods by:
1. Complexity — does the method have multiple branches or edge cases?
2. Criticality — would a bug here break core behaviour or corrupt saved data?
3. Purity — methods with no I/O or UI side-effects are the easiest and most reliable to unit-test; prefer them.

**After every code change** that adds, removes, or modifies a method covered by the target:

1. Update the relevant `*Test.java` file(s) under `src/test/java/` to reflect the change (add, remove, or adjust test cases as needed).
2. Ensure every test file is saved **without a BOM** (use `System.Text.UTF8Encoding($false)` in PowerShell, not `Set-Content -Encoding UTF8`).
3. Follow the naming convention `featureUnderTest_testScenario_expectedBehavior()` for test method names, e.g. `parseTodoCommand_emptyDescription_throwsLumineException()`.

**What is currently tested (as of initial coverage pass):**

| Test class | Class under test | Key methods covered |
|---|---|---|
| `lumine.parser.ParserTest` | `Parser` | `parse`, `normalize`, `isCommand`, `parseTaskNumber`, `parseTodoCommand`, `parseDeadlineCommand`, `parseEventCommand`, `parseDateCommand` |
| `lumine.task.TaskTest` | `Task` | `escapeStorageField`, `toFileString`, `toString`, `markDone`, `markUndone`, constructor validation |
| `lumine.task.DeadlineTest` | `Deadline` | `toString`, `toFileString`, `getDueDate`, constructor validation |
| `lumine.task.EventTest` | `Event` | `toString`, `toFileString`, `getToDate`, constructor validation |

**What is intentionally excluded** (below the 50% threshold):
* `Storage` — file I/O makes tests fragile and slow.
* `TaskList` — depends directly on `Storage`; test indirectly via integration/UI tests.
* `TaskType.getSymbol` — trivial enum getter with no logic.

## Git

All Git commits and branch names **must** follow the SE-EDU Git conventions.
Before making any commits, read and apply the project-specific `seedu-git-standard` skill.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change and follow the structure detailed in the `seedu-git-standard` skill.
Do not commit or push unless explicitly asked.

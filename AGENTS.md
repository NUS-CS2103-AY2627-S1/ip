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

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

For every proposed, reviewed, or created commit message, read and follow the
project-specific `.codex/skills/seedu-git-standard/SKILL.md` skill.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

# Advanced conventions

## Java coding conventions

For every Java code change, read and follow the project-specific
`.codex/skills/seedu-java-coding-standard/SKILL.md` skill.

Follow the required basic and intermediate rules in the SE-EDU Java coding standard
used by CS2103T. Treat advanced rules as optional unless the user explicitly requests
them or another project instruction makes them mandatory.

For topics not covered by these rules, follow the Google Java Style Guide.

### Naming

- Package names use lowercase.
- Class and enum names are nouns written in PascalCase.
- Variable names use camelCase.
- Method names are verbs written in camelCase.
- Constants use SCREAMING_SNAKE_CASE.
- Boolean variables and methods should read like booleans, preferably using prefixes such as:
  - `is`
  - `has`
  - `was`
  - `can`
  - `should`
- Collection variables should generally use plural names.
- Acronyms embedded in names should not be written entirely in uppercase.
  - Prefer `exportHtmlSource()` over `exportHTMLSource()`.
- Test methods may use the format:

```text
featureUnderTest_testScenario_expectedBehavior()
```

### Formatting

- Use 4 spaces for indentation.
- Do not use tabs for indentation.
- Aim to keep lines below 110 characters.
- Do not exceed 120 characters unless unavoidable.
- Wrapped lines should normally use 8 additional spaces of indentation.
- Use K&R-style braces.

Example:

```java
if (isValid) {
    processTask();
} else {
    showError();
}
```

- Always use braces for `if`, `else`, `for`, `while`, and other control-flow bodies, even when the body contains only one statement.
- Put spaces around operators.
- Put a space after commas.
- Put a space between Java control-flow keywords and `(`.
- Separate logical sections within a block using blank lines when it improves readability.

### Imports and packages

- Every class must belong to a package.
- Group related classes in appropriate packages.
- Keep import ordering consistent with the rest of the project.
- Use explicit imports.
- Do not use wildcard imports such as:

```java
import java.util.*;
```

### Variables and fields

- Declare variables in the smallest reasonable scope.
- Initialize variables when they are declared where practical.
- Avoid public mutable fields.
- Preserve encapsulation through appropriate access modifiers and methods.
- Write array types as:

```java
int[] values;
```

not:

```java
int values[];
```

- Avoid unnecessary use of `this`.
- Use `this.field = field` when required to distinguish a field from a shadowing parameter.

### Classes and methods

When applicable, organize class contents in this general order:

1. Class documentation
2. Class declaration
3. Static variables
4. Instance variables
5. Constructors
6. Methods

Access modifiers should appear first in method declarations.

Prefer:

```java
public static void run()
```

over:

```java
static public void run()
```

### Comments and Javadoc

- Write comments in English using American spelling.
- Public classes and public methods should have descriptive header comments/Javadoc, except where the course conventions permit omission, such as:
  - straightforward getters/setters,
  - test code,
  - overriding methods where inherited documentation applies exactly.
- Non-trivial private methods should have header comments when required by the full coding standard.
- Javadoc should describe intended behavior, not implementation mechanics.
- The first sentence of a method Javadoc should be a concise summary and normally begin with wording such as:
  - `Returns ...`
  - `Adds ...`
  - `Creates ...`
  - `Sends ...`
- Keep Javadoc concise and include `@param`, `@return`, and `@throws` tags where applicable.
- Tag descriptions should start with a capital letter and end with a period.
- Avoid comments that simply translate obvious Java statements into English.
- Indent comments consistently with the surrounding code.

### Switch statements

If a traditional `switch` case intentionally falls through to the next case, include:

```java
// Fallthrough
```

to make the intent explicit.

## JUnit testing conventions

- Place test code under `src/test/java`.
- Match the production package structure in the test source tree.
  - Example production class: `src/main/java/slotbot/Parser.java`
  - Corresponding test class: `src/test/java/slotbot/ParserTest.java`
- Name each test class after the class under test, such as `ParserTest`.
- Use JUnit 5 and mark test methods with `@Test`.
- Prefer test method names in the following form:

```text
unitBeingTested_descriptionOfTestInputs_expectedOutcome()
```

- Use JUnit assertions such as `assertEquals`, `assertTrue`, `assertFalse`,
  `assertNull`, `assertNotNull`, and `assertThrows` as appropriate.
- Design tests to catch plausible bugs, including normal, boundary, and invalid-input
  cases for non-trivial methods where relevant.
- Do not refactor production code solely to make testing easier unless there is a
  clear design reason.
- Run JUnit tests using Gradle with `.\gradlew.bat test`.
- Keep the existing text UI regression suite passing after test-related changes.

## Git conventions

Follow the CS2103T Git conventions below when proposing branch names or commit messages.

### Commit subject

Every commit must have a clear subject line.

- Aim for at most 50 characters.
- Hard limit: 72 characters.
- Use imperative mood.
- Capitalize the first letter.
- Do not end with a period.

Good:

```text
Add deadline parsing
```

Bad:

```text
Added deadline parsing
Adding deadline parsing
add deadline parsing
Add deadline parsing.
```

A scope or category prefix may be used where helpful, for example:

```text
Parser: Handle invalid dates
Main.java: Remove unused import
bug fix: Prevent duplicate tasks
chore: Update Gradle version
```

### Commit body

Non-trivial commits should include a body.

- Separate the subject and body with a blank line.
- Wrap body lines at 72 characters.
- Use blank lines between paragraphs.
- Use bullet points where they improve clarity.
- Explain WHAT changed and WHY.
- Do not spend the body describing HOW the implementation works when that is already evident from the diff.
- Avoid duplicating information already explained clearly in code comments.

A useful structure is:

```text
<current situation>

<why it needs to change>

<what this commit does>

<why this approach is appropriate>

<any other relevant information>
```

Use present tense for the existing situation and imperative wording for the change where appropriate.

Avoid unnecessary words such as `currently` or `originally` when simply describing the present state.

If a commit message requires a very long explanation covering multiple unrelated changes, prefer splitting the work into smaller logical commits.

### Branch names

Use meaningful kebab-case branch names.

Examples:

```text
add-deadline-command
refactor-parser
fix-task-index-validation
```

If a branch corresponds directly to an issue, use:

```text
issueNumber-some-keywords-from-issue-title
```

For example:

```text
1234-ui-freeze-error
```

## CS2103T iP workflow conventions

The individual project (iP) is developed through course-defined increments such as:

```text
Level-0
Level-1
Level-2
Level-3
Level-4
Level-5
Level-6
A-Enums
```

Other increment IDs may be introduced later by the course.

### Increment order

- Implement course increments in the order specified by the iP instructions.
- Do not skip ahead to later increments unless the user explicitly requests it.
- Keep changes scoped to the requirements of the current increment.
- Avoid implementing future increment requirements prematurely.

### Committing increments

Commit code at important development points.
Allow user to review all changes first, and ask user to incrementally commit by themselves.

At minimum, create a commit after completing each increment.

When helping decide whether to commit:

- Prefer logical, meaningful commits.
- Intermediate commits within an increment are allowed and encouraged when they represent useful checkpoints.
- The final commit for an increment should leave that increment fully implemented.
- Do not include generated `.class` files or other files that should not be revision-controlled.

General CS2103T commit-message conventions still apply.

### Tagging completed increments

After an increment is fully completed, tag the commit that completes the increment using the **exact increment ID** specified by the course.

Examples:

```bash
git tag Level-0
git tag Level-2
git tag A-Enums
git tag A-Classes
```

The increment ID is a Git tag, not the commit message.

Do not invent variations such as:

```text
level-2
Level2
level2
AEnums
```

when the required ID is:

```text
Level-2
A-Enums
```

Use lightweight tags unless the user explicitly requests an annotated tag.

Before tagging, verify that:

1. The increment requirements are complete.
2. The relevant changes have been committed.
3. The tag will point to the commit that completes the increment.

### Pushing completed increments

After completing and tagging an increment, push both the code and the tag to the user's fork.

A normal push does not necessarily push Git tags.

Typical workflow:

```bash
git push
git push origin Level-2
```

Replace `Level-2` with the exact increment ID being completed.

When appropriate, verify the local state before pushing using commands such as:

```bash
git status
git log --oneline --decorate -n 5
git tag
```

Do not commit, tag, or push unless the user explicitly asks.

### Increment completion checklist

When the work appears to complete an iP increment, check:

1. The requirements for the current increment are implemented.
2. The application builds or runs as expected.
3. Relevant tests or checks have been run where applicable.
4. `git status` does not show unintended files.
5. The completed work has been committed.
6. The completing commit is tagged with the exact increment ID.
7. The commit has been pushed to the user's fork.
8. The increment tag has also been pushed.

If helping only with implementation, do not automatically perform the Git operations. Remind the user when the increment reaches the commit/tag/push stage.

## UI testing workflow

After every Java code update, invoke the project-local `test-ui` skill. Update
`test/ui-test-plan.md` first when the user-visible behavior changes.

## Visual diff workflow

After completing each implementation checkpoint, generate a visual diff before
asking the user to inspect and commit the changes:

1. Use the project-local `present-changes-visually` skill.
2. Compare `HEAD` with `WORKTREE` so staged, unstaged, and untracked changes
   are included.
3. Write the report to `_temp/visual-diff.html`, overwriting the previous
   snapshot.
4. Verify that the generator succeeds and report its changed-file count.
5. Include a clickable absolute local-file link to the generated report.
6. Explain that the report is a snapshot and should be refreshed after it is
   regenerated.

Use the bundled workspace Python runtime if `python3` is not available. Do not
commit, tag, or push automatically; stop after showing the changes, test
results, visual-diff link, and suggested commit message.

## Regression test maintenance

Use the following workflow when updating the text UI:

1. Edit the Java code.
2. Update `text-ui-test/input.txt` only when adding or changing a test
   scenario.
3. Run `text-ui-test/runtest.bat`.
4. Inspect `text-ui-test/ACTUAL.TXT`.
5. If the output is unexpected, fix the code and rerun the test.
6. If the output is correct and intentional, copy `ACTUAL.TXT` to
   `EXPECTED.TXT`.
7. Rerun the test and confirm that it passes.

Never replace `EXPECTED.TXT` without reviewing `ACTUAL.TXT` first.

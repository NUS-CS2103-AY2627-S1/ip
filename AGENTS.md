# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: limited to one java OOP course last semester.
* IDE and level of expertise: cursor/intellijDEA, and had one software engineering internship before, but more of C# and kafka, but not very in depth, just surface level but understands the high-mid lvl architecture.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, concisely explain what it does.
  * Follow the project skill `.cursor/skills/seedu-java-coding-standard/SKILL.md` for all Java.
* Follow the project skill `.cursor/skills/seedu-git-standard/SKILL.md` for all commits and branch names.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Don't leave comments after every single block of code.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Follow the project skill `.cursor/skills/seedu-git-standard/SKILL.md` (SE-EDU Git conventions) for every commit message and branch name.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change (WHAT and WHY).
Do not commit or push unless explicitly asked.

## Java coding standard

Follow the project skill `.cursor/skills/seedu-java-coding-standard/SKILL.md` for all Java in this project.
That skill encodes SE-EDU basic + intermediate rules (https://se-education.org/guides/conventions/java/intermediate.html).
Apply it when writing, editing, or reviewing `src/main/java` and `src/test/java` code.


## Testing (JUnit)

Focus JUnit tests on the top ~50% highest-value methods.
Prioritize complex, core, or critical business logic (parsing, task-list mutations, date handling, command routing) over thin getters, UI printing, and the main loop.
Name long test methods as `featureUnderTest_testScenario_expectedBehavior()`, for example `parseDeadline_missingBy_throwsMouseException()`.
Keep tests under `src/test/java` in the same package as the class under test.
After each code change that affects behaviour, update the related JUnit tests so coverage still meets that ~50% target.

# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: intermediate
* IDE and level of expertise: beginner 

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

All Java code written or edited in this project (new code and existing code you touch) must follow the
SE-EDU Java Coding Standard (Basic + Intermediate levels): https://se-education.org/guides/conventions/java/intermediate.html

Load and follow the `seedu-java-coding-standard` skill whenever writing, editing, or reviewing Java source
in this repository. It covers naming, layout, statement style, and Javadoc/comment conventions distilled
from that page. When a violation's fix would be a large structural change beyond the task at hand (e.g.
moving every class into packages), flag it and ask rather than doing it unprompted.

## Testing

JUnit 5 tests live in `src/test/java`, mirroring the package and name of the class under test
(e.g. `src/main/java/Deadline.java` is tested by `src/test/java/DeadlineTest.java`). Run them with
`./gradlew test`. When a plain name would be unclear, name test methods
`featureUnderTest_testScenario_expectedBehavior()`, e.g. `sortList_emptyList_exceptionThrown()`.

* **Coverage target: ~50% of methods.** JUnit tests must cover at least the top ~50% highest-value
  methods in the codebase, prioritizing complex, core, or critical business logic (e.g. command
  parsing and dispatch in `Rocky.getResponse`, date parsing, save-file loading and saving) over
  trivial getters, simple delegation, and GUI layout code.
* **Update the tests after every code change.** Whenever code is added, changed, or removed, add,
  update, or remove JUnit tests in the same change so the codebase still meets the ~50% target, and
  run `./gradlew test` to confirm they all pass before considering the change done.
* Tests must never touch the real save files. The Gradle `test` task runs in `build/test-run/`, so
  any `data/` folder Rocky creates during tests ends up there instead of in the project folder.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

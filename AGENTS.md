# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: minimal
* IDE and level of expertise: Intellij 

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Mandatory Java coding standard

All Java code in this project, including tests and newly generated code, must
follow the SE-EDU basic and intermediate Java coding standard. Before writing,
modifying, or reviewing Java code, read and apply the project-specific skill
`seedu-java-coding-standard` at `.codex/skills/seedu-java-coding-standard/SKILL.md`.
Use it during the final review as well; a formatter alone does not establish compliance.
Keep header Javadoc for all non-private classes and declared methods, including
constructors, tests and accessors, and for non-trivial private methods. This local
requirement is stricter than the upstream documentation exceptions.
Apply conventions without changing behavior unless the task explicitly calls for it.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

All future commits must follow the SE-EDU Git conventions. Before preparing,
reviewing, or creating a commit, read and apply the project-specific skill
`seedu-git-standard` at `.codex/skills/seedu-git-standard/SKILL.md`.
Check both the staged change boundaries and the commit message against the skill.
Keep standalone changes in separate commits when requested, and report test
limitations accurately. This requirement does not authorize commits or pushes.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## JUnit testing target

Focus JUnit tests on approximately the highest-value 50% of implemented methods,
prioritizing complex parsing, persistence, core task operations, and critical
command behavior. This is a method-selection target, not a requirement to attain
exactly 50% line coverage or to test trivial getters and enum constants.

After every code change, review and update the JUnit tests as needed to maintain
this target. Cover normal inputs, boundaries, invalid inputs, and relevant failure
paths for the selected methods. Never weaken assertions to conceal a defect.
Record any pre-existing defects exposed by tests separately from regressions.

Follow Gradle/JUnit Jupiter conventions: mirror production packages under
`src/test/java`, name test classes `ClassNameTest`, and use descriptive method
names such as `feature_scenario_expectedBehavior`. Use temporary directories for
file tests and restore any replaced console streams. Run `gradlew.bat test` with
Java 25 and inspect the report before claiming tests pass. See
`test/junit-test-plan.md` for the current prioritized method selection.

## UI regression testing

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change affects the chatbot's commands, output, or other user-visible behavior. Add or revise test cases and expected output as needed.
2. Invoke the project-specific `test-ui` skill at `.codex/skills/test-ui/SKILL.md` to run the recorded UI tests.
3. Follow the skill's failure policy: stop at the first failed test, record the complete console session in `test/ui-test-plan.md`, and report the actual and expected output.

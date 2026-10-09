# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Second-year university student with background in basic programming, OOP, data structures and algorithms, and functional programming.
* IDE and level of expertise: IntelliJ and quite new to using IDEs, only some experience with Git CLI and Vim

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every Java code change in this project, invoke and follow the project-specific
`$seedu-java-coding-standard` skill in `.agents/skills/seedu-java-coding-standard`. Apply the
standard to production code and test code, and resolve any violations in code touched by the
change before completing the task.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Gradle cache on Windows

When Codex runs Gradle on Windows, use the normal user-level Gradle cache instead of a
workspace-local cache. Set `GRADLE_USER_HOME` in the same PowerShell session before running
any Gradle task:

```powershell
$env:GRADLE_USER_HOME = "$env:USERPROFILE\.gradle"
.\gradlew.bat <tasks>
```

Do not use a `.gradle-user` directory inside the repository. In the Codex sandbox, files in
that directory can receive permissions that cause Java to fail while reading cached JAR files.

## JUnit test coverage after code updates:

Focus JUnit tests on approximately the top 50% highest-value methods in the codebase, prioritizing methods that contain complex logic, implement core application behavior, or are critical to correctness. After every code change, reassess the affected code and update or add JUnit tests as needed to continue meeting this coverage target. Do not change expected behavior merely to make a failing test pass.

After completing each coherent code change, run the JUnit suite and Checkstyle together in one Gradle invocation from the repository root:

```powershell
.\gradlew.bat test checkstyleMain checkstyleTest --rerun-tasks
```

Always include `--rerun-tasks` so Gradle executes the tests and Checkstyle checks afresh instead of reporting previously cached tasks as `UP-TO-DATE`. Report the number of passing tests and any JUnit failures or Checkstyle violations. Do not consider the code change complete until this command succeeds, unless an environmental problem prevents it; in that case, report the exact blocker and the tasks that could not run.

## UI testing after code updates:

After every code update, inspect the change and update `test/ui-test-plan.md` when the change affects the console UI, its inputs, outputs, or test coverage. Then invoke the project-specific `$test-ui` skill and run the documented UI test plan. Do not change expected output merely to make a failing test pass; report the first failure with its actual and expected output.

## Git

For every commit created or proposed in this project, invoke and follow the project-specific
`$seedu-git-standard` skill in `.agents/skills/seedu-git-standard`. Review the staged changes and
the complete commit message against the skill before committing.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

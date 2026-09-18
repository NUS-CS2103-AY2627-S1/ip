# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Ok
* IDE and level of expertise: Ok also

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project skills

For every Java implementation, refactoring, test, or review, follow
`.agents/skills/seedu-java-coding-standard/SKILL.md` and keep the Gradle Checkstyle
checks passing. For every commit or branch operation, follow
`.agents/skills/seedu-git-standard/SKILL.md`.

# Project-specific requirements

## UI testing

After each code update:

1. Update `test/ui-test-plan.md` when the observable console behavior or requirements change.
2. Invoke the project-specific `.agents/skills/test-ui` skill and review its console input/output record.
3. Stop at the first failed case and report the actual and expected output before making further changes.

## JUnit testing

Maintain JUnit tests for approximately the highest-value 50% of non-trivial methods,
prioritising core parsing, task behavior, storage, and other business logic. Update
the relevant JUnit tests after every code change and run them with Gradle.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

### Commit subjects

Every commit must have a clear subject line:

* Prefer 50 characters or fewer; never exceed 72 characters.
* Use the imperative mood, such as `Add README.md`, not `Added README.md`.
* Capitalize the first word.
* Do not end the subject with a period.
* A relevant scope or category prefix is allowed, such as `chore:` or `Task:`.

### Commit bodies

Every commit should use a full commit message: a clear subject followed by a body
separated by a blank line. Wrap body lines at 72 characters and use blank lines
between paragraphs when useful. Explain what changed and why, rather than
describing implementation mechanics already visible in the diff. A useful
structure is:

1. Describe the current situation in the present tense.
2. Explain why it needs to change.
3. State what is being done and why that approach was chosen.
4. Include other relevant context, such as compatibility or testing notes.

Use bullet points when they make the explanation clearer.

For example:

```text
Add JavaFX launcher

Start the application through a separate launcher class so JavaFX can initialise
reliably across supported environments.
```

### Branch names

Use meaningful kebab-case branch names, such as `refactor-ui-tests`. If a branch is
related to an issue, use `issueNumber-keywords`, such as `1234-ui-freeze-error`.

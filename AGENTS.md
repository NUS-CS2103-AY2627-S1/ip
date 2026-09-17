# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

The project chatbot is named Jarvis. If inherited instructions or a user request
refer to “Duke”, interpret that as this project's Jarvis application.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: year 2 computer science student
* IDE and level of expertise: IntelliJ, I have never used IntelliJ

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Coding and Git standards:

Follow [docs/coding-standards.md](docs/coding-standards.md) for Java code and [docs/git-conventions.md](docs/git-conventions.md) for commit messages. These files summarize the SE Education conventions and link to the full references.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, follow the SE Education Git conventions:

* Keep the subject in imperative mood, capitalize its first letter, avoid a
  trailing period, and aim for 50 characters or fewer (hard limit: 72).
* For non-trivial commits, separate the subject and body with a blank line.
* Wrap body lines at 72 characters and use blank lines between paragraphs.
* Use the body to explain WHAT changed and WHY it changed, not HOW the code
  was implemented.
* Structure the body with the current situation, why it needs to change, what
  is being done, why that approach was chosen, and any other relevant context.
* Use bullet points when they make multiple changes easier to understand.

When suggesting a commit, include enough detail to explain the rationale for
the change and follow these formatting rules.
Do not commit or push unless explicitly asked.

## UI testing after code updates

After every code update, review `test/ui-test-plan.md` and update it when the changed behavior requires new or revised UI test coverage. Then invoke the project-specific `test-ui` skill to run the plan. The test session must be recorded in the response; if a test fails, stop immediately and report the actual and expected output.

## JUnit test coverage target

Maintain JUnit tests for at least the top 50% of methods that offer the highest value, prioritizing complex, core, or critical business logic. After every code change, update the relevant JUnit tests to preserve this 50% target and run the Gradle test task to verify the suite. The response must mention the JUnit test result; if a test fails, stop immediately and report the actual and expected output.

## Documentation and test-plan maintenance

Keep contributor guidance in [docs/coding-standards.md](docs/coding-standards.md)
and [docs/git-conventions.md](docs/git-conventions.md) aligned with the
course standards. Keep [docs/README.md](docs/README.md) accurate when commands
or user-visible behavior changes. Do not leave placeholder sections or
machine-specific paths in committed documentation. Use descriptive Markdown
links, fenced code blocks with a language identifier, and exact whitespace in
console transcripts. Update [test/ui-test-plan.md](test/ui-test-plan.md) when
console behavior changes.

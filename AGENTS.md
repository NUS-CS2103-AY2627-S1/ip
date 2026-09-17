# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Basic, trivial
* IDE and level of expertise: IntelliJ IDEA, basic

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks.

## Git

Before preparing, reviewing, proposing, or creating any commit or branch in this
project, use `$seedu-git-standard` and follow
`.codex/skills/seedu-git-standard/SKILL.md`. This project-specific skill is
mandatory for all future commits and branch names. It is based on the SE-EDU Git
conventions and is the source of truth for Git message and branch style in this
repository.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Documentation

Unless otherwise stated, you are to respond in and write all documentation adhering to ASD-STE100 Simplified Technical English. For complex queries and responses, you are to ask the user if using more technical language is ok.
For documentation style, you are to follow the Google developer documentation style guide.

## Java Coding Standard

Before modifying or reviewing any Java code in this project, use
`$seedu-java-coding-standard` and follow
`.codex/skills/seedu-java-coding-standard/SKILL.md`. This project-specific skill
is mandatory for production and test Java code. It is based on the SE-EDU
intermediate Java coding conventions and is the source of truth for Java style
rules in this repository.

Use the Google Java Style Guide for topics not covered here.

### Naming

- Use lowercase package names based on the project or group name.
- Use `PascalCase` nouns for classes and enums.
- Use `camelCase` verbs for methods and `camelCase` for variables.
- Use `SCREAMING_SNAKE_CASE` for constants.
- Write all identifiers in English.
- Treat acronyms as words: `exportHtmlSource`, not `exportHTMLSource`.
- Name booleans like predicates, preferably with `is`, `has`, `can`, `was`, or `should`.
- Use plural names for collections.
- Test methods may use `featureUnderTest_scenario_expectedBehavior`.

### Formatting

- Indent with 4 spaces; never use tabs.
- Keep lines under 110 characters where practical; 120 is the hard limit.
- Indent wrapped lines by 8 additional spaces.
- When wrapping, break after commas and before operators.
- Keep method names attached to their opening parenthesis.
- Use K&R braces, with the opening brace on the same line.
- Always use braces for loops and conditionals, including single statements.
- Put conditional bodies on separate lines.
- Add spaces around operators and after keywords, commas, and semicolons.
- Separate logical units within a block with one blank line.

### Packages, imports, and types

- Put every class in a package.
- Keep import ordering consistent across the project.
- Use explicit imports; never use wildcard imports.
- Attach array brackets to the type: `int[] values`, not `int values[]`.

### Variables and fields

- Declare variables in the smallest possible scope.
- Initialize variables when declared when a valid initial value exists.
- Do not use placeholder values merely to initialize a variable.
- Keep fields non-public unless they are constants or belong to a behavior-free data class.
- Use short names such as `i` and `j` only for small scopes and loop indices.

### Control flow

- Follow standard Java block formatting for `if`, `else`, `for`, `while`,
  `do-while`, `switch`, `try`, `catch`, and `finally`.
- Add `// Fallthrough` when a traditional `switch` case intentionally omits
  `break`.

### Comments and Javadoc

- Write comments in English using American spelling.
- Indent comments consistently with the surrounding code.
- Add descriptive Javadoc to all public classes and methods.
- Javadoc may be omitted for getters, setters, tests, and exact overrides.
- Begin Javadoc with a concise summary such as `Returns ...` or `Adds ...`.
- Separate the description from tags with a blank line.
- End parameter and tag descriptions with punctuation.
- Include either all useful `@param` tags or none when every parameter is
  already self-explanatory.
- Use `{@inheritDoc}` when an override needs to reuse and extend inherited
  documentation.

## Git Conventions

### Commit messages
You are to suggest 3 possible git commit messages at the end of every iteration.

- Write a clear subject line, preferably within 50 characters and never over 72.
- Use imperative mood: `Add README.md`, not `Added README.md`.
- Capitalize the subject and do not end it with a period.
- Optionally prefix the subject with a scope or category, such as `Person class:` or `bug fix:`.
- For non-trivial changes, add a body separated from the subject by a blank line.
- Wrap body text at 72 characters and use paragraphs or bullet points for clarity.
- Explain what changed and why; let the diff show how.
- Describe the existing situation in present tense and the proposed action in imperative mood.
- If the message becomes too long or covers unrelated changes, split the commit.

### Branch names

- Use meaningful kebab-case names, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords`, such as `1234-ui-freeze-error`.

## SWE Practices

You are to OOP-ify where needed. You have to justify whether something needs to be OOP, or not.
You are an expert software engineer and expected to follow all good SWE practices.

## JUnit test coverage

Maintain a minimum JUnit test coverage target of 67% for the production Java code.
After every code change, review and update the JUnit tests as needed to keep test coverage at or above 67%.
Run the JUnit test suite after each code change and report whether the tests pass.
At the end of every code iteration, suggest relevant JUnit tests to add or update.
Focus JUnit tests on the top ~67% highest-value methods (prioritizing complex, core, or critical business logic).

## UI test workflow after code updates

After every code update:

1. Review `test/ui-test-plan.md` and update it if the code change adds, removes, or changes user-visible console behaviour. You must tell the user what has been modified, and the reasoning.
2. Invoke the project-local `$test-ui` skill and run the documented UI test cases.

Do not consider a code update complete until both checks are done. If the UI test plan has a placeholder or an expected-output mismatch, report it clearly.

You are to report the success or failure of tests in every response that modifies code.

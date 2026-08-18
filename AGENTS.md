# Project context

This repository is a starter template for a greenfield Java project used in an
introductory software engineering course in an undergraduate computer science program.
Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a
project in this repository. If the user identifies themselves as an instructor or
another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Fluent in Python with medium-sized codebases
  (10 years, >50k LOCs), beginner in Java (<1 year, ~100 LOCs)
* IDE and level of expertise: IntelliJ, competent

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use
  of AI. For example:

    * When suggesting a Git command, briefly explain what it does.
    * Add explanatory Javadoc comments to all classes and public methods, subject to
      the exceptions in the Java coding standard below. Add comments to nontrivial
      fields when their purpose or behavior is not obvious.
    * Make generated code as self-explanatory as possible, and include explanatory
      comments where they improve understanding.
    * When faced with a design choice, choose the simplest option that is sufficient for
      the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use
`sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Required Java coding standard

Follow the basic and intermediate rules in the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
The advanced rules are optional. In particular:

* Use lowercase package names. Use nouns in `PascalCase` for classes and enums,
  `camelCase` for variables, verbs in `camelCase` for methods, and
  `SCREAMING_SNAKE_CASE` for constants.
* Write names and comments in English. Do not fully capitalize acronyms embedded in
  names (e.g., use `exportHtmlSource`, not `exportHTMLSource`).
* Name boolean variables and methods so they read as booleans, normally using prefixes
  such as `is`, `has`, `can`, and `should`. Use plural names for collections.
* Test methods may use the format
  `featureUnderTest_testScenario_expectedBehavior`.
* Indent using four spaces, never tabs. Aim for no more than 110 characters per line;
  120 characters is the hard limit. Indent wrapped lines by eight additional spaces.
* Use K&R/Egyptian braces. Put spaces around operators and after commas and Java
  keywords. Separate logical sections with a blank line.
* Use braces for every loop and conditional body, including one-line bodies. Put the
  conditional and its body on separate lines. Mark intentional `switch` fall-through
  with `// Fallthrough`.
* Put every class in a package, keep import ordering consistent, and import classes
  explicitly rather than using wildcard imports.
* Attach array brackets to the type (e.g., `int[] values`). Initialize variables when
  declaring them and give them the smallest practical scope.
* Do not expose fields publicly, except constants or fields in a genuine behavior-free
  data class.
* Add descriptive Javadoc/header comments to every class and public method. Comments
  may be omitted for getters/setters, test code, and overrides whose inherited Javadoc
  applies exactly. Start Javadoc with a short summary sentence such as `Returns ...`,
  `Adds ...`, or `Sends ...`, and use proper Javadoc structure and punctuation.
* Use English with American spelling for comments and indent comments consistently
  with the surrounding code.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the
rationale for the change.
Do not commit or push unless explicitly asked.

Follow the commit subject rules in the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html):

* Give every commit a meaningful subject.
* Aim for no more than 50 characters; 72 characters is the hard limit.
* Use imperative mood, capitalize the first letter, and do not end with a period.
* An optional scope or category prefix is allowed (e.g.,
  `Parser: Handle invalid dates`).

Commit bodies are optional. If a body is used:

* Separate it from the subject with a blank line, wrap it at 72 characters, and use
  blank lines between paragraphs.
* Explain what changed and why, rather than narrating how the code implements it.
* Use present tense for the existing situation and imperative mood for the change.
* Avoid repeating information already clear from comments or the diff.

## Individual project setup and workflow

Follow the
[Week 2 project instructions](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week2/project.html)
and the
[course standards and conventions](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html).

* Keep the repository name as `ip`, the default branch as `master`, and source code
  under `[project root]/src` so the grading scripts can find the project.
* The fork must contain the full repository, and GitHub Issues must be enabled.
* Do not place the repository in a cloud-synced directory such as OneDrive or Dropbox.
* Verify the setup by running `Duke.java`.
* Complete the Week 2 increments in order: `Level-0`, `Level-1`, `Level-2`, `Level-3`,
  `Level-4`, `Level-5`, `Level-6`, and `A-Enums`.
* Commit at meaningful points and at least after every increment. Tag the completing
  commit using the increment's exact ID.
* Do not commit `.class` files or other generated files.
* When explicitly asked to push completed work, push both commits and tags because a
  normal `git push` does not automatically push tags.
* Spread work across the assigned weeks; do not work more than one week ahead.

The linked Markdown and Google documentation style guides are optional.
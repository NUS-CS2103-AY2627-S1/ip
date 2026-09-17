# Coding Standards

This project follows the coding standards below. Apply them when writing, reviewing, or modifying code and documentation. When helping with this project, check proposed and completed changes against these standards and point out relevant violations.

## Java Standards

Follow the complete [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/index.html). This includes the [basic and intermediate Java rules](https://se-education.org/guides/conventions/java/intermediate.html). For topics the SE-EDU standard does not cover, follow the Google Java Style Guide as directed by SE-EDU.

Key checks include:

* Use lower-case package names, PascalCase noun names for classes and enums, camelCase verb names for methods, camelCase names for variables, and SCREAMING_SNAKE_CASE for constants.
* Give boolean variables and methods names that read as booleans, normally using prefixes such as `is`, `has`, `can`, `should`, or `was`.
* Use plural names for collections and write all identifiers and comments in English.
* Indent with four spaces, never tabs. Aim for lines shorter than 110 characters and do not exceed 120 characters. Indent wrapped lines by eight additional spaces.
* Use K&R braces and include braces for every loop and conditional body, including single-statement bodies.
* Put each class in an appropriate package, group related classes, keep import ordering consistent, and use explicit imports instead of wildcard imports.
* Attach array brackets to the type, as in `int[] values`.
* Declare variables in the smallest practical scope and initialize them where they are declared when a valid initial value is available.
* Preserve encapsulation: do not expose class variables publicly unless the class is a behavior-free data class; constants are exempt.
* Organize class members consistently: documentation, declaration, class variables, instance variables, constructors, and methods.
* Write Javadoc for every class and public method, except getters/setters, test code, and overrides whose inherited documentation applies exactly. Also document non-trivial private methods.
* Use clear comments to explain intent or rationale rather than restating obvious code. Write comments in English using American spelling.

## Git Conventions

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html), subject to the project-specific exception below.

Every commit must have a clear subject line that:

* Uses the imperative mood, for example `Add task validation` rather than `Added task validation`.
* Starts with a capital letter.
* Does not end with a period.
* Aims for 50 characters or fewer and never exceeds 72 characters.
* May begin with a useful scope or category, such as `Parser: Handle empty input`.

Writing a commit message body is optional, including for non-trivial commits. If a body is included, follow at least these basic conventions:

* Separate the subject and body with one blank line.
* Wrap body text at 72 characters.
* Separate paragraphs with blank lines and use bullet points where helpful.
* Explain what changed and why; let the diff show how it changed.
* Avoid repeating information already evident from the code or its comments.

## Markdown Conventions

Follow the [SE-EDU Markdown coding standard](https://se-education.org/guides/conventions/markdown.html) for GitHub Flavored Markdown.

Key checks include:

* Follow Markdown syntax strictly so documents render consistently outside GitHub as well.
* Do not wrap natural-language Markdown lines to a fixed width.
* Put a blank line before each list and code block.
* Put a space after heading markers, for example `## Heading`, and separate headings from surrounding content with blank lines.
* Prefix every line of a multi-line blockquote with `>`.
* Use `1.` for every item in an ordered list so items can be inserted or reordered easily.
* Use `*` for unordered-list bullets.
* Use underscores for italics, for example `_important_`.

## Review Expectation

For future project work, use these standards as a review checklist. Before considering a change complete:

1. Check changed Java files against the complete Java standard.
1. Check documentation changes against the Markdown standard.
1. If suggesting or creating a commit message, check its subject and any optional body against the Git conventions above.
1. Explain significant issues and their rationale so corrections also support learning.

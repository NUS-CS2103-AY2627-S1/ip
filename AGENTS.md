# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [Occasionally good to horrendous]
* IDE and level of expertise: [Decent - clicking buttons]

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

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Java coding standard

All Java code in this project MUST follow the SE-EDU Java coding standard (intermediate level):
https://se-education.org/guides/conventions/java/intermediate.html

Apply it when writing new code, editing existing code, and reviewing code. Where the standard and the surrounding code disagree, follow the standard.

**Naming**

* Packages: all lower case. Classes and enums: nouns in PascalCase (`Task`, `TaskList`). Methods: verbs in camelCase (`getName()`). Variables: camelCase. Constants: SCREAMING_SNAKE_CASE.
* Abbreviations are not uppercased inside a name: `exportHtmlSource()`, not `exportHTMLSource()`.
* Booleans sound like booleans: `isSet`, `hasData`. Boolean setters take the form `void setFound(boolean isFound)`.
* Collections take the plural: `Collection<Point> points`.
* Loop iterators may be `i`, then `j`, `k` for nested loops only.
* Test methods: `featureUnderTest_testScenario_expectedBehavior()`.

**Layout**

* 4 spaces of indentation, never tabs.
* Line length: soft limit 110 chars, hard limit 120.
* Wrapped lines indent 8 spaces past the parent line. Break after a comma; break before an operator.
* A method or constructor name stays attached to its opening `(`.

**Braces and statements**

* K&R (Egyptian) braces: opening brace on the same line.
* Loop bodies and conditionals are ALWAYS braced, however short. Never `if (isDone) doCleanup();`.
* The conditional goes on its own line.
* A traditional `switch` case without a `break` needs an explicit `// Fallthrough` comment.

**Whitespace**

* Spaces around operators, after reserved words, and after commas.
* Separate logical units within a block with one blank line.

**Types, variables, imports**

* Array brackets attach to the type: `int[] a`, never `int a[]`.
* Initialize variables where declared, in the smallest possible scope.
* Class variables are never `public` unless the class is a pure data class. Constants are exempt.
* Every class lives in a package. List imports explicitly: never `import java.util.*;`. Keep import order consistent (static, then `java`, `javax`, `org`, `com`, then others).

**Comments and Javadoc**

* English, American spelling.
* Header comments are REQUIRED for all classes and all public methods. They may be omitted for getters/setters, for overriding methods where the parent's Javadoc applies as-is, and in test code.
* First sentence is a short summary starting with a verb: `Returns ...`, `Adds ...`.
* Blank line between the description and the `@param` section; punctuation after each parameter description; no blank line between the Javadoc block and what it documents.
* `@return` may be omitted when nothing is returned or the return is obvious; `@param` may be omitted when every parameter name is self-explanatory.
* Comments are indented to match the code they describe.

## Git commit message standard

All commit messages and branch names in this project MUST follow the SE-EDU Git conventions:
https://se-education.org/guides/conventions/git.html

**Subject line**

* Imperative mood: "Add README.md", not "Added README.md" or "Adding README.md". It should complete the sentence "If applied, this commit will ___".
* Capitalize the first letter. No trailing period.
* Limit to 50 characters, hard limit 72.
* An optional scope prefix is allowed: `Person class: Remove static imports`.

**Body**

* Separate subject from body with a blank line. Wrap the body at 72 characters.
* Explain WHAT and WHY, not HOW — the diff already shows how.
* Useful order: current situation, why a change is needed, what is being done, why it is done that way.
* Present tense for the current situation, imperative mood for the change.

**Branch names**

* Meaningful keywords in kebab-case: `refactor-ui-tests`.
* For issue branches: `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.

Increment IDs belong in the tag (`git tag Level-7`), not in the commit subject line.

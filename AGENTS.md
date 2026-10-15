# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: 6/10 (Built more than 3 full stack website with different languages like Golang, NestJS, ReactJS, VueJS, Laravel)
* IDE and level of expertise: 4/10

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

# Java coding standard

For every Java source or test change, invoke `$seedu-java-coding-standard` and
follow the SE-EDU basic and intermediate Java conventions. This includes
package/import organization, naming, K&R braces, four-space indentation,
120-character line limits, complete braces around control flow, Javadocs for
classes/public methods/non-trivial private methods, descriptive test names,
and keeping generated binaries/build output out of version control.

# Git standard

For all future commits and branches, invoke `$seedu-git-standard` and follow
the SE-EDU Git conventions. Commit subjects must be imperative, capitalized,
period-free, and no longer than 72 characters (prefer 50). Non-trivial
commits must have a blank-line-separated body wrapped at 72 characters that
explains what changed and why. Branch names must be meaningful kebab-case, or
issue-number-prefixed kebab-case when tied to an issue. Do not commit or push
unless explicitly requested.

# UI test workflow

After every code update, review `test/ui-test-plan.md` and update it when the
change adds, removes, or alters observable program behavior. Then invoke the
project-specific `$test-ui` skill to run the listed UI tests and record the
console input/output. If no test plan exists, create it before invoking the
skill. Follow the skill's fail-fast behavior: stop at the first failed case and
report the actual and expected output.

# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

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

All Java code in this project (new or edited) must follow the SE-EDU
intermediate Java coding standard, captured in the `seedu-java-coding-standard`
skill. Apply it automatically; no need to wait for the user to ask.

## Git

Use lightweight tags unless the user requests an annotated tag.
Commit messages (subject line and body) must follow the SE-EDU Git
conventions, captured in the `seedu-git-standard` skill — include enough
detail in the body to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Testing

After each code update that changes the chatbot's runtime behavior:

1. Update `test/ui-test-plan.md` if the change adds, removes, or alters a
   command or its output (new/changed test cases; never hand-type expected
   output — capture it by actually running the program).
2. Invoke the `test-ui` skill to compile the program and run the test plan
   against it, reporting any failure immediately.

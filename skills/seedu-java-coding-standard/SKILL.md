---
name: seedu-java-coding-standard
description: Review, write, and update Java code in this repository according to the SE-EDU basic and intermediate Java coding standard. Use for every task that creates, changes, or reviews production or test Java code in this project.
---

# SE-EDU Java Coding Standard

Apply the project's Java conventions whenever working with a `.java` file.

Before reviewing or changing Java code, read
[references/java-standard.md](references/java-standard.md). Treat its mandatory rules as project
requirements. For topics it does not cover, use the Google Java Style Guide as directed by the
authoritative SE-EDU standard.

When a request is specifically about coding-standard compliance:

1. Audit all Java files in scope, including tests, without assuming an existing style is correct.
2. Make the smallest behavior-preserving changes that achieve compliance.
3. Do not add comments that merely restate self-explanatory code. Apply the documented Javadoc
   exemptions for getters, setters, exact-behavior overrides, and test code.
4. Check the final diff for naming, imports, indentation, wrapping, braces, whitespace, declaration
   scope, and comments.
5. Compile and run the relevant tests with Java 25. Generate Javadocs when production documentation
   changed.

User instructions take precedence if they deliberately require a different style. State any such
exception in the handoff rather than silently mixing conventions.

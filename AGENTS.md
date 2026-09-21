# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Working style

Keep changes focused and explanations brief. Avoid modifying unrelated files.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing

Run relevant JUnit tests after Java behavior changes. Add or update tests when
behavior changes or existing coverage is affected. Manual UI tests are optional
and should be run only when explicitly requested.

## Git

Do not commit, tag, or push unless explicitly asked. Use lightweight tags unless
an annotated tag is requested.

## Java coding standard

Java changes must follow `.codex/skills/seedu-java-coding-standard/SKILL.md`.
Add Javadoc for public APIs and non-obvious logic; trivial methods do not need it.

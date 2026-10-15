---
name: seedu-java-coding-standard
description: Apply and review this project's Java code against the SE-EDU basic and intermediate coding standard. Use whenever creating, editing, refactoring, or reviewing Java code in this repository.
---

# SE-EDU Java Coding Standard

Apply the project rules in [references/standard.md](references/standard.md) to all Java source and test code touched by the task. Read that reference before changing or reviewing Java code.

Preserve program behavior unless the user asks for a behavior change. Fix violations in touched code and any directly related code needed to keep names, documentation, and tests consistent.

After editing Java code:

1. Manually review the changes against the reference; Checkstyle does not enforce every rule.
2. Use Java 25 as required by `AGENTS.md`.
3. Run `./gradlew checkstyleMain checkstyleTest` and resolve all reported violations.
4. Follow the project's separate post-change test requirements in `AGENTS.md`.

For topics the reference does not cover, follow the Google Java Style Guide unless doing so conflicts with repository-specific instructions.

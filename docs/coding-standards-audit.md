# Basic Java Coding-Standard Audit

Reference: [SE-EDU Java Coding Standard](https://se-education.org/guides/conventions/java/intermediate.html).

## Refactored violations

- Added the `labubu` package to every Java class.
- Replaced the wildcard `java.util.*` import with explicit imports.
- Reformatted the `else` chain to use K&R brace style.
- Wrapped source lines longer than the 110-character soft limit.
- Added missing Javadocs to public classes, constructors, and non-obvious public methods.
- Reduced `userInput` to the smallest practical scope.
- Made task fields private or protected to state their intended accessibility.

## Remaining half-violations

- `Labubu.java` remains directly under `src/main/java` rather than the `labubu` package directory. It compiles correctly, but relocating it would align the directory layout with the package name.
- `Labubu.main` still combines console I/O, command parsing, validation, and task-list management. This is acceptable for the current small project, but future versions should separate those responsibilities.
- The `Task` subclasses keep their display fields mutable. They are private and not currently changed, but immutable fields would better express that task details do not change after creation.

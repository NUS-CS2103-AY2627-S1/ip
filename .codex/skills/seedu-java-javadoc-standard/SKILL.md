---
name: seedu-java-javadoc-standard
description: Review and lint this project's JavaDoc against the established SE-EDU documentation conventions and Checkstyle rules.
---

# Seedu JavaDoc Standard

Apply this skill when reviewing or editing JavaDoc in production or test Java code. Preserve the existing behavior of the code unless the user requests a behavioral change.

## Documentation rules

- Add a descriptive JavaDoc header to every public class, interface, enum, constructor, and method. Getters, setters, and exact overrides may omit a duplicate comment; use `@inheritDoc` when inherited documentation applies but a local comment is still useful.
- Document non-trivial protected and private methods, constructors, and fields when their purpose or behavior is not obvious from their names and types.
- Start the first sentence with a concise verb such as `Creates`, `Returns`, `Adds`, `Parses`, or `Checks`. Describe the contract and observable behavior rather than repeating the implementation.
- Use American English, complete sentences, and punctuation. Keep comments focused and use `{@code ...}` for code symbols, literals, and short examples.
- Put a blank line between the description and block tags. Use `@param` for every meaningful parameter, `@return` for non-void results, and `@throws` for documented exceptional conditions. Describe the condition that causes an exception rather than merely naming the exception.
- Put one block tag per line. Continue long tag descriptions with at least four additional spaces of indentation, and keep all lines within the project's 120-character limit where practical.
- Place `/**` immediately before the declaration it documents. Format multi-line comments with a leading asterisk on every non-empty line and a space after that asterisk.

## Lint workflow

1. Inspect all changed `.java` files, including tests, and identify public API and non-trivial implementation elements that need documentation.
2. Run `./gradlew checkstyleMain checkstyleTest` (or `gradlew.bat checkstyleMain checkstyleTest` on Windows) with Java 25. Treat JavaDoc violations from the project's `config/checkstyle/checkstyle.xml` as findings to fix, not as reasons to weaken the configuration.
3. Review the resulting JavaDoc manually for accuracy: parameter names must match the declaration, return descriptions must match the actual result, and documented exceptions must be possible on the documented path.
4. Re-run the lint task after editing. Report any pre-existing violations separately from violations introduced by the change.

The project's Checkstyle configuration is the executable source of truth for structural JavaDoc checks, including tag order and location, leading asterisks, type and method documentation, and invalid JavaDoc placement. This skill supplies the readability and contract-accuracy checks that static lint cannot establish.

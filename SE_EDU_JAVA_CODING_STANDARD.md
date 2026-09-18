# SE-EDU Java Coding Standard: Quick Reference

This project follows the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Use this checklist when writing or reviewing Java code.

## Naming

- Packages: lowercase (`martin.storage`).
- Classes, enums, and interfaces: PascalCase nouns (`TaskList`).
- Methods: camelCase verbs (`getAllTasks()`).
- Variables and parameters: camelCase (`filePath`).
- Constants: `SCREAMING_SNAKE_CASE` (`MAX_RETRIES`).
- Boolean names: use prefixes such as `is`, `has`, `can`, `should`, or `was` (`isDone`).
- Collections: use plural names (`tasks`, `users`).
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.

## Layout and whitespace

- Use four spaces for indentation; never use tabs.
- Keep lines at 120 characters or fewer (prefer 110).
- Use K&R braces:

```java
if (condition) {
    doSomething();
}
```

- Put spaces around operators, after commas, and after Java keywords.
- Separate logical units in a block with one blank line.
- When wrapping lines, break after commas or before operators and indent continuation lines by eight additional spaces.

## Statements and variables

- Put every class in a package.
- Keep imports explicit and consistently ordered; never use wildcard imports.
- Declare variables in the smallest practical scope and initialize them at declaration when possible.
- Use braces for every loop and conditional, even for a single statement.
- Keep conditional bodies on separate lines.
- Mark intentional traditional-switch fall-through with `// Fallthrough`.

## Comments and Javadocs

- Write comments in English using American spelling.
- Add descriptive Javadoc to every public class and public method.
- Javadocs may be omitted for simple getters/setters, exact inherited overrides, and test code.
- Start method summaries with a third-person verb such as “Returns”, “Adds”, or “Creates”.
- Put a blank line before Javadoc tags and end tag descriptions with punctuation.

## Before submitting

- Check names, indentation, braces, imports, line lengths, and Javadocs.
- Run the project tests using Java 25.
- Explain any intentional exception to this standard.

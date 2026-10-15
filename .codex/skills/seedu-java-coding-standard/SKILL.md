---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when editing Java code in this project.
---

# SE-EDU Java coding standard

Apply these rules to all Java production and test code in this project. Use the
official standard for details: <https://se-education.org/guides/conventions/java/intermediate.html>.

## Naming

- Use lowercase package names.
- Use PascalCase nouns for classes and enums.
- Use camelCase nouns for variables and verbs for methods.
- Use SCREAMING_SNAKE_CASE for constants.
- Use names that are clear, English, and consistent with their scope.
- Name boolean variables and methods as predicates, such as `isDone` or `hasData`.
  This applies to local variables and parameters too, not only fields: write
  `boolean isEscaped`, never `boolean escaped`. Checkstyle does not catch this,
  so check it yourself.
- Use plural names for collections and a common prefix for associated constants.
- Do not write acronyms in all capitals inside names.

## Layout and structure

- Use four spaces for indentation and K&R braces.
- Keep lines at or below 120 characters; wrap long lines with eight-space continuation indentation.
- Separate logical units with one blank line.
- Keep imports explicit and consistently ordered.
- Organize each class as documentation, declaration, static fields, instance fields, constructors,
  then methods.
- Put access modifiers first and use the standard modifier order.
- Attach array brackets to the type, initialize variables at declaration when practical, and keep
  variables in the smallest useful scope.
- Use `this` only when a field is shadowed by a parameter or local variable.
- Always use braces for loop and conditional bodies.

## Size

- Keep classes focused on one responsibility. A class past roughly 300 lines is a
  signal that it has absorbed several jobs and should be split.
- Keep methods short enough to read without scrolling, roughly 30 lines. Extract a
  named helper for each step rather than letting a method grow.

## Single Level of Abstraction Principle

Every statement in a method must sit at the same level of abstraction. A method
either orchestrates named steps or performs one step; it never does both.

A method violates SLAP when a reader has to shift between "what is happening"
and "how it happens" while reading it top to bottom. The usual symptoms are a
loop or a string-building block sitting next to a call to a well-named helper,
or a method whose name describes one thing while its body also handles setup,
formatting, and error reporting.

Fix it by extracting the low-level fragment into a method whose name states the
step in the vocabulary of the surrounding method. The caller then reads as a
sequence of steps, and each extracted method contains only detail at its own
level.

```java
// Violates SLAP: dispatching a command sits beside character-level parsing.
private String handle(String command) {
    if (command.startsWith("todo")) {
        String description = command.substring(4).trim();
        if (description.isEmpty()) {
            throw new SevenSixException("A todo needs a description.");
        }
        return addTask(new Todo(description));
    }
    return showUnknownCommand();
}

// Follows SLAP: the method reads as named steps at one level.
private String handle(String command) {
    if (isCommand(command, "todo")) {
        return addTodo(command);
    }
    return showUnknownCommand();
}
```

Apply the same rule to classes. A class follows SLAP when its public methods
describe one kind of work, so `TaskStorage` exposes reading and writing rather
than also exposing string escaping.

## Comments

- Write comments in clear American English.
- Add descriptive JavaDoc to every public class and public method, and to every non-trivial private
  method.
- Start JavaDoc with a short summary sentence and document parameters, return values, and exceptions
  when they add useful information.

Review this skill whenever Java code is added or changed, and preserve these conventions in the
resulting code.

After changing Java code, run `./gradlew checkstyleMain checkstyleTest` and then
follow the `code-quality-self-check` skill for the rules Checkstyle cannot
enforce.

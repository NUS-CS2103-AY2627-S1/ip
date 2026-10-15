---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard (basic and intermediate rules) when creating, changing, reviewing, or documenting Java code in this project.
---

# SE-EDU Java Coding Standard

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html) for every Java code change or review in this project. Use Google Java Style for topics the SE-EDU standard does not cover. Do not impose optional advanced SE-EDU rules unless the user requests them or the existing code already follows them.

## Apply the standard

- Keep package names lowercase; use PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase English names for variables, and SCREAMING_SNAKE_CASE for constants. Treat acronyms as words.
- Name booleans to read as boolean expressions, normally with prefixes such as `is`, `has`, `was`, `can`, or `should`. Use plural names for collections.
- Indent with 4 spaces and never tabs. Prefer lines below 110 characters and never exceed 120. Indent continuations by 8 additional spaces, normally breaking after commas and before operators.
- Use K&R braces and braces around every loop and conditional body. Put each body on a separate line. Mark intentional traditional `switch` fall-through with `// Fallthrough`.
- Use standard Java whitespace. Separate logical units inside a block with one blank line.
- Put every class in a package. Import classes explicitly without wildcards, remove unused imports, and keep import groups and ordering consistent with neighboring files.
- Attach array brackets to the type. Declare variables in the smallest practical scope and initialize them at declaration when a valid value is available.
- Organize class members predictably: documentation, type declaration, static fields, instance fields, constructors, then methods. Within field groups, order access from public to private. Put access modifiers first.
- Do not expose mutable fields publicly unless the class is intentionally a behavior-free data class.
- Write comments in English using American spelling. Add descriptive JavaDoc to classes and public methods except test code, obvious getters/setters, and overrides whose inherited contract applies exactly. Document non-trivial private methods when the explanation adds value.
- Format JavaDoc with a concise third-person summary such as `Returns`, `Creates`, or `Adds`; a blank line before tags; complete `@param`, `@return`, and `@throws` tags where useful; and punctuation in tag descriptions.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior` when descriptive names would otherwise be unwieldy.

## Review workflow

1. Inspect changed Java files and nearby code before editing so local conventions remain consistent.
2. Apply all relevant basic and intermediate rules without refactoring unrelated correct code solely for optional preferences.
3. Check modified Java files for tabs and lines over 120 characters, then compile and run the project-required tests.

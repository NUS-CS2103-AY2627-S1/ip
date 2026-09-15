# SE-EDU Java standard: project checklist

This checklist summarizes the [SE-EDU basic + intermediate Java coding
standard](https://se-education.org/guides/conventions/java/intermediate.html).
That page is authoritative. Use the Google Java Style Guide only for topics it
does not cover.

## Naming

- Use lowercase package names rooted in the project name, such as
  `luigibot.task`.
- Name classes and enums with English nouns in PascalCase.
- Name methods with English verbs in camelCase.
- Name variables in camelCase and constants in SCREAMING_SNAKE_CASE.
- Write acronyms as normal words inside names, for example `exportHtml`, not
  `exportHTML`.
- Give wide-scope variables descriptive names. Short names such as `i` are
  suitable only for small-scope scratch values and loop indexes.
- Make boolean names read as booleans, normally with `is`, `has`, `was`,
  `can`, or `should`.
- Use plural names for collections.
- JUnit test methods may use
  `featureUnderTest_testScenario_expectedBehavior()`.

## Layout

- Indent with four spaces and never tabs.
- Aim for at most 110 characters per line and never exceed 120 characters.
- Indent wrapped continuation lines by eight spaces relative to the parent.
- Break after commas and before operators when wrapping. Keep a method name
  attached to its opening parenthesis.
- Use K&R braces: the opening brace stays on the declaration or control-flow
  line.
- Separate logical units inside a block with a blank line.
- Surround operators with spaces. Put spaces after keywords, commas, and
  semicolons in `for` statements.

## Packages, imports, and declarations

- Put every class in a suitable package under its Java source root.
- Import each type explicitly; do not use wildcard imports.
- Keep import ordering consistent and remove unused imports.
- Attach array brackets to the type, for example `String[] args`.
- Put access modifiers first, for example `public static`, not `static public`.
- Do not expose mutable class variables publicly. Constants are exempt.

## Variables and control flow

- Declare variables in the smallest practical scope and initialize them at the
  declaration when a valid value is available.
- Use braces around every loop and conditional body, including one-line bodies.
- Put each conditional body on its own line.
- Format `if`/`else`, loops, `switch`, and `try`/`catch` consistently with K&R
  braces.
- Mark deliberate traditional-switch fall-through with `// Fallthrough`.

## Comments and Javadocs

- Write comments in English using American spelling. Intentional Luigi-style
  string literals are user-facing dialogue, not code comments.
- Add descriptive Javadocs to every public class and public method, except
  straightforward getters/setters, test code, and overrides whose inherited
  documentation applies exactly.
- Add header comments to non-trivial private methods when they clarify the
  method's contract or purpose.
- Start a method Javadoc summary with a third-person verb such as `Returns`,
  `Adds`, or `Saves`.
- Put `/**` on its own line, align each `*`, leave one blank line before block
  tags, and place the comment immediately before the declaration.
- Use either all useful `@param` tags or none. End parameter descriptions with
  punctuation. Document return values and thrown exceptions when they are not
  already obvious from the summary.
- Use `{@inheritDoc}` when an override needs to reuse and extend inherited
  documentation.
- Indent comments with the code they describe. Avoid comments that merely
  repeat the implementation.

## Common mistakes

- Treating compilation or passing tests as proof that formatting is compliant.
- Applying an IDE formatter without reviewing whether its line breaks improve
  readability.
- Renaming Luigi's dialogue to formal English even though it is intentional UI
  personality.
- Adding redundant comments to self-explanatory code.
- Reformatting unrelated files during a focused feature change.

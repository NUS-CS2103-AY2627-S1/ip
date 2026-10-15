# Project Java standard checklist

This checklist distills the rules that commonly affect this project. The authoritative
source is the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

## Naming

- Use lowercase package names organized under the project name.
- Use PascalCase nouns for classes and enums.
- Use camelCase verbs for methods and camelCase English names for variables.
- Use SCREAMING_SNAKE_CASE for constants.
- Name booleans so they read as booleans, preferably with `is`, `has`, `was`, `can`, or
  `should` prefixes.
- Use plural names for collections.
- Keep acronyms lowercase inside camelCase names.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`.
- Prefer longer descriptive names for wider scopes; short scratch names are acceptable
  for narrow scopes and loop indices.

## Layout and statements

- Indent with four spaces, never tabs.
- Prefer lines of at most 110 characters; never exceed 120 characters.
- Indent wrapped lines eight spaces beyond the parent line. Break after commas and
  before operators, including dots in chained calls.
- Use K&R braces. Always use braces for loops and conditionals, including one-line
  bodies, and put conditional bodies on separate lines.
- Indent switch cases one level inside the switch. Mark intentional traditional-switch
  fallthrough with `// Fallthrough`; grouped arrow labels do not need this comment.
- Put spaces around operators and after keywords, commas, and `for` semicolons.
- Separate logical units with a blank line where it improves readability.
- Declare variables in the smallest useful scope and initialize them where declared.
- Keep non-constant fields non-public unless the class is a behavior-free data class.

## Packages and imports

- Put every class in a package.
- List imports explicitly; never use wildcard imports.
- Keep imports minimal and consistently ordered: static imports first, then `java`,
  `javax`, third-party, and project imports, with blank lines between groups.
- Attach array brackets to the type, such as `String[] args`.

## Documentation

- Write comments in English using American spelling.
- Add descriptive Javadocs to every public class and public method, including
  constructors. Getters, setters, overrides whose inherited documentation remains
  accurate, and test classes or methods may omit them.
- Begin Javadocs with a short summary sentence using third-person wording such as
  `Returns`, `Adds`, or `Creates`.
- Put `/**` on its own line, align each `*`, and leave a blank line before tag sections.
- Either document all parameters or omit all self-explanatory parameters. End parameter
  descriptions with punctuation and keep `@return` and `@throws` descriptions clear.
- Indent implementation comments with the code they describe. Avoid comments that only
  restate self-explanatory code.

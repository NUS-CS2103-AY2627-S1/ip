---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic + intermediate Java coding standard whenever creating, editing, refactoring, or reviewing Java code in this repository.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU basic + intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
to all application and test Java code in this repository. Use the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) only for topics the
SE-EDU standard does not cover.

## Apply the standard

- Use lowercase logical package names; PascalCase noun names for classes and enums; camelCase verb
  names for methods; camelCase variable names; and SCREAMING_SNAKE_CASE constant names.
- Use English names and comments, American spelling, boolean-sounding boolean names, plural names
  for collections, and names whose descriptiveness matches their scope.
- Indent with four spaces and no tabs. Keep lines under 120 characters, aim for 110, and indent
  wrapped lines eight spaces beyond their parent. Break after commas and before operators when
  wrapping. Use K&R braces and preserve readable blank lines between logical units.
- Surround operators with spaces, put spaces after Java keywords, commas, and `for` semicolons, and
  keep every loop and conditional body in braces on separate lines.
- Put every class in a package. Keep import ordering consistent, list imports explicitly, and remove
  unused imports. Attach array brackets to the type.
- Initialize variables at declaration where a real value is available, declare them in the smallest
  practical scope, and keep non-constant fields non-public unless the class is behavior-free data.
- Write English Javadoc for every public class and public method except getters/setters, exact
  overrides, and test code. Start method summaries with a third-person verb such as `Returns`,
  `Adds`, or `Sends`; use a separate `/**` line; align stars; separate the description from tags;
  punctuate parameter descriptions; and keep all-or-none `@param` tags when they add value.
- Mark intentional traditional-switch fallthrough with `// Fallthrough`.

## Review changes

After editing Java, inspect all touched Java files—not only the changed lines—for violations that
the edit exposes. Correct violations within the touched code when doing so is behavior-preserving;
do not expand a focused task into an unrelated redesign. Before handing off, check at minimum for
tabs, lines over 120 characters, wildcard imports, naming problems, missing braces, and missing or
malformed required Javadocs.

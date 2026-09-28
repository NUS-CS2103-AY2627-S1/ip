---
name: seedu-java-coding-standard
description: "Apply the SE-EDU Java basic and intermediate coding standard to Java production and test code in this project."
---

# SE-EDU Java Coding Standard

Apply this skill to every Java file changed in this project, including tests.

- Use lowercase package names; PascalCase nouns for classes and enums; camelCase for variables and
  ordinary method names. Boolean names should read like boolean questions (`isDone`, `hasData`),
  and collection names should be plural.
- Use four spaces, never tabs, and keep lines at or below 120 characters. Wrap long lines for
  readability, with continuation indentation of eight spaces relative to the parent line. Keep
  method or constructor names attached to the following `(`.
- Keep logical units separated by one blank line. Use braces for every loop and conditional body.
  Put conditions on their own line and use spaces around operators and after commas.
- Put every class in a package. Keep imports explicit and consistently ordered: static imports,
  standard Java imports, `org.*` imports, then other project/third-party imports; sort each group
  alphabetically and separate groups with blank lines.
- Initialize variables at declaration where practical, keep them in the smallest useful scope, and
  avoid public mutable fields. Put array brackets on the type (`String[] args`).
- Write English, American-spelled comments. Add descriptive Javadocs to public classes and public
  methods, except getters/setters, test code, and overridden methods whose inherited documentation
  applies unchanged. Start method summaries with a verb such as `Returns`, `Adds`, or `Creates`.
- Use the SE-EDU standard as the authority for details not listed here:
  https://se-education.org/guides/conventions/java/intermediate.html

Before handing off Java changes, inspect the complete diff and run the project's Checkstyle and
test tasks when the Gradle environment permits it.

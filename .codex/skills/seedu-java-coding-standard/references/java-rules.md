# Java basic and intermediate rules

Use this checklist for production and test Java. It summarizes the official [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html), reviewed on 2026-08-27. For a topic absent here and on that page, use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Naming

- Write package names in lowercase. For school projects, start with the group or project name, followed by logical package names.
- Use noun-based `PascalCase` class and enum names.
- Use `camelCase` variable names and verb-based `camelCase` method names.
- Use `SCREAMING_SNAKE_CASE` for constants. Give associated constants a common prefix.
- In test method names, underscores may separate `featureUnderTest_testScenario_expectedBehavior`; the last one or two parts may be omitted when unnecessary.
- Keep abbreviations and acronyms lowercase within names: `exportHtmlSource`, not `exportHTMLSource`.
- Write all names in English.
- Use longer, descriptive names for larger scopes. A field such as `dueDate` needs a descriptive name; short scratch names such as `i` are suitable only for a few nearby lines. Reserve `j` and `k` for nested loops.
- Name booleans so they read as booleans, preferably with `is`, `has`, `was`, `can`, or `should`. A boolean setter takes the form `setFound(boolean isFound)`.
- Use plural names for collections and arrays.

## Layout

- Indent with 4 spaces, never tabs.
- Aim for at most 110 characters per line; 120 characters is the hard limit.
- Indent wrapped lines 8 spaces beyond the parent line. Optimize wrapping for readability rather than accepting IDE output blindly.
- Break after commas and before operators, including `.`, the `&` in type bounds, and the `|` in multi-catch. Keep a method or constructor name attached to its opening parenthesis and prefer higher-level breaks.
- Use either an inline ternary or place `?` and `:` on their own consistently indented continuation lines.
- Use K&R braces: the opening brace stays on the declaration or control-statement line.
- Format method declarations and `if`/`else`, `for`, `while`, `do`/`while`, `switch`, `try`/`catch`/`finally` blocks in the standard K&R forms.
- Add `// Fallthrough` whenever a colon-style `switch` case intentionally continues into the next case.
- Put spaces around operators, after Java keywords, after commas, around a ternary colon, and after semicolons in `for` headers.
- Separate logical units within a block with one blank line.

## Statements

- Put every class in a package.
- Keep import ordering consistent. List imported classes explicitly; do not use wildcard imports.
- Attach array brackets to the type: `int[] values`, not `int values[]`.
- Initialize variables where declared when a valid value is available, and declare them in the smallest practical scope.
- Do not expose class variables publicly unless the class is a behavior-free data class. Constants are exempt.
- Always wrap loop and conditional bodies in braces, even for one statement.
- Put a conditional and its body on separate lines.

## Comments and Javadoc

- Write comments in English with American spelling and avoid local slang.
- Write descriptive header comments for every class and public method. They may be omitted for getters/setters, test code, and overrides when the inherited Javadoc applies exactly.
- Start a method Javadoc summary with a third-person verb such as `Returns`, `Adds`, or `Sends`.
- Put `/**` on its own line. Align later `*` characters, include one space after `*`, leave one blank Javadoc line between the description and tags, and put no blank line between the Javadoc block and its declaration.
- End parameter descriptions with punctuation. Include all `@param` tags or omit all of them when every parameter is self-explanatory. Omit `@return` only when it adds no value or the method returns `void`.
- Use `{@inheritDoc}` when an override reuses the parent contract but needs extra detail.
- A short member comment may use one-line Javadoc. Indent comments with the code they describe; trailing comments are allowed.

## Google fallback rule needed in this repository

The SE-EDU page explicitly delegates uncovered topics to Google Java Style. Apply this relevant fallback rule without needing a web lookup:

- Put one space on both sides of the colon in an enhanced `for` statement: `for (Task task : tasks)`.

## Non-assessed repository hygiene

When edits are authorized, end Java source files with a newline and remove accidental duplicate Javadocs. These are repository-hygiene recommendations, not assessed SE-EDU or Google Java Style rules.

## Complete example

```java
package verity.report;

import java.util.List;

/**
 * Builds HTML summaries for tasks.
 */
public class HtmlReportBuilder {
    private static final int REPORT_MAX_ITEMS = 20;

    /**
     * Returns whether the report can include all supplied tasks.
     *
     * @param tasks Tasks to include.
     * @return True if the report has sufficient capacity.
     */
    public boolean canInclude(List<String> tasks) {
        return tasks.size() <= REPORT_MAX_ITEMS;
    }
}
```

# SE-EDU Java Coding Standard (Intermediate)

Apply the following rules to **all Java code** in this project. These rules are based on the [SE-EDU intermediate Java coding conventions](https://se-education.org/guides/conventions/java/intermediate.html).

---

## Naming

1. **Packages** — all lower case (e.g., `johnny.ui`, `johnny.task`).
2. **Classes and enums** — PascalCase nouns (e.g., `TaskList`, `Command`).
3. **Variables** — camelCase (e.g., `taskCount`, `filePath`).
4. **Constants** — ALL_UPPERCASE with underscores (e.g., `MAX_ITERATIONS`, `DISPLAY_FORMAT`).
5. **Methods** — camelCase verbs (e.g., `parseCommand()`, `markAsDone()`).
6. **Test methods** — `featureUnderTest_testScenario_expectedBehavior` (e.g., `parseTodo_emptyDescription_throwsException`).
7. **Abbreviations** — do not uppercase entire abbreviation in a name (`exportHtmlSource`, not `exportHTMLSource`).
8. **Language** — all names in English.
9. **Scope-proportional length** — large-scope variables get long descriptive names; loop counters can be `i`, `j`, `k`.
10. **Booleans** — prefix with `is`, `has`, `was`, `can`, `should` (e.g., `isDone`, `hasNext`).
11. **Boolean setters** — `void setFound(boolean isFound)`.
12. **Collections** — use plural names (e.g., `tasks`, `lines`).
13. **Iterator variables** — `i`, `j`, `k` only; `j`/`k` only inside nested loops.
14. **Associated constants** — share a common prefix (e.g., `COLOR_RED`, `COLOR_GREEN`).

## Layout

15. **Indentation** — 4 spaces, no tabs.
16. **Line length** — max 120 characters (aim for ≤110).
17. **Wrapped-line indent** — 8 spaces (double the normal indent).
18. **Line breaks** — after commas; before operators (including `.`, `&`, `|`).
19. **Method/constructor name** — stays attached to `(` (no space before parenthesis).
20. **Braces** — K&R / Egyptian style: opening brace on same line.
21. **if-else** — `else` on new line after closing brace; always use braces, even for single-statement bodies.
22. **for / while / do-while** — always use braces.
23. **switch** — indent case labels; include `break` or `// Fallthrough` comment.
24. **try-catch-finally** — `catch`/`finally` on same line as closing brace.
25. **Operators** — surround with spaces (`a + b`, not `a+b`).
26. **Reserved words** — followed by a space (`if (`, not `if(`).
27. **Commas** — followed by a space.
28. **Blank lines** — separate logical units within a block.

## Statements

29. **Package declaration** — every class in a package.
30. **Import order** — static imports first, then `java.*`, `javax.*`, `org.*`, `com.*`, `javafx.*`. Blank line between groups.
31. **No wildcard imports** — always list imported classes explicitly.
32. **Array brackets** — on the type, not the variable (`int[] data`, not `int data[]`).
33. **Variable initialization** — initialize where declared; declare in the smallest scope possible.
34. **No public fields** — unless the class is a pure data holder with no behavior. Constants are an exception.
35. **Loop braces** — always use braces for loop bodies.
36. **Conditional braces** — always use braces for if/else bodies; condition on a separate line from the keyword.

## Comments and Javadoc

37. **Language** — all comments in English, American spelling.
38. **Header Javadoc** — required on all public classes and public methods. May omit for trivial getters/setters, `@Override` methods, and test methods.
39. **Javadoc format** — opening `/**` on its own line; each continuation line starts with aligned ` * `; closing ` */` on its own line. Space after `*`.
40. **Javadoc summary** — first sentence is a short summary starting with a verb (`Returns`, `Parses`, `Adds`).
41. **Javadoc tags** — blank line between description and `@param`/`@return`/`@throws`. Omit a tag section entirely if all entries would be self-explanatory.
42. **`@inheritDoc`** — use for overridden methods that modify inherited behavior.
43. **Single-line member docs** — `/** Number of connections */` is acceptable for fields.
44. **Comment indentation** — indent comments to match the code they describe.
45. **Trailing comments** — allowed on the same line as code.

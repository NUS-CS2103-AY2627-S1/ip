# SE-EDU Java intermediate rules (reference)

Condensed from https://se-education.org/guides/conventions/java/intermediate.html.
Uncovered topics: [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Naming

Packages all lowercase. School projects: root is group/project name plus logical groups (`thomas.ui`), not `edu.nus.comp.*`.

Class/enum names: nouns, PascalCase (`Line`, `AudioSystem`).

Variables: camelCase. Constants: `SCREAMING_SNAKE_CASE` (`MAX_ITERATIONS`). See Google Java Style for what counts as a constant.

Methods: verbs, camelCase (`getName()`, `computeTotalWidth()`).

Test methods: `featureUnderTest_testScenario_expectedBehavior()`. Later parts may be omitted (`sortList_emptyList()`, `sortList()`).

Acronyms are not fully uppercase in names: `exportHtmlSource()`, not `exportHTMLSource()`.

All names in English.

Large scope → long names; small scope → short names. Integer scratch: `i, j, k, m, n`. Character scratch: `c, d`. Nested loops: `j`, `k` only when nested.

Booleans: `isSet`, `hasData`, `wasOpen`, `hasLicense()`, `canEvaluate()`, `shouldAbort`. Setter: `void setFound(boolean isFound)`.

Collections: plural (`Collection<Point> points`, `int[] values`).

Iterators may be `i`, `j`, `k`.

Associated constants share a prefix (`COLOR_RED`, `COLOR_GREEN`).

## Layout

4-space indent, not tabs.

Line length: prefer under 110; hard limit 120. Wrapped lines indent **8 spaces** more than the parent.

```
setText("Long line split"
        + "into two parts.");
```

Wrapping: break after a comma; break before an operator, `.`, type-bound `&`, and catch `|`. Method/constructor name stays on the same line as `(`. Prefer higher-level breaks. Ternary:

```
alpha = (aLongBooleanExpression) ? beta : gamma;
alpha = (aLongBooleanExpression)
        ? beta
        : gamma;
```

K&R braces. Methods:

```
public void someMethod() throws SomeException {
    ...
}
```

`if`/`else if`/`else`, `for`, `while`, `do-while`, `try`/`catch`/`finally`: opening `{` on the same line; `} else {` on one line.

Classic `switch`: indent `case`; include `// Fallthrough` when there is no `break`. Arrow `switch` is allowed.

Whitespace: `a = (b + c) * d;`, `while (true) {`, `doSomething(a, b, c);`, `for (i = 0; i < 10; i++) {`.

One blank line between logical units in a block.

## Statements

Every class belongs to a package.

Import order must be consistent. Example grouping: static → `java` → `javax` → third-party → project, with a blank line between groups. No `import foo.*`.

Array specifiers on the type: `int[] a = new int[20];`.

Initialize where declared; smallest scope. Leave uninitialized rather than using a fake value if no valid value exists yet.

No public instance fields unless the type is a data class with no behavior. Constants may be public.

Always wrap loop and conditional bodies in `{ }`. Never `if (isDone) doCleanup();`.

## Comments

American English; no local slang.

Required class and public-method header comments, except getters/setters, exact-behavior overrides, and tests.

```
/**
 * Returns lateral location of the specified position.
 * If the position is unset, NaN is returned.
 *
 * @param x X coordinate of position.
 * @param y Y coordinate of position.
 * @param zone Zone of position.
 * @return Lateral location.
 * @throws IllegalArgumentException If zone is <= 0.
 */
```

First sentence is the summary; for methods start with `Returns`/`Sends`/`Adds`/similar. `@inheritDoc` allowed when extending parent Javadoc. Field Javadoc may be one line: `/** Number of connections to this database */`.

Comments indent with the code. Trailing comments are allowed.

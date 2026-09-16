# CS2103T iP — Serangooner

Project instructions for Claude Code. These rules are mandatory for this
module. Follow them for every Java file, every commit, and every increment.

**Get them right the first time.** Do not write code that violates the rules
below and plan to clean it up afterwards — a style violation reaching the diff
is a defect. If a rule here is ambiguous, follow the linked reference; if the
reference is silent, follow the Google Java Style Guide.

## 0. Project facts

- Bot name: **Serangooner**. Source root is `src/main/java/`, and every class
  sits under `serangooner`, split into packages:

  | Package | Holds |
  | --- | --- |
  | `serangooner` | `Serangooner` (entry point and wiring), `Parser`, `SerangoonerException` |
  | `serangooner.command` | `Command`, `CommandType`, and every `*Command` |
  | `serangooner.storage` | `Storage` |
  | `serangooner.task` | `Task`, `Todo`, `Deadline`, `Event`, `TaskList`, `TaskDateTime`, `DateRange` |
  | `serangooner.ui` | `Ui`, `SoundPlayer` |

  Dependencies run one way only:
  `serangooner` → `command` → {`ui`, `storage`} → `task`. The sole exception is
  `SerangoonerException`, which every package may import from the root.
  **Do not add an import that points back up this chain.** If a class needs a
  type from a higher layer, that is a design problem to raise with me, not to
  import around. A Javadoc-only reference across packages uses a qualified
  link — `{@link serangooner.ui.Ui Ui}` — so it costs no import.
- **Java 25.** On macOS switch with `sdk use java 25.0.3.fx-zulu` before
  running or building.
- **Gradle** drives the build — use the wrapper, never a bare `javac`:
  `./gradlew build` compiles and runs the tests, `./gradlew run` starts the
  bot, and `./gradlew shadowJar` packages `build/libs/serangooner.jar`. The
  wrapper pins Gradle 9.6.1, so nothing needs installing.
- The existing code has been brought in line with these rules — every class is
  packaged, and Javadoc replaces the old `/* */` headers. Keep it that way. **Do
  not refactor working code unprompted**; if you notice a violation, mention it
  and let me decide.
- A new class goes in the package matching its job. Adding a package is a
  design change: propose it, don't just create it.

## 1. Working style

- Explain the rationale for significant actions: what you did and why.
- Keep explanations brief but instructive — I am learning this material.
- On a non-trivial design choice, stop and give me a few options with the
  trade-offs, then wait for my answer before continuing.
- **Never commit, tag, or push unless I explicitly ask.**

## 2. Git commit message convention

Full reference: https://se-education.org/guides/conventions/git.html

**Subject line**
- ≤50 characters (hard limit 72)
- Imperative mood: "Add README.md", not "Added README.md" / "Adding README.md"
- **Capitalize the first letter** — including after a scope prefix.
  `feat: Add error handling`, never `feat: add error handling`
- No trailing period
- Optional scope prefix, e.g. `Person class: Remove static imports`

**Body (when needed)**
- Blank line between subject and body
- Wrap body text at 72 characters
- Blank lines between paragraphs; bullets OK
- Explain WHAT and WHY, not HOW (the diff shows how)
- Structure: current situation (present tense) / why it must change /
  `Let's ...` what is being done (imperative) / why that way
- Avoid dated language like "currently" / "originally" — they are implied
- If the body runs long, that is a sign the commit should be split

**Branch names**
- kebab-case, meaningful keywords
- Issue-linked: `issueNumber-descriptive-keywords`, e.g. `1234-ui-freeze-error`

## 3. Java coding standard (se-education, basic + intermediate)

Full reference: https://se-education.org/guides/conventions/java/intermediate.html

**Naming**
- Packages: all lowercase, e.g. `todobuddy.ui`
- Classes/enums: PascalCase nouns, e.g. `AudioSystem`
- Methods: camelCase verbs, e.g. `computeTotalWidth()`
- Variables: camelCase
- Constants: SCREAMING_SNAKE_CASE; associated constants share a prefix
- Booleans: `is`/`has`/`was`/`can`/`should` prefix, e.g. `hasLicense()`.
  Boolean setters take the form `setFound(boolean isFound)`
- Collections: plural noun, e.g. `Collection<Point> points`
- Test methods: `featureUnderTest_testScenario_expectedBehavior()`
- Never capitalize abbreviations in names: `exportHtmlSource()`, not
  `exportHTMLSource()`
- All names in English

**Layout & formatting**
- 4-space indent, no tabs
- Line length: soft limit 110, hard limit 120
- K&R brace style (opening brace on same line)
- Break after commas or before an operator (`.`, `&`, `|`); a method name stays
  attached to its `(`; prefer higher-level breaks
- **Wrapped lines get 8-space indent** (double), including a `throws` clause
  pushed onto its own line
- Spaces around operators, after reserved words, after commas
- One blank line between logical units in a block
- Always brace loop/conditional bodies, even single statements. Never
  `if (x) doThing();` on one line

**Statements & declarations**
- Every class in a package
- No wildcard imports — list classes explicitly
- Import order: static imports, then `java.*`, `javax.*`, `org.*`, `com.*`,
  `javafx.*`, then `serangooner.*` last; blank line between groups, each group
  sorted alphabetically
- Array brackets attach to type: `int[] a`, not `int a[]`
- Initialize variables at declaration, smallest possible scope
- No public class fields unless it's a plain data class (constants exempt)
- `case` labels indent one level deeper than `switch`
- `// Fallthrough` comment for intentional switch fallthrough

**Comments & Javadoc**
- Header comment required for public classes and public methods. Exempt:
  getters/setters, faithful `@Override`s, test code
- Use `/** */`, never `/* */`, for header comments
- American English
- First sentence is a one-line summary starting with a verb in the third
  person: "Returns...", "Sends...", "Adds..." — never "Return", "Returning"
- `/**` on its own line, `*` aligned, space after each `*`
- Blank line between the description and the `@param` block; punctuation after
  every parameter description; no blank line before the signature
- `@param` is all-or-nothing: every parameter or none
- `@return` may be omitted if there is no return value or it is obvious
- Use `@inheritDoc` when an override needs the parent's text plus changes

```java
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
public double computeLocation(double x, double y, int zone)
        throws IllegalArgumentException {
    // ...
}
```

## 4. Tests (JUnit 5)

- Location: `src/test/java/`, package structure mirroring `src/main/java/`
- `Foo.java` is tested by `FooTest.java` in the matching package
- Method names: `featureUnderTest_testScenario_expectedBehavior()`,
  e.g. `sortList_emptyList_exceptionThrown()`. Trailing parts may be dropped
- Static-import assertions; use `assertThrows` for exception cases
- Header comments not required in test code

**Coverage target**

- Test roughly the **top 50% of methods by value** in each class — the
  complex, core, or business-critical ones. Judge a method by what breaking
  it would cost, not by line count.
- Skip the other half deliberately: getters, setters, faithful `@Override`s,
  and one-line delegation earn no test. Padding the count to hit a number is
  worse than not testing.
- **Every code change updates the tests in the same commit.** New method
  in the valuable half — write its tests. Changed behaviour — update the
  tests that pin it. Deleted method — delete its tests. A commit that moves
  logic without touching a test is incomplete unless nothing valuable moved.
- `./gradlew test` must be green before the change is done.

## 5. Tag and push each increment

Reference: https://git-mastery.org/lessons/tag/#/cs2103

- Use **lightweight** tags unless I ask for an annotated one
- Tag the commit that completes an increment with the **exact** increment ID:
  ```
  git tag Level-2
  ```
- Push the code AND the tag to the forked repo (tags don't push automatically):
  ```
  git push origin master
  git push origin Level-2
  # or: git push origin --tags
  ```
- Never overwrite or delete an existing tag without asking me first

## 6. AI usage citation (course policy)

Course policy requires AI-assisted work to be cited. I maintain a citation in
`README.md` covering broad usage. If your involvement in a file is localised
and substantial, add a brief comment naming the tool near that code, and tell
me when the overall scope changes so I can update the README.

## Checklist before calling an increment done

- [ ] Every commit subject: imperative, capitalized, ≤50 chars, no trailing period
- [ ] Commit bodies (if used) explain what/why, wrapped at 72 chars
- [ ] Java code follows naming, layout, and Javadoc rules above
- [ ] No wildcard imports; braces on every loop/conditional
- [ ] Javadoc on every new public class and method, first sentence a verb
- [ ] `./gradlew build` passes on Java 25 (compiles and tests green)
- [ ] Tests cover the valuable ~50% of methods, and every changed
      method's tests were updated alongside it
- [ ] Completing commit is tagged with the exact increment ID
- [ ] Commit and tag both pushed to the forked repo
- [ ] AI usage citation still accurate

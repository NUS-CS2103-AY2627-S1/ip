# Kenbot — study notes

Everything that changed from A-Packages through Level-9, why it changed, and the
theory behind it. Written to be read in order, but each section stands alone.

- [Where the project stands](#where-the-project-stands)
- [The architecture, and why it looks like that](#the-architecture-and-why-it-looks-like-that)
- [Packages](#packages)
- [Gradle](#gradle)
- [JUnit and testing](#junit-and-testing)
- [The two test suites, and what each one catches](#the-two-test-suites-and-what-each-one-catches)
- [Building a JAR](#building-a-jar)
- [Javadoc](#javadoc)
- [The coding standard](#the-coding-standard)
- [Level-9: Find](#level-9-find)
- [Java features used, and when they arrived](#java-features-used-and-when-they-arrived)
- [Git](#git)
- [Design ideas this project demonstrates](#design-ideas-this-project-demonstrates)
- [Known limitations](#known-limitations)
- [Grading checklist](#grading-checklist)

---

## Where the project stands

| | |
| --- | --- |
| Classes in `src/main/java` | 12, about 1,155 lines |
| Test classes | 4, holding 38 JUnit tests |
| Text-UI test cases | 31 |
| Commits | 84 |
| Tags | 18 |

Increments finished, each on its own branch and merged with `--no-ff` so the
history shows a visible fork and rejoin:

`Level-0` … `Level-9`, plus `A-Enums` (tagged `enums`), `A-MoreOOP`,
`A-Packages`, `A-Gradle`, `A-JUnit`, `A-Jar`, `A-JavaDoc`, `A-CodingStandard`.

The last three were done as **parallel** branches — all three forked from the
same commit and were merged separately, which is what produced the one genuine
merge conflict described under [Git](#git).

---

## The architecture, and why it looks like that

Twelve classes in two packages:

```
kenbot/
  Kenbot            owns the other parts and decides the order they work in
  Ui                reads input, prints output. Decides nothing.
  Parser            turns a typed line into a command + argument. Prints nothing.
  TaskList          holds the tasks, checks every task number
  Storage           reads and writes the save file
  CommandType       the list of commands that exist
  KenbotException   a problem caused by something the user typed
kenbot/task/
  Task              a description and whether it is done
  Todo              a task with nothing else
  Deadline          a task with a due date
  Event             a task with a start and an end
  TaskDate          a real date, plus optional time text
```

### The flow for one typed line

```
run()  →  ui.readCommand()      get the raw text
       →  Parser.parse()        work out WHICH command
       →  the switch            DO it (tasks.mark, Todo.of, ...)
       →  storage.save()        write the file
       →  ui.show()             print the result
```

`run()` is the only piece that knows the sequence. That is its whole job.

### The test that tells you whether a class is in the right place

- `Ui` never decides anything
- `Parser` never prints and never touches a task
- `Kenbot` never touches `Scanner` or `System.out`

All three hold today, and they are checkable with `grep`. If one starts failing,
something has leaked into the wrong class.

### Why `Parser` was not created earlier

At Level-5 a `Parser` would have contained one line and done nothing useful — a
class that does nothing is worse division of responsibility than no class at
all. It earned its place at A-MoreOOP, once there were three real jobs to move
into it: splitting the line, looking up the command, and rejecting a bar.

---

## Packages

### What a package is

A named folder for classes. Two halves that must agree:

```
package kenbot.task;                      the declaration, first line of the file
src/main/java/kenbot/task/Todo.java       the folder it must live in
```

A dot in the package name **is** a folder separator. `kenbot.task.Todo` and
`kenbot/task/Todo.java` are the same statement written two ways. If the two
disagree, the compiler refuses.

### You were already using them

`import java.util.Scanner;` — `java.util` is a package, `Scanner` is a class in
it. Every import ever written reaches into someone's package.

**Classes in the same package see each other with no import.** That is why
moving all 12 classes into one package needed zero import changes, and why
splitting `kenbot.task` out afterwards needed them in both directions.

### Three reasons packages exist

1. **Name collisions.** Java itself has two classes called `Date` —
   `java.util.Date` and `java.sql.Date`. Same short name, different packages, no
   conflict. Without packages, two libraries each defining a `Task` could never
   be used together.
2. **Organisation.** Twelve files in one folder is fine. Fifty is not.
3. **Visibility.** `public` now means something it could not before:

   | Written as | Who can see it |
   | --- | --- |
   | `public class Storage` | anyone, anywhere |
   | `class Storage` (no modifier) | only classes in the same package |
   | `private` field | only that class |

   This is a third level of encapsulation, above `private` fields and above
   class design.

### The source root, and the classic mistake

The **source root** is where the compiler starts counting. Every folder below it
becomes part of the package name.

Correct:

```
src/main/java/          ← source root
  duke/ui/Duke.java     → package duke.ui
```

Incorrect:

```
[project root]          ← source root, and this is the mistake
  src/main/java/Duke.java   → package src.main.java
```

The file sits in the same physical place in both. Only the *setting* differs.
`src`, `main` and `java` are the Gradle and Maven convention for where source
lives — they describe the build layout, not the code. Consequences of getting it
wrong:

- Gradle assumes `src/main/java` is the source root; declare
  `package src.main.java` and the build fails or puts classes in the wrong place
- a package segment literally called `java` is confusing next to Java's own
  `java.util`
- change build tools later and every package name is wrong

In IntelliJ this is set by right-clicking a folder → **Mark Directory as →
Sources Root**. Mark the project root by mistake and IntelliJ will generate
`package src.main.java` for you.

---

## Gradle

### What it is

A **build tool**. It compiles, runs tests, resolves libraries, and packages the
app — all from one description of the project instead of remembering commands.
Before this, everything was hand-run: `javac` with a file list, `java` with a
classpath. Gradle is that knowledge written down.

### The wrapper

`gradlew` (and `gradlew.bat` for Windows) is the **Gradle wrapper**. It is a
small script committed to the repo that downloads the exact Gradle version the
project expects, so nobody needs Gradle installed and everyone builds with the
same version. That version is pinned in
`gradle/wrapper/gradle-wrapper.properties`:

```
distributionUrl=https\://services.gradle.org/distributions/gradle-9.6.1-bin.zip
```

**Always run `./gradlew`, never a system `gradle`.** That is the point of it.

### `build.gradle`, section by section

```gradle
plugins {
    id 'java'                                  // compile Java, run tests
    id 'application'                           // gives the `run` task
    id 'com.gradleup.shadow' version '9.5.1'   // builds a fat JAR
}
```

A **plugin** adds tasks and settings. `java` alone gives `compileJava` and
`test`; `application` adds `run`; `shadow` adds `shadowJar`.

```gradle
java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}
```

Which Java version the source is written in, and which version of bytecode to
emit. Both 25, matching the course requirement.

```gradle
repositories { mavenCentral() }
```

**Where to download libraries from.** Maven Central is the main public library
host. Without this, Gradle has nowhere to fetch JUnit from.

```gradle
dependencies {
    String jUnitVersion = '5.14.4'
    testImplementation "org.junit.jupiter:junit-jupiter-api:${jUnitVersion}"
    testRuntimeOnly    "org.junit.jupiter:junit-jupiter-engine:${jUnitVersion}"
    testRuntimeOnly    'org.junit.platform:junit-platform-launcher:1.14.4'
}
```

Each dependency is written **`group:name:version`**. The prefix is a
**configuration**, meaning when it is needed:

- `testImplementation` — needed to *compile* the tests (the `@Test` annotation,
  the assertions)
- `testRuntimeOnly` — needed only to *run* them (the engine that finds and
  executes tests). Splitting them keeps the compile classpath small, so test
  code cannot accidentally depend on engine internals.
- there is no plain `implementation` entry, because Kenbot itself uses no
  outside libraries at all

```gradle
test {
    useJUnitPlatform()
    testLogging { events 'passed', 'skipped', 'failed'
                  showExceptions = true; exceptionFormat = 'full' }
}
```

`useJUnitPlatform()` says "these are JUnit 5 tests" — without it Gradle looks
for the older JUnit 4 style and finds nothing. The logging block is why the
console prints each test name and full stack traces on failure.

```gradle
application { mainClass = 'kenbot.Kenbot' }
```

Which class holds `main`. **This had to change from the template's
`seedu.duke.Duke`** — a fully qualified name, package included.

```gradle
jar {
    enabled = false
    archiveClassifier = 'without-dependencies'
}
shadowJar { archiveFileName = 'kenbot.jar' }
run { standardInput = System.in }
```

The plain `jar` task is switched off because the fat JAR replaces it. `run {
standardInput = System.in }` is essential for a console app — without it,
`./gradlew run` cannot read anything you type.

### Tasks worth knowing

| Command | What it does |
| --- | --- |
| `./gradlew build` | compile, test, and assemble everything |
| `./gradlew run` | start the app |
| `./gradlew test` | run only the JUnit tests |
| `./gradlew shadowJar` | build `build/libs/kenbot.jar` |
| `./gradlew javadoc` | generate API docs, and report Javadoc warnings |
| `./gradlew clean` | delete `build/` |

Tasks form a graph — `build` depends on `test`, which depends on
`compileTestJava`, which depends on `compileJava`. Gradle also caches: a task
whose inputs have not changed prints `UP-TO-DATE` and is skipped.

`build/` and `.gradle/` are gitignored. Generated output never belongs in a
repository.

### One trap that bit during this session

`./gradlew run` executes **in the project root**, so the app wrote to the real
`data/Kenbot.txt` and overwrote it. Run experiments from a scratch folder using
the JAR instead.

---

## JUnit and testing

### What a unit test is

Code that calls one small piece of your program with a known input and checks
the answer. It runs in milliseconds, needs no console, and is repeatable —
unlike typing commands by hand.

### The anatomy of a test

```java
@Test
public void of_wordInsteadOfDate_throwsWithTheAcceptedFormat() {
    KenbotException thrown = assertThrows(KenbotException.class,
            () -> TaskDate.of("Sunday"));
    assertEquals("'Sunday' is not a date I understand."
            + " Write it as yyyy-mm-dd, like: 2019-10-15 1800", thrown.getMessage());
}
```

- **`@Test`** marks a method as a test. JUnit finds it by reflection; you never
  call it yourself.
- **Assertions** are the check. `assertEquals(expected, actual)` — expected
  first, always, or failure messages read backwards. Also `assertTrue`,
  `assertThrows`.
- **`assertThrows`** checks that code *fails* the way it should. The
  `() -> ...` is a **lambda** — a chunk of code passed as a value, so JUnit can
  run it and catch what comes out. It returns the exception, so its message can
  be checked too.
- **Static imports.** `import static org.junit.jupiter.api.Assertions.assertEquals;`
  lets you write `assertEquals(...)` rather than `Assertions.assertEquals(...)`.
  The `static` keyword there imports a *method*, not a class.

### Naming

`method_condition_expectedResult` — for example
`delete_middleTask_closesTheGapInTheNumbering`. A failing test then names the
bug in the report without anyone opening the file. This is the convention used
in the course's larger project too.

### `@TempDir`

```java
@TempDir
private Path folder;
```

JUnit creates a fresh empty folder for **each test method** and deletes it
afterwards. That is what makes `StorageTest` safe: it writes real files, but
never near the real save file, and no test can see another's leftovers.

This is also why `Storage` was changed at A-MoreOOP to take its path as a
constructor argument. A hardcoded path is untestable. **Making code testable
usually means making it more flexible, which usually means better design** —
that is the real argument for unit tests, beyond catching bugs.

### What was tested, and why those

| Test class | Covers | Why it earns its place |
| --- | --- | --- |
| `TaskDateTest` (8) | `TaskDate.of`, `toStorable`, `toString` | date parsing has the most ways to go wrong; the round-trip test proves saving and loading agree |
| `ParserTest` (6) | `Parser.parse` | one small pure function, many inputs — the ideal unit test target |
| `TaskListTest` (16) | add, delete, mark, unmark, describe, find | all the task-number validation, plus that renumbering after a delete is correct |
| `StorageTest` (8) | save, load, corrupt files | the only tests that touch the filesystem |

The rubric asks for good tests on **at least two** methods. There are 38 across
four classes.

### What makes a test good

- **One idea per test.** A test named for two things cannot report clearly.
- **Test the boundaries.** `mark 0`, `mark 5` with one task, `delete` on an
  empty list — off-by-one bugs live exactly there.
- **Test the failures, not just the successes.** Roughly half of these tests
  assert that something is *refused*.
- **Test the bug you actually had.** `load_oneBadLineAmongGoodOnes_keepsTheGoodOnes`
  exists because that bug destroyed real data. A test written after a fix is the
  guarantee it stays fixed.
- **Do not test the obvious.** No test asserts that a getter returns the field.

---

## The two test suites, and what each one catches

| | Text-UI tests (31) | JUnit tests (38) |
| --- | --- | --- |
| What runs | the whole program, via the console | one method, directly |
| Sees | exactly what a user sees | return values and exceptions |
| Speed | about a second each | milliseconds |
| Good at | wording, spacing, the greeting, whole-session behaviour | edge cases, error messages, logic |
| Blind to | anything not printed | anything about layout or wording |

**Neither replaces the other**, and this session proved it in both directions.

The text-UI suite made A-MoreOOP safe: moving code between four classes is
exactly where things break silently, and 28 passing cases said the refactor
changed nothing a user could see. Most students do that refactor blind.

The JUnit suite reaches places the text-UI suite structurally cannot: a text-UI
case is a **single run** of the program, so it can never test that tasks survive
a restart, and it starts from an empty folder, so it can never test a corrupt
save file. Those live in `StorageTest`.

### How the text-UI runner works

`python3 .codex/skills/test-ui/scripts/run_ui_tests.py`

Each case in `test/ui-test-plan.md` gets a **fresh JVM** and a **fresh empty
working folder**, so the save file one case writes can never be seen by the
next, and the real `data/Kenbot.txt` is never touched. Input is piped in,
output compared whole, and the run **stops at the first failure** printing
expected, actual and a diff.

The golden rule for the plan: **write expected output from what the program
should print, never from what it currently prints.** Pasting current output to
make a test pass turns a bug into a rule.

---

## Building a JAR

A **JAR** is a zip file of compiled classes plus a manifest saying which class
holds `main`. `java -jar kenbot.jar` reads that manifest and starts there.

**Lean vs fat.** A lean JAR holds only your classes and needs its libraries
supplied separately. A **fat** (or "shadow", or "uber") JAR packs the
dependencies inside, so it runs anywhere with a JVM and nothing else. The
`shadow` plugin builds the fat one; the plain `jar` task is disabled so there is
no confusion about which file to hand out.

Kenbot's JAR is only 19 KB because it uses no outside libraries — JUnit is a
test-only dependency and is correctly absent.

Verified by copying `kenbot.jar` alone into an empty folder and running it: it
created its own `data/` and saved correctly. That is the whole point — one file,
no instructions.

---

## Javadoc

A `/** ... */` comment before a class or member. The `javadoc` tool turns them
into browsable HTML, and IntelliJ shows them on hover.

Conventions that matter:

- **First sentence is a summary** and ends with a period. It appears alone in
  index pages, so it must stand by itself.
- **`@param`** for every parameter, **`@return`** unless `void`, **`@throws`**
  for every declared exception.
- **`{@code x}`** for inline code, **`{@link Ui}`** for a cross-reference that
  becomes a hyperlink.
- **`<p>` for later paragraphs.** Javadoc is HTML; blank lines do not create
  paragraphs.
- **Say *why*, not *what*.** `// increments i` is noise. The comment in
  `TaskList.indexOf` explaining that it returns a *position* so `delete` does
  not depend on object equality is the useful kind.

`./gradlew javadoc` reports gaps. Two came up that reading the code would not
have shown:

1. **Enum constants each need their own comment** — the tool flags a bare
   `BYE, LIST, MARK, ...` line eight times.
2. **A class with no explicit constructor** gets a warning about the invisible
   default one. `Ui` was given an explicit documented constructor for this.

Now at **zero warnings**. The rubric asks for Javadoc on at least half of public
classes and methods; this is at 100%.

---

## The coding standard

Checked and clean: no line over 120 characters, no tabs, no trailing whitespace,
no wildcard imports (`import java.util.*` hides where names come from and breaks
when two packages define the same name).

**Import order** — `java.*` first, blank line, then project imports,
alphabetical within each group:

```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import kenbot.KenbotException;
```

Three things were actually fixed:

1. **`main` was missing `public`.** Java 25 permits it, but
   `public static void main(String[] args)` is universal and anything else makes
   a reader stop and wonder.
2. **`Parser`'s private constructor sat at the bottom.** Constructors come
   before methods.
3. **`Storage`'s private helpers sat between two public methods.** Public
   members group together, then private ones, so a reader meets the interface
   before the machinery.

The general ordering rule: **fields, constructors, public methods, private
methods.** Read top to bottom and you meet what a class *offers* before how it
works.

---

## Level-9: Find

Adding `FIND` to `CommandType` **broke the build immediately**:

```
Kenbot.java:107: error: the switch expression does not cover all possible input values
```

That is the safety net built at A-Enums paying off. The switch in
`handleCommand` is an **expression**, not a statement, and an expression must
cover every enum value — so the compiler pointed straight at the work. A
`default ->` branch would have swallowed it and produced a runtime crash later
instead.

Note the two guards face opposite ways:

- `CommandType.from()` catches **a word with no enum value** — a user typing
  `blah`
- the exhaustive switch catches **an enum value with no case** — a half-finished
  feature

Adding `FIND` moves `find` from the first category to the second. `from()` was
never watching for that.

### The implementation

`TaskList.find(keyword)` walks the tasks, compares lower-cased descriptions, and
builds numbered text — deliberately mirroring `describe()`, which already did
the same job without filtering.

Choices made on purpose:

- **Case-insensitive**, because searching is meant to be forgiving
- **Substring, not whole word**, so `find book` also finds "bookshop"
- **Description only**, matching the page's wording: *"searching for a keyword in
  the task description"*
- **A blank keyword is refused** rather than matching everything
- **No matches gets its own message**, rather than a heading with nothing under
  it

### The one real caveat

Matches are **renumbered from 1**, so the numbers `find` shows are *not* the
numbers `mark` and `delete` expect. `find book` showing `1.` and `2.` followed
by `delete 1` may well delete the wrong task.

This matches the page's example and the conventional solution, so it stays — but
it is a genuine usability trap, it is documented in the user guide, and showing
the original positions instead would be a defensible improvement.

---

## Java features used, and when they arrived

| Feature | Java version | Where |
| --- | --- | --- |
| `enum` | 5 (2004) | `CommandType` |
| `java.time.LocalDate`, `DateTimeFormatter` | 8 (2014) | `TaskDate` |
| `java.nio.file.Path`, `Files` | 7 / 8 | `Storage` |
| `var` | 10 | not used here |
| switch **expressions** and `yield` | 14 (2020) | `handleCommand`, `parseLine` |
| `record` | 16 (2021) | `LoadResult`, `ParsedCommand` |
| `List.copyOf` | 10 | `TaskList.getTasks` |
| non-public `main` | 25 (2025) | was used, then made `public` for convention |

Three worth restating:

**`record`** is a keyword like `class` or `enum`, not something invented here.
One line generates a constructor, accessors, `equals`, `hashCode` and
`toString`. `javap` on a compiled record shows all six. Records are immutable,
their accessors are `tasks()` not `getTasks()`, and they cannot extend anything —
so `Task` could never be one.

**`yield`** is needed only when a switch branch uses `{ }` braces, because a
block of statements is not a value. `yield` names the value the branch produces.
`return` is *illegal* there — Java says "attempt to return out of a switch
expression" — and that refusal is useful, because `return` would exit the whole
method and skip the code after the switch. In `parseLine` that code applies the
done flag, so a `return` would have loaded every saved task as not-done.
**`return` leaves the method; `yield` leaves the branch.**

**Regex double-backslashes.** `split(" \\| ")` looks like escaping twice but is
not. Two programs read that text in turn: the Java compiler builds the string,
then the regex engine reads it as a pattern. Each consumes one backslash. `"\\|"`
becomes the two characters `\|`, which the regex reads as a literal bar. Writing
`"\|"` does not compile at all — `\|` is not a legal Java escape. Writing `"|"`
compiles and means "or", which splits between every character. Java has no
regex-literal syntax, so every regex backslash gets doubled — the same reason
`split("\\s+")` looks the way it does.

---

## Git

### Branch, merge, tag — the increment loop

```bash
git switch -c branch-Level-9      # create and move onto a branch
# ...work, commit...
git push -u origin branch-Level-9 # first push: -u records the pairing
git switch master
git merge --no-ff branch-Level-9
git tag Level-9
git push origin master Level-9
```

Never delete the increment branches — the course requires them to survive.

### `--no-ff` and why it matters

If `master` has no commits of its own since branching, git can reach the branch
tip by just sliding the pointer forward. That is a **fast-forward**, and it
leaves a straight line with no evidence a branch existed. `--no-ff` forces a
merge commit anyway, producing the fork-and-rejoin shape:

```
*   Merge branch-Level-9
|\
| * Add find command
|/
*   Merge branch-A-CodingStandard
```

**That shape is the deliverable being checked.** Forgetting the flag happened
once this session; the fix was:

```bash
git reset --hard <commit master was on before>
git merge --no-ff branch-Level-8
```

`reset --hard` moves the branch pointer *and* the working files. It is safe here
because the commit is still reachable from the branch — the work is not deleted,
`master` just stops pointing at it. The alarming part is that the files appear
to vanish from the folder; they come back when the merge runs.

`ORIG_HEAD` is set by `git merge` to wherever HEAD was before it, which is
usually the commit to reset to.

### `-u` on the first push

Writes two lines into `.git/config`:

```
branch.branch-Level-9.remote  origin
branch.branch-Level-9.merge   refs/heads/branch-Level-9
```

That pairing is what makes bare `git push` and `git pull` work afterwards, and
what lets `git status` say `[ahead 1]`. Without it git cannot tell you whether
your work is safely on GitHub. `master` already had this because `git clone` sets
it up for the branch it cloned.

### Lightweight vs annotated tags

| Command | Editor opens | Creates |
| --- | --- | --- |
| `git tag Level-9` | no | **lightweight** — a name pointing at a commit |
| `git tag -a Level-9` | yes | **annotated** — an object with author, date, message |

All tags here are lightweight, which is what this project wants. Tags sit on the
**merge commits** so the tag marks the increment arriving on `master`.

### Parallel branches, and the conflict they caused

`A-JavaDoc`, `A-CodingStandard` and `Level-9` all forked from the same commit,
which is exactly what the page asks for. Two of them then edited the same line:

- `A-JavaDoc` rewrote the enum constants as a documented multi-line block
- `Level-9` added `FIND` to the old single-line version

Merging the second one produced:

```
<<<<<<< HEAD
    /** Ends the program. */
    BYE,
    ...
=======
    BYE, LIST, MARK, UNMARK, TODO, DEADLINE, EVENT, DELETE, FIND;
>>>>>>> branch-Level-9
```

A conflict is git saying *"both sides changed the same lines, and I will not
guess."* The markers mean: `HEAD` is the branch you are on, the other label is
the branch coming in, `=======` divides them. Resolving means editing the file
into what you actually want, deleting all three markers, then `git add` and
commit. Here the answer was to keep the documented constants **and** document
`FIND` to match — neither side alone.

### Commit messages

The course requires the standard from week 3 onward: **capitalised, imperative
subject, no full stop** — "Add find command", not "added find command." or
"adds find command". Past messages are best left alone; editing them changes
timestamps and confuses the progress-tracking scripts.

---

## Design ideas this project demonstrates

**Single responsibility.** Each class has one job, stated in its Javadoc, and
the `grep` checks above show they hold. When a class starts needing "and" to
describe it, it is doing too much.

**Encapsulation, three levels deep.** `private` fields in `Task`; a private
`ArrayList` in `TaskList` reachable only through validating methods; and now
package-level visibility as a third option.

`TaskList.getTasks()` returns `List.copyOf(tasks)` — a copy, so a caller can
read but not reach in and mutate. There is a test asserting that trying to add
to it throws.

**Polymorphism instead of type checking.** `toString()` and `toStorable()` are
one question asked of every task, answered differently by each. No code anywhere
asks "is this a Deadline?" and casts. Adding a new task type means writing one
class, not editing a switch in three places.

**Exception translation.** Three times over: `NumberFormatException` →
`KenbotException` in `TaskList.indexOf`, `IOException` → `KenbotException` in
`Storage`, `DateTimeParseException` → `KenbotException` in `TaskDate`. Each time
a library's error becomes *your* error, so callers have exactly one kind of
problem to handle. That is what makes the single catch block in `run()` possible.

**Checked vs unchecked.** `IOException` is **checked** — the compiler forces you
to deal with it, because file operations fail for reasons no code can prevent:
disk full, permission denied, drive unplugged. `NumberFormatException` and
`DateTimeParseException` are **unchecked**, so catching them is a choice. Both
get caught here anyway, for the same reason.

**Display format is not storage format.** `toString()` gives
`[D][X] return book (by: Oct 15 2019)` for a human. `toStorable()` gives
`D | 1 | return book | 2019-10-15` for the program. Mixing them is the classic
Level-7 bug: the display form cannot be parsed back. The single most dangerous
line in Level-8 was `by.toStorable()` in `Deadline.toStorable()` — writing
`+ by` instead would compile, look right, save `Oct 15 2019`, and fail to load.

**Fail before you commit to anything.** Because the save happens *after* the
switch, a rejected command throws before the file is ever touched. That falls out
of the structure rather than needing to be arranged.

**The compiler as a safety net.** Switch expressions over an enum are checked
for exhaustiveness. A `default ->` branch throws that away — it turns a compile
error into a runtime crash. `default` is right when input is genuinely open
(text from a file, as in `parseLine`), and wrong over an enum, where the compiler
can enumerate the cases for you.

**Simplest thing that suffices.** Saving happens after *every* command, not only
the ones that change the list. Two pointless rewrites of a tiny file cost
microseconds and removed the need for an extra method. A `changesTasks()` flag
was designed, considered, and dropped.

---

## Known limitations

- **`find` renumbers from 1**, so its numbers do not work with `mark`/`delete`.
- **Times are not validated.** `deadline x /by 2019-10-15 banana` is accepted
  with the time "banana". `LocalDate` holds no time, so the time is carried as
  text. `LocalDateTime` would fix it.
- **`|` is banned in descriptions**, because it is the save-file separator.
  Escaping it would be the fancier solution.
- **Commands are case-sensitive.** `Todo` is an unknown command.
- **Corrupt lines are dropped on the next save.** They are reported when skipped,
  but the following save rewrites the file without them, so hand-repair is not
  possible. Copying the file aside before overwriting was designed and cut.
- **"1 tasks in the list"** — the message does not handle singulars.
- **No GUI yet.** Required for the final submission.

---

## Grading checklist

| Requirement | Status |
| --- | --- |
| Increments completed | Level-0 … Level-9, A-Enums, A-MoreOOP, A-Packages, A-Gradle, A-JUnit, A-Jar, A-JavaDoc, A-CodingStandard |
| Object-oriented design, some inheritance | `Task` → `Todo`/`Deadline`/`Event`; work split across `Ui`, `Parser`, `Storage`, `TaskList` |
| Javadoc on ≥ half of public classes and methods | 100%, zero `javadoc` warnings |
| Errors handled with exceptions | `KenbotException`, with three library exceptions translated into it |
| Good JUnit tests for ≥ 2 methods | 38 tests across 4 classes |
| No major bugs | 31 text-UI cases and 38 JUnit tests passing; the data-destruction bug found and fixed with a regression test |
| Code quality | no long lines, no tabs, no wildcard imports, small focused methods, no commented-out code |
| Git conventions | branch per increment, `--no-ff` merges, lightweight tags, capitalised imperative subjects |
| Documentation | `README.md` for developers, `docs/README.md` user guide covering every command |
| **A fit-for-purpose GUI** | **not done — required for the final submission** |
| **Two optional increments with AI assistance** | A-Enums and the A-Packages stretch goal both qualify; confirm how the course wants these declared |

### What is left

1. **The GUI** — JavaFX, at least as complete as Part 4 of the tutorial. This is
   the largest remaining piece, roughly 5–9 hours. Your JDK is Zulu **FX**, so
   JavaFX is already installed and the usual module-path setup pain does not
   apply.
2. **A product screenshot** in `docs/README.md`, once there is a GUI to
   screenshot.
3. Optionally the two stretch goals that were skipped: `*Command` classes for
   A-MoreOOP, and a "what is due on this date" command for Level-8. The second
   is about 80% of the same work as `find`, so it is cheap now that the pattern
   exists.

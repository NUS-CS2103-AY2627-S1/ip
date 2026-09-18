# Bibi

[![Java CI](https://github.com/joshmode/ip/actions/workflows/gradle.yml/badge.svg)](https://github.com/joshmode/ip/actions/workflows/gradle.yml)

Bibi is your (not so) friendly local clanker! A small, QUICK and dependable task keeper with a dry sense of humor,
borne in a post-apocalyptic cyberpunk world.

Log your ToDos, deadlines and events _simply_ and _safely_. Everything stays in a
plaintext file beside the app: nothing is uploaded, and nothing leaves your
machine. Your plans, private. Created as part of CS2103T 26/27 Sem 1.

📖 **[Read the User Guide](https://joshmode.github.io/ip/)** — or see [`docs/README.md`](docs/README.md).

All you need to do is:

download it from releases.
double click the downloaded file.
add your tasks.
let it manage your tasks for you 😉

and it is completely FREE! (yes, in this economy)

## Setting up in IntelliJ (for the old-fashioned)

Prerequisites: JDK 25, update IntelliJ to the most recent version.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
2. Open the project into IntelliJ as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. After that, locate the `src/main/java/bibi/Launcher.java` file, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, a window opens showing Bibi's greeting, with a box to type commands into.
5. The text-based interface is still there. Run `src/main/java/bibi/Bibi.java` the same way to get it, and it greets you in the console with the banner:
   ```
   B B B B    i    b b b    i
   B       B       b       b
   B B B B   iii   b b b b  iii
   B       B  i    b       b  i
   B B B B  iii   b b b b  iii
   ```

**Note:** always start the GUI from `Launcher`, never from `Main`. JavaFX refuses to start when the class holding `main` extends `Application` and the JavaFX runtime is on the classpath, which is how this project supplies it; the error reads `JavaFX runtime components are missing`.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running with Gradle

The project uses the Gradle wrapper, so no separate Gradle install is needed.
Run these from the project root, with JDK 25 selected.

Compile everything:

```
./gradlew compileJava
```

Run the chatbot. This opens the GUI:

```
./gradlew run
```

The text-based interface has its own task. It is what the scripted UI tests
drive, since a console is far easier to feed a script than a window is:

```
./gradlew runCli
```

On Windows `cmd`, use `gradlew.bat` instead of `./gradlew`.

Both interfaces are the same chatbot underneath, and both save to
`./data/bibi.txt`, relative to the folder the build runs in.

## Checking the coding standard

The build applies [Checkstyle](https://checkstyle.org) to enforce the
[SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
mechanically, so style is settled by the tool rather than by argument in review.
The rules live in `config/checkstyle/checkstyle.xml`, with narrow exemptions in
`config/checkstyle/suppressions.xml`.

Run it on its own:

```
./gradlew checkstyleMain checkstyleTest
```

Checkstyle also runs as part of `./gradlew check` and `./gradlew build`, so a
style violation fails the build the same way a failing test does.

When something is flagged, the console names the file, line, and rule. A browsable
report is written to `build/reports/checkstyle/main.html` (and `test.html`).

Note that the build sets `maxWarnings = 0`. Most rules in the SE-EDU config carry
severity `warning`, and Gradle fails a Checkstyle task on errors only, so without
that setting those rules would be reported and then ignored.

## Packaging as a runnable JAR

The build uses the Shadow plugin to produce a *fat* JAR: one file containing the
compiled classes and any dependencies, so nothing else needs to be installed
alongside it.

Create it:

```
./gradlew shadowJar
```

The result is written to `build/libs/bibi.jar`. That folder is ignored by Git,
because generated binaries do not belong in the repository; publish the JAR
through a GitHub release instead.

Run it from any folder. This opens the GUI:

```
java -jar "bibi.jar"
```

The same JAR still holds the text-based interface:

```
java -cp "bibi.jar" bibi.Bibi
```

Bibi creates its `data/bibi.txt` save file relative to the folder the command is
run in, so copying the JAR into an empty folder gives it a fresh task list, and
running it there again restores what was saved.

### A note on architectures

The fat JAR bundles JavaFX's native libraries for Windows (x86-64), Linux
(x86-64) and Apple Silicon macOS, which covers every machine the course expects.

It is one architecture per operating system by necessity: the two macOS builds
of JavaFX ship their libraries under identical filenames, so only one set can
survive packaging. Apple Silicon is the one bundled, because it is what the JDK
in the course's macOS advisory targets, and since Intel Macs are discontinued.

On an Intel Mac, or on a Linux machine that is not x86-64, the window will not
open. Use the text interface, which needs no native libraries at all:

```
java -cp "bibi.jar" bibi.Bibi
```

To build a JAR for one of those machines instead, swap the `mac-aarch64`
classifier in `build.gradle` for `mac`, or `linux` for `linux-aarch64`, and run
`./gradlew clean shadowJar` again.

## Acknowledgements

### Third-party libraries
Special thanks to the providers of the following Open-Source software.

| Library | Used for |
|---------|----------|
| [JavaFX](https://openjfx.io) | the graphical interface |
| [JUnit 5](https://junit.org/junit5/) | the automated tests |
| [Gradle Shadow](https://github.com/GradleUp/shadow) | packaging the fat JAR |
| [Checkstyle](https://checkstyle.org) | enforcing the coding standard |

### AI assistance

**This project was written with extensive AI assistance.** The tools used were
**Claude (Opus 5), through Claude Code** and **OpenAI Codex**, both driven by
[@joshmode](https://github.com/joshmode), who is the sole author of the work in
the sense the course means: every design decision was made by the author, and
every generated change was read, run and verified before it was committed.
Tweaks were made where appropriate, and most baseline code was written by hand,
with IDE autocomplete and/or AI assistance to further develop ideas.

The use was **widespread rather than localized**. It is therefore declared here,
in full, rather than annotated next to individual lines, per course policy.
The codebase was extensively parsed and/or altered by agentic AI, at the author's
sole behest.

#### What the assistants were used for

| Area | Extent of assistance |
|---|---|
| Java source | Drafting and refactoring across every increment, including the parser, class division, `Storage`, and the JavaFX interface |
| Tests | Writing the JUnit tests and the scripted console test plan, and diagnosing the failures they surfaced |
| Documentation | This README, the user guide in `docs/`, occasionally commit messages and pull request descriptions |
| Review | Reviewing the code against the SE-EDU coding standard and the course rubric, and proposing the fixes that followed |

#### What was not delegated

The product decisions were made by the author in conversation with the tools, not
handed to them: what each command should do, how strict the input handling should
be, what the error messages should say, how the window should look, and what
Bibi's voice should be. Assistant proposals and diffs were thoroughly checked prior
to commit. Baseline code was written and developed by hand. All design decisions,
structures and specifications are solely attributed to the author.

Every change was verified before merging: `./gradlew checkstyleMain checkstyleTest
test javadoc` clean, the scripted console tests passing, and smoke tested thoroughly prior to
commit.

### Images

| File | Origin |
|---|---|
| `src/main/resources/images/DaBibi.png` | Generated by Gemini 3.8-Flash, resized to 512×512 and otherwise unaltered. |

### Compliance with the course reuse policy

Recorded explicitly so a reader can check each requirement against this file:

| Requirement | How this project meets it |
|---|---|
| Widespread AI use is declared in the README, naming the tool, the user, and the extent | See **AI assistance** above: the tools, the author, and a per-area breakdown of what they did |
| Reused or adapted code is credited | The only external structure reused is the JavaFX interface layout, credited under **Course materials** below; no third-party source files are copied into this repository |
| Third-party libraries are listed under Acknowledgements | See **Third-party libraries** above. All four are course-standard for this project; no library beyond them has been added |
| Generated binaries are not committed | `build/` is git-ignored and no JAR is tracked; the runnable JAR is distributed through GitHub releases |
| Generated and reused assets are attributed | See **Images** above: Bibi's portrait is AI-Generated rather than the author's own, and is credited to Gemini |

### Course materials

The structure of the JavaFX interface follows the
[JavaFX tutorial @SE-EDU](https://se-education.org/guides/tutorials/javaFx.html), and
the Checkstyle rules are the ones published with
[addressbook-level3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle).

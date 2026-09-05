# Bibi project template

Bibi is your friendly neighbourhood clanker! 

Use me to log your tasks _simply_ and _safely_. Privacy and confidentiality is at the core of our work. Created as part of CS2103T 26/27.

All you need to do is:

download it from the repo link.
set up as below.
add your tasks.
let it manage your tasks for you 😉
And it is FREE!

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/bibi/Bibi.java` file, right-click it, and choose `Run Bibi.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see Bibi's welcome banner and command instructions.
   ```
   B B B B    i    b b b    i
   B       B       b       b
   B B B B   iii   b b b b  iii
   B       B  i    b       b  i
   B B B B  iii   b b b b  iii
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running with Gradle

The project uses the Gradle wrapper, so no separate Gradle install is needed.
Run these from the project root, with JDK 25 selected.

Compile everything:

```
./gradlew compileJava
```

Run the chatbot (`standardInput` is wired to the console, so Bibi can read your
commands):

```
./gradlew run
```

On Windows `cmd`, use `gradlew.bat` instead of `./gradlew`.

Tasks are saved to `./data/bibi.txt`, relative to the folder the build runs in.

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

Run it from any folder:

```
java -jar "bibi.jar"
```

Bibi creates its `data/bibi.txt` save file relative to the folder the command is
run in, so copying the JAR into an empty folder gives it a fresh task list, and
running it there again restores what was saved.

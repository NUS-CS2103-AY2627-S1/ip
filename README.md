# Tally

Tally is a chatbot that helps you keep a tally of your tasks, in a window or in a terminal. Given below are instructions on how to set it up.

If you want to *use* Tally rather than build it, read the [user guide](https://dillionlim.github.io/ip/) instead.

## Building and running

The project builds with Gradle, so no JDK setup beyond Java 25 is needed:

```bash
./gradlew run     # start the chatbot in a window
./gradlew build   # compile, test, check the style and package
```

Tally also still runs in a terminal, which is how its console tests drive it:

```bash
java -cp build/classes/java/main tally.Tally
```

To hand the chatbot to someone else, build the jar and give them that one file:

```bash
./gradlew shadowJar                  # writes build/libs/tally.jar
java -jar "build/libs/tally.jar"     # or copy it anywhere and run it from there
```

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/tally/Tally.java` file, right-click it, and choose `Run Tally.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
    _____     _ _
   |_   _|_ _| | |_   _
     | |/ _` | | | | | |
     | | (_| | | | |_| |
     |_|\__,_|_|_|\__, |
                  |___/
   Tally.
   I keep the count. You keep the promises.
   ____________________________________________________________
   Session ended. Your tasks did not.
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

### Use of AI tools

Claude (Anthropic), through Claude Code, was used sparingly throughout
this project. The use is in comments beside the individual pieces of code it shaped.

**Increments done with AI assistance,** using the prompts the course supplies for
them:

- `A-Assertions`: documenting the assumptions that hold between the parts
- `A-CodeQuality`: reviewing against the code quality guidelines and fixing the
  highest-priority issue found
- `A-MoreErrorHandling`: finding the commands and data files Tally acted on
  rather than questioned, and refusing them
- `A-Personality`: rewriting every phrase in one voice, and dressing the window
  to match it
- `A-BetterGui`: laying the window out, redrawing the portraits, and cutting the
  space spent on neither words nor input
- `A-MoreTesting`: measuring what the suite reached and covering what it did not

**Written by Claude to my requirements,** and marked as such in the files
themselves:

- `test/run-ui-tests.py`: the console test runner
- `test/ui-test-plan.md`: the console test cases and their expected output
- `.claude/skills/test-ui/SKILL.md`: the skill that drives it

**Individual suggestions,** each marked with a comment where it was taken:

| Where | What |
| --- | --- |
| `Tally.java` | switch over the command enum in place of an if-else chain |
| `Tally.java` | `String.format` in place of manual string concatenation |
| `Tally.java` | a grammatical error identified, fixed by hand |
| `Parser.java` | a bug in event parsing found, fixed by hand |
| `TallyException.java` | the missing `serialVersionUID` identified and fixed |

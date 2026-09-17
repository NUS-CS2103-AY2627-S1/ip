# Jarvis project template

This is a project template for a greenfield Java project. It's named _Jarvis_. Given below are instructions on how to use it.

## Setting up in IntelliJ

Prerequisites: JDK 25 and the latest version of IntelliJ.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project in IntelliJ as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in the [IntelliJ JDK setup guide](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/jarvis/Jarvis.java`, right-click it, and choose `Run Jarvis.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the chatbot banner as the output:
   ```
   Jarvis
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Project conventions

See [Java coding standards](docs/coding-standards.md) and [Git conventions](docs/git-conventions.md) before contributing.

## Before submitting changes

Use JDK 25 and run the complete verification suite from the project root:

```sh
./gradlew checkstyleMain checkstyleTest test
```

If a console command or its output changes, update the
[UI test plan](test/ui-test-plan.md) and run it with:

```sh
python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

Review the final diff with `git diff --check`. Do not include build output,
temporary files, credentials, or unrelated user data.

## Running the graphical interface

The JavaFX interface can be started with JDK 25 using:

```sh
./gradlew runGui
```

Enter commands such as `todo read a book`, `list`, `mark 1`, or `delete 1` in
the input box. Tasks are saved in the same `data/jarvis.txt` file used by the
console interface.

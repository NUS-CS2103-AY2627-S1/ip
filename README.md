# Lobby project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Finding tasks

Use `find <keywords>` to search task descriptions.
Every keyword must appear somewhere in the description, ignoring letter case and keyword order.
Partial words match too: `find LAB mark` matches `Finish marking lab reports`.

- `find lab marking` finds descriptions containing both `lab` and `marking`.

Searches include both completed and incomplete tasks. Dates and event times are not searched.
If nothing matches, Lobby displays `No matching tasks found.`
Search results keep their original order but are numbered from 1 within the results.
Use `list` to check the full task numbers before using `mark`, `unmark`, or `delete`.

## Command errors and data recovery

`list` and `bye` take no arguments. Invalid commands such as `bye extra` show an error and keep Lobby running.
Specify `/by` only once for deadlines. For events, specify `/from` and `/to` once each, in that order.
Markers are case-insensitive and can be separated by multiple spaces or tabs.

If Lobby skips malformed saved records or cannot read the save file, it preserves the original bytes before
the next save. Recovery copies appear beside the save file as `<filename>.recovery-<unique number>.bak`.
Keep this copy if you need to inspect or recover records that were not loaded. Existing recovery copies are
never overwritten. If the original cannot be preserved, Lobby rejects the save and undoes the task change.
File-error messages identify the configured save path; a filename without a folder is also supported.

## Continuous integration

The workflow in [`.github/workflows/gradle.yml`](.github/workflows/gradle.yml) is based on
the [SE-EDU Duke workflow](https://github.com/se-edu/duke/blob/full-template/.github/workflows/gradle.yml).
It runs automatically on pushes and when pull requests are opened, reopened, or updated.

Each run checks the project on Linux, macOS, and Windows. On each operating system, GitHub Actions:

1. Checks out the repository so the runner can access the project files.
2. Validates the Gradle Wrapper JAR against known Gradle releases.
3. Installs Zulu JDK 25 with JavaFX.
4. Runs `./gradlew check`, which compiles the code, runs all JUnit tests, and checks main and test code
   with Checkstyle. A failed test, compilation error, or Checkstyle violation fails the check.

To set this up in your own GitHub repository:

1. Place the workflow file at `.github/workflows/gradle.yml`, relative to the project root.
   Keep the indentation intact because YAML uses indentation to group settings.
2. Check locally using Java 25: run `java -version`, then `./gradlew check` on macOS/Linux or
   `.\gradlew.bat check` in Windows PowerShell.
3. Commit and push the workflow file to your GitHub repository. If you authenticate over HTTPS with
   a classic personal access token (PAT), it needs the `workflow` scope in addition to the repository
   access needed to push. Configure the token in your Git client's authentication settings;
   do not put it in the workflow file.
4. Open the repository's **Actions** tab. If GitHub prompts you to enable workflows for a fork,
   enable them, then push another change to trigger a run.
5. Open **Java CI**, select the run, and inspect the three operating-system jobs. Green checks mean
   they passed. For a failed job, expand the failed step to read its error output, fix the issue,
   and push the correction to run CI again.

The workflow uses read-only repository permissions and does not need a PAT stored as a repository
secret. It checks code; it does not publish releases or deploy the application. Branch protection
can be configured separately if passing CI should be required before merging.

For more background, see the [SE-EDU GitHub Actions guide](https://se-education.org/guides/tutorials/githubActions.html).

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/lobby/Lobby.java` file, right-click it, and choose `Run Lobby.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
   ____________________________________________________________
    _           _     _
   | |    ___  | |__ | |__  _   _
   | |   / _ \ | '_ \| '_ \| | | |
   | |__| (_) | |_) | |_) | |_| |
   |_____\___/|_.__/|_.__/ \__, |
                            |___/
   Hello! I'm Lobby.
   What can I do for you?
   ____________________________________________________________
   Bye. Hope to see you again soon!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

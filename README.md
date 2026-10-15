# TBlade project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/tblade/TBlade.java` file, right-click it, and choose `Run TBlade.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   #####  ####   #       ###   ####   #####
     #    #   #  #      #   #  #   #  #
     #    ####   #      #####  #   #  ####
     #    #   #  #      #   #  #   #  #
     #    ####   #####  #   #  ####   #####
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running the JAR file

1. Download the `tblade.jar` file from the [releases page](../../releases).
1. Copy it into an empty folder.
1. Open a command window in that folder.
1. Run the command `java -jar "tblade.jar"`.

Tasks are saved to a `data` folder created next to the JAR file, so they persist between runs.

## Developer setup

**Checkstyle** enforces the project's [Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

- Run it manually with `./gradlew checkstyleMain checkstyleTest`, or `./gradlew check` (also runs the tests), or `./gradlew build` (also builds the jar) — all three run Checkstyle automatically.
- To have it run automatically before every commit (blocking the commit if there are violations), run this once per clone:
  ```
  git config core.hooksPath .githooks
  ```
  This points git at the [`.githooks/pre-commit`](.githooks/pre-commit) script tracked in this repo. It's a local git setting, not something git can pick up on its own from a fresh clone, so each clone needs to run the command once. To skip the check for one commit, use `git commit --no-verify`.

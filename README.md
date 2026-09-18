# Oreo project template

This is a project template for a greenfield Java project. The chatbot is named _Oreo_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/oreo/Oreo.java` file, right-click it, and choose `Run 'Oreo.main()'` (if the code editor is showing compile errors, try restarting the IDE). IntelliJ should recognise `src/main/java` as the source root and use the `oreo` package automatically. If the setup is correct, you should see something like the below as the output:
   ```
     OOO   RRRR   EEEEE  OOO  
    O   O  R   R  E     O   O 
    O   O  RRRR   EEEE  O   O 
    O   O  R R    E     O   O 
     OOO   R  RR  EEEEE  OOO  
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

The Java classes are organised into packages under the source root:

```text
src/main/java/oreo/
├── Oreo.java       # Application entry point
├── command/        # User commands
├── core/           # Parsing and shared application logic
├── model/          # Task-related classes
├── storage/        # File persistence
└── ui/             # Console input and output
```

To run the application from a terminal using JDK 25:

```sh
javac -d /tmp/oreo-classes $(find src/main/java -name '*.java')
java -cp /tmp/oreo-classes oreo.Oreo
```

## Create and run an executable fat JAR

This project uses the Shadow Gradle plugin to package the application and its
dependencies into one executable JAR file.

1. Select Java 25:

   ```sh
   sdk use java 25.0.3.fx-zulu
   ```

2. Build the fat JAR from the project root:

   ```sh
   ./gradlew shadowJar
   ```

3. Locate the generated file at `build/libs/oreo-all.jar` and run it with:

   ```sh
   java -jar build/libs/oreo-all.jar
   ```

The JAR is executable because its manifest names `oreo.ui.Launcher` as the
main class, so it opens the graphical interface directly. On Windows, use `gradlew.bat shadowJar` instead of `./gradlew
shadowJar`.

## Acknowledgement of AI use 

## Merchandise details

Tasks can optionally store one free-form merchandise detail. Use
`merchandise TASK_NUMBER DETAILS` to add it, `edit-merchandise` to replace it,
`delete-merchandise` to remove it, `find-merchandise KEYWORD` to search it, and
`listall` to display tasks with their merchandise. The existing `list` and
`find` commands continue to operate on tasks only.

### Week 2 iP
- ChatGPT/Codex was used mostly for the code in the submission following the wk 2 iP Increments as followed
- Review was done and tested personally myself for the example test cases as well.

### Week 3 iP
- ChatGPT/Codex was used mostly for the code in the submission following the wk 3 iP Increments as instructed
- Review was done making use of the test scripts and reviewed manually by myself
- made use of google AI to help understand some code I was unfamiliar with as well.

#### Handling merge conflicts

During Week 3, some merge conflicts were painful to resolve, especially when
two branches modified the same part of a Java file. The following process can
make conflict resolution safer:

1. Save or commit local work before starting a merge, then update the target
   branch from `origin`.
2. Read every conflict marker (`<<<<<<<`, `=======`, and `>>>>>>>`) and compare
   both versions. Do not blindly choose “ours” or “theirs”.
3. Keep the intended parts from both branches, remove all conflict markers,
   and check that the resulting code still follows the project coding
   standard.
4. Compile the application and run the unit and UI tests before committing the
   merge. Review the final diff as an additional check.

Conflicts can also be reduced by planning the branch structure more carefully:

- Merge foundational changes, such as package moves or large refactorings,
  before feature branches that add or modify code in those files.
- Avoid parallel branches changing the same core files at the same time. For
  example, a feature branch should wait for a package or coding-standard
  refactor to be merged, or be rebased onto it first.
- Keep commits small and focused, separating refactoring, feature work, tests,
  and documentation. This makes changes easier to review and cherry-pick.
- Prefer adding new classes or methods over repeatedly editing the same large
  file, and coordinate ownership of shared files such as `TaskList.java`.
- Merge short-lived branches regularly and rebase a feature branch before
  opening a pull request so conflicts are discovered while the relevant code
  is still familiar.

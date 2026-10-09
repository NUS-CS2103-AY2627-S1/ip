# Sumo project template

This is a project template for a greenfield Java project. The chatbot is named _Sumo_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, run `./gradlew run` (macOS/Linux) or `.\gradlew.bat run` (Windows) from the
   project root. Alternatively, run `sumo.Launcher.main()` from IntelliJ. The application opens a
   simple chat window with a command field and a **Send** button.

   Add your own avatar images at the following paths. The GUI also works while either image is
   missing.

   - `src/main/resources/images/UserPfp.png`
   - `src/main/resources/images/SumoPfp.png`

   Enter the same commands supported by the console version, such as `todo read book`, `list`,
   `mark 1`, and `bye`. Press **Enter** or click **Send** to submit a command.

   The original console interface remains available by running `sumo.Sumo.main()`.
   See the [User Guide](docs/README.md) for current commands, examples, and JAR launch instructions.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Command validation and saved-data recovery

Sumo accepts leading, trailing, and repeated horizontal whitespace (including tabs), and stores
descriptions with single spaces. Command words are case-sensitive; `find` matches descriptions without regard to capitalization. `list`, `sort`, and `bye` take no arguments;
task numbers must contain only digits and refer to a task in the normal list.

Use `deadline <description> /by <date> [HHmm]` or
`event <description> /from <date> [HHmm] /to <date> [HHmm]`. Parameters must appear exactly once,
in that order. Standalone slash-prefixed tokens in dated commands are reserved for parameters.
Dates must be real calendar dates in `yyyy-MM-dd` or `d/M/yyyy` format, with years from 0001 to 9999.
Times use four-digit 24-hour format, from `0000` to `2359`. An event must end strictly after it starts;
an omitted time means midnight, so a same-day event needs an explicit end time after its start.
Descriptions cannot contain `|`, line breaks, or control characters.

Tasks with the same type, description, and dates/times are duplicates even when their capitalization,
spacing, or completion status differs. A date without a time compares equal to that date at midnight.
Use different dates or details for recurring work, or delete the old task before adding it again.

A missing data folder or file is created as needed. When saved records are malformed or duplicated,
Sumo reports their line numbers and loads the valid records for viewing. Saving is disabled to preserve
the original file. The same protection applies when the file cannot be read. Back up and repair
`data/sumo.txt` (including its permissions, if necessary), then restart Sumo. Loading warnings appear
in both the console and chat window.

If saving fails, the attempted change is rolled back and Sumo shows an error. Files changed or deleted
outside the current session are detected before saving; restart to load the current data before editing.

## Checking the Java coding standard

This project uses Checkstyle to automate part of the SE-EDU Java coding standard. Run the
following command from the repository root before submitting changes:

```powershell
.\gradlew.bat checkstyleMain checkstyleTest
```

The Checkstyle configuration is in `config/checkstyle/checkstyle.xml`, with test-specific
exceptions in `config/checkstyle/suppressions.xml`. To get feedback while coding in IntelliJ,
install the Checkstyle-IDEA plugin and import the local `config/checkstyle/checkstyle.xml` file
using Checkstyle version 10.24.0.

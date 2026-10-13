# Baymax project template

This is a project template for a greenfield Java project. It's named _Baymax_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/baymax/Launcher.java`, right-click it, and choose `Run 'Launcher.main()'` to start the JavaFX interface (if the code editor is showing compile errors, try restarting the IDE). To run the preserved console interface, use `gradlew.bat runTextUi` or run `Baymax.main()` directly.
   ```
   
                BBBB   aaa   y   y  m     m   aaa   x   x
                B   B a   a  y   y  mm   mm  a   a  x   x
                B   B a   a   y y   m m m m  a   a   x x
                BBBB  aaaaa    y    m  m  m  aaaaa    x
                B   B a   a    y    m     m  a   a   x x
                B   B a   a    y    m     m  a   a  x   x
                BBBB  a   a    y    m     m  a   a  x   x
                
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running Baymax

Use Java 25 for all commands.

- `gradlew.bat run` starts the JavaFX user interface.
- `gradlew.bat runTextUi` starts the preserved console user interface.
- `gradlew.bat test` runs the JUnit test suite.
- `gradlew.bat shadowJar` builds `build/libs/baymax.jar`.

The JavaFX application starts through `baymax.Launcher`, which delegates to
`baymax.Main`. The separate launcher avoids the JavaFX classpath issue
described in the [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFxPart1.html).

## Input validation and storage recovery

Commands accept leading/trailing spaces, repeated spaces, and tabs. Task numbers
must be positive integers referring to an existing task. Commands with missing
arguments report an error and leave the task list unchanged.

Dates use `yyyy-MM-dd`, must exist on the calendar, and use years `0001` through
`9999`. Year zero, negative years, and larger years are rejected in commands and
stored records. Events must end strictly
after their start date; same-day events are rejected. Supply `/by` once for a
deadline, or `/from` followed by `/to` once each for an event. Slashes in these
commands are reserved for date parameters. Descriptions cannot contain `|` or
control characters because those characters would break the storage format.

Tasks with the same type, description, and dates are duplicates, even if one is
completed. Description comparisons are case-sensitive; command whitespace is
normalized before comparison.

UTF-8 files with an optional BOM at the very beginning can be loaded normally.
Baymax creates a missing data file and its parent folders when saving. It writes
UTF-8 data to a temporary file before replacing the previous file, using an atomic
replacement when the filesystem supports it. The console also saves when input
ends without `bye`. If saving fails after `bye`, input stays available for retry.

Baymax checks for changes made by another instance before saving. If a conflict
is reported, copy your unsaved tasks somewhere safe and restart to load the latest
file. A `Baymax.txt.lock` file coordinates saves between instances; leave it in
place while Baymax is running. The lock does not coordinate with external editors,
so avoid editing the data file while the application is saving.

If `data/Baymax.txt` cannot be read, or contains invalid or duplicate records,
Baymax displays a warning and enters read-only mode to protect the original file.
Adding, deleting, marking, and unmarking tasks are blocked. Listing, searching,
and exiting remain available; exiting in this mode does not write the data file.
Valid records remain available when individual records are invalid. Back up the
file, correct its contents or permissions, and restart Baymax before making changes
you need to save. A save failure in the JavaFX interface leaves input enabled.

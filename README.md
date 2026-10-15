# Sage

Sage is a calm, practical companion for tasks and small things worth remembering.
Its compact JavaFX interface keeps your commands brief and gives replies room to breathe.
Tasks and notes are saved to `data/sage.txt` relative to the folder from which you launch Sage.

## Quick start

Use **Java 25**. Build with `./gradlew clean check shadowJar`, then launch with
`java -jar build/libs/sage.jar`. On Windows, use `gradlew.bat` for Gradle commands.
See the [user guide](docs/README.md) for supported platforms and troubleshooting.
The JAR bundles its JavaFX dependencies, but not a Java runtime.

The window can be resized. Press Enter or select **Send** to submit a command, and select **Commands**
for a command reference. Errors have a separate labelled card, so they are distinguishable without
relying only on colour. Type `bye` to close Sage.

## Commands

| Command | Example |
| --- | --- |
| Add a task | `todo Read one chapter` |
| Add a deadline | `deadline Submit report /by 2026-10-01 18:00` |
| Add an event | `event Study session /from 2026-10-01 14:00 /to 2026-10-01 15:00` |
| Save a note | `note Movie to watch: Spirited Away` |
| List all entries | `list` |
| Search descriptions | `find chapter` |
| Complete a task | `mark 1` |
| Reopen a task | `unmark 1` |
| Delete an entry | `delete 1` |
| Exit | `bye` |

Command names are lowercase. Leading/trailing spaces and extra spacing between the command and
its arguments are accepted. Parameters such as `/by`, `/from`, and `/to` must appear exactly once
where required. Use `list` to check an entry's number before changing it.

Dates support `yyyy-MM-dd` and `d/M/yyyy`, optionally followed by `HH:mm` or `HHmm` (24-hour time).
Impossible numeric dates and events ending at or before their start are rejected.
Text such as `Sunday` is also accepted for convenience, but Sage cannot compare natural-language
times or schedule reminders. New entries with identical details are rejected, even if the existing task is complete.
Duplicate entries saved by older Sage versions remain readable.

## Keeping your data safe

A missing data file is normal on first launch; Sage creates it when you save your first entry.
If Sage cannot read existing data, it shows a warning and blocks changes to avoid overwriting it.
Back up the file, fix its contents or permissions, then restart Sage. A failed save is reported as an
error and does not leave an unsaved change in the list. Do not edit the file while Sage is running
or run two copies against the same file.

Keep personal data out of Git: the `data/` directory, build products, and local settings are ignored.
Do not include your own data file when sharing the JAR. Keep independent backups of important data.

## Remembering information with notes

Use `note <text>` to save a short, single-line snippet, for example:

```text
note Waist size: 32 inches
note Movie to watch: Spirited Away
list
find waist
delete 1
```

Notes appear as `[N]` entries alongside tasks. Use the number shown by `list` when deleting a note;
search results retain these same numbers. Notes can be searched and deleted but cannot be marked as completed.
Empty notes are rejected. Unicode and pipe characters (`|`) are preserved when notes are saved and reloaded.
Notes share the existing 100-entry limit with tasks.

## Setting up in IntelliJ

Prerequisites: JDK 25 and a recent IntelliJ version. On macOS, use the JavaFX-enabled Azul Zulu distribution required
by the SE-EDU JavaFX tutorial (`sdk use java 25.0.3.fx-zulu`).

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project
   first).
1. Open the project in IntelliJ as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Locate `src/main/java/sage/Launcher.java`, right-click it, and choose `Run Launcher.main()` (if the code editor
   shows compile errors, try reloading the Gradle project or restarting the IDE).

You can also start the GUI from a terminal with `./gradlew run`. The `sage.Sage` class retains the text-based entry
point for automated testing.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

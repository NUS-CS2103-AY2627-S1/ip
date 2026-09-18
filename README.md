# Kachow

Kachow is your Cars-inspired task companion, developed as part of an introductory software engineering course.
Think of it as a cheerful Radiator Springs pit-crew pal: Mater scouts out your tasks, Guido tunes up edits,
and completed tasks earn a Piston Cup celebration. It cheers you on through practice laps and pit stops,
while keeping task details and command guidance clear. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/com/benthecat/kachow/Kachow.java` file, right-click it, and choose `Run Kachow.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
       ____________________________________________________________
        _  __          _
       | |/ /__ _  ___| |__   _____      __
       | ' // _` |/ __| '_ \ / _ \ \ /\ / /
       | . \ (_| | (__| | | | (_) \ V  V /
       |_|\_\__,_|\___|_| |_|\___/ \_/\_/
       Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
       You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
       ____________________________________________________________

   todo read book
       ____________________________________________________________
       Green light, buddy! I've rolled this task onto the starting grid:
         [T][ ] read book
       Your garage now holds 1 task.
       ____________________________________________________________

   deadline return book /by 2/12/2019 1800
       ____________________________________________________________
       Green light, buddy! I've rolled this task onto the starting grid:
         [D][ ] return book (by: Dec 02 2019, 6:00 PM)
       Your garage now holds 2 tasks.
       ____________________________________________________________

   event project meeting /from 3/12/2019 1400 /to 1600
       ____________________________________________________________
       Green light, buddy! I've rolled this task onto the starting grid:
         [E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
       Your garage now holds 3 tasks.
       ____________________________________________________________

   list
       ____________________________________________________________
       Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
       1.[T][ ] read book
       2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
       3.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
       ____________________________________________________________

   mark 2
       ____________________________________________________________
       Ka-chow! That's Piston Cup spirit! This task is marked done:
         [D][X] return book (by: Dec 02 2019, 6:00 PM)
       Doc Hudson would be proud. One task at a time, one lap closer.
       ____________________________________________________________

   list
       ____________________________________________________________
       Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
       1.[T][ ] read book
       2.[D][X] return book (by: Dec 02 2019, 6:00 PM)
       3.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
       ____________________________________________________________

   unmark 2
       ____________________________________________________________
       Another practice lap! Even Lightning needs those. This task is marked not done:
         [D][ ] return book (by: Dec 02 2019, 6:00 PM)
       ____________________________________________________________

   bye
       ____________________________________________________________
       Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
       ____________________________________________________________
   ```

All output produced by Kachow is indented by four spaces to distinguish it from
the user's input. Add tasks with `todo DESCRIPTION`, `deadline DESCRIPTION /by
DATE_OR_TIME`, or `event DESCRIPTION /from START /to END`. Deadline and event
dates accept `yyyy-MM-dd`, `yyyy/M/d`, `d/M/yyyy`, or padded US-style
`MM/dd/yyyy`; add a time as `HHmm`, `HH:mm`, or an
AM/PM time such as `6pm` or `6 PM`. A time-only event end uses the event's start date and must not be
earlier than or equal to its start; use a full end date for an overnight event.
Use `on DATE` to list deadlines due and events occurring on a date. The command
retains the original task numbers, making its results usable with `mark`,
`unmark`, and `delete`. To preserve `2/12/2019` as 2 December, ambiguous US
dates must be zero-padded, for example `12/02/2019` for December 2.
Use `find KEYWORD` to search task descriptions without regard to letter case.
Search results also retain the tasks' original numbers.
Tasks are loaded from
`./data/kachow.txt` when Kachow starts and are saved there automatically after
every add, mark, unmark, edit, or delete command. Kachow creates the `data` directory
and file automatically on first use. Enter `list` to display the saved tasks.
Use `mark NUMBER` or `unmark NUMBER` to change a task's completion status; task
numbers start at 1 as shown by `list`. Use `delete NUMBER` to remove a task from
the race; the remaining tasks are renumbered automatically.
Use `edit NUMBER /description DESCRIPTION` to rename any task. For deadlines,
use `edit NUMBER /by DATE_OR_TIME`; for events, use `edit NUMBER /from START` or
`edit NUMBER /to END`. Include multiple fields in one command when needed, for
example `edit 3 /from 1300 /to 1700 /description planning meeting`. All fields
are applied together only after the complete edit is valid. The command keeps
the task's completion status, position, and unmentioned details. A time without
a date keeps that detail's existing date, for example `edit 3 /to 1700`.

Commands accept leading/trailing spaces, tabs, and repeated spaces, which are normalized.
Task numbers must use positive digits (`1`, `2`, ...); signs, decimals, and extra arguments are rejected.
Descriptions may contain punctuation and Unicode text, but not `|`, line breaks, control characters,
or separate slash-prefixed fields such as `/oops`. Command fields must appear exactly once where required.
An event must finish strictly after its start; date-only boundaries are compared at midnight.

Kachow rejects duplicate tasks on add and edit. A duplicate has the same task type, description
(ignoring case and repeated whitespace), and date/time values, regardless of completion status.
A date-only value and an explicit midnight time remain distinct details.

If a command is invalid or saving fails, neither the task list nor its saved data is changed.
Saves use an atomic file replacement; if your filesystem cannot support it, Kachow reports an error.
Check the file and directory permissions and free disk space before retrying a failed save.
If startup reports unreadable or malformed data (including duplicate records), saving is disabled
for that session to protect the original file. Repair the indicated record or restore a backup,
then restart Kachow. A missing file starts an empty list. The data file must be a regular file,
not a directory or symbolic link. If another application changes or deletes the file during a
session, restart Kachow to load that change before modifying tasks. Use one Kachow instance at a time.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

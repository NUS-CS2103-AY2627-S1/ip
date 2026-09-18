# Toothless User Guide

Toothless is a task-management chatbot. Use it to track todos, deadlines, and events. It saves your tasks between
sessions.

<img width="1470" height="902" alt="image" src="https://github.com/user-attachments/assets/3ff5465f-f759-4104-a6ff-5707c7ece6ec" />


## Quick start

1. Install **Java 25**.
2. Download `Toothless.jar` from the [latest release](https://github.com/wenyiliangg/ip/releases/latest).
3. Open a terminal in the folder containing the JAR and run:

   ```shell
   java -jar Toothless.jar
   ```

4. In the chat box, try `todo read a book`, then `list`. Press **Enter** or select **Send**.
5. Your tasks are saved automatically and loaded the next time you open Toothless.

## Run From Source

1. Install **JDK 25**. On a Mac using SDKMAN, run `sdk use java 25.0.3.fx-zulu` if needed.
2. Open a terminal in the project directory and run:

   ```shell
   ./gradlew run
   ```

3. Type a command in the chat box and press **Enter** or select **Send**. Try `todo read a book`, then `list`.


## Using Help

Select **Help** in the top-right corner to see clickable command examples. Selecting one fills the chat box
without sending it, so you can edit it first. Select **Edit task** in Help for edit examples. Select **Help**
again to close the panel. There is no typed `help` command.

## Command format

Replace `UPPER_CASE` words with your own values. For example, `todo DESCRIPTION` becomes `todo read a book`.
Type parts such as `/by` exactly as shown. Use the lowercase command words in the table below.

Use the task numbers from `list` for `mark`, `unmark`, `edit`, and `delete`. `find` renumbers its results for
display only; those numbers may not match the full list.

## Commands

| What it does | Format | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo read a book` |
| Add a deadline | `deadline DESCRIPTION /by yyyy-MM-dd` | `deadline return book /by 2026-12-31` |
| Add an event | `event DESCRIPTION /from START /to END` | `event meeting /from 2pm /to 4pm` |
| Show all tasks and their numbers | `list` | `list` |
| Find tasks by description, ignoring case | `find KEYWORD_OR_PHRASE` | `find book` |
| Mark a task done | `mark TASK_NUMBER` | `mark 1` |
| Mark a task not done | `unmark TASK_NUMBER` | `unmark 1` |
| Change a task's details | `edit TASK_NUMBER /FIELD NEW_VALUE` | `edit 1 /description read more` |
| Remove a task | `delete TASK_NUMBER` | `delete 1` |
| End the session | `bye` | `bye` |

For `edit`, use `/description` for any task, `/by` for a deadline, or `/from` and `/to` for an event. You can
change more than one field at once. For example, if task 2 is an event: `edit 2 /from 2pm /to 4pm`.

## Dates and times

Deadlines use `yyyy-MM-dd` dates. Events accept times such as `2pm`, `2:30pm`, and `14:30`. Add a day if needed,
such as `Monday 2pm`, `tomorrow 2pm`, or `2026-09-21 14:00`. For events spanning different days, give full dates
for both `/from` and `/to`. The start must be earlier than the end.

## Saving tasks

Toothless saves task changes automatically in `data/toothless.txt` and loads them when you reopen it. There is
no save command.

## Ending a session

Type `bye` and press **Enter** or select **Send**. Toothless says goodbye and closes the window. Run `java -jar Toothless.jar` or `./gradlew run` again to start a new session with your saved tasks.

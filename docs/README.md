# Laby User Guide

## Getting Started

1. Make sure Java 25 is installed.
2. Download the latest `laby.jar` file from the [Laby GitHub repository](https://github.com/bibikowlf/ip).

3. Open a terminal in the folder containing the downloaded JAR file and start Laby:

   ```powershell
   java -jar laby.jar
   ```

4. When the Laby window opens, type a command in the input box and press `Enter` or click `Send`.

   You will see an interface similar to this:

   ![Laby user interface](Ui.png)

   *Image credit: Profile pictures are taken from in-game screenshots of [Path to Nowhere](http://ptn.aisnogames.com/en-EN/home).*
5. Laby stores your tasks and contacts in `data/laby.txt`, so your data is available the next time you start the application.

In the command formats below, replace angle-bracketed values such as `<DESCRIPTION>` with your own information. Do not type the angle brackets.

## Adding To-Dos

Use `todo` followed by a description to add a task without a date or time.

Format: `todo <DESCRIPTION>`

```text
todo read chapter 1
```

Laby adds the to-do as an incomplete task.

## Adding Deadlines

Use `deadline` followed by a description and the `/by` marker. Enter the deadline in `yyyy-MM-dd HH:mm` format.

Format: `deadline <DESCRIPTION> /by <DATE_AND_TIME>`

```text
deadline submit report /by 2026-09-20 23:59
```

Laby adds the deadline as an incomplete task.

## Adding Events

Use `event` followed by a description, a start time with `/from`, and an end time with `/to`. Enter both times in `yyyy-MM-dd HH:mm` format.

Format: `event <DESCRIPTION> /from <START_DATE_AND_TIME> /to <END_DATE_AND_TIME>`

```text
event project meeting /from 2026-09-21 14:00 /to 2026-09-21 16:00
```

Laby adds the event as an incomplete task.

## Viewing Tasks and Contacts

Use `list` to display all tasks and contacts. Tasks and contacts are numbered separately.

Format: `list`

```text
list
```

Laby displays all tasks and contacts, with tasks and contacts numbered separately.

## Finding Tasks

Use `find` followed by a keyword to display tasks whose descriptions contain that keyword. The search is not case-sensitive.

Format: `find <KEYWORD>`

```text
find report
```

Laby displays all tasks whose descriptions contain the keyword.

## Marking Tasks as Done or Not Done

Use the task's index from the task list with `mark` or `unmark`.

Formats:

- `mark <TASK_INDEX>`
- `unmark <TASK_INDEX>`

The task index is the number shown beside the task in the task list. For example, `mark 1` marks the first task as done.

```text
mark 1
unmark 1
```

Laby marks or unmarks the task with the specified index.

## Deleting Tasks

Use `deletetask` followed by the task's index.

Format: `deletetask <TASK_INDEX>`

```text
deletetask 2
```

Laby deletes the task with the specified index.

Task indices are updated after a task is deleted.

## Adding Contacts

Use `contact` followed by a name, phone number with `/p`, and email address with `/e`.

Format: `contact <NAME> /p <PHONE_NUMBER> /e <EMAIL_ADDRESS>`

```text
contact Jane Doe /p 91234567 /e jane@example.com
```

Laby adds the contact to the contact list.

## Deleting Contacts

Use `deletecontact` followed by the contact's index from the contact list.

Format: `deletecontact <CONTACT_INDEX>`

```text
deletecontact 1
```

Laby deletes the contact with the specified index.

## Exiting Laby

Use `bye` to close the application.

Format: `bye`

```text
bye
```

Laby closes the application.

## Command Summary

- `todo <DESCRIPTION>`
- `deadline <DESCRIPTION> /by <DATE_AND_TIME>`
- `event <DESCRIPTION> /from <START_DATE_AND_TIME> /to <END_DATE_AND_TIME>`
- `list`
- `find <KEYWORD>`
- `mark <TASK_INDEX>`
- `unmark <TASK_INDEX>`
- `deletetask <TASK_INDEX>`
- `contact <NAME> /p <PHONE_NUMBER> /e <EMAIL_ADDRESS>`
- `deletecontact <CONTACT_INDEX>`
- `bye`

## Credit

This project is assisted by AI. ChatGPT Codex was used by the developer to generate and review code with human oversight. The use includes documentation and testing.
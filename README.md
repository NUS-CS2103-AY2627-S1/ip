# Jimbo User Guide

![Ui.png](docs/Ui.png)

Always wanted an app that can help you track your tasks, deadlines, and events? 🤔

Jimbo is your perfect companion as you do your stuff!

# Requirements

- Java 25

You can check your java version by doing `java -version` in a terminal

# Quick Start

1. Ensure you have Java 25 installed
2. Install jimbo.jar from the GitHub releases page
3. Place the jar file in the folder you want you tasks to be saved to
4. Open the terminal in that folder, and run `java -jar jimbo.jar`
5. You can now start using Jimbo!

# Features

## Note About Inputs

### Datetimes

All datetime inputs should be in the format

`yyyy-MM-dd HHmm`, where `HHmm` is 24 hour time.

For example, for 15 December 2026, 2:35pm, you should type:
`2026-12-15 1435`

### Index

For commands requiring indices, they can be found by using the `list` command.

## Adding Todos

Adds a simple task to your to-do list.

Format: `todo <task description>`

Example: `todo buy pineapples`

## Adding Deadlines

Adds a task with a deadline.

Format: `deadline <task description> /by <deadline datetime>`

Example: `deadline eat pineapples /by 2026-12-12 1212`

## Adding Events

Adds a task that lasts from one time to another time.

Format: `event <task description> /from <start datetime> /to <end datetime>`

Example: `event pineapple eating content /from 2026-12-15 1200 /to 2026-12-15 1500`

Note that /from should always come before /to.

## Listing All Tasks

Lists all tasks in your to-do list.

Format: `list`

## Deleting Tasks

Deletes a task given its index (can be found using `list`).

Format: `delete <task index>`

Example:
`list` output:
```
you should probably do these soon:
1. [T][] buy lemon
2. [T][] sell lemon
```
`delete 1` will delete the first task


## Finding Tasks

Finds a task whose description contains the given text.

Format: `find <text to search>`

Example:
To find all tasks with the word "marbles" in the description:
`find marbles`

## Marking Tasks

Marks a task as done. Task index can be found using `list`.

Format: `mark <task index>`

Completed tasks will be marked with a checkbox [X].

Example completed task:
`1. [T][X] sleep`

## Unmarking Tasks

Unmarks a task as done. Task index can be found using `list`.

Format: `unmark <task index>`

Incomplete tasks will be marked with an empty checkbox [ ].

Example completed task:
`1. [T][ ] wake up`

## Tagging Tasks

Adds a tag to a task. Useful for adding small pieces of information to a task.

Format: `tag <task index> /tag <tag description>`

Example:

If you have a task `1. [T][ ] buy groceries`

You can add tags to remind yourself what to buy:

```
tag 1 /tag banana
tag 1 /tag pineapple
tag 1 /tag eggs
```
and the tags will be shown at the side:

`1. [T][ ] buy groceries (banana, pineapple, eggs)`

## Untagging Tasks

Removes a tag from a task. Will remove the tag that matches the provided tag description exactly.

Format: `untag <task index> /tag <tag description>`

Example:

Continuing the example from tagging tasks above, if we want to remove the "pineapple" tag, we can do:

`untag 1 /tag pineapple`

and the task will be updated:
`1. [T][ ] buy groceries (banana, eggs)`

## Saving

Tasks are automatically saved. If you wish to do a manual save, you can do
`save`.

## Exiting

Simply say `bye` to Jimbo and he will leave.

# Credits

- Claude Sonnet 5 was used to improve the GUI (A-BetterGui).
- Claude Sonnet 5 and ChatGPT-5.6 Luna was used to write tests (A-MoreTesting).
- Inspiration was taken from A-MoreOOP AI guidance section for Command design.
  (https://nus-cs2103-ay2627-s1.github.io/website/schedule/week3/project.html#a-moreoop)
- Datetime formats were adapted from the Oracle Java docs
# Nova User Guide

Nova is a desktop task-management chatbot. Type commands into the input box and Nova will maintain your task list and save it between sessions.

## Adding a todo

Format: `todo DESCRIPTION`

Example: `todo read book`

## Adding a deadline

Format: `deadline DESCRIPTION /by YYYY-MM-DD`

Example: `deadline submit report /by 2026-09-18`

## Adding an event

Format: `event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD`

Example: `event project meeting /from 2026-09-18 /to 2026-09-19`

## Listing tasks

Format: `list`

Displays all tasks in their current order.

## Marking and unmarking tasks

Formats:

- `mark TASK_NUMBER`
- `unmark TASK_NUMBER`

Example: `mark 2`

## Deleting a task

Format: `delete TASK_NUMBER`

Example: `delete 2`

## Finding tasks

Format: `find KEYWORD`

Example: `find report`

The search is case-insensitive and matches the keyword anywhere in the task description.

## Sorting tasks

Format: `sort`

Sorts tasks alphabetically by description, ignoring letter case, and saves the new order.

## Exiting Nova

Format: `bye`

Closes the application after displaying the goodbye message.


## AI Usage Declaration

Liu Junhan (ZachL1113) used Claude (Anthropic) and OpenAI Codex to generate most of the code, tests and documentation in this project. I directed the work, reviewed the generated changes and tested them before submission. The `CLAUDE.md` and `AGENTS.md` files in this repository are the instruction files used with these tools.

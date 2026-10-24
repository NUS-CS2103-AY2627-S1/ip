# Nabmak User Guide

Nabmak is a task manager that helps you keep track of your tasks and places
you want to remember.

## Command format

- Words in `UPPER_CASE` represent information that you need to provide.
- Dates and times must use the format `dd-MM-yyyy HH:mm`.
- Examples are provided for each command below.

## Adding a todo

Adds a task without a specific date or time.

Format:

`todo DESCRIPTION`

Example:

`todo finish homework`

## Adding a deadline

Adds a task that needs to be completed by a specific date and time.

Format:

`deadline DESCRIPTION /by DATE`

Example:

`deadline submit report /by 20-09-2026 23:59`

## Adding an event

Adds an event with a start and end date/time.

Format:

`event DESCRIPTION /from START /to END`

Example:

`event lecture /from 20-09-2026 10:00 /to 20-09-2026 12:00`

## Listing tasks

Shows all your saved tasks.

`list`

## Marking a task as done

Format:

`mark TASK_NUMBER`

Example:

`mark 2`

## Marking a task as not done

Format:

`unmark TASK_NUMBER`

Example:

`unmark 2`

## Deleting a task

Format:

`delete TASK_NUMBER`

Example:

`delete 2`

## Finding tasks

Finds tasks whose descriptions contain the given keyword.

Format:

`find KEYWORD`

Example:

`find homework`

## Saving places

Nabmak can also remember places and details about them.

### Adding a place

Format:

`place add NAME /details DETAILS`

Example:

`place add Toast Box /details Good place for breakfast`

### Listing places

`place list`

### Deleting a place

Format:

`place delete PLACE_NUMBER`

Example:

`place delete 1`

## Exiting Nabmak

Use:

`bye`
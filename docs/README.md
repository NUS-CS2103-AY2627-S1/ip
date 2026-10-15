# Orangutan User Guide

> "Apes together strong." – Caesar (_Rise of the Planet of the Apes_)

The Orangutan ChatBot is here to help you with managing tasks, deadlines and events.

* [Adding a To-Do: `todo`](#adding-a-to-do-todo)
* [Adding a Deadline: `deadline`](#adding-a-deadline-deadline)
* [Adding an Event: `event`](#adding-an-event-event)
* [Listing all Items: `list`](#listing-all-items-list)
* [Marking an Item as Completed: `mark`](#marking-an-item-mark)
* [Unmarking an Item as To Be Completed: `unmark`](#unmarking-an-item-unmark)
* [Deleting an Item: `delete`](#deleting-an-item-delete)
* [Searching for an Item by Name: `find`](#searching-for-an-item-find)
* [Sorting the List: `sort`](#sorting-the-list-sort)
* [Exiting Orangutan: `bye`](#exiting-orangutan-bye)
* [Command Summary](#command-summary)
* [Acknowledgements](#acknowledgements)

## Adding a To-Do: `todo`

Adds a to-do to the list.

Format: `todo <name>`

Examples:
* `todo project`
* `todo forage for berries`

## Adding a Deadline: `deadline`

Adds a deadline to the list.

Format: `deadline <name> \by <due date time>`
* Date and time must be in the format `YYYYMMDD HHMM`.

Example:
* `deadline project presentation /by 20260831 1000`

## Adding an Event: `event`

Adds an event to the list.

Format: `event <name> \from <start date time> /to <end date time>`
* Dates and times must be in the format `YYYYMMDD HHMM`.

Examples: 
* `event board meeting /from 20260501 1000 /to 20260501 1830`

## Listing all Items: `list`

Lists all recorded events, along with completion status and relevant dates/times (if applicable).

Format: `list`

## Marking an Item: `mark`

Marks list item as completed.

Format: `mark <index>`
* Marks the item at the given `index`. This index is the position of the item based on the current sorting, 
  as displayed by `list`.
* the index must be a positive integer between 1 and the length of the list.

Example:
* `mark 1` marks the 1st item in the list.

## Unmarking an Item: `unmark`

Unmarks a list item as to be completed.

Format: `unmark <index>`
* Marks the item at the given `index`. This index is the position of the item based on the current sorting,
  as displayed by `list`.
* the index must be a positive integer between 1 and the length of the list.

Example:
* `unmark 4` unmarks the 4th item in the list.

## Deleting an Item: `delete`

Deletes an item from the list.

Format: `delete <index>`
* Marks the item at the given `index`. This index is the position of the item based on the current sorting,
  as displayed by `list`.
* the index must be a positive integer between 1 and the length of the list.

Example:
* `delete 12` deletes the 12th item in the list.

## Searching for an Item: `find`

Searches for a list item by name.

Format: `find <keyword>`
* `keyword` can contain whitespace.
* Find is case-insensitive: `Lunch`, `lunch` and `LUNCH` are treated as identical.
* Find only considers the item's name.
* Items whose names contain the keyword are returned.

> The index of items in the returned list do not correspond to their indices under `list`.

Examples:
* `find Email` returns all items with `email` in the title, including instances of `Email`, `EMAIL`, etc.
* `find et ti` returns all items with `et ti` in the title, such as `set timer` or `get timetable`

## Sorting the List: `sort`

Sets the sorting method for the list.

Format `sort <method>`
* Immediately sorts the list by the specified method. Note that this also changes their list indices.
* `method` must be one of the following: `none`, `datetime`, `completion`
  * Sorting by `none` does not change the order of the list. Any additions are added to the end of the list.
  * Sorting by `datetime` sorts the list by ascending order of deadline due date and time / event start date and time. 
    to-dos do not have a date and time, so they are placed at the top. 
      * Any additions are added to their sorted position in the list.
  * Sorting by `completion` places all uncompleted tasks at the top of the list, followed by completed tasks.
    * Ties are broken by ascending alphabetical order.
    * Any additions are added to their sorted position in the list.
* `method` is case-insensitive: `datetime`, `Datetime` and `DATETIME` are treated as identical.

Examples:
* `sort datetime` sorts the list by due date and time (for deadlines) and start date and time (for events).
* `sort completion` sorts the list by completion status.

## Exiting Orangutan: `bye`

Saves the list to disk and terminates Orangutan.

Format: `bye`
* Upon receiving this command, Orangutan will cease to take in any input.
* The list will be saved in /data/orangutan.txt, under the same folder orangutan.jar is located.
  * This list will be loaded into Orangutan the next time it is opened, so you can start from where you left off.
> You can edit the save file using a text editor, as long as each line is of the correct format.

## Command Summary

| Action          | Format, Examples                                                                                                            |
|-----------------|-----------------------------------------------------------------------------------------------------------------------------|
| Add to-do       | `todo <name>`<br/> e.g. `todo eat lunch`                                                                                    |
| Add deadline    | `deadline <name> \by <due date, time>`<br/> e.g. `deadline report /by 20260831 2359`                                        |
| Add event       | `event <name> /from <start date, time> /to <end date, time>`<br/>e.g. `event meeting /from 20260901 1000 /to 20260901 1200` |
| List all items  | `list`                                                                                                                      |
| Mark item       | `mark <index>`<br/>e.g. `mark 2`                                                                                            |
| Unmark item     | `unmark <index>`<br/>e.g. `unmark 5`                                                                                        |
| Delete item     | `delete <index>`<br/>e.g. `delete 1`                                                                                        |
| Search for item | `find <keyword>`<br/>e.g. `find email`                                                                                      |
| sort list       | `sort <method>`<br/>e.g. `sort datetime`, `sort completion`, `sort none`                                                    |
| exit            | `bye`                                                                                                                       |

## Acknowledgements
* Media Assets:
  * App Icon: www.flaticon.com/free-icon/orangutan_7743169
  * User Profile Picture: www.pexels.com/photo/portrait-of-man-in-suit-10041264/
  * Orangutan Profile Picture: www.orangutans-sos.org/content/uploads/2025/04/Three-Faces-of-the-Forest.jpg
  * Chat Window Background: www.facebook.com/photo?fbid=450518333786018
  * Command Line ASCII Banner: adapted from www.ascii.co.uk/art/orangutan 
* AI Use: 
  * Google Gemini was used to provide the syntax of important java and FXML functions.
  * DeepSeek and ChatGPT Codex were used to:
    * Check code for style violations
    * Give advice on how classes should be structured for good OOP
    * Troubleshooting, especially for visual bugs in FXML.
  * In addition, Codex was used to assist in supplementing more unit tests.
* Course Materials:
  * All FXML markup, stylesheets, and code are adapted from www.se-education.org/guides/tutorials/javaFx.html.


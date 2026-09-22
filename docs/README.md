
# Echo user guide

Echo is a todo list chatbot that responds to various specific commands. It can add 3 different types
of tasks, delete tasks, mark tasks and so on. Echo stores all tasks in the todo list in a text file.

## How to run

Open a terminal and navigate to the file directory where the jar file is stored.

Run: ```java -jar [name of jar file]```

To help avoid any issues use Java 25.

## Echo commands and usages

1. ```todo```
    - Adds a todo task that only contains a description.
    - Example usage: ```todo Do the dishes```
2. ```deadline```
    - Adds a deadline task that contains a description and a deadline date, and optionally a deadline time.
    - Example usage: ```deadline Do homework /2023-12-23```
    - Example usage 2: ```deadline Do homework /2023-12-23 /1200```
3. ```event```
    - Adds an event task that contains a description, start date and end date, and optionally the times
      associated with the dates.
    - Example usage: ```event Holiday /2023-10-21 /2023-10-23 /1200 /1400```
    - The first date is the start date and similarly the first time is the start time.
    - The second date would be the end date, and the second time is the end time.
4. ```list```
    - Causes Echo to output a list of all the tasks currently stored in the todo list.
    - Everything after list is ignored by Echo.
    - Example usage: ```list```
5. ```delete```
    - Deletes a task stored by the todo list, at the position specified by the integer argument provided.
    - Example usage: ```delete 2``` - deletes the task at position 2 of the list.
    - Position provided must be a positive integer.
6. ```mark``` and ```unmark```
    - Marks/unmarks a task stored by the todo list, at the position specified by the integer argument provided.
    - Example usage: ```mark 2``` - marks the task at position 2 of the list.
    - Position provided must be a positive integer.
7. ```find```
    - Finds the tasks stored by the todo list containing the word/string specified by the argument provided.
    - Example usage: ```find work``` - finds all the tasks in the list that contain 'work', such as 'Do work',
      'Work on project' etc.
8. ```undo```
    - Reverses the previous command that made a change to the list. So ```todo Work``` would be undone but not
      ```mark 2``` if the task at position 2 of the list was already marked.
    - The program can only reverse the previous 2 actions.
    - Example usage: ```undo```
9. ```bye```
    - Causes Echo to display a farewell message and exits the program.
    - Example usage: ```bye```

## Other notes

- Do not edit the text file used to store the tasks as it has to be in a specific format for the program to work.
- If any accidental changes are made simply remove the changes, or edit them to follow the storage format.
  
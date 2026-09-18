# Geek Manual Test Plan

## C-Sort

Start Geek with an empty data file, then enter the commands below in order.

1. Add tasks in a deliberately unsorted order:

   ```text
   todo read book
   deadline submit report /by 3/12/2019
   event morning meeting /from 2/12/2019 0900 /to 2/12/2019 1000
   deadline return book /by 2/12/2019
   todo write notes
   ```

2. Enter `sort`.

   Expected: `return book`, `morning meeting`, and `submit report` are shown
   in chronological order, followed by `read book` and `write notes` in their
   original relative order.

3. Exit Geek, restart it, and enter `list`.

   Expected: the order produced in step 2 is preserved.

4. Enter `sort date`.

   Expected: Geek reports that the `sort` command does not accept arguments,
   and the task list is unchanged.

## Error handling

1. Enter a valid command with extra whitespace, such as `  list  `.

   Expected: Geek accepts the command and displays the task list.

2. Enter `list all`.

   Expected: Geek explains that `list` does not accept arguments.

3. Enter `deadline report /by 2/12/2026 /by 3/12/2026`.

   Expected: Geek explains that `/by` can be used only once.

4. Enter `event meeting /from 2/12/2026 1800 /to 2/12/2026 1700`.

   Expected: Geek explains that the event must end after it starts.

5. Enter `deadline report /by 30/2/2026`.

   Expected: Geek displays guidance for supported date and time formats.

## Graphical interface

1. Start Geek using `./gradlew run`, then enter `list` and `list all`.

   Expected: The valid `list` response uses the normal blue dialog style. The
   invalid `list all` response uses the red alert dialog style.

2. Resize the application window narrower, wider, shorter, and taller.

   Expected: The header, conversation, input field, and send button remain
   usable. Long messages wrap inside their dialogs, and the conversation can
   be scrolled vertically.

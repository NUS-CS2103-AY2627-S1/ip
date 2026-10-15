package walle.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import walle.exceptions.WALLEException;
import walle.note.Note;
import walle.task.Task;

/**
 * Handles reading user input, and formats WALLE's greetings, updates, and
 * error messages for display. Formatting is kept separate from delivery
 * (printing to a console, or showing in a GUI dialog box) so both the CLI
 * and the JavaFX GUI can share the exact same message text.
 */
public class Ui {

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Reads the next line of user input.
     *
     * @return the raw command string entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Closes the input scanner. Should be called once, when WALLE shuts down.
     */
    public void closeScanner() {
        scanner.close();
    }

    /**
     * Formats WALLE's greeting when the program starts.
     *
     * @return the greeting message.
     */
    public String formatGreeting() {
        return "\n"
                + "Hey! I'm WALLE.\n"
                + "What can I do for you?\n"
                + "———————————————————————\n";
    }

    /**
     * Formats WALLE's farewell message when the user exits.
     *
     * @return the farewell message.
     */
    public String formatFarewell() {
        return "\n  Bye bye! See you next time, I hope!\n";
    }

    /**
     * Formats an error message for a generic (non-WALLEException) exception.
     *
     * @param e the exception that was thrown.
     * @return the formatted error message.
     */
    public String formatErrorMessage(Exception e) {
        return "\n    ERROR: " + e.getMessage() + " :(\n";
    }

    /**
     * Formats an error message for a WALLEException, whose message is already
     * formatted for direct display to the user.
     *
     * @param e the WALLEException that was thrown.
     * @return the formatted error message.
     */
    public String formatErrorMessage(WALLEException e) {
        return e.getMessage();
    }

    /**
     * Formats a warning that some lines in a save file were skipped because
     * their content wasn't as expected.
     *
     * @param skippedLines the number of lines that were skipped.
     * @return the formatted warning message.
     */
    public String formatSkippedLinesWarning(int skippedLines) {
        return "\n   WARNING :( " + skippedLines + " saved line(s) looked corrupted and were skipped.\n";
    }

    /**
     * Formats a confirmation after a task has been deleted.
     *
     * @param deletedTask the task that was removed.
     * @param tasksLeft   the number of tasks remaining in the list.
     * @return the formatted confirmation message.
     */
    public String formatTaskDeletionUpdate(Task deletedTask, int tasksLeft) {
        return formatItemUpdate("Got it! I've removed the task", deletedTask)
                + "    " + tasksLeft + " task(s) left, let's go :D\n";
    }

    /**
     * Formats a confirmation after a task has been added.
     *
     * @param newTask  the task that was added.
     * @param numTasks the total number of tasks now in the list.
     * @return the formatted confirmation message.
     */
    public String formatTaskAdditionUpdate(Task newTask, int numTasks) {
        return formatItemUpdate("Got it! I've added the task", newTask)
                + "    Now you have " + numTasks + " task(s) on your list!\n";
    }

    /**
     * Formats every task currently in the given list, numbered from 1.
     *
     * @param taskList the list of tasks to display.
     * @return the formatted task list.
     */
    public String formatTaskList(ArrayList<Task> taskList) {
        return formatNumberedItems("Here are the tasks in your list:", taskList);
    }

    /**
     * Formats the tasks that matched a {@code find} search, numbered from 1.
     *
     * @param matchingTasks the matching tasks to display.
     * @return the formatted list of matching tasks.
     */
    public String formatMatchingTasks(ArrayList<Task> matchingTasks) {
        return formatNumberedItems("Here are the matching tasks in your list:", matchingTasks);
    }

    /**
     * Formats a confirmation after a task has been marked as done.
     *
     * @param markedTask the task that was marked.
     * @return the formatted confirmation message.
     */
    public String formatTaskMarkedUpdate(Task markedTask) {
        return formatItemUpdate("Nice! I've marked this task as done", markedTask);
    }

    /**
     * Formats a confirmation after a task has been unmarked as done.
     *
     * @param unmarkedTask the task that was unmarked.
     * @return the formatted confirmation message.
     */
    public String formatTaskUnmarkedUpdate(Task unmarkedTask) {
        return formatItemUpdate("OK, I've marked this task as not done yet", unmarkedTask);
    }

    /**
     * Formats a confirmation after a note has been added.
     *
     * @param newNote  the note that was added.
     * @param numNotes the total number of notes now recorded.
     * @return the formatted confirmation message.
     */
    public String formatNoteAdditionUpdate(Note newNote, int numNotes) {
        return formatItemUpdate("Got it! I've added the note", newNote)
                + "    Now you have " + numNotes + " note(s) recorded!\n";
    }

    /**
     * Formats a confirmation after a note has been deleted.
     *
     * @param deletedNote the note that was removed.
     * @param notesLeft   the number of notes remaining.
     * @return the formatted confirmation message.
     */
    public String formatNoteDeletionUpdate(Note deletedNote, int notesLeft) {
        return formatItemUpdate("Got it! I've removed the note", deletedNote)
                + "    " + notesLeft + " note(s) left\n";
    }

    /**
     * Formats every note currently recorded, numbered from 1.
     *
     * @param noteList the list of notes to display.
     * @return the formatted note list.
     */
    public String formatNoteList(ArrayList<Note> noteList) {
        return formatNumberedItems("Here are your notes:", noteList);
    }

    // Format a header followed by the given items, numbered from 1
    private String formatNumberedItems(String header, List<?> items) {
        StringBuilder message = new StringBuilder("\n  ").append(header);
        for (int pos = 0; pos < items.size(); pos++) {
            message.append("\n   ")
                    .append(pos + 1)
                    .append(".")
                    .append(items.get(pos));
        }
        message.append("\n");
        return message.toString();
    }

    // Format a header, colon, and the given item on the following indented line
    private String formatItemUpdate(String header, Object item) {
        return "\n    " + header + ":\n        " + item + "\n";
    }

}

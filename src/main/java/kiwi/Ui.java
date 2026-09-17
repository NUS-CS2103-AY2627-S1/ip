package kiwi;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import kiwi.task.FreeSlot;
import kiwi.task.KiwiDate;
import kiwi.task.Task;
import kiwi.task.TaskList;

/**
 * Builds user-facing messages and, for the text UI, writes them to the console.
 * The GUI reads the same messages via {@link #consumeResponse()} without printing.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private static final String BANNER = " _  ___          _ \n"
            + "| |/ (_)_      _(_)\n"
            + "| ' /| \\ \\ /\\ / / |\n"
            + "| . \\| |\\ V  V /| |\n"
            + "|_|\\_\\_| \\_/\\_/ |_|\n";

    private final Scanner in;
    private final StringBuilder response = new StringBuilder();

    /** Creates a UI that reads from standard input. */
    public Ui() {
        this.in = new Scanner(System.in);
    }

    /**
     * Records each of {@code messages} for {@link #consumeResponse()} or {@link #printResponse()}.
     *
     * @param messages one or more lines of chatbot output.
     */
    private void show(String... messages) {
        for (String message : messages) {
            if (response.length() > 0) {
                response.append('\n');
            }
            response.append(message);
        }
    }

    /**
     * Returns messages recorded since the last consume, then clears the buffer.
     * Used by the GUI so a dialog box can display the reply without printing to the terminal.
     *
     * @return the accumulated reply, or an empty string if nothing was shown.
     */
    public String consumeResponse() {
        String result = response.toString();
        response.setLength(0);
        return result;
    }

    /**
     * Writes the buffered reply to standard output, then clears the buffer.
     * Used by the text UI loop; the GUI uses {@link #consumeResponse()} instead.
     */
    public void printResponse() {
        String reply = consumeResponse();
        if (!reply.isEmpty()) {
            System.out.println(reply);
        }
    }

    /**
     * Prints save-file warnings before the welcome banner (text UI only).
     * The GUI shows the same warnings inside the opening chat bubble.
     *
     * @param warnings load warnings from {@link Storage#getLoadWarnings()}.
     */
    public void showLoadWarnings(List<String> warnings) {
        assert warnings != null : "Storage.getLoadWarnings() always returns a list";
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }

    /** Prints the horizontal divider used between chatbot messages. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Shows the banner and welcome message. */
    public void showWelcome() {
        showLine();
        System.out.print(BANNER);
        System.out.println(getGuiWelcome());
        showLine();
    }

    /**
     * Returns the welcome text used by the GUI (no ASCII banner).
     *
     * @return a short greeting.
     */
    public String getGuiWelcome() {
        return "Hello! I'm Kiwi.\nWhat can I do for you?";
    }

    /**
     * Shows the goodbye message only (no divider lines).
     * The main loop prints the dividers around each command, including exit.
     */
    public void showGoodbye() {
        show("Bye. Hope to see you again soon!");
    }

    /**
     * Reads the next full line of user input.
     * End of input is treated as {@code bye} so the session can close cleanly.
     *
     * @return the command line typed by the user.
     */
    public String readCommand() {
        if (!in.hasNextLine()) {
            return "bye";
        }
        return in.nextLine();
    }

    /**
     * Shows an error message (typically from a {@link KiwiException}).
     *
     * @param message text to display.
     */
    public void showError(String message) {
        show(message);
    }

    /**
     * Confirms that a task was added and reports the new list size.
     *
     * @param task added task.
     * @param size number of tasks after the add.
     */
    public void showTaskAdded(Task task, int size) {
        show("Got it. I've added this task:",
                "  " + task,
                formatTaskCount(size));
    }

    /**
     * Prints all tasks with 1-based numbering.
     *
     * @param tasks current task list.
     */
    public void showTaskList(TaskList tasks) {
        show("Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Prints tasks whose descriptions matched a {@code find} keyword, numbered from 1.
     * Prints {@code None found.} when the filtered list is empty.
     *
     * @param matches tasks whose descriptions contain the search keyword
     */
    public void showMatchingTasks(TaskList matches) {
        show("Here are the matching tasks in your list:");
        showNumberedTasks(matches);
        if (matches.size() == 0) {
            show("None found.");
        }
    }

    /**
     * Prints deadlines/events that fall on {@code date}, or "None found."
     * Uses each task's original 1-based index in the full list.
     *
     * @param date date being queried.
     * @param tasks full task list.
     */
    public void showTasksOn(LocalDate date, TaskList tasks) {
        show("Here are the deadlines/events on " + KiwiDate.format(date) + ":");
        int shown = 0;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                showNumberedTask(i, task);
                shown++;
            }
        }
        if (shown == 0) {
            show("None found.");
        }
    }

    /**
     * Shows the nearest free slot of {@code hours} hours.
     *
     * @param hours length of the requested slot.
     * @param slot the matching free period.
     */
    public void showFreeSlot(int hours, FreeSlot slot) {
        show("The nearest " + hours + "-hour free slot is on " + slot.toDisplayString() + ".");
    }

    /**
     * Shows that no free slot of {@code hours} hours was found in the search window.
     *
     * @param hours length of the requested slot.
     */
    public void showNoFreeSlot(int hours) {
        show("Couldn't find a " + hours + "-hour free slot in the next "
                + FreeSlot.SEARCH_DAYS + " days.");
    }

    /**
     * Confirms a task was marked done.
     *
     * @param displayNumber 1-based task number shown to the user.
     * @param task the marked task.
     */
    public void showMarked(int displayNumber, Task task) {
        show("Marked this task as done:",
                displayNumber + "." + task);
    }

    /**
     * Confirms a task was marked not done.
     *
     * @param displayNumber 1-based task number shown to the user.
     * @param task the unmarked task.
     */
    public void showUnmarked(int displayNumber, Task task) {
        show("Marked this task as not done yet:",
                displayNumber + "." + task);
    }

    /**
     * Confirms a task was removed and reports the new list size.
     *
     * @param removed the removed task.
     * @param size number of tasks after the delete.
     */
    public void showTaskDeleted(Task removed, int size) {
        show("Noted. I've removed this task:",
                "  " + removed,
                formatTaskCount(size));
    }

    private void showNumberedTasks(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            showNumberedTask(i, tasks.get(i));
        }
    }

    private void showNumberedTask(int zeroBasedIndex, Task task) {
        show((zeroBasedIndex + 1) + "." + task);
    }

    private String formatTaskCount(int size) {
        String taskWord = (size == 1) ? "task" : "tasks";
        return "Now you have " + size + " " + taskWord + " in the list.";
    }
}

package slotbot;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import slotbot.task.Deadline;
import slotbot.task.Task;
import slotbot.task.TaskList;

/**
 * Formats responses for the console or graphical interface.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";

    private final Scanner scanner;
    private final Consumer<String> output;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::print;
    }

    /**
     * Creates a response formatter that sends text to the supplied destination.
     *
     * @param output Destination for formatted responses.
     */
    public Ui(Consumer<String> output) {
        scanner = new Scanner("");
        this.output = output;
    }

    /**
     * Returns whether another command is available to read.
     *
     * @return True if another command is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command entered by the user.
     *
     * @return Next user command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays each supplied line in order, followed by the system line separator.
     * Supplying no lines produces no output; an empty string produces a blank line.
     *
     * @param lines Lines to display.
     */
    public void showLines(String... lines) {
        for (String line : lines) {
            output.accept(line + System.lineSeparator());
        }
    }

    /** Displays the welcome message. */
    public void showWelcome() {
        output.accept("""
                Hello! I'm SlotBot.
                Let's keep your time and tasks in order.
                ____________________________________________________________
                """);
    }

    /** Displays the goodbye message. */
    public void showGoodbye() {
        output.accept("""
                %s
                All done. See you next time!
                %s
                """.formatted(SEPARATOR, SEPARATOR));
    }

    /**
     * Displays a task whose completion status changed.
     *
     * @param task Task whose status changed.
     * @param isMarked True if the task was marked, or false if it was unmarked.
     */
    public void showTaskStatusChanged(Task task, boolean isMarked) {
        String message = isMarked
                ? "Nice! We got one."
                : "OK, I've marked this task as not done yet:";

        output.accept("""
                %s
                %s
                  %s
                %s

                """.formatted(SEPARATOR, message, task, SEPARATOR));
    }

    /**
     * Displays a deleted task and the new task count.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks remaining.
     */
    public void showDeletedTask(Task task, int taskCount) {
        output.accept("""
                %s
                Noted. I've removed this task:
                  %s
                Now you have %d tasks in the list.
                %s

                """.formatted(SEPARATOR, task, taskCount, SEPARATOR));
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        output.accept("""
                %s
                Here are the tasks in your list:
                """.formatted(SEPARATOR));

        for (int i = 0; i < tasks.size(); i++) {
            showLines((i + 1) + ". " + tasks.get(i));
        }

        output.accept("""
                %s

                """.formatted(SEPARATOR));
    }

    /**
     * Displays matching tasks with their indexes from the complete task list.
     *
     * @param tasks Complete task list.
     * @param matchingTaskIndexes Zero-based indexes of matching tasks.
     * @param keyword Keyword used to find the tasks.
     */
    public void showMatchingTasks(TaskList tasks, List<Integer> matchingTaskIndexes,
            String keyword) {
        showLines(SEPARATOR);

        if (matchingTaskIndexes.isEmpty()) {
            showLines("No tasks match \"" + keyword + "\".");
        } else {
            showLines("Here are the matching tasks in your list:");
            for (int taskIndex : matchingTaskIndexes) {
                showLines((taskIndex + 1) + ". " + tasks.get(taskIndex));
            }
        }

        output.accept("""
                %s

                """.formatted(SEPARATOR));
    }

    /**
     * Displays unfinished deadlines within the reminder window.
     *
     * @param reminders Deadlines to display in due-date order.
     * @param daysAhead Number of days after today included in the window.
     */
    public void showReminders(List<Deadline> reminders, int daysAhead) {
        output.accept("""
                %s
                Upcoming reminders (through the next %d days):
                """.formatted(SEPARATOR, daysAhead));

        if (reminders.isEmpty()) {
            showLines("No upcoming deadlines.");
        } else {
            for (int i = 0; i < reminders.size(); i++) {
                showLines((i + 1) + ". " + reminders.get(i));
            }
        }

        output.accept("""
                %s

                """.formatted(SEPARATOR));
    }

    /**
     * Displays an added task and the new task count.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after adding the task.
     */
    public void showAddedTask(Task task, int taskCount) {
        output.accept("""
                %s
                Got it. I've added this task:
                  %s
                Now you have %d tasks in the list.
                %s

                """.formatted(SEPARATOR, task, taskCount, SEPARATOR));
    }

    /**
     * Displays a user-facing command error.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        output.accept("""
                %s
                %s
                %s

                """.formatted(SEPARATOR, message, SEPARATOR));
    }

    /** Displays a warning that tasks could not be saved. */
    public void showSaveError() {
        showLines("Warning: Unable to save tasks to disk.");
    }

    /** Displays warnings that saved tasks could not be loaded. */
    public void showLoadError() {
        showLines("Warning: Unable to load saved tasks.", "Starting with an empty list.");
    }

    /**
     * Displays a warning about an invalid line in the task data file.
     *
     * @param lineNumber One-based line number containing invalid data.
     */
    public void showInvalidTaskDataWarning(int lineNumber) {
        showLines("Warning: Ignoring invalid task data on line " + lineNumber + ".");
    }
}

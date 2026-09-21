package peter.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

import peter.task.ScheduleDateTime;
import peter.task.Task;
import peter.task.TaskList;

/**
 * Handles the application's basic input and output.
 *
 * <p>Output goes to an injected stream rather than directly to
 * {@code System.out}, so a caller that needs the text of a response can
 * supply a buffer instead of the console.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = " ____      _\n"
            + "|  _ \\ ___| |_ ___ _ __\n"
            + "| |_) / _ \\ __/ _ \\ '__|\n"
            + "|  __/  __/ ||  __/ |\n"
            + "|_|   \\___|\\__\\___|_|\n";

    private final PrintStream out;

    /** Created on first use, since a UI that only writes never reads a command. */
    private Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input and writes to
     * standard output.
     */
    public Ui() {
        this(System.out);
    }

    /**
     * Creates a UI that writes to the given stream.
     *
     * @param out stream to write all output to.
     */
    public Ui(PrintStream out) {
        this.out = out;
    }

    /**
     * Displays the application's full console welcome, banner included.
     */
    public void showWelcome() {
        showDivider();
        out.print(BANNER);
        showGreeting();
        showDivider();
    }

    /**
     * Displays the greeting on its own, without the banner or dividers that
     * only suit a console.
     */
    public void showGreeting() {
        showLines("Hey! I'm Peter, your friendly neighborhood task buddy.",
                "What's on the web today?");
    }

    /**
     * Returns whether another command is available from the user.
     */
    public boolean hasNextCommand() {
        return getScanner().hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     */
    public String readCommand() {
        return getScanner().nextLine();
    }

    /** Returns the scanner over standard input, creating it if needed. */
    private Scanner getScanner() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }

    /**
     * Displays the application's farewell message.
     */
    public void showGoodbye() {
        out.println("Swing by again soon. Your task web will be right here!");
    }

    /**
     * Displays an error message without exposing implementation details.
     */
    public void showError(String message) {
        out.println(message);
    }

    /**
     * Displays the divider used to separate console interactions.
     */
    public void showDivider() {
        out.println(DIVIDER);
    }

    /**
     * Displays each of the given lines in order, one line per row.
     *
     * <p>Var-args keeps a multi-line message readable as a single call at the
     * call site, instead of a run of separate print statements.
     */
    private void showLines(String... lines) {
        for (String line : lines) {
            out.println(line);
        }
    }

    /**
     * Displays every task with its one-based list number.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            out.println("Your task web is empty. Add a task to get started!");
            return;
        }

        out.println("Here's what's caught in your task web:");
        for (int i = 0; i < tasks.size(); i++) {
            showNumberedTask(i, tasks.get(i));
        }
    }

    /**
     * Confirms that a task was added and reports the new task count.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     */
    public void showAddedTask(Task task, int taskCount) {
        showLines("Thwip! This task is on the web:",
                "  " + formatTask(task));
        showTaskCount(taskCount);
    }

    /**
     * Confirms that a task was removed and reports the new task count.
     *
     * @param task task that was removed.
     * @param taskCount number of tasks after the removal.
     */
    public void showRemovedTask(Task task, int taskCount) {
        showLines("Snip! This task is off the web:",
                "  " + formatTask(task));
        showTaskCount(taskCount);
    }

    /**
     * Confirms a change to a task's completion status.
     *
     * @param task task whose status changed.
     * @param isDone new completion status.
     */
    public void showTaskStatusChange(Task task, boolean isDone) {
        if (isDone) {
            out.println("Amazing! This task is wrapped up:");
        } else {
            out.println("Back on the web! This task is active again:");
        }
        out.println("  [" + task.getStatusIcon() + "] " + task.getDescription());
    }

    /**
     * Displays the scheduled tasks occurring on a date, keeping their original
     * task numbers so subsequent task commands can refer to them directly.
     *
     * @param tasks tasks to search.
     * @param date date to report on.
     */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        String dateText = ScheduleDateTime.format(date);
        showFilteredTasks(tasks, task -> task.occursOn(date),
                "Tasks swinging in on " + dateText + ":",
                "No tasks are swinging in on " + dateText + ".");
    }

    /**
     * Displays the tasks a keyword search selects, keeping their original task
     * numbers so subsequent task commands can refer to them directly.
     *
     * <p>The caller decides what counts as a match, so this method is
     * responsible only for the output.
     *
     * @param tasks tasks to search.
     * @param isMatch test deciding whether a task matches the search.
     */
    public void showMatchingTasks(TaskList tasks, Predicate<Task> isMatch) {
        showFilteredTasks(tasks, isMatch,
                "My spider-sense found these matches:",
                "My spider-sense couldn't find a match.");
    }

    /**
     * Displays the tasks a search selects, keeping their original task numbers
     * so subsequent task commands can refer to them directly.
     *
     * <p>A search that selects nothing shows {@code emptyMessage} instead of
     * the heading. Both the {@code on} and {@code find} searches display their
     * results this way, and differ only in the three values passed here.
     *
     * <p>The task list decides which tasks match, leaving this method
     * responsible only for the output.
     *
     * @param tasks tasks to search.
     * @param isMatch test deciding whether a task is part of the result.
     * @param heading line introducing the matches.
     * @param emptyMessage line shown instead when nothing matches.
     */
    private void showFilteredTasks(TaskList tasks, Predicate<Task> isMatch,
            String heading, String emptyMessage) {
        List<Integer> matchingIndexes = tasks.findMatchingIndexes(isMatch);
        if (matchingIndexes.isEmpty()) {
            out.println(emptyMessage);
            return;
        }

        out.println(heading);
        matchingIndexes.forEach(index -> showNumberedTask(index, tasks.get(index)));
    }

    /** Displays a task prefixed by its one-based list number. */
    private void showNumberedTask(int index, Task task) {
        // The displayed number is the index plus one, so a negative index
        // would print a task number the user could not then type back.
        assert index >= 0 : "a displayed task index must not be negative";
        out.println((index + 1) + "." + formatTask(task));
    }

    /** Reports how many tasks the list now holds. */
    private void showTaskCount(int taskCount) {
        String taskLabel = taskCount == 1 ? "task" : "tasks";
        out.println("You've got " + taskCount + " " + taskLabel + " on the web.");
    }

    /**
     * Renders a task as its type icon, status icon, description, and schedule,
     * which is the form used wherever a full task is displayed.
     */
    private String formatTask(Task task) {
        return "[" + task.getTaskTypeIcon() + "][" + task.getStatusIcon() + "] "
                + task.getDescription() + task.getScheduleDetails();
    }
}

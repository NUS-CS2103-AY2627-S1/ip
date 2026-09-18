package daddy.ui;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Consumer;

import daddy.task.Task;
import daddy.task.TaskList;

/**
 * Handles all console output shown to a Daddy user.
 */
public class Ui {
    /** Indents every line shown in Daddy's console interface. */
    private static final String INDENT = "    ";
    /** Separates distinct messages in Daddy's console interface. */
    private static final String DIVIDER = "____________________________________________________________";
    /** Formats the beginning of a free slot in a stable, friendly form. */
    private static final DateTimeFormatter FREE_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "EEE, d MMM yyyy, HH:mm", Locale.ENGLISH);
    /** Reads commands entered through standard input. */
    private final Scanner scanner;
    /** Receives each complete line that Daddy displays. */
    private final Consumer<String> output;
    /** Determines whether console-only decorations and indentation should be displayed. */
    private final boolean usesConsoleFormatting;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.output = System.out::println;
        this.usesConsoleFormatting = true;
    }

    /**
     * Creates an interface that sends displayed lines to a supplied output receiver.
     *
     * @param output the receiver for each complete displayed line
     */
    public Ui(Consumer<String> output) {
        this.scanner = null;
        this.output = output;
        this.usesConsoleFormatting = false;
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the complete command line
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("this UI does not read console commands");
        }
        return scanner.nextLine();
    }

    /** Releases the input stream used by this interface. */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /** Displays Daddy's greeting banner. */
    public void showGreeting() {
        String banner = " ____                      _       _       \n"
                + "|  _ \\   __ _   __| |   __| |  _   _ \n"
                + "| | | | / _\u0060 | / _\u0060 |  / _\u0060 | | | | |\n"
                + "| | | || (_| || (_| | | (_| | | |_| |\n"
                + "| |_| | \\__,_| \\__,_|  \\__,_|  \\__, |\n"
                + "|____/                          |___/ \n";
        if (usesConsoleFormatting) {
            printDivider();
            printIndentedBanner(banner);
        }
        printLine(INDENT + "Hello, my friend.");
        printLine(INDENT + "Big dreams require big actions. What are we getting done today?");
        printDivider();
    }

    /**
     * Displays every task in its numbered list order.
     *
     * @param tasks the tasks to display
     */
    public void showTaskList(TaskList tasks) {
        printDivider();
        printLine(INDENT + " Here's the game plan, my friend:");
        showNumberedTasks(tasks);
        printDivider();
    }

    /**
     * Displays tasks that occur on the supplied date.
     *
     * @param date the date used to filter tasks
     * @param tasks the tasks that occur on the date
     */
    public void showTasksOnDate(LocalDate date, List<Task> tasks) {
        printDivider();
        printLine(INDENT + " Here's the programme for " + date + ":");
        if (showNumberedTasks(tasks) == 0) {
            printLine(INDENT + " That day is clear. Use it wisely.");
        }
        printDivider();
    }

    /**
     * Displays the tasks whose descriptions match a user-provided keyword.
     *
     * @param tasks the tasks that match the keyword
     */
    public void showMatchingTasks(List<Task> tasks) {
        printDivider();
        printLine(INDENT + " Here are your matching tasks, my friend:");
        if (showNumberedTasks(tasks) == 0) {
            printLine(INDENT + " No match, my friend. Try another keyword.");
        }
        printDivider();
    }

    /**
     * Displays the earliest free slot that can contain a requested duration.
     *
     * @param start the beginning of the free slot
     * @param duration the duration requested by the user
     */
    public void showFreeTime(LocalDateTime start, Duration duration) {
        LocalDateTime end = start.plus(duration);
        printDivider();
        printLine(INDENT + " Daddy found you a recovery window:");
        printLine(INDENT + "   " + start.format(FREE_TIME_FORMAT) + "\u2013" + end.toLocalTime());
        printLine(INDENT + " That's a " + formatDuration(duration)
                + " slot. Protect it, then get back to the mission.");
        printDivider();
    }

    /**
     * Formats a duration as an adjectival hour or minute amount.
     *
     * @param duration the duration to describe
     * @return a label such as {@code 4-hour} or {@code 90-minute}
     */
    private String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        if (minutes % 60 == 0) {
            return minutes / 60 + "-hour";
        }
        return minutes + "-minute";
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks after adding
     */
    public void showTaskAdded(Task task, int taskCount) {
        printDivider();
        printLine(INDENT + " Good. The plan just got stronger:");
        printLine(INDENT + "   " + task.getTypeIcon() + "["
                + task.getStatusIcon() + "] " + task.getDisplayDescription());
        printLine(INDENT + " That's " + taskCount + " " + formatTaskCount(taskCount)
                + " in the list. Small steps, real progress.");
        printDivider();
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task the task that was marked
     */
    public void showTaskMarked(Task task) {
        printDivider();
        printLine(INDENT + " That's what I like to see, my friend. Task crushed:");
        printLine(INDENT + "   [X] " + task.getDisplayDescription());
        printDivider();
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task the task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        printDivider();
        printLine(INDENT + " Honesty first. This task is back in the programme:");
        printLine(INDENT + "   [ ] " + task.getDisplayDescription());
        printDivider();
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task the task that was deleted
     * @param taskCount the number of tasks after deletion
     */
    public void showTaskDeleted(Task task, int taskCount) {
        printDivider();
        printLine(INDENT + " Clean plan, clear mind. I've removed this task:");
        printLine(INDENT + "   " + task.getTypeIcon() + "[" + task.getStatusIcon()
                + "] " + task.getDisplayDescription());
        String remainingVerb = taskCount == 1 ? "remains" : "remain";
        printLine(INDENT + " " + taskCount + " " + formatTaskCount(taskCount)
                + " " + remainingVerb + ". Focus on what matters.");
        printDivider();
    }

    /**
     * Displays a user-friendly error message.
     *
     * @param message the error explanation and correction
     */
    public void showError(String message) {
        printDivider();
        printLine(INDENT + " " + message);
        printDivider();
    }

    /** Displays Daddy's farewell message. */
    public void showExit() {
        printLine(INDENT + "Go make Daddy proud, my friend. See you soon.");
        printDivider();
    }

    /**
     * Returns the correctly pluralized word used to describe a task count.
     *
     * @param taskCount the number of tasks being described
     * @return {@code task} for one task, or {@code tasks} otherwise
     */
    private String formatTaskCount(int taskCount) {
        return taskCount == 1 ? "task" : "tasks";
    }

    /**
     * Displays tasks with consecutive one-based numbers.
     *
     * @param tasks the tasks to display
     * @return the number of tasks displayed
     */
    private int showNumberedTasks(Iterable<Task> tasks) {
        int taskNumber = 1;
        for (Task task : tasks) {
            printLine(INDENT + " " + taskNumber + "." + task.getTypeIcon()
                    + "[" + task.getStatusIcon() + "] " + task.getDisplayDescription());
            taskNumber++;
        }
        return taskNumber - 1;
    }

    /** Prints a divider line with the standard indentation. */
    private void printDivider() {
        if (usesConsoleFormatting) {
            printLine(INDENT + DIVIDER);
        }
    }

    /**
     * Prints every line in a banner with the standard indentation.
     *
     * @param banner the multi-line banner to print
     */
    private void printIndentedBanner(String banner) {
        for (String line : banner.split("\\n")) {
            printLine(INDENT + line);
        }
    }

    /**
     * Sends one complete output line through this interface's configured receiver.
     *
     * @param line the line to display
     */
    private void printLine(String line) {
        output.accept(removeConsoleIndent(line));
    }

    /**
     * Removes console-only leading indentation from a graphical interface message.
     *
     * @param line the interface message before graphical formatting is applied
     * @return the original console line or the graphical version without leading indentation
     */
    private String removeConsoleIndent(String line) {
        if (usesConsoleFormatting || !line.startsWith(INDENT)) {
            return line;
        }
        return line.substring(INDENT.length());
    }
}

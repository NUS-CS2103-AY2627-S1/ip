package yachiyo.ui;

import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import yachiyo.task.NumberedTask;
import yachiyo.task.Task;

/**
 * Handles input from and output to the user.
 */
public class Ui implements AutoCloseable {
    private static final String BANNER =
            "__   __    _      ____   _   _   _____  __   __   ___  \n"
            + "\\ \\ / /   / \\    / ___| | | | | |_   _| \\ \\ / /  / _ \\ \n"
            + " \\ V /   / _ \\  | |     | |_| |   | |    \\ V /  | | | |\n"
            + "  | |   / ___ \\ | |___  |  _  |  _| |_    | |   | |_| |\n"
            + "  |_|  /_/   \\_\\ \\____| |_| |_| |_____|   |_|    \\___/ \n";
    private static final LocalTime AFTERNOON_START = LocalTime.NOON;
    private static final LocalTime EVENING_START = LocalTime.of(18, 0);
    private static final String GREETING_SUFFIX =
            "! Yachiyo here!\nWhat shall we accomplish today?";
    private static final String EXIT_MESSAGE = "Until we meet again. Take care!~";
    private static final String BREAKER =
            "===================================================================================";
    private static final DateTimeFormatter DATE_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final Scanner scanner;
    private final PrintWriter output;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.output = new PrintWriter(System.out, true);
    }

    /**
     * Creates a user interface that writes responses to the supplied destination.
     *
     * @param writer destination for command responses.
     */
    public Ui(Writer writer) {
        this.scanner = new Scanner("");
        this.output = new PrintWriter(writer, true);
    }

    /**
     * Checks whether another line of user input is available.
     *
     * @return true if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the next input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Yachiyo's banner and greeting.
     */
    public void showIntroduction() {
        output.println(BANNER);
        output.println(getGreeting());
        output.println(BREAKER);
    }

    /**
     * Returns Yachiyo's greeting for interfaces that display it directly.
     *
     * @return introductory greeting.
     */
    public static String getGreeting() {
        return getGreeting(LocalTime.now());
    }

    /**
     * Returns Yachiyo's greeting for the specified time of day.
     *
     * @param time local time used to select the greeting.
     * @return time-appropriate introductory greeting.
     */
    static String getGreeting(LocalTime time) {
        String salutation;
        if (time.isBefore(AFTERNOON_START)) {
            salutation = "Ohayou";
        } else if (time.isBefore(EVENING_START)) {
            salutation = "Konnichiwa";
        } else {
            salutation = "Konbanwa";
        }
        return salutation + GREETING_SUFFIX;
    }

    /**
     * Displays the divider before a command response.
     */
    public void showCommandStart() {
        output.println(BREAKER);
    }

    /**
     * Displays the divider and spacing after a command response.
     */
    public void showCommandEnd() {
        output.println(BREAKER);
        output.println();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message explanation of the error.
     */
    public void showError(String message) {
        output.println(message);
    }

    /**
     * Displays all tasks with their one-based task numbers.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            output.println("Our lineup is empty for now. What shall we take on next?");
            return;
        }

        output.println("Here's everything in our lineup:");
        for (int i = 0; i < tasks.size(); i++) {
            showIndexedTask(i + 1, tasks.get(i));
        }
    }

    /**
     * Displays the heading for tasks whose descriptions match a keyword.
     *
     * @param keyword Keyword matched by the displayed tasks.
     */
    public void showMatchingTasksHeader(String keyword) {
        output.printf("Here are the tasks in our lineup matching \"%s\":%n", keyword);
    }

    /**
     * Reports that no task descriptions match a keyword.
     *
     * @param keyword Keyword searched by the user.
     */
    public void showNoMatchingTasks(String keyword) {
        output.printf("I couldn't find any tasks matching \"%s\".%n", keyword);
    }

    /**
     * Displays the heading for tasks occurring on a specified date.
     *
     * @param date date whose matching tasks will be displayed.
     */
    public void showTasksOnDateHeader(LocalDate date) {
        output.printf("Here are the deadlines and events on %s:%n",
                date.format(DATE_DISPLAY_FORMATTER));
    }

    /**
     * Reports that no deadlines or events occur on a specified date.
     *
     * @param date date checked by the user.
     */
    public void showNoTasksOnDate(LocalDate date) {
        output.printf("There are no deadlines or events on %s.%n",
                date.format(DATE_DISPLAY_FORMATTER));
    }

    /**
     * Displays one task with its number from the complete task list.
     *
     * @param taskNumber one-based number of the task.
     * @param task task to display.
     */
    public void showIndexedTask(int taskNumber, Task task) {
        output.printf("%d. %s%n", taskNumber, task);
    }

    /**
     * Displays numbered tasks using their numbers from the complete task list.
     *
     * @param numberedTasks tasks and their original task numbers.
     */
    public void showNumberedTasks(List<NumberedTask> numberedTasks) {
        for (NumberedTask numberedTask : numberedTasks) {
            showIndexedTask(numberedTask.number(), numberedTask.task());
        }
    }

    /**
     * Reports that a task was already completed.
     *
     * @param task completed task.
     */
    public void showAlreadyMarked(Task task) {
        output.println("This task is already shining as complete!");
        showTask(task);
    }

    /**
     * Confirms that a task was marked and reports how many remain.
     *
     * @param task task that was marked.
     * @param remainingCount number of incomplete tasks.
     */
    public void showTaskMarked(Task task, int remainingCount) {
        output.println("Woohoo! Another task is complete:");
        showTask(task);
        if (remainingCount == 0) {
            output.println("Yayyy! Everything in our lineup is complete!🥳🎉");
            output.println("Good job!");
        } else {
            output.printf("And with that, our lineup now has %s remaining!%n",
                    formatTaskCount(remainingCount));
        }
    }

    /**
     * Reports that a task was already incomplete.
     *
     * @param task incomplete task.
     */
    public void showAlreadyUnmarked(Task task) {
        output.println("No changes needed-this task is already waiting in our lineup!");
        showTask(task);
    }

    /**
     * Confirms that a task was unmarked and reports how many remain.
     *
     * @param task task that was unmarked.
     * @param remainingCount number of incomplete tasks.
     */
    public void showTaskUnmarked(Task task, int remainingCount) {
        output.println("Not quite finished? No worries, I've marked it as not done:");
        showTask(task);
        output.printf("Our lineup now has %s remaining!%n", formatTaskCount(remainingCount));
    }

    /**
     * Confirms that a task was added and reports the new total.
     *
     * @param task task that was added.
     * @param totalCount total number of tasks.
     */
    public void showTaskAdded(Task task, int totalCount) {
        output.println("All right, I've added this to our lineup:");
        showTask(task);
        output.printf("And with that, our lineup now has %s in total!%n",
                formatTaskCount(totalCount));
    }

    /**
     * Confirms that one detail of a task was edited.
     *
     * @param task task containing the edited detail.
     */
    public void showTaskEdited(Task task) {
        output.println("All right, I've updated this task:");
        showTask(task);
    }

    /**
     * Confirms that a task was deleted and reports the new total.
     *
     * @param task task that was deleted.
     * @param totalCount total number of remaining tasks.
     */
    public void showTaskDeleted(Task task, int totalCount) {
        output.println("All right, I've taken this task out of our lineup:");
        showTask(task);
        if (totalCount == 0) {
            output.println("And with that, our lineup is empty again. "
                    + "What shall we take on next?");
        } else {
            output.printf("And with that, our lineup now has %s in total!%n",
                    formatTaskCount(totalCount));
        }
    }

    /**
     * Displays Yachiyo's farewell.
     */
    public void showExit() {
        output.println(EXIT_MESSAGE);
    }

    private void showTask(Task task) {
        output.printf("- %s%n", task);
    }

    private static String formatTaskCount(int count) {
        return String.format("%d task%s", count, count == 1 ? "" : "s");
    }

    /**
     * Releases the input scanner when the application finishes.
     */
    @Override
    public void close() {
        scanner.close();
        output.flush();
    }
}

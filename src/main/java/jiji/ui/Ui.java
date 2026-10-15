package jiji.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jiji.parser.DateTimeUtil;
import jiji.task.Deadline;
import jiji.task.Event;
import jiji.task.Task;
import jiji.task.TaskList;

/**
 * Handles all user interactions with the Jiji chatbot, including reading user commands
 * and rendering formatted responses, banners, task notifications, and error messages.
 */
public class Ui {

    /** Standard indentation for chatbot output. */
    private static final String INDENT = "     ";

    /** Horizontal divider line matching the standard Jiji UI layout. */
    private static final String DIVIDER = "    ____________________________________________________________";

    /** ASCII art banner for Jiji. */
    private static final String BANNER =
            "         _     _          _     _ \n"
            + "        | |   (_)        (_)   (_)\n"
            + "        | |    _          _     _ \n"
            + "     _  | |   | |        | |   | |\n"
            + "    | |_| |   | |     _  | |   | |\n"
            + "     \\___/    |_|    | |_| |   |_|\n"
            + "                      \\___/       \n";

    private final Scanner scanner;

    /**
     * Constructs a new Ui instance reading from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads a single line of command input from the user.
     *
     * @return The raw command string entered by the user, or empty string if input stream ended.
     */
    public String readCommand() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "";
    }

    /**
     * Checks if there are more lines of input available.
     *
     * @return True if another line is available, false otherwise.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Prints the horizontal divider line.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays the welcome message and ASCII art banner upon startup.
     */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        for (String line : PersonalityBank.getWelcomeLines()) {
            System.out.println(INDENT + line);
        }
        showLine();
    }

    /**
     * Displays one or more message lines indented and enclosed within standard divider lines.
     *
     * @param messages Variable number of message strings to display.
     */
    public void showMessages(String... messages) {
        showLine();
        if (messages != null) {
            for (String message : messages) {
                System.out.println(INDENT + message);
            }
        }
        showLine();
    }

    /**
     * Displays the farewell message upon exit.
     */
    public void showGoodbye() {
        showMessages(formatGoodbye());
    }

    /**
     * Formats the farewell message upon exit.
     *
     * @return The formatted farewell string.
     */
    public String formatGoodbye() {
        return PersonalityBank.formatGoodbye();
    }

    /**
     * Displays an error message formatted within standard divider lines.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        showMessages(message);
    }

    /**
     * Displays a warning message when existing task data cannot be loaded.
     */
    public void showLoadingError() {
        showMessages("Warning: Could not load tasks from storage. Starting with an empty task list.");
    }

    /**
     * Displays all current tasks in the task list formatted with 1-based indexing.
     *
     * @param taskList The task list to display.
     */
    public void showTaskList(TaskList taskList) {
        showMessages(formatTaskList(taskList).split("\n"));
    }

    /**
     * Formats all current tasks in the task list with 1-based indexing.
     *
     * @param taskList The task list to format.
     * @return The formatted task list string.
     */
    public String formatTaskList(TaskList taskList) {
        String items = IntStream.range(0, taskList.size())
                .mapToObj(i -> (i + 1) + "." + taskList.get(i))
                .collect(Collectors.joining("\n"));
        return items.isEmpty()
                ? "Here are the tasks in your list:"
                : "Here are the tasks in your list:\n" + items;
    }

    /**
     * Displays all matching tasks found from a keyword search.
     *
     * @param matchingTasks The list of matching tasks to display.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showMessages(formatMatchingTasks(matchingTasks).split("\n"));
    }

    /**
     * Formats all matching tasks found from a keyword search.
     *
     * @param matchingTasks The list of matching tasks to format.
     * @return The formatted matching tasks string.
     */
    public String formatMatchingTasks(List<Task> matchingTasks) {
        String items = IntStream.range(0, matchingTasks.size())
                .mapToObj(i -> (i + 1) + "." + matchingTasks.get(i))
                .collect(Collectors.joining("\n"));
        return items.isEmpty()
                ? "Here are the matching tasks in your list:"
                : "Here are the matching tasks in your list:\n" + items;
    }

    /**
     * Displays a confirmation message after a new task has been added.
     *
     * @param task The newly added task.
     * @param totalTasks The total number of tasks currently in the list.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        showMessages(formatTaskAdded(task, totalTasks).split("\n"));
    }

    /**
     * Formats a confirmation message after a new task has been added.
     *
     * @param task The newly added task.
     * @param totalTasks The total number of tasks currently in the list.
     * @return The formatted confirmation string.
     */
    public String formatTaskAdded(Task task, int totalTasks) {
        return PersonalityBank.formatTaskAdded(task, totalTasks);
    }

    /**
     * Displays a confirmation message after a task has been deleted.
     *
     * @param task The deleted task.
     * @param totalTasks The total number of tasks remaining in the list.
     */
    public void showTaskRemoved(Task task, int totalTasks) {
        showMessages(formatTaskRemoved(task, totalTasks).split("\n"));
    }

    /**
     * Formats a confirmation message after a task has been deleted.
     *
     * @param task The deleted task.
     * @param totalTasks The total number of tasks remaining in the list.
     * @return The formatted confirmation string.
     */
    public String formatTaskRemoved(Task task, int totalTasks) {
        return PersonalityBank.formatTaskRemoved(task, totalTasks);
    }

    /**
     * Displays a confirmation message after a task has been marked as done.
     *
     * @param task The marked task.
     */
    public void showTaskMarked(Task task) {
        showMessages(formatTaskMarked(task).split("\n"));
    }

    /**
     * Formats a confirmation message after a task has been marked as done.
     *
     * @param task The marked task.
     * @return The formatted confirmation string.
     */
    public String formatTaskMarked(Task task) {
        return PersonalityBank.formatTaskMarked(task);
    }

    /**
     * Displays a confirmation message after a task has been marked as not done.
     *
     * @param task The unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        showMessages(formatTaskUnmarked(task).split("\n"));
    }

    /**
     * Formats a confirmation message after a task has been marked as not done.
     *
     * @param task The unmarked task.
     * @return The formatted confirmation string.
     */
    public String formatTaskUnmarked(Task task) {
        return PersonalityBank.formatTaskUnmarked(task);
    }

    /**
     * Displays the help guide or command details enclosed in divider lines.
     *
     * @param helpMessage The formatted help message to display.
     */
    public void showHelp(String helpMessage) {
        assert helpMessage != null : "Help message cannot be null";
        showMessages(helpMessage.split("\n"));
    }

    /**
     * Formats tasks matching a predicate filter, preserving master 1-based indices.
     *
     * @param taskList The task list to format.
     * @param filter The condition a task must meet to be included.
     * @param header The header message preceding matching tasks.
     * @param emptyMessage The message to display when no tasks match the filter.
     * @return The formatted task list string.
     */
    private String formatFilteredTasks(TaskList taskList, Predicate<Task> filter,
            String header, String emptyMessage) {
        assert taskList != null : "TaskList cannot be null";
        assert filter != null : "Filter predicate cannot be null";
        String items = IntStream.range(0, taskList.size())
                .filter(i -> filter.test(taskList.get(i)))
                .mapToObj(i -> (i + 1) + "." + taskList.get(i))
                .collect(Collectors.joining("\n"));
        return items.isEmpty() ? emptyMessage : header + "\n" + items;
    }

    /**
     * Displays all incomplete (pending) tasks in the task list, preserving master indices.
     *
     * @param taskList The task list to display.
     */
    public void showPendingTasks(TaskList taskList) {
        showMessages(formatPendingTasks(taskList).split("\n"));
    }

    /**
     * Formats all incomplete (pending) tasks in the task list, preserving master indices.
     *
     * @param taskList The task list to format.
     * @return Formatted pending tasks string.
     */
    public String formatPendingTasks(TaskList taskList) {
        return formatFilteredTasks(taskList, t -> !t.isDone(),
                "Here are the pending tasks in your list:",
                "You have no pending tasks! Great job! ₍^. .^₎");
    }

    /**
     * Displays all completed tasks in the task list, preserving master indices.
     *
     * @param taskList The task list to display.
     */
    public void showDoneTasks(TaskList taskList) {
        showMessages(formatDoneTasks(taskList).split("\n"));
    }

    /**
     * Formats all completed tasks in the task list, preserving master indices.
     *
     * @param taskList The task list to format.
     * @return Formatted completed tasks string.
     */
    public String formatDoneTasks(TaskList taskList) {
        return formatFilteredTasks(taskList, Task::isDone,
                "Here are the completed tasks in your list:",
                "You have no completed tasks yet.");
    }

    /**
     * Displays the statistics dashboard within standard divider lines.
     *
     * @param statsMessage The formatted statistics message to display.
     */
    public void showStats(String statsMessage) {
        assert statsMessage != null : "Stats message cannot be null";
        showMessages(statsMessage.split("\n"));
    }

    /**
     * Formats the schedule of tasks occurring on or due by the specified date.
     * Preserves master 1-based indices.
     *
     * @param taskList The task list to format from.
     * @param targetDate The date to inspect.
     * @return Formatted schedule string.
     */
    public String formatSchedule(TaskList taskList, LocalDate targetDate) {
        assert taskList != null : "TaskList cannot be null";
        assert targetDate != null : "Target date cannot be null";

        String dateDisplay = DateTimeUtil.formatDateForDisplay(targetDate);
        String items = IntStream.range(0, taskList.size())
                .filter(i -> {
                    Task task = taskList.get(i);
                    if (task instanceof Deadline) {
                        LocalDate deadlineDate = ((Deadline) task).getDeadlineDate();
                        return deadlineDate != null && deadlineDate.equals(targetDate);
                    } else if (task instanceof Event) {
                        return ((Event) task).occursOn(targetDate);
                    }
                    return false;
                })
                .mapToObj(i -> (i + 1) + "." + taskList.get(i))
                .collect(Collectors.joining("\n"));

        if (items.isEmpty()) {
            return PersonalityBank.formatEmptySchedule(dateDisplay);
        }
        return "Schedule for " + dateDisplay + ":\n" + items;
    }

    /**
     * Displays the schedule of tasks occurring on the specified date within standard divider lines.
     *
     * @param scheduleText The formatted schedule text to display.
     */
    public void showSchedule(String scheduleText) {
        assert scheduleText != null : "Schedule text cannot be null";
        showMessages(scheduleText.split("\n"));
    }
}

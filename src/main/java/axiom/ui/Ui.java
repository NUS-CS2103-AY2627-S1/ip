package axiom.ui;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import axiom.task.Task;
import axiom.task.TaskList;

/**
 * Represents a component that handles interactions with the user via standard input and output.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String TASK_DETAIL_INDENT = "   ";
    private static final String BANNER = "     _    __  _____ ___  __  __ \n"
                                       + "    / \\   \\ \\/ /_ _/ _ \\|  \\/  |\n"
                                       + "   / _ \\   \\  / | | | | | |\\/| |\n"
                                       + "  / ___ \\  /  \\ | | |_| | |  | |\n"
                                       + " /_/   \\_\\/_/\\_\\___\\___/|_|  |_|\n";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome banner and greeting.
     */
    public void showWelcome() {
        System.out.println(formatWelcome());
    }

    /**
     * Displays a horizontal separator line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Displays a message to the user.
     *
     * @param message Text to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error description.
     */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /**
     * Returns the next command entered by the user.
     *
     * @return The command line entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Returns the welcome banner and greeting.
     *
     * @return Welcome text for the chatbot.
     */
    public String formatWelcome() {
        return BANNER + "\nHello! I'm AXIOM.\nWhat can I do for you?\n" + LINE;
    }

    /**
     * Returns a compact welcome greeting for the GUI.
     * Omits the ASCII banner so the chat pane is used for conversation, not decoration.
     *
     * @return Welcome text without the ASCII banner.
     */
    public String formatGuiWelcome() {
        return "Hello! I'm AXIOM.\nWhat can I do for you?";
    }

    /**
     * Returns the goodbye message.
     *
     * @return Goodbye text.
     */
    public String formatGoodbye() {
        return " Bye. Hope to see you again soon!";
    }

    /**
     * Returns all tasks in the list with their one-based indices.
     *
     * @param tasks Task list to display.
     * @return Formatted task list text.
     */
    public String formatTaskList(TaskList tasks) {
        return formatNumberedTasks(" Here are the tasks in your list:", tasks);
    }

    /**
     * Returns all tasks after they have been sorted chronologically.
     *
     * @param tasks Sorted task list to display.
     * @return Formatted sorted task list text.
     */
    public String formatSortedTaskList(TaskList tasks) {
        return formatNumberedTasks(" OK, I've sorted your tasks chronologically:", tasks);
    }

    /**
     * Returns tasks whose descriptions match the search keyword.
     *
     * @param tasks Task list to search.
     * @param matchingIndexes Zero-based indexes of matching tasks.
     * @return Formatted matching-task text.
     */
    public String formatMatchingTasks(TaskList tasks, ArrayList<Integer> matchingIndexes) {
        assert tasks != null : "Task list should not be null";
        assert matchingIndexes != null : "Matching indexes should not be null";
        return Stream.concat(
                Stream.of(" Here are the matching tasks in your list:"),
                matchingIndexes.stream()
                        .map(index -> {
                            assert index >= 0 && index < tasks.size()
                                    : "Matching index should be a valid 0-based task index";
                            return formatNumberedTask(index + 1, tasks.get(index));
                        }))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task The task that was added.
     * @param taskCount Total number of tasks after the addition.
     * @return Formatted add-task confirmation.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return " Got it. I've added this task:" + formatIndentedTask(task)
                + "\n" + formatTaskCount(taskCount);
    }

    /**
     * Returns confirmation that a task was marked as done.
     *
     * @param task The task that was marked.
     * @return Formatted mark confirmation.
     */
    public String formatMarked(Task task) {
        return " Nice! I've marked this task as done:" + formatIndentedTask(task);
    }

    /**
     * Returns confirmation that a task was marked as not done.
     *
     * @param task The task that was unmarked.
     * @return Formatted unmark confirmation.
     */
    public String formatUnmarked(Task task) {
        return " OK, I've marked this task as not done yet:" + formatIndentedTask(task);
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task The task that was removed.
     * @param taskCount Total number of tasks after the deletion.
     * @return Formatted delete confirmation.
     */
    public String formatDeleted(Task task, int taskCount) {
        return " Noted. I've removed this task:" + formatIndentedTask(task)
                + "\n" + formatTaskCount(taskCount);
    }

    /**
     * Returns a header followed by numbered task lines.
     *
     * @param header First line of the display.
     * @param tasks Tasks to number and display.
     * @return Formatted header and numbered tasks.
     */
    private String formatNumberedTasks(String header, TaskList tasks) {
        return Stream.concat(
                Stream.of(header),
                IntStream.range(0, tasks.size())
                        .mapToObj(i -> formatNumberedTask(i + 1, tasks.get(i))))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns the line that reports how many tasks remain in the list.
     *
     * @param taskCount Current number of tasks.
     * @return Formatted task-count text.
     */
    private String formatTaskCount(int taskCount) {
        return " Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns a task on its own indented line.
     *
     * @param task Task to display.
     * @return Formatted task line.
     */
    private String formatIndentedTask(Task task) {
        return "\n" + TASK_DETAIL_INDENT + task;
    }

    /**
     * Returns a numbered task line for list displays.
     *
     * @param taskNumber One-based task number to display.
     * @param task Task to display.
     * @return Formatted numbered task line.
     */
    private String formatNumberedTask(int taskNumber, Task task) {
        return " " + taskNumber + "." + task;
    }
}

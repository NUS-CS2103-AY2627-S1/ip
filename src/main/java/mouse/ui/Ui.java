package mouse.ui;

import java.util.Scanner;

import mouse.task.Task;
import mouse.task.TaskList;

/**
 * Reads user input and prints chatbot replies.
 */
public class Ui {
    private static final String LINE = "    " + "_".repeat(60);
    private static final String INDENT = "     ";
    private static final String BANNER = " __  __                      \n"
            + "|  \\/  | ___  _   _ ___  ___ \n"
            + "| |\\/| |/ _ \\| | | / __|/ _ \\\n"
            + "| |  | | (_) | |_| \\__ \\  __/\n"
            + "|_|  |_|\\___/ \\__,_|___/\\___|";
    private static final String ERROR_PREFIX = "SQUEAK!!! ";

    private final Scanner scanner;

    /**
     * Creates a UI that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints the startup banner and greeting.
     */
    public void showGreeting() {
        showReply(append(BANNER.split("\n"),
                "Squeak! I'm Mouse.",
                "Got any crumbs to stash?"));
    }

    /**
     * Reads the next command line from the user.
     *
     * @return Raw input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Returns the GUI greeting without the ASCII banner.
     *
     * @return Greeting text for a chat bubble.
     */
    public String formatGreeting() {
        return joinLines("Squeak! I'm Mouse.", "Got any crumbs to stash?");
    }

    /**
     * Returns the goodbye message.
     *
     * @return Goodbye text.
     */
    public String formatBye() {
        return "Squeak. Mouse is off to nibble. See you.";
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Current number of tasks.
     * @return Confirmation text.
     */
    public String formatAdded(Task task, int taskCount) {
        return joinLines(
                "Stashed this crumb:",
                "  " + task,
                formatStashCount(taskCount));
    }

    /**
     * Returns confirmation that a task was marked done.
     *
     * @param task Marked task.
     * @return Confirmation text.
     */
    public String formatMarked(Task task) {
        return joinLines("Nibble done. Marked this crumb:", "  " + task);
    }

    /**
     * Returns confirmation that a task was unmarked.
     *
     * @param task Unmarked task.
     * @return Confirmation text.
     */
    public String formatUnmarked(Task task) {
        return joinLines("Un-nibbled. This crumb is open again:", "  " + task);
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Remaining number of tasks.
     * @return Confirmation text.
     */
    public String formatDeleted(Task task, int taskCount) {
        return joinLines(
                "Tossed this crumb:",
                "  " + task,
                formatStashCount(taskCount));
    }

    /**
     * Returns an error message with the {@code SQUEAK!!!} prefix.
     *
     * @param errorMessage Error text without the prefix.
     * @return Error text for display.
     */
    public String formatError(String errorMessage) {
        return ERROR_PREFIX + errorMessage;
    }

    /**
     * Returns whether {@code text} is an error reply.
     *
     * @param text Reply text.
     * @return {@code true} if the reply is an error.
     */
    public boolean isErrorMessage(String text) {
        return text != null && text.startsWith(ERROR_PREFIX);
    }

    /**
     * Returns the numbered task list.
     *
     * @param tasks Task list to display.
     * @return List text.
     */
    public String formatList(TaskList tasks) {
        return formatNumberedTasks("Crumbs in the stash:", tasks);
    }

    /**
     * Returns matching tasks from a find, numbered with their stash indices.
     *
     * @param tasks Full task list, used for the displayed indices.
     * @param matches Tasks that matched the keyword.
     * @return Find result text.
     */
    public String formatFind(TaskList tasks, TaskList matches) {
        String[] lines = new String[matches.size() + 1];
        lines[0] = "Crumbs matching that sniff:";
        int lineIndex = 1;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            for (int j = 0; j < matches.size(); j++) {
                if (task == matches.get(j)) {
                    lines[lineIndex] = (i + 1) + "." + task;
                    lineIndex++;
                    break;
                }
            }
        }
        return joinLines(lines);
    }

    /**
     * Returns confirmation that a task's priority was updated.
     *
     * @param task Updated task.
     * @return Confirmation text.
     */
    public String formatPriority(Task task) {
        return joinLines("OK, Mouse tagged this crumb:", "  " + task);
    }

    /**
     * Returns a short guide to the commands Mouse understands.
     *
     * @return Help text.
     */
    public String formatHelp() {
        return joinLines(
                "Mouse hoards crumbs. Commands it understands:",
                "• todo DESCRIPTION - Add a to-do",
                "• deadline DESCRIPTION /by WHEN - Add a deadline (e.g. 2019-12-02)",
                "• event DESCRIPTION /from START /to END - Add an event",
                "• list - Show all tasks",
                "• mark INDEX - Mark that task as done (1 is first)",
                "• unmark INDEX - Mark that task as not done",
                "• delete INDEX - Remove that task",
                "• find KEYWORD - Show tasks containing KEYWORD",
                "• priority INDEX high|low|none - Set that task's priority",
                "• help - Show this guide",
                "• bye - Exit Mouse");
    }

    /**
     * Prints the goodbye message.
     */
    public void showBye() {
        showReply(formatBye());
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Current number of tasks.
     */
    public void showAdded(Task task, int taskCount) {
        showReply(splitLines(formatAdded(task, taskCount)));
    }

    /**
     * Prints confirmation that a task was marked done.
     *
     * @param task Marked task.
     */
    public void showMarked(Task task) {
        showReply(splitLines(formatMarked(task)));
    }

    /**
     * Prints confirmation that a task was unmarked.
     *
     * @param task Unmarked task.
     */
    public void showUnmarked(Task task) {
        showReply(splitLines(formatUnmarked(task)));
    }

    /**
     * Prints confirmation that a task was deleted, with the new task count.
     *
     * @param task Deleted task.
     * @param taskCount Remaining number of tasks.
     */
    public void showDeleted(Task task, int taskCount) {
        showReply(splitLines(formatDeleted(task, taskCount)));
    }

    /**
     * Prints confirmation that a task was deleted.
     *
     * @param task Deleted task.
     */
    public void showDeleted(Task task) {
        showReply("Tossed this crumb:", " " + task);
    }

    /**
     * Prints an error message wrapped like other replies.
     *
     * @param errorMessage Error text without the {@code SQUEAK!!!} prefix.
     */
    public void showError(String errorMessage) {
        showReply(formatError(errorMessage));
    }

    /**
     * Prints the numbered task list.
     *
     * @param tasks Task list to display.
     */
    public void showList(TaskList tasks) {
        showReply(numberedLines("Crumbs in the stash:", tasks));
    }

    /**
     * Prints matching tasks from a find, numbered from 1 among matches only.
     *
     * @param matches Tasks that matched the keyword.
     */
    public void showFind(TaskList matches) {
        showReply(numberedLines("Crumbs matching that sniff:", matches));
    }

    /**
     * Prints a short guide to the commands Mouse understands.
     */
    public void showHelp() {
        showReply(splitLines(formatHelp()));
    }

    /**
     * Prints each message wrapped in horizontal lines.
     *
     * @param messages Lines to print between the borders.
     */
    public void showReply(String... messages) {
        System.out.println(LINE);
        for (String message : messages) {
            System.out.println(INDENT + message);
        }
        System.out.println(LINE);
    }

    /**
     * Returns the stash-size line with singular or plural wording.
     *
     * @param taskCount Number of tasks now stored.
     * @return Count sentence.
     */
    private String formatStashCount(int taskCount) {
        String crumbWord = taskCount == 1 ? "crumb" : "crumbs";
        return "The stash now holds " + taskCount + " " + crumbWord + ".";
    }

    /**
     * Returns a header plus one numbered line per task.
     *
     * @param header First line of the reply.
     * @param tasks Tasks to list.
     * @return Joined list text.
     */
    private String formatNumberedTasks(String header, TaskList tasks) {
        return joinLines(numberedLines(header, tasks));
    }

    /**
     * Joins {@code lines} with newlines.
     *
     * @param lines Lines to join.
     * @return Single multiline string.
     */
    private String joinLines(String... lines) {
        return String.join("\n", lines);
    }

    /**
     * Splits {@code text} into lines, keeping empty trailing lines.
     *
     * @param text Multiline text.
     * @return Individual lines.
     */
    private String[] splitLines(String text) {
        return text.split("\n", -1);
    }

    /**
     * Builds a header line plus {@code 1.task} lines for {@code tasks}.
     *
     * @param header First line of the reply.
     * @param tasks Tasks to number.
     * @return Header and numbered task lines.
     */
    private String[] numberedLines(String header, TaskList tasks) {
        String[] lines = new String[tasks.size() + 1];
        lines[0] = header;
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = (i + 1) + "." + tasks.get(i);
        }
        return lines;
    }

    /**
     * Concatenates {@code prefix} and {@code extra} into one array.
     *
     * @param prefix Leading lines.
     * @param extra Lines to append.
     * @return Combined lines.
     */
    private String[] append(String[] prefix, String... extra) {
        String[] result = new String[prefix.length + extra.length];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(extra, 0, result, prefix.length, extra.length);
        return result;
    }

}

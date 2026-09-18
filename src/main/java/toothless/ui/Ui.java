package toothless.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import toothless.DisplaySymbols;
import toothless.task.Task;
import toothless.task.TaskList;

/**
 * Handles console input and presents Toothless's responses to the user.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = "  __/\\__           __/\\__\n"
            + " /     \\_________/     \\\n"
            + "/   /\\   O     O   /\\   \\\n"
            + "\\__/  \\     ^     /  \\__/\n"
            + "       \\  \\___/  /\n"
            + "    ____|       |____\n"
            + " __/    |       |    \\__\n"
            + "/___/   /|_______|\\   \\___\\\n"
            + "        /_/     \\_\\";

    private final Scanner scanner;
    private final PrintStream output;
    private final boolean isDividerEnabled;
    private boolean hasError;
    private boolean hasMalformedDataWarning;

    /**
     * Creates a UI connected to the application's current console streams.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied streams.
     *
     * @param input stream containing user commands.
     * @param output stream receiving application responses.
     */
    public Ui(InputStream input, PrintStream output) {
        this(input, output, true);
    }

    /**
     * Creates a UI using the supplied streams and divider preference.
     *
     * @param input stream containing user commands.
     * @param output stream receiving application responses.
     * @param isDividerEnabled whether console dividers should be displayed.
     */
    public Ui(InputStream input, PrintStream output, boolean isDividerEnabled) {
        this.scanner = new Scanner(input);
        this.output = output;
        this.isDividerEnabled = isDividerEnabled;
    }

    /**
     * Displays the greeting and command summary shown at startup.
     */
    public void showWelcome() {
        output.println(DIVIDER);
        output.println(BANNER);
        output.println();
        output.println("Hi there! I'm Toothless. It's wonderful to meet you!");
        output.println("What can I do for you today?");
        output.println("Ready for our next little adventure? Tell me what to remember:");
        output.println("  - todo [DESCRIPTION]");
        output.println("  - deadline [DESCRIPTION] /by [yyyy-MM-dd]");
        output.println("  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]");
        output.println("  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION]"
                + " [/by yyyy-MM-dd] [/from START] [/to END]");
        output.println("You can also type list to see all our quests. Tiny roar! "
                + DisplaySymbols.getDecorativeMark());
        showDivider();
    }

    /**
     * Returns whether another line of user input is available.
     *
     * @return true when another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next complete command line.
     *
     * @return next line entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a warning when some saved task lines could not be understood.
     *
     * @param malformedLineCount number of saved lines that were skipped.
     */
    public void showMalformedDataWarning(int malformedLineCount) {
        hasError = true;
        hasMalformedDataWarning = true;
        String lineWord = malformedLineCount == 1 ? "line" : "lines";
        output.println("Toothless found " + malformedLineCount + " puzzling " + lineWord
                + " in his saved quests.");
        output.println("He skipped them and kept every task he could understand.");
        output.println("Saved changes are paused until the file is repaired; your data stays untouched.");
        showDivider();
    }

    /**
     * Displays the friendly fallback used when saved tasks cannot be read.
     */
    public void showLoadError() {
        hasError = true;
        output.println("Toothless had trouble reading his saved quests.");
        output.println("He'll start with an empty cave, but the saved file was left untouched.");
        output.println("Saved changes are paused until the file can be read.");
        showDivider();
    }

    /**
     * Displays all current tasks with one-based numbering.
     *
     * @param taskList tasks to display.
     */
    public void showTaskList(TaskList taskList) {
        if (taskList.isEmpty()) {
            output.println("Your task list is empty. Ready for a new adventure!");
            return;
        }
        output.println("Here are the tasks in your list:");
        for (int i = 0; i < taskList.size(); i++) {
            output.println((i + 1) + "." + taskList.getTask(i));
        }
    }

    /**
     * Displays tasks matching the latest search with fresh one-based numbering.
     *
     * @param matchingTasks tasks found in their original list order
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            output.println("Toothless couldn’t find any matching tasks in the cave.");
            output.println("Try another keyword and he'll sniff around again!");
            return;
        }
        output.println("Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.println((i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Confirms that a task was marked as completed.
     *
     * @param task task that was marked.
     */
    public void showTaskMarked(Task task) {
        output.println("A happy little roar! I've starred this task as done:");
        output.println("  " + task);
    }

    /**
     * Explains that marking an already completed task made no change.
     *
     * @param task task already completed.
     */
    public void showTaskAlreadyMarked(Task task) {
        output.println("This task is already starred as done, little rider:");
        output.println("  " + task);
    }

    /**
     * Explains that an incomplete task did not need to be unmarked.
     *
     * @param task task that was already incomplete.
     */
    public void showTaskAlreadyUnmarked(Task task) {
        output.println("This task wasn't marked as done before, little rider:");
        output.println("  " + task);
    }

    /**
     * Confirms that a task was changed back to incomplete.
     *
     * @param task task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        output.println("All right, little rider! I've unstarred this task for now:");
        output.println("  " + task);
    }

    /**
     * Confirms that a task was removed and displays the remaining task count.
     *
     * @param task task that was removed.
     * @param taskCount number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("A tiny farewell roar! Toothless has removed this task:");
        output.println("  " + task);
        output.println(formatTaskCount(taskCount));
    }

    /**
     * Confirms that selected fields of a task were updated.
     *
     * @param task updated task.
     */
    public void showTaskEdited(Task task) {
        output.println("A clever little roar! Toothless has updated this task:");
        output.println("  " + task);
    }

    /**
     * Confirms that a task was added and displays the new task count.
     *
     * @param task task that was added.
     * @param taskCount current number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Got it! Toothless has added this task for you:");
        output.println("  " + task);
        output.println(formatTaskCount(taskCount) + " " + DisplaySymbols.getDecorativeMark());
    }

    /**
     * Displays the friendly warning used when changed tasks cannot be saved.
     */
    public void showSaveError() {
        hasError = true;
        output.println("Toothless couldn’t tuck these changes into his data file.");
        output.println("Nothing changed. Check the saved data or file access, then try again.");
    }

    /**
     * Displays a validation or command error reported by the parser.
     *
     * @param message error message to display.
     */
    public void showError(String message) {
        hasError = true;
        output.println(message);
    }

    /**
     * Reports an unforeseen command problem without exposing internal details.
     */
    public void showUnexpectedError() {
        showError("Toothless hit a snag with that command. Please try again.");
    }

    /**
     * Returns whether this response includes a validation or storage error.
     *
     * @return true when an error was reported while producing this response
     */
    public boolean hasError() {
        return hasError;
    }

    /**
     * Returns whether loading encountered malformed saved task records.
     *
     * @return true when at least one saved record could not be loaded.
     */
    public boolean hasMalformedDataWarning() {
        return hasMalformedDataWarning;
    }

    /**
     * Displays the farewell for the current session.
     */
    public void showGoodbye() {
        output.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays the divider separating responses.
     */
    public void showDivider() {
        if (isDividerEnabled) {
            output.println(DIVIDER);
        }
    }

    /**
     * Releases the scanner when the application finishes.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Formats the current task count with correct singular or plural grammar.
     *
     * @param taskCount current number of tasks.
     * @return sentence describing the current task count
     */
    private String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return "Now you have " + taskCount + " " + taskWord + " in the list.";
    }
}

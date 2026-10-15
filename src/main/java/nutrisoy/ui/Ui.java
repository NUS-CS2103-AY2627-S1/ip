package nutrisoy.ui;

import java.util.Scanner;
import java.util.stream.IntStream;

import nutrisoy.task.Task;
import nutrisoy.task.TaskList;

/**
 * Handles console input and output for the NutriSoy application.
 */
public class Ui {
    /** Display name used consistently by the console and graphical interfaces. */
    public static final String BOT_NAME = "Soya";

    private final String divider = "____________________________________________________________";
    private final Scanner scanner;
    private StringBuilder capturedOutput;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the application's welcome message.
     */
    public void showWelcome() {
        String logo = """
                         .----------------.
                         |      SOYA      |
                         '----------------'
                """;
        System.out.println(divider);
        System.out.print(logo);
        System.out.println(" Hey, I'm " + BOT_NAME + " -- your sassy task sidekick.");
        System.out.println(" Hand me a task; I'll keep the chaos organised.");
        System.out.println(divider);
    }

    /**
     * Displays a divider line.
     */
    public void showLine() {
        showMessage(divider);
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return command read from standard input
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Displays an error message.
     *
     * @param message error explanation to display
     */
    public void showError(String message) {
        assert message != null : "Displayed error message must not be null";
        showMessage(" Nice try. " + message);
    }

    /**
     * Displays an error message for an unsuccessful task load.
     */
    public void showLoadingError() {
        showLoadingError("Check that the task file exists and is readable.");
    }

    /**
     * Displays an error message for an unsuccessful task load with diagnostic details.
     *
     * @param details explanation of the storage failure
     */
    public void showLoadingError(String details) {
        assert details != null : "Loading error details must not be null";
        showMessage(" Your save file needs attention. Task changes are disabled to protect your data.");
        showMessage(" " + details);
    }

    /**
     * Displays the application's farewell message.
     */
    public void showGoodbye() {
        showMessage(" That's a wrap. Go be iconic -- and maybe finish the rest later.");
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task task that was added
     * @param totalTasks number of tasks now in the list
     */
    public void showTaskAdded(Task task, int totalTasks) {
        assert task != null && totalTasks >= 0 : "Added task and total count must be valid";
        showMessage(" Consider it handled. I added:");
        showMessage("   " + task);
        showTaskCount(totalTasks);
    }

    /**
     * Displays confirmation that a task was removed.
     *
     * @param task task that was removed
     * @param totalTasks number of tasks now in the list
     */
    public void showTaskRemoved(Task task, int totalTasks) {
        assert task != null && totalTasks >= 0 : "Removed task and total count must be valid";
        showMessage(" And... cut! I removed:");
        showMessage("   " + task);
        showTaskCount(totalTasks);
    }

    /**
     * Displays confirmation that a task was marked as complete.
     *
     * @param task task that was marked
     */
    public void showTaskMarked(Task task) {
        assert task != null : "Marked task must not be null";
        showMessage(" Look at you being productive. Completed:");
        showMessage("   " + task);
    }

    /**
     * Displays confirmation that a task was marked as incomplete.
     *
     * @param task task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        assert task != null : "Unmarked task must not be null";
        showMessage(" Plot twist -- this task is back:");
        showMessage("   " + task);
    }

    /**
     * Displays confirmation that a tag was added to a task.
     *
     * @param task task that was tagged
     * @param tagName tag that was added
     */
    public void showTaskTagged(Task task, String tagName) {
        assert task != null && tagName != null : "Tagged task and tag must not be null";
        showMessage(" Accessorised. I added #" + tagName + " to:");
        showMessage("   " + task);
    }

    /**
     * Displays confirmation that a tag was removed from a task.
     *
     * @param task task that was untagged
     * @param tagName tag that was removed
     */
    public void showTaskUntagged(Task task, String tagName) {
        assert task != null && tagName != null : "Untagged task and tag must not be null";
        showMessage(" That tag is so last season. I removed #" + tagName + " from:");
        showMessage("   " + task);
    }

    /**
     * Displays every task in the supplied task list.
     *
     * @param tasks task list to display
     */
    public void showTaskList(TaskList tasks) {
        assert tasks != null : "Task list to display must not be null";
        if (tasks.isEmpty()) {
            showMessage(" Your task list is empty. Very minimalist of you.");
            return;
        }
        showMessage(" Here's your lineup:");
        IntStream.range(0, tasks.size()).forEach(i -> {
            showMessage(" " + (i + 1) + "." + tasks.get(i));
        });
    }

    /**
     * Displays the list of tasks that match the search keyword.
     *
     * @param matchingTasks The TaskList containing the matching tasks.
     */
    public void showMatchingTasks(TaskList matchingTasks) {
        assert matchingTasks != null : "Matching task list must not be null";
        if (matchingTasks.isEmpty()) {
            showMessage(" No matches. Even I can't serve results that don't exist.");
            return;
        }
        showMessage(" Found them. Obviously:");
        IntStream.range(0, matchingTasks.size()).forEach(i -> {
            showMessage(" " + (i + 1) + "." + matchingTasks.get(i));
        });
    }

    /**
     * Begins collecting messages for a graphical user interface response.
     */
    public void startCapturingOutput() {
        capturedOutput = new StringBuilder();
    }

    /**
     * Stops collecting messages and returns the accumulated response.
     *
     * @return response produced since output capture began
     */
    public String stopCapturingOutput() {
        assert capturedOutput != null : "Output capture must be started before it is stopped";
        String response = capturedOutput.toString().stripTrailing();
        capturedOutput = null;
        return response;
    }

    /**
     * Displays a message in the console or appends it to a GUI response.
     *
     * @param message text to display or capture
     */
    public void showMessage(String message) {
        assert message != null : "Displayed message must not be null";
        if (capturedOutput == null) {
            System.out.println(message);
            return;
        }
        capturedOutput.append(message).append(System.lineSeparator());
    }

    /**
     * Displays a grammatically correct task count after the list changes.
     *
     * @param totalTasks number of tasks currently in the list
     */
    private void showTaskCount(int totalTasks) {
        String taskWord = totalTasks == 1 ? "task" : "tasks";
        showMessage(" Your list is now serving " + totalTasks + " " + taskWord + ".");
    }
}

package alfred.ui;

import java.util.List;
import java.util.Scanner;

import alfred.task.Task;
import alfred.task.TaskList;

/**
 * Handles Alfred's console input and output.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BANNER = "      *==/          |     |            \\==*\n"
            + "     /XX/           |\\__\\/|             \\XX\\\n"
            + "   /XXXX\\           |XXXXX|             /XXXX\\\n"
            + " |XXXXXX\\_         *XXXXXXX*         \\_/XXXXXX|\n"
            + "XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX\n"
            + "|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|\n"
            + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n"
            + "|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|\n"
            + " XXXXXX/^^^^\"\\XXXXXXXXXXXXXXXXXXXXX/^^^^^\\XXXXXX\n"
            + " |XXX|       \\XXX/^^\\XXXXX/^^\\XXX/       |XXX|\n"
            + "   \\XX\\       \\X/    \\XXX/    \\X/       /XX/\n"
            + "      \"\\       \"      \\X/      \"       /\n"
            + "\n"
            + "    _    _  __             _\n"
            + "   / \\  | |/ _|_ __ ___  __| |\n"
            + "  / _ \\ | | |_| '__/ _ \\/ _` |\n"
            + " / ___ \\| |  _| | |  __/ (_| |\n"
            + "/_/   \\_\\_|_| |_|  \\___|\\__,_|\n";

    private final Scanner scanner;
    private final StringBuilder responseBuffer;

    /**
     * Creates a console user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        responseBuffer = null;
    }

    /**
     * Creates a user interface that records responses for display in the GUI.
     *
     * @param responseBuffer buffer that receives displayed messages.
     */
    public Ui(StringBuilder responseBuffer) {
        scanner = new Scanner(System.in);
        this.responseBuffer = responseBuffer;
    }

    /** Displays Alfred's welcome message. */
    public void showWelcome() {
        showMessage(SEPARATOR);
        showMessage(BANNER);
        showMessage("Good day, sir. Alfred is at your service in the cave. What shall we accomplish?");
        showMessage(SEPARATOR);
    }

    /** Reads the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the separator used before and after command responses. */
    public void showSeparator() {
        showMessage(SEPARATOR);
    }

    /** Displays Alfred's farewell message. */
    public void showGoodbye() {
        showMessage("The cave is secure. Until our next mission, sir.");
    }

    /** Displays every task in the current task list. */
    public void showTaskList(TaskList tasks) {
        showMessage("Here is your cave ledger:");
        showNumberedTasks(tasks);
    }

    /** Displays the tasks whose descriptions match a search keyword. */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showMessage("Here are the matching missions in your cave ledger:");
        showNumberedTasks(matchingTasks);
    }

    /** Displays a chronological task view without changing the task list. */
    public void showSortedTasks(List<Task> sortedTasks, boolean isAscending) {
        String direction = isAscending ? "ascending" : "descending";
        showMessage("Here is your cave ledger in " + direction + " chronological order:");
        showNumberedTasks(sortedTasks);
    }

    /** Displays tasks with the one-based numbering used in Alfred's task lists. */
    private void showNumberedTasks(Iterable<Task> tasks) {
        int taskNumber = 1;
        for (Task task : tasks) {
            showMessage(taskNumber + "." + task);
            taskNumber++;
        }
    }

    /** Displays confirmation that a task was marked as done. */
    public void showTaskMarked(Task task) {
        showMessage("Mission accomplished. I've marked this task as done:");
        showMessage("  " + task);
    }

    /** Displays confirmation that a task was marked as not done. */
    public void showTaskUnmarked(Task task) {
        showMessage("I've reopened this mission:");
        showMessage("  " + task);
    }

    /** Displays confirmation that a task was removed. */
    public void showTaskDeleted(Task task, int taskCount) {
        showMessage("The following mission has left the cave ledger:");
        showMessage("  " + task);
        showMessage(getTaskCountMessage(taskCount));
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        showMessage("Consider it noted in the cave ledger:");
        showMessage("  " + task);
        showMessage(getTaskCountMessage(taskCount));
    }

    /** Displays an error message. */
    public void showError(String message) {
        showMessage("Apologies, sir: " + message);
    }

    /**
     * Returns a cave-ledger count message with the appropriate singular or plural noun.
     */
    private String getTaskCountMessage(int taskCount) {
        assert taskCount >= 0 : "Task counts must not be negative";
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        return "Your cave ledger now holds " + taskCount + " " + taskNoun + ".";
    }

    /**
     * Sends a message to the console or GUI response buffer.
     *
     * @param message text to display.
     */
    private void showMessage(String message) {
        if (responseBuffer == null) {
            System.out.println(message);
        } else {
            responseBuffer.append(message).append(System.lineSeparator());
        }
    }
}

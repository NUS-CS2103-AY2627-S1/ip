package chausistant.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Handles all console input and output for the Chausistant chatbot.
 *
 * <p>The rest of the application gives this class already formatted task
 * strings and messages. This keeps user-facing text and console operations
 * in one place without giving the UI responsibility for task logic.</p>
 */
public class Ui {
    private final Scanner scanner;
    private final Consumer<String> output;
    private boolean hasShownError;

    /** Creates a UI that reads commands from the standard input stream. */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
    }

    /**
     * Creates a UI that appends its output to the supplied response.
     *
     * @param response response that receives formatted chatbot messages
     */
    public Ui(StringBuilder response) {
        scanner = new Scanner("");
        output = message -> response.append(message).append(System.lineSeparator());
    }

    /** Returns whether the user has another command to enter. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and trims one command entered by the user. */
    public String readCommand() {
        return scanner.nextLine().strip();
    }

    /** Displays the chatbot's startup banner. */
    public void showWelcome() {
        String banner = String.join(System.lineSeparator(),
                " hi! i'm",
                "",
                " ████ █   █  ███  █   █  ████ █████"
                        + "  ████ █████  ███  █   █ █████",
                "█     █   █ █   █ █   █ █       █   █"
                        + "       █   █   █ ██  █   █",
                "█     █   █ █   █ █   █ █       █   █"
                        + "       █   █   █ ██  █   █",
                "█     █████ █████ █   █  ███    █"
                        + "    ███    █   █████ █ █ █   █",
                "█     █   █ █   █ █   █    █    █"
                        + "      █    █   █   █ █  ██   █",
                "█     █   █ █   █ █   █    █    █"
                        + "      █    █   █   █ █  ██   █",
                " ████ █   █ █   █  ███  ████  █████"
                        + " ████    █   █   █ █   █ █   █",
                "",
                "                      chausistant",
                "",
                "ready for a tiny win today?")
                + System.lineSeparator();
        show(banner);
    }

    /** Displays an error message with the chatbot's standard error prefix. */
    public void showError(String message) {
        hasShownError = true;
        show("oopsie! " + message);
    }

    /** Returns whether this UI has displayed at least one error message. */
    public boolean hasShownError() {
        return hasShownError;
    }

    /** Displays the task created by a successful add command. */
    public void showTaskAdded(String task, int taskCount) {
        show("yay! i've added this little mission:");
        show(task);
        show(formatTaskCount(taskCount));
    }

    /** Displays the task affected by a successful mark or unmark command. */
    public void showTaskStatus(String task) {
        show("looking good! here's your task now:");
        show(task);
    }

    /** Displays the task removed by a successful delete command. */
    public void showTaskDeleted(String task, int taskCount) {
        show("poof! i've tucked this task away:");
        show(task);
        show("your list now has " + taskCount + " "
                + (taskCount == 1 ? "task" : "tasks") + ".");
    }

    /** Displays every task currently in the task list. */
    public void showTaskList(List<String> tasks) {
        show("here's your tiny adventure list:");
        if (tasks.isEmpty()) {
            show("your list is all clear! tiny victory dance time!");
        }

        showNumberedTasks(tasks);
    }

    /** Displays tasks whose descriptions match a user-provided search phrase. */
    public void showMatchingTasks(List<String> tasks) {
        show("i found these task twins:");
        if (tasks.isEmpty()) {
            show("no task twins found yet!");
            return;
        }

        showNumberedTasks(tasks);
    }

    /** Displays each supplied task on its own one-based numbered line. */
    private void showNumberedTasks(List<String> tasks) {
        for (int index = 0; index < tasks.size(); index++) {
            show((index + 1) + "." + tasks.get(index));
        }
    }

    /** Displays scheduled events and deadlines for one requested date. */
    public void showSchedule(String date, List<String> events, List<String> deadlines) {
        show("here's your day at a glance for " + date + ":");
        show("events:");
        if (events.isEmpty()) {
            show("no events here -- your calendar gets a cozy breather!");
        } else {
            events.forEach(this::show);
        }

        show("--------------------");
        show("deadlines:");
        if (deadlines.isEmpty()) {
            show("no deadlines here -- you're all clear!");
        } else {
            deadlines.forEach(this::show);
        }
    }

    /** Displays incomplete deadlines due within the fixed reminder window. */
    public void showUpcomingDeadlines(List<String> deadlines) {
        show("peek-a-boo! here are your upcoming deadlines:");
        if (deadlines.isEmpty()) {
            show("no upcoming deadlines in the next 7 days -- you're all caught up!");
            return;
        }

        deadlines.forEach(this::show);
    }

    /** Displays the chatbot's farewell message. */
    public void showGoodbye() {
        show("bye-bye for now! chausistant will be cheering for you!");
    }

    /** Formats an encouraging task-count message with grammatically correct wording. */
    private String formatTaskCount(int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        return "don't give up! you have " + taskCount + " " + taskNoun + " ahead of you...";
    }

    /** Sends one formatted message to this UI's output destination. */
    private void show(String message) {
        output.accept(message);
    }
}

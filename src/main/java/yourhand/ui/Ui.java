package yourhand.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Scanner;

import yourhand.exceptions.YourHandException;
import yourhand.tasks.Task;
import yourhand.tasks.TaskList;

/**
 * Handles console input and all messages shown by YourHand.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner;
    private final PrintStream output;

    /** Creates a UI that writes responses to standard output. */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /** Creates a UI that writes responses to the given output stream. */
    public Ui(PrintStream output) {
        this(new Scanner(System.in), output);
    }

    /** Creates a UI with controllable input and output streams for automated tests. */
    Ui(Scanner scanner, PrintStream output) {
        this.scanner = Objects.requireNonNull(scanner, "input scanner must be provided");
        this.output = Objects.requireNonNull(output, "output stream must be provided");
    }

    /** Displays the application banner and greeting. */
    public void showWelcomeMessage() {
        String banner = "__   __                 _   _                 _\n"
                + "\\ \\ / /__  _   _ _ __  | | | | __ _ _ __   __| |\n"
                + " \\ V / _ \\| | | | '__| | |_| |/ _` | '_ \\ / _` |\n"
                + "  | | (_) | |_| | |    |  _  | (_| | | | | (_| |\n"
                + "  |_|\\___/ \\__,_|_|    |_| |_|\\__,_|_| |_|\\__,_|";
        output.println(banner);
        showSeparator();
        output.println(" Selamat Datang 早上好! YourHand 为你服务");
        output.println(" 你来这干嘛 What are you here for?");
        showSeparator();
    }

    /** Returns whether another command can be read from the console. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next user command without changing it, so format errors can be explained. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the separator used around command responses. */
    public void showSeparator() {
        output.println(SEPARATOR);
    }

    /** Displays the farewell message. */
    public void showGoodbyeMessage() {
        output.println(" See you never :)");
    }

    /** Displays the commands supported by YourHand. */
    public void showHelp() {
        output.println(" Here is what I can help you keep in hand:");
        output.println(" todo DESCRIPTION");
        output.println("   Add something you need to do.");
        output.println(" deadline DESCRIPTION /by DATE_OR_TIME");
        output.println("   Add something with a due date or time.");
        output.println(" event DESCRIPTION /from DATE_OR_TIME /to DATE_OR_TIME");
        output.println("   Add something happening over a period of time.");
        output.println(" list");
        output.println("   Show all your tasks.");
        output.println(" find KEYWORD");
        output.println("   Search your tasks.");
        output.println(" view schedule DATE");
        output.println("   Show deadlines and events for a date.");
        output.println(" mark NUMBER");
        output.println("   Mark a task as done.");
        output.println(" unmark NUMBER");
        output.println("   Put a completed task back on your plate.");
        output.println(" delete NUMBER");
        output.println("   Remove a task.");
        output.println(" help");
        output.println("   Show this command guide.");
        output.println(" bye");
        output.println("   Let me rest my fingers.");
        output.println(" Try a command whenever you're ready!");
    }

    /** Displays an error message caused by a user command. */
    public void showError(String message) {
        output.println(" " + message);
    }

    /** Displays every task in the current list, or an empty-list message. */
    public void showTaskList(TaskList taskList) {
        if (taskList.isEmpty()) {
            output.println(" As empty as your wallet");
            return;
        }

        output.println(" Here's your list of responsibilities:");
        int taskNumber = 1;
        for (Task task : taskList.getTasks()) {
            output.println(" " + taskNumber + "." + task);
            taskNumber++;
        }
    }

    /** Displays tasks whose descriptions contain the given keyword. */
    public void showSearchResults(TaskList taskList, String keyword) throws YourHandException {
        var matchingTaskNumbers = taskList.findTaskNumbersByDescriptionKeyword(keyword);
        if (matchingTaskNumbers.isEmpty()) {
            output.println(" No tasks found containing \"" + keyword + "\".");
            return;
        }
        output.println(" Here are the matching tasks in your list:");
        for (int taskNumber : matchingTaskNumbers) {
            output.println(" " + taskNumber + "." + taskList.getTask(taskNumber));
        }
    }

    /** Displays dated tasks occurring on the selected date. */
    public void showSchedule(TaskList taskList, LocalDate date) throws YourHandException {
        var taskNumbers = taskList.findTaskNumbersForDate(date);
        output.println(" Schedule for " + date + ":");
        if (taskNumbers.isEmpty()) {
            output.println(" No scheduled tasks for this date.");
            return;
        }
        for (int taskNumber : taskNumbers) {
            output.println(" " + taskNumber + "." + taskList.getTask(taskNumber));
        }
    }

    /** Warns that a task with the same description already exists. */
    public void showDuplicateTaskWarning(int existingTaskNumber) {
        output.println(" Heads up: task " + existingTaskNumber + " already has that description."
                + " I'll add this one too.");
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        output.println(" Fine, I'll note this down for you haiz:");
        output.println("   " + task);
        String taskWord = taskCount == 1 ? "task" : "tasks";
        output.println(" That's " + taskCount + " " + taskWord + " on your plate. Have fun :)");
    }

    /** Displays the result of marking or unmarking a task. */
    public void showTaskStatus(Task task, boolean isMarkCommand, boolean wasUpdated) {
        if (isMarkCommand) {
            output.println(wasUpdated
                    ? " Good job for surviving. I'll mark this as done:"
                    : " That task was already done. Double-checking never hurts:");
        } else {
            output.println(wasUpdated
                    ? " Stop scamming me >:("
                    : " Thanks for reminding me of your laziness...");
        }
        output.println("   " + task);
    }

    /** Displays confirmation that a task was removed. */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(" Poof. You ran away from your responsibility:");
        output.println("   " + task);
        String taskWord = taskCount == 1 ? "task" : "tasks";
        output.println(" That's " + taskCount + " " + taskWord + " left on your plate.");
    }

    /** Informs the user that no tasks were loaded from malformed saved data. */
    public void showCorruptFileWarning() {
        output.println(" Man got hacked ggwp");
    }

    /** Informs the user that saved data could not be read. */
    public void showLoadingError() {
        output.println(" Hands can't save you from load failure zzz");
    }
}

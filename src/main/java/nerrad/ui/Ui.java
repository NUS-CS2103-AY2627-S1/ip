package nerrad.ui;

import java.util.List;
import java.util.Scanner;

import nerrad.loan.Loan;
import nerrad.task.Task;

/**
 * Handles all console input and output for the Nerrad chatbot.
 */
public class Ui {
    /** Horizontal line used to separate chatbot messages. */
    private static final String SEPARATOR = "____________________________________________________________";

    /** ASCII-art banner displayed when the chatbot starts. */
    private static final String BANNER = " _   _                         _ \n"
            + "| \\ | | ___ _ __ _ __ __ _  __| |\n"
            + "|  \\| |/ _ \\ '__| '__/ _` |/ _` |\n"
            + "| |\\  |  __/ |  | | | (_| | | (_| |\n"
            + "|_| \\_|\\___|_|  |_|  \\__,_|\\__,_|\n";

    /** Displays the chatbot's startup banner and greeting. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.print(BANNER);
        System.out.println("\nWelcome, wanderer. I'm Nerrad - Darren from the other side.");
        System.out.println("Let's clear the path ahead, one task at a time.");
        System.out.println(SEPARATOR);
    }

    /**
     * Shows the input prompt and reads one command from the console.
     *
     * @param scanner Scanner connected to standard input.
     * @return The entered command, or {@code null} when there is no more input.
     */
    public String readCommand(Scanner scanner) {
        System.out.print("You: ");
        System.out.flush();
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Displays one formatted chatbot response in the console.
     *
     * @param message Response text to display.
     */
    public void showResponse(String message) {
        System.out.println();
        System.out.println(message);
        System.out.println(SEPARATOR);
    }

    /**
     * Returns Nerrad's startup greeting without console-only decoration.
     *
     * @return Greeting for a graphical user interface.
     */
    public String getWelcomeMessage() {
        return "Welcome, wanderer. I'm Nerrad - Darren from the other side.\n"
                + "Let's clear the path ahead, one task at a time.";
    }

    /**
     * Returns Nerrad's farewell message.
     *
     * @return Farewell message.
     */
    public String getGoodbyeMessage() {
        return "  Safe travels. May your path stay clear.";
    }

    /**
     * Returns a formatted task-list message.
     *
     * @param tasks Tasks to include in the message.
     * @return Task-list message.
     */
    public String getTaskListMessage(List<Task> tasks) {
        StringBuilder message = new StringBuilder("  Here is the path ahead:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n  ").append(i + 1).append(".").append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Returns a formatted message containing tasks matching a keyword.
     *
     * @param matchingTasks Tasks to include in the message.
     * @return Matching-task message.
     */
    public String getMatchingTasksMessage(List<Task> matchingTasks) {
        StringBuilder message = new StringBuilder("  Here are the matching waypoints:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            message.append("\n  ").append(i + 1).append(".").append(matchingTasks.get(i));
        }
        return message.toString();
    }

    /**
     * Returns a formatted confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param taskCount Updated number of tasks.
     * @return Added-task confirmation.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "  Placed on your path:\n"
                + "    " + task + "\n"
                + "  Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns a formatted confirmation that a task was marked as done.
     *
     * @param task Task that was marked.
     * @return Marked-task confirmation.
     */
    public String getTaskMarkedMessage(Task task) {
        return "  A bright step forward - this task is complete:\n    " + task;
    }

    /**
     * Returns a formatted confirmation that a task was marked as not done.
     *
     * @param task Task that was unmarked.
     * @return Unmarked-task confirmation.
     */
    public String getTaskUnmarkedMessage(Task task) {
        return "  No worries. This task can wait a little longer:\n    " + task;
    }

    /**
     * Returns a formatted confirmation that a task was deleted.
     *
     * @param task Task that was deleted.
     * @param taskCount Updated number of tasks.
     * @return Deleted-task confirmation.
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return "  Released from your path:\n"
                + "    " + task + "\n"
                + "  Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns a formatted loan-list message.
     *
     * @param loans Loans to include in the message.
     * @return Loan-list message.
     */
    public String getLoanListMessage(List<Loan> loans) {
        StringBuilder message = new StringBuilder("  Here is your loan ledger:");
        for (int i = 0; i < loans.size(); i++) {
            message.append("\n  ").append(i + 1).append(".").append(loans.get(i));
        }
        return message.toString();
    }

    /**
     * Returns a formatted confirmation that a loan was added.
     *
     * @param loan Loan that was added.
     * @param loanCount Updated number of loans.
     * @return Added-loan confirmation.
     */
    public String getLoanAddedMessage(Loan loan, int loanCount) {
        return "  Kept safely in your ledger:\n"
                + "    " + loan + "\n"
                + "  You now have " + loanCount + " loan records.";
    }

    /**
     * Returns a formatted confirmation that a loan was settled.
     *
     * @param loan Loan that was settled.
     * @return Settled-loan confirmation.
     */
    public String getLoanSettledMessage(Loan loan) {
        return "  Your ledger is balanced for this loan:\n    " + loan;
    }

    /**
     * Returns a short summary for the graphical user interface header.
     *
     * @param taskCount Number of tasks.
     * @param completedTaskCount Number of completed tasks.
     * @param loanCount Number of loan records.
     * @return Compact task and loan summary.
     */
    public String getDashboardSummary(int taskCount, int completedTaskCount, int loanCount) {
        return taskCount + " tasks  ·  " + completedTaskCount + " complete  ·  "
                + loanCount + " loan records";
    }

    /**
     * Returns a formatted user-facing error message.
     *
     * @param message Explanation of the problem.
     * @return Error message.
     */
    public String getErrorMessage(String message) {
        return "  OOPS!!! " + message;
    }

    /**
     * Returns the error message used when saved data cannot be loaded.
     *
     * @return Loading error message.
     */
    public String getLoadingErrorMessage() {
        return "  OOPS!!! I could not load your saved tasks.";
    }

    /** Displays the error used when saved data cannot be loaded. */
    public void showLoadingError() {
        System.out.println("  OOPS!!! I could not load your saved tasks.");
        System.out.println(SEPARATOR);
    }
}


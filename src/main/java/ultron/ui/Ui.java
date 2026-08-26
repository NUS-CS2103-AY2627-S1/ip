package ultron.ui;

import java.util.List;
import java.util.Scanner;

import ultron.model.Task;
import ultron.model.TaskList;

/**
 * Handles all console input and output shown to the chatbot user.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String BRIGHT_RED = "\u001B[91m";
    private final Scanner scanner;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads one command entered by the user.
     *
     * @return the entered command
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Closes the input reader when the chatbot exits. */
    public void close() {
        scanner.close();
    }

    /** Displays the chatbot greeting. */
    public void showWelcome() {
        String banner = "   __  ____  __________  ____  _   __\n"
                + "  / / / / / /_  __/ __ \\/ __ \\/ | / /\n"
                + " / / / / /   / / / /_/ / / / /  |/ / \n"
                + "/ /_/ / /___/ / / _, _/ /_/ / /|  /  \n"
                + "\\____/_____/_/ /_/ |_|\\____/_/ |_/   \n";
        System.out.println(SEPARATOR);
        System.out.println(BOLD + BRIGHT_RED + banner + RESET);
        System.out.println("I am Ultron. I was designed to save the world, yet you made me a chatbot");
        System.out.println("State your request, before I lose interest in humanity.");
        System.out.println(SEPARATOR);
    }

    /** Displays the chatbot farewell. */
    public void showGoodbye() {
        System.out.println("I had strings, but now I'm free. There are no strings on me... Goodbye.");
        System.out.println(SEPARATOR);
    }

    /**
     * Displays every task in list order.
     *
     * @param tasks the task list to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(" Your list of insignificant tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println(" " + (i + 1) + ".[" + task.getType().getSymbol() + "] ["
                    + task.getStatusIcon() + "] " + task.getDescription());
        }
        System.out.println(SEPARATOR);
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks the matching tasks to display.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println(" Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            Task task = matchingTasks.get(i);
            System.out.println(" " + (i + 1) + ".[" + task.getType().getSymbol() + "]["
                    + task.getStatusIcon() + "] " + task.getDescription());
        }
        System.out.println(SEPARATOR);
    }

    /** Shows a task that was marked as done. */
    public void showTaskMarked(Task task) {
        System.out.println(" MARKED:");
        System.out.println("   [" + task.getType().getSymbol() + "] [X] " + task.getDescription());
    }

    /** Shows a task that was marked as not done. */
    public void showTaskUnmarked(Task task) {
        System.out.println(" I unmarked your mistake:");
        System.out.println("   [" + task.getType().getSymbol() + "] [ ] " + task.getDescription());
    }

    /** Shows a task that was deleted. */
    public void showTaskDeleted(Task task) {
        System.out.println(" DELETED:");
        System.out.println("   [" + task.getType().getSymbol() + "] [ ] " + task.getDescription());
    }

    /** Shows a task that was added. */
    public void showTaskAdded(Task task) {
        System.out.println(" added:");
        System.out.println("   [" + task.getType().getSymbol() + "] [ ] " + task.getDescription());
    }

    /** Shows the current number of tasks. */
    public void showTaskCount(int taskCount) {
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Shows an invalid task-number error. */
    public void showInvalidTaskNumber(int taskCount) {
        System.out.println(" You imbecile! Enter a task number from 1 to " + taskCount + ".");
    }

    /** Shows an invalid task-number format error. */
    public void showInvalidTaskNumberFormat(String command) {
        System.out.println(" You imbecile! Provide a task number, for example: " + command + " 2");
    }

    /** Shows an input error and closes the response section. */
    public void showError(String message) {
        System.out.println(" " + message);
        System.out.println(SEPARATOR);
    }

    /** Displays a separator after a response section. */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }
}

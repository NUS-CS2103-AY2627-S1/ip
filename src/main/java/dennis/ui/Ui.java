package dennis.ui;

import java.util.Scanner;

import dennis.task.Task;

/**
 * Handles every interaction with the user: reading command lines from
 * standard input and printing Dennis's replies to standard output.
 *
 * <p>Design note: keeping all {@code System.in}/{@code System.out} details in
 * one class means the rest of the program never touches the console directly.
 * That makes the command logic easier to read and test, and it would let a
 * different front end (for example a GUI) be swapped in by replacing only this
 * class.</p>
 */
public class Ui {
    /** Horizontal rule Dennis prints to separate some of its replies. */
    private static final String LINE =
            "_____________________________________________________";

    /** Source of user input; wraps standard input for the whole session. */
    private final Scanner scanner;

    /** Creates a UI that reads user input from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Prints the startup banner and greeting, followed by a divider line. */
    public void showWelcome() {
        String banner = " ____                   _     \n"
                + "|  _ \\  ___ _ __  _ __ (_)___ \n"
                + "| | | |/ _ \\ '_ \\| '_ \\| / __|\n"
                + "| |_| |  __/ | | | | | | \\__ \\\n"
                + "|____/ \\___|_| |_|_| |_|_|___/\n";
        showMessage(banner, "Purrrrr, I'm awake. I'm Dennis, do you want to play?");
        showLine();
    }

    /** Prints the farewell message shown when the user types {@code bye}. */
    public void showGoodbye() {
        showMessage("Time for a cat-nap. Bye!");
    }

    /**
     * Reports whether the user has entered another line of input.
     *
     * @return {@code true} if another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next raw command line from the user.
     *
     * @return the line exactly as typed (no trimming)
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the horizontal divider line on its own. */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Prints the given lines of text to the user, each on its own line.
     * Called with no arguments it prints nothing.
     *
     * @param messages the lines to display, in order
     */
    public void showMessage(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

    /**
     * Prints an error in Dennis's standard style ({@code HISS!! ...}).
     *
     * @param message explanation of what went wrong
     */
    public void showError(String message) {
        showMessage("HISS!! " + message);
    }

    /**
     * Confirms that a task was added and reports the new list size.
     *
     * @param task      the task that was added
     * @param taskCount the number of tasks now in the list
     */
    public void showAddedTask(Task task, int taskCount) {
        showMessage("Purrfect, I've added this task:",
                "  " + task,
                "Meow you have " + taskCount + " tasks!");
    }

    /**
     * Confirms that a task was removed and reports the new list size.
     *
     * @param task      the task that was removed
     * @param taskCount the number of tasks left in the list
     */
    public void showRemovedTask(Task task, int taskCount) {
        showMessage("Scratched this task off the list:",
                "  " + task,
                "Meow there are " + taskCount + " tasks left!");
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task that was marked
     */
    public void showMarkedTask(Task task) {
        showMessage("Paw-sitively marked as done:", "  " + task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task that was unmarked
     */
    public void showUnmarkedTask(Task task) {
        showMessage("Un-fur-tunately, back on the list:", "  " + task);
    }
}

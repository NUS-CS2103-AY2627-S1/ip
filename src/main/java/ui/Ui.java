package puyo.ui;

import java.util.Scanner;

import puyo.task.Task;
import puyo.task.TaskList;

/**
 * Handles user interface interactions, including reading inputs and displaying output messages.
 */
public class Ui {

    private static final String LINE = "─".repeat(70);
    private static final String SOLID_LINE = "━".repeat(70);
    private static final String BANNER =
                    "██████╗ ██╗   ██╗██╗   ██╗ ██████╗\n"
                  + "██╔══██╗██║   ██║╚██╗ ██╔╝██╔═══██╗\n"
                  + "██████╔╝██║   ██║ ╚████╔╝ ██║   ██║\n"
                  + "██╔═══╝ ██║   ██║  ╚██╔╝  ██║   ██║\n"
                  + "██║     ╚██████╔╝   ██║   ╚██████╔╝\n"
                  + "╚═╝      ╚═════╝    ╚═╝    ╚═════╝";

    private final Scanner scanner;

    /**
     * Constructs a {@code Ui} instance initialized with standard system input scanner.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome banner and introductory message to the user.
     */
    public void showWelcome() {
        System.out.println(SOLID_LINE);
        System.out.println(BANNER);
        System.out.println(SOLID_LINE);
        System.out.println("Hello! My name is Puyo!");
        System.out.println("I'm a penguin!");
        System.out.println("Regardless, I can do a lot of things to make your life easier!");
        System.out.println(SOLID_LINE);
        System.out.println("  Type something! (type 'bye' to exit)");
        System.out.println(SOLID_LINE);
    }

    /**
     * Displays the farewell message when the user exits the application.
     */
    public void showBye() {
        System.out.println("Okay. See you again! Bye!");
    }

    /**
     * Displays a thin horizontal separator line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Displays a generic output message.
     *
     * @param message The text message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message formatted with an error prefix.
     *
     * @param message The error message text.
     */
    public void showError(String message) {
        System.out.println(" Error: " + message);
    }

    /**
     * Displays the full list of tasks to the user.
     *
     * @param tasks The {@code TaskList} containing tasks to be printed.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("(Note: X means you haven't done the task yet, ✓ means you have done the task)");
        if (tasks.isEmpty()) {
            System.out.println(" (no tasks yet)");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + tasks.get(i));
            }
        }
    }

    /**
     * Displays a confirmation message after a new task has been added.
     *
     * @param task The task that was added.
     * @param totalTasks The current total number of tasks in the list.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        System.out.println(" Don't forget to do this!");
        System.out.println(" " + task);
        System.out.println(" (You now have " + totalTasks + " tasks to do! Good luck!)");
    }

    /**
     * Reads a line of command input from the user.
     *
     * @return Trimmed command input string.
     */
    public String readCommand() {
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    /**
     * Closes the underlying input scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Displays tasks that match a search query.
     *
     * @param tasks The {@code TaskList} containing matching tasks.
     */
    public void showFoundTasks(TaskList tasks) {
        if (tasks.isEmpty()) {
            System.out.println(" No matching tasks found!");
        } else {
            System.out.println(" Here are the matching tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + tasks.get(i));
            }
        }
    }
}

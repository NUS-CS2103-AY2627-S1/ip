package florkofcows.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import florkofcows.task.Task;
import florkofcows.task.TaskList;

/**
 * User interaction helper for console I/O.
 *
 * All public methods provide simple console operations used by the application
 * to read input and display messages. Keep logic here minimal; UI should not
 * perform business logic.
 */
public class Ui {
    private final Scanner scanner = new Scanner(System.in);

    public void showLine() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Displays the welcome message and banner to the user.
     *
     * @param banner the ASCII art banner to display.
     */
    public void showWelcome(String banner) {
        showLine();
        System.out.println(banner);
        System.out.println("Wassup! Got something on your mind today?");
        System.out.println();
        System.out.println("Here are some useful commands!");
        System.out.println("Usage: <command> [arguments]");
        System.out.println("1. list               - View tasks");
        System.out.println("2. todo <description> - Add a todo");
        System.out.println("3. event <name> /from <start> /to <end> - Schedule event");
        System.out.println("4. mark/unmark <index> - Toggle task");
        System.out.println("5. delete <index>     - Remove task");
        System.out.println("6. find <keyword>     - Search tasks");
        System.out.println("7. on <yyyy-MM-dd>    - View date");
        System.out.println("8. bye                - Exit program");
        System.out.println("9. tag <index> <tag> - Label task");
        System.out.println("10. date format       - Show date format");
        System.out.println();
        System.out.println("Date format: yyyy-MM-dd");
        showLine();
    }

    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the exit message to the user.
     */
    public void showBye() {
        showLine();
        System.out.println("See ya!");
        showLine();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message the error message to display.
     */
    public void showError(String message) {
        showLine();
        System.out.println(" Eh lock in!!! " + message);
        showLine();
    }

    /**
     * Displays a loading error message to the user, indicating that saved tasks
     * could not be loaded and that a fresh start will be used instead.
     *
     * @param message the error message to display.
     */
    public void showLoadingError(String message) {
        System.out.println(" Warning: couldn't load saved tasks (" + message + "). Starting fresh.");
    }

    /**
     * Displays the list of tasks to the user.
     *
     * @param tasks the list of tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        showLine();
        if (tasks.isEmpty()) {
            System.out.println(" Shiok eh! No tasks.");
        } else {
            System.out.println(" Shag sia.");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + tasks.get(i).toString());
            }
        }
        showLine();
    }

    /**
     * Displays a message indicating that a task has been added, along with the
     * task details and the updated total count of tasks.
     *
     * @param newTask    the task that was added.
     * @param totalCount the updated total count of tasks.
     */
    public void showTaskAdded(Task newTask, int totalCount) {
        showLine();
        System.out.println(" Okayyy added!");
        System.out.println("   " + newTask.toString());
        System.out.println(" You now have " + totalCount + " tasks. Jiayous!");
        showLine();
    }

    /**
     * Displays a message indicating that a task has been deleted, along with the
     * task details and the updated remaining count of tasks.
     *
     * @param removedTask    the task that was deleted.
     * @param remainingCount the updated remaining count of tasks.
     */
    public void showTaskDeleted(Task removedTask, int remainingCount) {
        showLine();
        System.out.println(" Cans. Deleted!");
        System.out.println("   " + removedTask.toString());
        System.out.println(" Shiok, you have " + remainingCount + " tasks left. Jiayous!");
        showLine();
    }

    /**
     * Displays a message indicating that a task has been marked as done, along
     * with the task details.
     *
     * @param text the string representation of the task that was marked.
     */
    public void showTaskMarked(String text) {
        showLine();
        System.out.println(" Marked it!");
        System.out.println("   " + text);
        showLine();
    }

    /**
     * Displays a message indicating that a task has been unmarked (marked as not
     * done), along with the task details.
     *
     * @param text the string representation of the task that was unmarked.
     */
    public void showTaskUnmarked(String text) {
        showLine();
        System.out.println(" Unmarked it!");
        System.out.println("   " + text);
        showLine();
    }

    /**
     * Displays a message indicating that tags were added to a task.
     *
     * @param text the string representation of the tagged task.
     */
    public void showTaskTagged(String text) {
        showLine();
        System.out.println(" Tag added!");
        System.out.println("   " + text);
        showLine();
    }

    /**
     * Displays the list of tasks that match a specific date to the user.
     *
     * @param matches   the list of tasks that match the specified date.
     * @param queryDate the date for which matching tasks are displayed.
     */
    public void showTasksOnDate(ArrayList<Task> matches, LocalDate queryDate) {
        showLine();
        if (matches.isEmpty()) {
            System.out.println(" Nothing happening on " + queryDate + ".");
        } else {
            System.out.println(" Here's what's on " + queryDate + ":");
            for (int i = 0; i < matches.size(); i++) {
                System.out.println(" " + (i + 1) + "." + matches.get(i).toString());
            }
        }
        showLine();
    }

    /**
     * Displays the list of tasks that match a specific keyword to the user.
     *
     * @param matches the list of tasks that match the specified keyword.
     */
    public void showMatchingTasks(ArrayList<Task> matches) {
        showLine();
        if (matches.isEmpty()) {
            System.out.println(" Don't have this task eh!");
        } else {
            System.out.println(" Nah here:");
            for (int i = 0; i < matches.size(); i++) {
                System.out.println(" " + (i + 1) + "." + matches.get(i).toString());
            }
        }
        showLine();
    }
}

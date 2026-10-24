package clara.ui;

import java.util.List;
import java.util.Scanner;

import clara.exception.ClaraException;
import clara.task.Task;
import clara.task.TaskList;

// NOTE: AI-assisted OOP refactoring. See CITATIONS.md [C-008].

/**
 * Handles interaction with the user via console input and output.
 */
public class Ui {
    private static final String CLARA_HEADER = "\n[Clara] ";
    private static final String USER_HEADER = ">> ";
    private final Scanner scanner;

    /**
     * Constructs a Ui instance using standard system input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prompts the user and reads a line of input.
     *
     * @return the command string entered by the user
     */
    public String readCommand() {
        System.out.print(USER_HEADER);
        return this.scanner.nextLine();
    }

    // AI-assisted output formatting. See CITATIONS.md [C-003]

    /**
     * Displays Clara's greeting message to the user.
     */
    public void greet() {
        String greetingLine = CLARA_HEADER + "Hello. I'm Clara. How can I assist you?";
        System.out.println(greetingLine);
    }

    /**
     * Displays Clara's goodbye message to the user.
     */
    public void end() {
        String exitLine = CLARA_HEADER + "Goodbye. See you again.";
        System.out.println(exitLine);
    }

    /**
     * Displays task loading start message.
     */
    public void showLoadingTasks() {
        System.out.println(CLARA_HEADER + "Loading tasks...");
    }

    /**
     * Displays task loading completion message.
     */
    public void showLoadingDone() {
        System.out.println(CLARA_HEADER + "Done.");
    }

    /**
     * Displays an error message when loading tasks from disk fails.
     *
     * @param ex the exception that occurred during loading
     */
    public void showLoadingError(ClaraException ex) {
        System.out.println(
                CLARA_HEADER
                        + "Something went wrong while loading tasks:\n"
                        + ex.getMessage()
                        + "\nTasks will not be loaded, and we'll start clean.");
    }

    /**
     * Displays an error message to the console.
     *
     * @param ex the exception containing the error message
     */
    public void displayErrorAtConsole(ClaraException ex) {
        System.out.println(CLARA_HEADER + "Something went wrong:\n" + ex.getMessage() + "\nTry again.");
    }

    /**
     * Displays all tasks currently in the task list.
     *
     * @param taskList the list of tasks to display
     */
    public void showTaskList(TaskList taskList) {
        if (taskList.size() > 0) {
            System.out.println(CLARA_HEADER + "Here are your tasks in a list:");
            for (int i = 0; i < taskList.size(); i++) {
                String currentTask = (i + 1) + ". " + taskList.getTasks().get(i);
                System.out.println(currentTask);
            }
        } else {
            System.out.println(CLARA_HEADER + "There are no tasks. Yay...?");
        }
    }

    /**
     * Displays a confirmation when a task is added.
     *
     * @param task       the added task
     * @param totalTasks the total number of tasks after addition
     */
    public void showTaskAdded(Task task, int totalTasks) {
        String echoString =
                CLARA_HEADER + "added:\n" + task.toString() + " (task #" + totalTasks + ")";
        System.out.println(echoString);
    }

    /**
     * Displays a confirmation when a task is marked as completed.
     *
     * @param task      the marked task
     * @param taskIndex the 1-based index of the task
     */
    public void showTaskMarked(Task task, int taskIndex) {
        String echoString = CLARA_HEADER + "marked task " + taskIndex + ":\n" + task.toString();
        System.out.println(echoString);
    }

    /**
     * Displays a confirmation when a task is unmarked.
     *
     * @param task      the unmarked task
     * @param taskIndex the 1-based index of the task
     */
    public void showTaskUnmarked(Task task, int taskIndex) {
        String echoString = CLARA_HEADER + "unmarked task " + taskIndex + ":\n" + task.toString();
        System.out.println(echoString);
    }

    /**
     * Displays a confirmation when a task is deleted.
     *
     * @param task           the deleted task
     * @param taskIndex      the 1-based index of the deleted task
     * @param remainingTasks the number of remaining tasks
     */
    public void showTaskDeleted(Task task, int taskIndex, int remainingTasks) {
        String echoString =
                CLARA_HEADER
                        + "deleted task "
                        + taskIndex
                        + ":\n"
                        + task
                        + " ("
                        + remainingTasks
                        + " task"
                        + (remainingTasks == 1 ? " " : "s ")
                        + "remain)";
        System.out.println(echoString);
    }

    /**
     * Displays the search results for tasks matching a query.
     *
     * @param taskList        the task list containing the tasks
     * @param matchingIndices the 0-based indices of the matching tasks
     * @param query           the search query string
     */
    public void showFindResults(TaskList taskList, List<Integer> matchingIndices, String query) {
        System.out.println(CLARA_HEADER + "Finding tasks with task names containing: " + query);
        for (int i = 0; i < matchingIndices.size(); i++) {
            int originalIndex = matchingIndices.get(i);
            System.out.println((originalIndex + 1) + ". " + taskList.getTasks().get(originalIndex));
        }
        System.out.println(
                matchingIndices.size() + " task" + (matchingIndices.size() == 1 ? " " : "s ") + " found.");
    }
}

package nabmak.ui;

import nabmak.task.Task;
import nabmak.task.TaskList;

/**
 * Provides the dialgoues for the application to interact with the user.
 */
public class Ui {
    private static final String BANNER = "________________________________________";
    private static final String INTRO = "Yo im Nabmak.";
    private static final String GREETING = "Whatchu wanna do?";

    /**
     * Prints the greeting dialogue.
     */
    public void showHi() {
        System.out.println(BANNER);
        System.out.println(INTRO);
        System.out.println(GREETING);
        System.out.println(BANNER);
    }
    /**
     * Prints the goodbye dialogue.
     */
    public void showBye() {
        System.out.println(BANNER);
        System.out.println("BYE!");
    }

    /**
     * Prints error message for invalid user input.
     *
     * @param message error message
     */
    public void showError(String message) {
        System.out.println(BANNER);
        System.out.println("TOUGH! " + message);
        System.out.println(BANNER);
    }

    /**
     * Prints list of existing tasks.
     *
     * @param tasks existing list of tasks
     */
    public void showList(TaskList tasks) {
        System.out.println(BANNER);
        System.out.println("Your TODOLIST");

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
        System.out.println(BANNER);
    }

    /**
     * Prints result when task is marked.
     *
     * @param task task being marked
     */
    public void showDone(Task task) {
        System.out.println("Good that task is DONE");
        System.out.println(task);
        System.out.println(BANNER);
    }

    /**
     * Prints result when task is unmarked.
     *
     * @param task task being unmarked
     */
    public void showUndone(Task task) {
        System.out.println("Tuff this task not done :(");
        System.out.println(task);
        System.out.println(BANNER);
    }

    /**
     * Prints dialogue for adding a new task and number of tasks in list.
     *
     * @param task task being added
     * @param size number of tasks in list
     */
    public void showAdded(Task task, int size) {
        System.out.println(BANNER);
        System.out.println("Ok new task!");
        System.out.println(task);
        System.out.println("Now got " + size + " tasks.");
        System.out.println(BANNER);
    }
    /**
     * Prints dialogue for deleting a task and number of tasks remaining in list.
     *
     * @param deleted task being deleted
     * @param size number of tasks remaining in list
     */
    public void showDeleted(Task deleted, int size) {
        System.out.println(BANNER);
        System.out.println("Noted. I've remove the task:");
        System.out.println(deleted);
        System.out.println("Now got " + size + " tasks.");
        System.out.println(BANNER);
    }

    /**
     * Prints dialogue for tasks which match user inputted keyword.
     *
     * @param matches list of tasks which match keyword
     */
    public void showFind(TaskList matches) {
        System.out.println(BANNER);
        System.out.println("You looking for these?:");

        for (int i = 0; i < matches.size(); i++) {
            System.out.println((i + 1) + ". " + matches.get(i));
        }

        System.out.println(BANNER);
    }
}

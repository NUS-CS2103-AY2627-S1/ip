package esther.ui;

import java.util.List;
import java.util.Scanner;

import esther.task.Task;
import esther.task.TaskList;

/**
 * Handles interactions between Esther and the user.
 */
public class Ui {
    private static final String DIVIDER =
            "___________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return Command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows Esther's welcome message.
     */
    public void showWelcome() {
        showDivider();
        showToUser(
                "Heyyyy! I'm your favourite assistant Esther.",
                "What can I do for you today?");
        showDivider();
    }

    /**
     * Shows Esther's goodbye message.
     */
    public void showGoodbye() {
        showDivider();
        showToUser("Byeee! Hope to see you soooon!");
        showDivider();
    }

    /**
     * Shows an error encountered while loading saved tasks.
     */
    public void showLoadingError() {
        showDivider();
        showToUser(
                "Uh-oh, your saved tasks wouldn't load, "
                        + "so we're starting with a fresh list.");
        showDivider();
    }

    /**
     * Shows all stored tasks and the number that remain incomplete.
     *
     * @param tasks Tasks to show.
     */
    public void showTaskList(TaskList tasks) {
        int tasksLeft = tasks.countNotDone();

        showDivider();

        if (tasksLeft == 0) {
            showToUser("WOW! You have no tasks left, good work!");
        } else if (tasksLeft <= 5) {
            String taskWord = getTaskWord(tasksLeft);
            showToUser(String.format(
                    "Only %d %s left! Should be a piece of cake for you!",
                    tasksLeft,
                    taskWord
            ));
        } else {
            showToUser(String.format(
                    "%d tasks left?? What have you been doing this whole "
                            + "time?? You better focus up!",
                    tasksLeft
            ));
        }

        for (int i = 0; i < tasks.size(); i++) {
            showToUser(String.format(
                    "%d.%s",
                    i + 1,
                    tasks.get(i)
            ));
        }

        showDivider();
    }

    /**
     * Shows tasks whose descriptions match a keyword.
     *
     * @param matchingTasks Tasks matching the search keyword.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showDivider();

        if (matchingTasks.isEmpty()) {
            showToUser(
                    "I searched everywhere, but that task is playing hide-and-seek.");
        } else {
            showToUser("Found them! Here's what matched:");

            for (int i = 0; i < matchingTasks.size(); i++) {
                showToUser(String.format(
                        "%d.%s",
                        i + 1,
                        matchingTasks.get(i)
                ));
            }
        }

        showDivider();
    }

    /**
     * Shows a task that has been marked as done.
     *
     * @param task Task that was marked.
     */
    public void showTaskMarked(Task task) {
        showDivider();
        showToUser(
                "Nice! I've marked this task as done:",
                " " + task);
        showDivider();
    }

    /**
     * Shows a task that has been marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        showDivider();
        showToUser(
                "Oops, back onto the unfinished pile it goes:",
                " " + task);
        showDivider();
    }

    /**
     * Shows a task that has been deleted.
     *
     * @param task Task that was deleted.
     * @param taskCount Number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showDivider();
        showToUser(
                "Poof! This task is officially gone:",
                "  " + task,
                "Your list now has " + taskCount + " "
                        + getTaskWord(taskCount)
                        + ". I'm keeping track, obviously.");
        showDivider();
    }

    /**
     * Shows a task that has been added.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks stored.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showDivider();
        showToUser(
                "Yesss, consider it added:",
                "  " + task,
                "Your list now has " + taskCount + " "
                        + getTaskWord(taskCount)
                        + ". I'm keeping track, obviously.");
        showDivider();
    }

    /**
     * Shows a command-processing error.
     *
     * @param message Error message to show.
     */
    public void showError(String message) {
        showDivider();
        showToUser(message);
        showDivider();
    }

    /**
     * Shows an error encountered while saving tasks.
     */
    public void showSaveError() {
        showDivider();
        showToUser(
                "Uh-oh, I couldn't save your tasks. "
                        + "Please try again before they escape!");
        showDivider();
    }

    /**
     * Shows a command processor response with the standard CLI dividers.
     *
     * @param messages Response messages to show in order.
     */
    public void showResponse(List<String> messages) {
        showDivider();
        showToUser(messages.toArray(String[]::new));
        showDivider();
    }

    /**
     * Shows one or more messages to the user.
     *
     * @param messages Messages to show, in display order.
     */
    public void showToUser(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Shows the divider used between responses.
     */
    private void showDivider() {
        showToUser(DIVIDER);
    }

    /**
     * Returns the singular or plural form of task for a count.
     *
     * @param taskCount Number of tasks.
     * @return Singular or plural task label.
     */
    private String getTaskWord(int taskCount) {
        return taskCount == 1 ? "task" : "tasks";
    }
}

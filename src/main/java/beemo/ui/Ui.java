package beemo.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import beemo.task.Task;

/**
 * Handles command input and text displayed to the user.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = " ____  _____ _____ __  __  ___  \n"
            + "| __ )| ____| ____|  \\/  |/ _ \\ \n"
            + "|  _ \\|  _| |  _| | |\\/| | | | |\n"
            + "| |_) | |___| |___| |  | | |_| |\n"
            + "|____/|_____|_____|_|  |_|\\___/ ";

    private final Scanner scanner;
    private final Consumer<String> output;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
    }

    /**
     * Creates a user interface that sends each output line to the supplied consumer.
     *
     * @param output Destination for output lines.
     */
    public Ui(Consumer<String> output) {
        scanner = null;
        this.output = output;
    }

    /**
     * Displays Beemo's greeting.
     */
    public void showWelcome() {
        showLine();
        output.accept(BANNER);
        output.accept("Hello! I'm Beemo! (˶ᵔ ᵕ ᵔ˶)");
        output.accept("What can I do for you? ᵔ ᵕ ᵔ");
        showLine();
    }

    /**
     * Returns whether another command is available.
     *
     * @return True if another command can be read, otherwise false.
     */
    public boolean hasNextCommand() {
        return scanner != null && scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return Next command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the horizontal response divider.
     */
    public void showLine() {
        output.accept(DIVIDER);
    }

    /**
     * Displays Beemo's farewell.
     */
    public void showGoodbye() {
        output.accept("Beemo signing off! See you next time! ૮ ˶ᵔ ᵕ ᵔ˶ ა");
    }

    /**
     * Displays guidance for all supported commands.
     */
    public void showHelp() {
        output.accept("Here are the commands I understand:");
        output.accept("  todo DESCRIPTION");
        output.accept("  deadline DESCRIPTION /by yyyy-MM-dd");
        output.accept("  event DESCRIPTION /from START /to END");
        output.accept("  list");
        output.accept("  find KEYWORD");
        output.accept("  mark TASK_NUMBER");
        output.accept("  unmark TASK_NUMBER");
        output.accept("  delete TASK_NUMBER");
        output.accept("  help");
        output.accept("  bye");
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            output.accept("Your task list is empty. Add a task whenever you're ready!");
            return;
        }
        output.accept("Here are the tasks in your list:");
        showTasks(tasks);
    }

    /**
     * Displays tasks that match a search keyword.
     *
     * @param tasks Matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        output.accept("Here are the matching tasks in your list:");
        showTasks(tasks);
    }

    /**
     * Displays tasks as a numbered list.
     *
     * @param tasks Tasks to display.
     */
    private void showTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output.accept((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task Task that was marked.
     */
    public void showMarkedTask(Task task) {
        output.accept("Yay good job! I've marked this task as done:");
        output.accept("  " + task);
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showUnmarkedTask(Task task) {
        output.accept("No problem! I've marked this task as not done yet:");
        output.accept("  " + task);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task Task that was deleted.
     * @param taskCount Number of tasks remaining.
     */
    public void showDeletedTask(Task task, int taskCount) {
        output.accept("All right, I've removed this task:");
        output.accept("  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks after the addition.
     */
    public void showAddedTask(Task task, int taskCount) {
        output.accept("Got it! I've added this task for you:");
        output.accept("  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays an error message.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        output.accept(message);
    }

    private void showTaskCount(int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        output.accept("Now you have " + taskCount + " " + taskNoun + " in the list.");
    }
}

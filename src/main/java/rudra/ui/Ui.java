package rudra.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import rudra.task.Task;

/**
 * Handles console input and output for the chatbot.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " ____            _            \n"
            + "|  _ \\ _   _  __| |_ __ __ _ \n"
            + "| |_) | | | |/ _` | '__/ _` |\n"
            + "|  _ <| |_| | (_| | | | (_| |\n"
            + "|_| \\_\\\\__,_|\\__,_|_|  \\__,_|\n";

    private final Scanner scanner;
    private final Consumer<String> output;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.output = System.out::println;
    }

    /**
     * Creates a UI that sends chatbot messages to the given output handler.
     *
     * @param output Handler for chatbot output messages.
     */
    public Ui(Consumer<String> output) {
        this.scanner = null;
        this.output = output;
    }

    /**
     * Prints the startup banner and greeting.
     */
    public void showWelcome() {
        show(LINE);
        show(BANNER);
        show("Yo! Rudra's online.");
        show("What are we getting done today?");
        show(LINE);
    }

    /**
     * Returns whether another input line is available.
     *
     * @return True if another command can be read.
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads the next user command from standard input.
     *
     * @return Raw command text.
     */
    public String readCommand() {
        return this.scanner.nextLine();
    }

    /**
     * Prints the farewell message shown when the chatbot exits.
     */
    public void showGoodbye() {
        show("Catch you on the flip side! Rudra signing off.");
        show(LINE);
    }

    /**
     * Prints the current task list.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        show("Here's what's on your radar:");
        printTaskCollection(tasks);
        show(LINE);
    }

    /**
     * Prints confirmation that deadlines were sorted, followed by the updated task list.
     *
     * @param tasks Tasks in their updated order.
     */
    public void showTasksSorted(List<Task> tasks) {
        show("All set - your deadlines now run from earliest to latest.");
        showTaskList(tasks);
    }

    /**
     * Prints the tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks Matching tasks to display.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            show("No matches on the radar, homie.");
            show(LINE);
            return;
        }

        show("Here are the matches I found:");
        printTaskCollection(matchingTasks);
        show(LINE);
    }

    private void printTaskCollection(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            show((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints the confirmation shown after a task is added.
     *
     * @param task Added task.
     * @param updatedTaskCount Task count after the addition.
     */
    public void showTaskAdded(Task task, int updatedTaskCount) {
        show("You got it, homie - task locked in!");
        show(task.toString());
        showTaskCount(updatedTaskCount);
        show(LINE);
    }

    /**
     * Prints the confirmation shown after a task is marked done.
     *
     * @param task Updated task.
     */
    public void showTaskMarked(Task task) {
        show("Let's gooo! Another one handled:");
        show(task.toString());
        show(LINE);
    }

    /**
     * Prints the confirmation shown after a task is marked not done.
     *
     * @param task Updated task.
     */
    public void showTaskUnmarked(Task task) {
        show("No stress - this task is back in play:");
        show(task.toString());
        show(LINE);
    }

    /**
     * Prints the confirmation shown after a task is deleted.
     *
     * @param removedTask Deleted task.
     * @param updatedTaskCount Task count after the deletion.
     */
    public void showTaskDeleted(Task removedTask, int updatedTaskCount) {
        show("Poof! This task is outta here:");
        show(removedTask.toString());
        showTaskCount(updatedTaskCount);
        show(LINE);
    }

    /**
     * Prints an error message followed by the divider line.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        show("Whoa! " + message);
        show(LINE);
    }

    /**
     * Prints a startup warning about corrupted saved tasks.
     *
     * @param skippedTaskCount Number of skipped tasks.
     */
    public void showCorruptedTaskWarning(int skippedTaskCount) {
        show("Heads up! I skipped " + skippedTaskCount + " corrupted saved task(s).");
        show(LINE);
    }

    /**
     * Prints the startup message shown when loading saved tasks fails.
     *
     * @param message Storage error message.
     */
    public void showLoadingError(String message) {
        show(message);
        show("No stress - I'm starting with an empty task list instead.");
        show(LINE);
    }

    private void showTaskCount(int taskCount) {
        String taskLabel = taskCount == 1 ? "task" : "tasks";
        show("Your radar now has " + taskCount + " " + taskLabel + ".");
    }

    private void show(String message) {
        this.output.accept(message);
    }
}

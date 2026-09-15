package duke.ui;

import java.util.ArrayList;
import java.util.stream.IntStream;

import duke.task.Task;

/**
 * A {@link Ui} implementation that accumulates output into a single string instead of printing to stdout.
 *
 * <p>This is used by the JavaFX GUI, where responses should be displayed in the UI rather than written to the console.
 */
public class GuiUi extends Ui {
    private final StringBuilder output = new StringBuilder();
    private boolean hasError;

    /**
     * Returns and clears the accumulated output.
     *
     * @return Output accumulated since the last call.
     */
    public String consumeOutput() {
        String result = output.toString().trim();
        output.setLength(0);
        hasError = false;
        return result;
    }

    /**
     * Returns whether an error was added to this UI's output.
     *
     * @return {@code true} if the output contains an error, otherwise {@code false}.
     */
    public boolean hasError() {
        return hasError;
    }

    private void appendLine(String line) {
        if (output.length() > 0) {
            output.append('\n');
        }
        output.append(line);
    }

    private void appendLines(String... lines) {
        for (String line : lines) {
            appendLine(line);
        }
    }

    @Override
    public void showError(String message) {
        hasError = true;
        appendLine(message);
    }

    @Override
    public void showMessage(String message) {
        appendLine(message);
    }

    @Override
    public void showGoodbye() {
        appendLine("Bye! Hope to see you again soon!");
    }

    @Override
    public void showTaskAdded(Task task, int taskCount) {
        appendLines(
                "Got it! Here's the task you added:",
                "  " + task,
                "Now you have " + taskCount + " tasks in your list!"
        );
    }

    @Override
    public void showTaskRemoved(Task task, int taskCount) {
        appendLines(
                "Noted. This task has been removed:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list."
        );
    }

    @Override
    public void showMarkedTask(int taskNumber, boolean isDone) {
        appendLine("Gary marked task " + taskNumber + (isDone ? " as done!" : " as undone!"));
    }

    @Override
    public void showTaskList(ArrayList<Task> tasks) {
        appendLine("These are the tasks you have in your list!");
        IntStream.range(0, tasks.size())
                .forEach(index -> appendLine((index + 1) + ". " + tasks.get(index)));
    }

    @Override
    public void showMatchingTasks(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            appendLine("No matching tasks found.");
            return;
        }

        appendLine("Here are the matching tasks in your list:");
        IntStream.range(0, tasks.size())
                .forEach(index -> appendLine((index + 1) + ". " + tasks.get(index)));
    }
}

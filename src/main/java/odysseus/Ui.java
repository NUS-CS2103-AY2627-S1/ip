package odysseus;

import java.io.PrintStream;
import java.util.List;

/**
 * Handles messages shown to the traveler.
 */
public class Ui {
    private final PrintStream output;

    /**
     * Creates a UI that writes to the given output stream.
     */
    public Ui(PrintStream output) {
        this.output = output;
    }

    /**
     * Shows a task list in its existing one-based order.
     *
     * @param tasks tasks to display.
     * @throws OdysseusException if a task cannot be retrieved for display.
     */
    public void showTaskList(TaskList tasks) throws OdysseusException {
        if (tasks.getTaskCount() == 0) {
            output.println("My ship's log is clear, traveler.");
            return;
        }
        output.println("Here are the tasks on our voyage, traveler:");
        for (int number = 1; number <= tasks.getTaskCount(); number++) {
            output.println(number + ". " + tasks.getTask(number));
        }
    }

    /**
     * Shows the tasks that match a keyword, retaining their task numbers.
     */
    public void showMatchingTasks(TaskList tasks, List<Integer> matchingNumbers) throws OdysseusException {
        output.println("Here are the matching tasks in your list:");
        for (int taskNumber : matchingNumbers) {
            output.println(taskNumber + ". " + tasks.getTask(taskNumber));
        }
    }

    /**
     * Shows a user-facing message.
     */
    public void show(String message) {
        output.println(message);
    }

    /**
     * Shows a task with its preceding message.
     */
    public void showTask(String message, Task task) {
        show(message);
        output.println("  " + task);
    }
}

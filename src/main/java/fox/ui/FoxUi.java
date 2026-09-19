package fox.ui;

import java.io.PrintStream;
import java.util.List;

import fox.task.Task;
import fox.task.TaskList;

/** Owns Fox's console presentation while leaving command logic in domain classes. */
public class FoxUi {
    /** The separator printed around Fox's console responses. */
    public static final String SEPARATOR = "    ____________________________________________________________";
    private static final String HAPPY_EXPRESSION = "  /\\_/\\\n ( ^ᴗ^ )\n  > ^ <";
    private static final String SAD_EXPRESSION = "  /\\_/\\\n ( •︵• )\n  > ^ <";
    private final PrintStream output;

    /** Creates a console presenter for Fox responses. */
    public FoxUi() {
        output = null;
    }

    /** Creates a presenter that writes responses to the supplied stream. */
    public FoxUi(PrintStream output) {
        this.output = output;
    }

    /**
     * Prints the task list in one-based display order.
     *
     * @param taskList the tasks to display
     */
    public void showTasks(TaskList taskList) {
        printLine(SEPARATOR);
        printHappyExpression();
        printLine("     Here are the tasks in your list:");
        int number = 1;
        for (Task task : taskList) {
            printLine("     " + number++ + "." + task);
        }
        printLine(SEPARATOR);
    }

    /**
     * Prints tasks found by a keyword search while retaining their original task numbers.
     *
     * @param taskList the complete task list
     * @param matchingTaskNumbers the one-based numbers of matching tasks
     */
    public void showFoundTasks(TaskList taskList, List<Integer> matchingTaskNumbers) {
        printLine(SEPARATOR);
        printHappyExpression();
        if (matchingTaskNumbers.isEmpty()) {
            printLine("     There are no matching tasks.");
        } else {
            printLine("     Here are the matching tasks in your list:");
            int taskNumber = 1;
            for (Task task : taskList) {
                if (matchingTaskNumbers.contains(taskNumber)) {
                    printLine("     " + taskNumber + "." + task);
                }
                taskNumber++;
            }
        }
        printLine(SEPARATOR);
    }

    /**
     * Prints the standard success response for a newly added task.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks after the addition
     */
    public void showAdded(Task task, int taskCount) {
        printLine(SEPARATOR);
        printHappyExpression();
        printLine("     Got it. I've added this task:");
        printLine("       " + task);
        printLine("     Now you have " + taskCount + " tasks in the list.");
        printLine(SEPARATOR);
    }

    /**
     * Prints a task mutation response.
     *
     * @param task the task whose completion status changed
     * @param isDone whether the task was marked done
     */
    public void showMarked(Task task, boolean isDone) {
        printLine(SEPARATOR);
        printHappyExpression();
        printLine(isDone ? "     Nice! I've marked this task as done:"
                : "     OK, I've marked this task as not done yet:");
        printLine("       " + task);
        printLine(SEPARATOR);
    }

    /**
     * Prints a deletion response.
     *
     * @param task the task that was removed
     * @param remaining the number of tasks left after deletion
     */
    public void showDeleted(Task task, int remaining) {
        printLine(SEPARATOR);
        printLine("     Noted. I've removed this task:");
        printLine("       " + task);
        printLine("     Now you have " + remaining + " tasks in the list.");
        printLine(SEPARATOR);
    }

    /**
     * Prints a command error using Fox's sad expression.
     *
     * @param message the error text to display
     */
    public void showError(String message) {
        printLine(SEPARATOR);
        printLine(SAD_EXPRESSION);
        printLine("     " + message);
        printLine(SEPARATOR);
    }

    private void printHappyExpression() {
        printLine(HAPPY_EXPRESSION);
    }

    private void printLine(String message) {
        PrintStream target = output == null ? System.out : output;
        target.println(message);
    }
}

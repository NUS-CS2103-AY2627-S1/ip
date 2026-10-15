package alexa;

import java.util.List;
import java.util.Scanner;

/**
 * Handles Alexa's console input and formats messages for console and graphical user interfaces.
 */
public class Ui {
    /** A visual divider used to frame chatbot messages. */
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner input;

    /** Creates a console user interface that reads commands from standard input. */
    public Ui() {
        input = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} when a command can be read.
     */
    public boolean hasNextCommand() {
        return input.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return The entered command.
     */
    public String readCommand() {
        return input.nextLine();
    }

    /** Shows Alexa's greeting. */
    public void showGreeting() {
        showResponse(getGreetingMessage());
    }

    /** Shows a response in Alexa's standard console message frame. */
    public void showResponse(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    /** Shows a recoverable error when saved tasks cannot be loaded. */
    public void showLoadingError() {
        showResponse("OOPS!!! I could not load your saved tasks. Starting with an empty list.");
    }

    /**
     * Returns Alexa's greeting message.
     *
     * @return The greeting message.
     */
    public String getGreetingMessage() {
        return "                 A L E X A\nHello! I'm Alexa.\nWhat can I do for you?";
    }

    /**
     * Returns Alexa's farewell message.
     *
     * @return The farewell message.
     */
    public String getFarewellMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Returns the formatted list of all tasks.
     *
     * @param tasks The tasks to display.
     * @return The formatted task list.
     */
    public String getTaskListMessage(TaskList tasks) {
        StringBuilder message = new StringBuilder("Here are the tasks in your list:");
        appendTasks(message, tasks.asList());
        return message.toString();
    }

    /**
     * Returns the formatted list of tasks matching a keyword.
     *
     * @param matchingTasks The matching tasks to display.
     * @return The formatted matching task list.
     */
    public String getMatchingTasksMessage(List<Task> matchingTasks) {
        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:");
        appendTasks(message, matchingTasks);
        return message.toString();
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task The added task.
     * @param taskCount The number of tasks after addition.
     * @return The addition confirmation.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns confirmation that a task's completion status changed.
     *
     * @param task The updated task.
     * @param isDone Whether the task is now complete.
     * @return The status-change confirmation.
     */
    public String getTaskStatusMessage(Task task, boolean isDone) {
        String status = isDone ? "Nice! I've marked this task as done:"
                : "Ok, I've marked this task as not done yet:";
        return status + "\n  " + task;
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task The deleted task.
     * @param taskCount The number of tasks remaining.
     * @return The deletion confirmation.
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return "Noted. I've removed this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.";
    }

    /**
     * Returns an error message in Alexa's standard tone.
     *
     * @param message The explanation of the error.
     * @return The formatted error message.
     */
    public String getErrorMessage(String message) {
        return "OOPS!!! " + message;
    }

    /** Appends a numbered task list to a message. */
    private void appendTasks(StringBuilder message, List<Task> tasks) {
        for (int index = 0; index < tasks.size(); index++) {
            message.append('\n').append(index + 1).append('.').append(tasks.get(index));
        }
    }
}
package ducky;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Formats all messages shown to the user and reads commands from standard input.
 * Callers (the text UI or the GUI) decide how to display the formatted messages.
 */
public class Ui {
    private static final String BANNER = "DUCKY\n";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Formats the chatbot's welcome message.
     *
     * @return the welcome message.
     */
    public String showWelcome() {
        return BANNER + "\n"
                + "Hello! I'm Ducky.\n"
                + "Quack! I’m ready to help you keep your tasks in order.\n"
                + "What would you like to do?";
    }

    /**
     * Reads one command from the user.
     *
     * @return the user's command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Formats the chatbot's exit message.
     *
     * @return the exit message.
     */
    public String showExitMessage() {
        return "Quack! Goodbye! See you in the pond soon!";
    }

    /**
     * Formats the task-added confirmation.
     *
     * @param task the added task.
     * @param taskCount the number of tasks after adding.
     * @return the task-added confirmation.
     */
    public String showTaskAdded(Task task, int taskCount) {
        return "Quack! I’ve added this task to your pond:\n"
                + "  " + task + "\n"
                + String.format("Now you have %d tasks in the list.", taskCount);
    }

    /**
     * Formats all tasks.
     *
     * @param tasks the tasks to display.
     * @return the formatted task list.
     */
    public String showTasks(TaskList tasks) {
        String taskLines = IntStream.range(0, tasks.size())
                .mapToObj(index -> String.format("%d.%s", index + 1, tasks.get(index)))
                .collect(Collectors.joining("\n"));
        return "Here are all the tasks swimming in your pond:\n" + taskLines;
    }

    /**
     * Formats tasks matching a search keyword.
     *
     * @param matchingTasks the tasks matching the search keyword.
     * @return the formatted matching task list.
     */
    public String showMatchingTasks(List<Task> matchingTasks) {
        String taskLines = IntStream.range(0, matchingTasks.size())
                .mapToObj(index -> String.format("%d.%s", index + 1, matchingTasks.get(index)))
                .collect(Collectors.joining("\n"));
        return "Here are the tasks matching your search:\n" + taskLines;
    }

    /**
     * Formats a task-marked-as-done confirmation.
     *
     * @param task the updated task.
     * @return the task-marked-as-done confirmation.
     */
    public String showTaskMarkedAsDone(Task task) {
        return "Nice quack! This task is complete:\n"
                + "  " + task;
    }

    /**
     * Formats a task-unmarked confirmation.
     *
     * @param task the updated task.
     * @return the task-unmarked confirmation.
     */
    public String showTaskUnmarked(Task task) {
        return "No worries — this task is back in the pond:\n"
                + "  " + task;
    }

    /**
     * Formats a task-deleted confirmation.
     *
     * @param task the deleted task.
     * @param taskCount the number of tasks after deletion.
     * @return the task-deleted confirmation.
     */
    public String showTaskDeleted(Task task, int taskCount) {
        return "Quack! I’ve removed this task from your pond:\n"
                + "  " + task + "\n"
                + String.format("Now you have %d tasks in the list.", taskCount);
    }

    /**
     * Formats an error message.
     *
     * @param message the user-facing error message.
     * @return the formatted error message.
     */
    public String showError(String message) {
        return "QUACK! " + message;
    }
}

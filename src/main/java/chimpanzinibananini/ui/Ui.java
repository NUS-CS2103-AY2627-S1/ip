package chimpanzinibananini.ui;

import java.util.List;
import java.util.Scanner;

import chimpanzinibananini.task.Task;
import chimpanzinibananini.task.TaskList;

/**
 * Handles all console input and output for the chatbot.
 */
public class Ui implements AutoCloseable {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
    }

    /**
     * Displays the greeting shown when the chatbot starts.
     */
    public void showWelcome() {
        System.out.println("BOMBARDIRO CROCODILO!!!");
        System.out.println("ChimpanziniBananini has entered the chat.");
        System.out.println("What can I cook for you, sigma?");
        System.out.println();
    }

    /**
     * Returns whether another command is available from standard input.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays every task with its one-based number.
     */
    public void showTaskList(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }
    /** Displays matching tasks with numbering local to the search results. */
    public void showFoundTasks(List<Task> matchingTasks) {
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println((i + 1) + ". " + matchingTasks.get(i));
        }
    }
    /** Displays the standard response after adding a task. */
    public void showAddedTask(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the response after marking a task.
     */
    public void showMarkedTask(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Displays the response after deleting a task.
     */
    public void showDeletedTask(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays a command-validation error.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error that prevented tasks from being saved.
     */
    public void showSavingError(String message) {
        System.out.println("I couldn't save your tasks: " + message);
    }

    /**
     * Displays an error that prevented the chatbot from starting.
     */
    public void showLoadingError(String message) {
        System.out.println("I couldn't load the task file: " + message);
    }

    /**
     * Displays the farewell shown when the chatbot stops.
     */
    public void showGoodbye() {
        System.out.println("Tung tung tung sahur... I'm outta here 💀");
    }

    /**
     * Displays a response produced by the chatbot.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Releases the scanner used for console input.
     */
    @Override
    public void close() {
        scanner.close();
    }
}

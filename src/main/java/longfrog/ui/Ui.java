package longfrog.ui;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Consumer;

import longfrog.task.Task;

/**
 * Handles all console input and output for Longfrog.
 */
public class Ui {
    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner;
    private final Consumer<String> messageConsumer;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        messageConsumer = System.out::println;
    }

    /**
     * Creates a UI that sends messages to the supplied consumer.
     *
     * @param messageConsumer the destination for displayed messages
     */
    public Ui(Consumer<String> messageConsumer) {
        scanner = new Scanner(System.in);
        this.messageConsumer = Objects.requireNonNull(messageConsumer);
    }

    /** Returns the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the farewell message. */
    public void showExitMessage() {
        showMessage("Pond secured. Rest well—ribbit.");
    }

    /** Displays a message when saved tasks cannot be loaded. */
    public void showLoadingError() {
        showMessage("Warning: Save-file decoding failed. Booting with an empty task set.");
    }

    /** Displays a message when tasks cannot be saved. */
    public void showSavingError() {
        showMessage("I/O error: Task data could not be persisted to the pond archive.");
    }

    /** Displays a message on the console. */
    public void showMessage(String message) {
        messageConsumer.accept(message);
    }

    /**
     * Displays tasks as a one-based numbered list.
     *
     * @param tasks the tasks to display
     */
    public void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            showMessage((i + 1) + ": " + tasks.get(i));
        }
    }

    /**
     * Displays a contextual error for a task number outside the current list.
     *
     * @param index the invalid zero-based task index
     * @param taskCount the number of available tasks
     * @param action the command action that could not be performed
     */
    public void showInvalidTaskIndex(int index, int taskCount, String action) {
        assert index >= 0 : "Parsed task indexes must be non-negative";
        assert taskCount >= 0 : "Task count must not be negative";
        assert action != null && !action.isBlank() : "A task action must be supplied";

        if (taskCount == 0) {
            showMessage("The pond is clear—there are no tasks to " + action + ".");
            return;
        }

        showMessage("There is no task " + (index + 1)
                + ". Choose a task number from 1 to " + taskCount + ".");
    }

    /** Displays the separator used around command responses. */
    public void showLine() {
        showMessage(SEPARATOR);
    }

    /** Displays an empty line. */
    public void showEmptyLine() {
        showMessage("");
    }

    /** Closes the input reader after the application exits. */
    public void close() {
        scanner.close();
    }
}

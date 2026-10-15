package megatron.ui;

import java.util.ArrayList;
import java.util.function.Consumer;

import megatron.task.Task;

/** Handles messages displayed to the user. */
public class Ui {
    /** The name displayed for the chatbot persona. */
    public static final String CHATBOT_NAME = "Megatron";

    private final Consumer<String> messageConsumer;

    /** Creates a UI that displays messages in the terminal. */
    public Ui() {
        this(System.out::println);
    }

    /**
     * Creates a UI that sends messages to the supplied display function.
     *
     * @param messageConsumer function that displays one chatbot message
     */
    public Ui(Consumer<String> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    /** Displays the application greeting. */
    public void showWelcome() {
        messageConsumer.accept("At last. Megatron is online.\n"
                + "Your schedule will be conquered. State your command.");
    }

    /** Displays the tasks in list order. */
    public void showTasks(ArrayList<Task> tasks) {
        messageConsumer.accept(formatTasks("Behold the tasks under my command:", tasks));
    }

    /** Displays an error message. */
    public void showError(String message) {
        messageConsumer.accept("Foolish error: " + message + " Obey the command format.");
    }

    /** Displays the exit message. */
    public void showGoodbye() {
        messageConsumer.accept("Retreat if you must. Megatron will be waiting.");
    }
    /** Displays a successful addition. */
    public void showAdded(Task task, int count) {
        messageConsumer.accept("It is done. I have added this task to my conquest:\n  " + task
                + "\nYour empire now contains " + count + " tasks.");
    }
    /** Displays a successful deletion. */
    public void showDeleted(Task task, int count) {
        messageConsumer.accept("Crushed. This task has been erased from existence:\n  " + task
                + "\nOnly " + count + " tasks remain under my command.");
    }
    /** Displays a successful mark or unmark operation. */
    public void showMarked(Task task, boolean marked) {
        String message = marked
                ? "Excellent. This task has been conquered:"
                : "You dare undo my progress? The task is pending once more:";
        messageConsumer.accept(message + "\n  " + task);
    }
    /** Displays tasks matching a search keyword. */
    public void showMatchingTasks(ArrayList<Task> tasks) {
        messageConsumer.accept(formatTasks("My search has uncovered these matching targets:", tasks));
    }

    /** Displays a successful task rescheduling. */
    public void showSnoozed(Task task) {
        messageConsumer.accept("Time bends to my will. This task has been rescheduled:\n  " + task);
    }

    /** Formats a task collection with numbered entries under the supplied heading. */
    private String formatTasks(String heading, ArrayList<Task> tasks) {
        StringBuilder message = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            message.append(System.lineSeparator()).append(i + 1).append('.').append(tasks.get(i));
        }
        return message.toString();
    }
}

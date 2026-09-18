package aether.ui;

import java.util.Scanner;

import aether.task.Task;

/** Handles all text displayed to the user and reads commands from the console. */
public class Ui {
    private static final String NAME = "Aether";
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "    _         _   _               \n"
            + "   / \\   ___ | |_| |__   ___ _ __ \n"
            + "  / _ \\ / _ \\| __| '_ \\ / _ \\ '__|\n"
            + " / ___ \\  __/| |_| | | |  __/ |   \n"
            + "/_/   \\_\\___|\\__|_| |_|\\___|_|   \n";

    private final Scanner scanner;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Shows the chatbot greeting. */
    public void showWelcome() {
        showResponse(BANNER + getWelcomeMessage());
    }

    /** Returns whether another complete command can be read before the input stream ends. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads one complete command line from the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Shows an error message while keeping the chatbot ready for another command. */
    public void showError(String message) {
        showResponse(message);
    }

    /** Returns the chatbot greeting without console-specific divider lines. */
    public String getWelcomeMessage() {
        return "Hello! I'm " + NAME + ", your calm guide through a busy day.\n"
                + "What shall we bring into focus?";
    }

    /** Returns confirmation that a task was added. */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "Task captured and brought into focus:\n  " + task
                + getTaskCountMessage(taskCount);
    }

    /** Returns confirmation that a task was marked as done. */
    public String getTaskMarkedMessage(Task task) {
        return "Excellent - this task is complete:\n  " + task;
    }

    /** Returns confirmation that a task was marked as not done. */
    public String getTaskUnmarkedMessage(Task task) {
        return "No problem - this task is back in motion:\n  " + task;
    }

    /** Returns confirmation that a task was deleted. */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return "Consider it cleared from your orbit:\n  " + task
                + getTaskCountMessage(taskCount);
    }

    /** Returns the task list after it has been ordered by date. */
    public String getTasksSortedMessage(String sortedTasks) {
        return "Your schedule is aligned by date.\n" + sortedTasks;
    }

    /** Shows the farewell message. */
    public void showGoodbye() {
        showResponse(getGoodbyeMessage());
    }

    /** Returns the chatbot farewell without console-specific divider lines. */
    public String getGoodbyeMessage() {
        return "Until next time. May your day stay clear and focused.";
    }

    /** Prints one chatbot response between horizontal divider lines. */
    public void showResponse(String response) {
        System.out.println(LINE);
        System.out.println(response);
        System.out.println(LINE);
    }

    /** Closes the input stream after the chatbot exits. */
    public void close() {
        scanner.close();
    }

    /** Formats a task count with the correct singular or plural noun. */
    private String getTaskCountMessage(int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        return "\nNow you have " + taskCount + " " + taskNoun + " in the list.";
    }
}

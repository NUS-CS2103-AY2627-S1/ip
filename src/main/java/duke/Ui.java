package duke;

import java.util.ArrayList;
import java.util.Scanner;

/** Handles all console input and output for Furina. */
class Ui {
    private static final String SEPARATOR = "____________________________________________________________";

    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Furina's welcome message. */
    void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println("    F U R I N A");
        System.out.println("Hello! I'm Furina.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }

    /** Returns the next command, or {@code null} when input ends. */
    String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Displays the standard command separator. */
    void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Displays all tasks with their one-based list numbers. */
    void showTaskList(ArrayList<Task> tasks) {
        System.out.println("    Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("    " + (i + 1) + "." + tasks.get(i));
        }
    }

    /** Displays confirmation after adding a task. */
    void showAddedTask(Task task, int taskCount) {
        System.out.println("    Got it. I've added this task:");
        System.out.println("      " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays confirmation after changing a task's completion status. */
    void showUpdatedTask(Task task, boolean isDone) {
        if (isDone) {
            System.out.println("    Nice! I've marked this task as done:");
        } else {
            System.out.println("    OK, I've marked this task as not done yet:");
        }
        System.out.println("      " + task);
    }

    /** Displays confirmation after deleting a task. */
    void showDeletedTask(Task task, int taskCount) {
        System.out.println("    Noted. I've removed this task:");
        System.out.println("      " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays the farewell message. */
    void showGoodbye() {
        showLine();
        System.out.println("    Bye. Hope to see you again soon!");
        showLine();
    }

    /** Displays a user-facing error message. */
    void showError(String message) {
        System.out.println("    OOPS!!! " + message);
    }

    /** Displays a response returned by the shared command handler. */
    void showResponse(String response) {
        for (String line : response.split("\\R")) {
            String indentation = line.startsWith("[") ? "      " : "    ";
            System.out.println(indentation + line);
        }
    }
}

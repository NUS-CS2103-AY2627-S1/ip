package mrchatbot.ui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import mrchatbot.task.Task;
import mrchatbot.task.TaskList;

/**
 * Handles all interactions with the user.
 * This includes: 1. user inputs, 2. printing outputs
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "                       _           _   _           _   \n"
            + " _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ \n"
            + "| '_ ` _ \\| '__|  / __| '_ \\ / _` | __| '_ \\ / _ \\| __|\n"
            + "| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ \n"
            + "|_| |_| |_|_|     \\___|_| |_|\\__,_|\\__|_.__/ \\___/ \\__|\n";

    private final Scanner scanner;
    private final PrintStream output;
    private final String name;

    /**
     * Creates a UI helper that reads user commands from standard input.
     */
    public Ui(String name) {
        scanner = new Scanner(System.in);
        output = System.out;
        this.name = name;
    }

    /**
     * Creates a UI helper that writes chatbot responses to the given output stream.
     */
    public Ui(String name, PrintStream output) {
        scanner = new Scanner(System.in);
        this.output = output;
        this.name = name;
    }

    /**
     * Returns true if another user command is available.
     */
    public boolean hasNextInput() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next user command.
     */
    public String readInput() {
        return scanner.nextLine();
    }

    /**
     * Shows the startup banner and greeting.
     */
    public void showWelcome() {
        output.println(LINE);
        output.println(BANNER);
        output.println("Hello! I'm Mr Chatbot, your personal companion.");
        output.println("What can I do for you, Mr " + name + "?");
        output.println(LINE);
    }

    /**
     * Shows a divider before or after a command response.
     */
    public void showLine() {
        output.println(LINE);
    }

    /**
     * Shows the exit message.
     */
    public void showBye() {
        output.println("Bye Mr " + name + ". Hope to see you again soon!");
    }

    /**
     * Shows an error message.
     */
    public void showError(String message) {
        output.println(message);
    }

    /**
     * Shows the user all accepted commands and their formats.
     */
    public void showHelp() {
        output.println("Accepted inputs:");
        output.println("todo <description>");
        output.println("deadline <description> /by <yyyy-mm-dd>");
        output.println("event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>");
        output.println("recurring <description> /every day");
        output.println("recurring <description> /every week [/on <weekday>]");
        output.println("list");
        output.println("mark <task number>");
        output.println("unmark <task number>");
        output.println("delete <task number>");
        output.println("find <keyword>");
        output.println("bye");
        output.println("help");
    }

    /**
     * Shows all tasks in the list.
     */
    public void showTaskList(TaskList tasks) {
        output.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows the message for a newly added task.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Got it. I've added this task:");
        output.println("  " + task);
        output.println("Now you have " + taskCount + " " + taskWord(taskCount) + " in the list.");
    }

    /**
     * Shows the message for a marked task.
     */
    public void showTaskMarked(Task task) {
        output.println("Nice! I've marked this task as done:");
        output.println("  " + task);
    }

    /**
     * Shows the message for an unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        output.println("OK, I've marked this task as not done yet:");
        output.println("  " + task);
    }

    /**
     * Shows the message for a deleted task.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("Noted. I've removed this task:");
        output.println("  " + task);
        output.println("Now you have " + taskCount + " " + taskWord(taskCount) + " in the list.");
    }

    /**
     * Shows all tasks that match a find command.
     */
    public void showMatchingTasks(ArrayList<Task> matchingTasks) {
        output.println("Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.println((i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Returns task or tasks depending on the task count.
     */
    private String taskWord(int taskCount) {
        return taskCount == 1 ? "task" : "tasks";
    }
}

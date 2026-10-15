package doge.ui;

import java.util.List;
import java.util.Scanner;

import doge.model.Task;
import doge.model.TaskList;

/** Handles console input and output for the Doge application. */
public class UI {
    private static final String BANNER = "       ____   ___   ____ _____\n"
            + "      |  _ \\ / _ \\ / ___| ____|\n"
            + "      | | | | | | | |  _|  _|\n"
            + "      | |_| | |_| | |_| | |___\n"
            + "      |____/ \\___/ \\____|_____|";
    private static final String SEPARATOR = "    ____________________________________________________________";

    private final Scanner scanner = new Scanner(System.in);

    /** Displays the application banner and welcome message. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("    Much hello! I'm Doge, your task sidekick.");
        System.out.println("    What shall we fetch today?");
        System.out.println(SEPARATOR);
    }

    /** Reads and trims one command from standard input. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "bye";
    }

    /** Displays a message between the application's separators. */
    public void printMessage(String message) {
        System.out.println(SEPARATOR);
        System.out.println(message);
        System.out.println(SEPARATOR);
    }

    /** Displays confirmation that a task was marked as completed. */
    public void showTaskMarked(Task task) {
        System.out.println(SEPARATOR);
        System.out.println("    Much progress! This task is now done:");
        System.out.println("      " + task);
        System.out.println(SEPARATOR);
    }

    /** Displays confirmation that a task was marked as incomplete. */
    public void showTaskUnmarked(Task task) {
        System.out.println(SEPARATOR);
        System.out.println("    No zoomies yet: this task is back on the list:");
        System.out.println("      " + task);
        System.out.println(SEPARATOR);
    }

    /** Displays the application's goodbye message. */
    public void showGoodbye() {
        System.out.println(SEPARATOR);
        System.out.println("    Much farewell! See you on the next walk.");
        System.out.println(SEPARATOR);
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(SEPARATOR);
        System.out.println("    Much add! New task fetched: " + task);
        System.out.println("    Your pack now has " + taskCount + " tasks.");
        System.out.println(SEPARATOR);
    }

    /** Displays all tasks in the supplied task list. */
    public void printTaskList(TaskList tasks) {
        System.out.println(SEPARATOR);
        System.out.println(tasks.toString());
        System.out.println(SEPARATOR);
    }

    /**
     * Displays the tasks matching a search keyword.
     *
     * @param matchingTasks tasks selected by the search.
     */
    public String printMatchingTasks(List<Task> matchingTasks) {
        StringBuilder output = new StringBuilder("    Sniff sniff... here are the matching tasks:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.append("\n    ").append(i + 1).append(".").append(matchingTasks.get(i));
        }
        return output.toString();
    }
}

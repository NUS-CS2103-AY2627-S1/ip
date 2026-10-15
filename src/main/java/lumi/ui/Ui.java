package lumi.ui;

import java.util.List;
import java.util.Scanner;

import lumi.exception.LumiException;
import lumi.task.Task;

/**
 * Handles all console input and output for Lumi.
 * Keeping presentation details here lets the application coordinate behavior
 * without depending directly on {@link System#in} or {@link System#out}.
 */
public class Ui {
    /** Divider shown around each response block. */
    private static final String DIVIDER =
            "____________________________________________________________";

    /** Lumi logo shown when the application starts. */
    private static final String BANNER = " _      _   _ __  __ ___ \n"
            + "| |    | | | |  \\/  |_ _|\n"
            + "| |    | | | | |\\/| || | \n"
            + "| |___ | |_| | |  | || | \n"
            + "|_____| \\___/|_|  |_|___|\n";

    /** Reads commands from standard input. */
    private final Scanner scanner;

    /** Creates a console UI connected to standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Shows Lumi's startup banner and greeting. */
    public void showWelcome() {
        showDivider();
        System.out.print(BANNER);
        System.out.println("Hello. I'm Lumi, your guide through the little things.");
        System.out.println("Let's chart one clear step at a time.");
        System.out.println("What would you like to put on the map?");
    }

    /**
     * Reports whether another command is available.
     *
     * @return true when another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next user command.
     *
     * @return the next command, without surrounding whitespace.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Shows the divider used around console response blocks. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Shows a complete response produced by Lumi's shared command engine.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /** Shows the farewell message. */
    public void showGoodbye() {
        System.out.println(" Until next time. Your tasks are safe here.");
    }

    /**
     * Shows all tasks with their one-based list numbers.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println(" Here's your current constellation:");
        showNumberedTasks(tasks);
    }

    /**
     * Shows tasks whose descriptions match a find keyword.
     *
     * @param matchingTasks Matching tasks to display.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println(" These tasks match your signal:");
        showNumberedTasks(matchingTasks);
    }

    /**
     * Shows confirmation that a task was marked as complete.
     *
     * @param task Task that was marked as complete.
     */
    public void showMarked(Task task) {
        System.out.println(" A little brighter. This task is complete:");
        System.out.println("   " + task);
    }

    /**
     * Shows confirmation that a task was marked as incomplete.
     *
     * @param task Task that was marked as incomplete.
     */
    public void showUnmarked(Task task) {
        System.out.println(" Back in orbit. This task is active again:");
        System.out.println("   " + task);
    }

    /**
     * Shows the removed task and updated task count.
     *
     * @param task Task that was removed.
     * @param taskCount Number of remaining tasks.
     */
    public void showDeleted(Task task, int taskCount) {
        System.out.println(" Cleared from the map. I've removed this task:");
        System.out.println("   " + task);
        showTaskCount(taskCount);
    }

    /**
     * Shows the added task and updated task count.
     *
     * @param task Task that was added.
     * @param taskCount Number of stored tasks.
     */
    public void showAdded(Task task, int taskCount) {
        System.out.println(" It's on the map. I've added this task:");
        System.out.println("   " + task);
        showTaskCount(taskCount);
    }

    /**
     * Shows an input or command error in Lumi's response format.
     *
     * @param message User-friendly error explanation.
     */
    public void showError(String message) {
        System.out.println(" " + LumiException.ERROR_PREFIX + message);
    }

    /**
     * Shows a warning when malformed saved records were skipped.
     *
     * @param skippedLineCount Number of malformed records skipped.
     */
    public void showSkippedFileLines(int skippedLineCount) {
        System.out.println("I found " + skippedLineCount
                + " unreadable line(s) in your saved tasks and skipped them.");
    }

    /** Shows a warning when saved tasks cannot be loaded. */
    public void showLoadingError() {
        System.out.println("I couldn't open your saved tasks, so we're starting with a clear map.");
    }

    /** Shows a warning when changed tasks cannot be saved. */
    public void showSavingError() {
        System.out.println(" " + LumiException.ERROR_PREFIX
                + "I couldn't save the latest task changes.");
    }

    /** Shows the task count after an addition or deletion. */
    private void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println(" Your map now holds " + taskCount + " " + taskWord + ".");
    }

    /** Shows tasks with one-based numbers in their displayed order. */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }
}

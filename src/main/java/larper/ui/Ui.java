package larper.ui;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import larper.task.FindResult;
import larper.task.Task;
import larper.task.TaskList;

/**
 * Handles all console input and output for Larper.
 */
public class Ui {
    private static final String LINE = "_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_";
    private static final String BANNER = " _\n"
            + "| |       __ _   _ __   _ __     ___   _ __\n"
            + "| |      / _` | | '__| | '_ \\   / _ \\ | '__|\n"
            + "| |___  | (_| | | |    | |_) | |  __/ | |\n"
            + "|_____|  \\__,_| |_|    | .__/   \\___| |_|\n"
            + "                       |_|\n";

    private Scanner scanner;

    /**
     * Creates a console user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether the console has another line of user input.
     */
    public boolean hasNextInput() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next line entered by the user.
     */
    public String readInput() {
        return scanner.nextLine();
    }

    /**
     * Shows Larper's greeting and command prompt.
     */
    public void showWelcome() {
        System.out.println(LINE);
        System.out.print(formatWelcomeMessage());
        System.out.println(LINE);
    }

    /**
     * Shows the separator line used between console messages.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Shows Larper's exit message.
     */
    public void showExit() {
        showMessage(formatExitMessage());
    }

    /**
     * Shows all tasks in their current order.
     */
    public void showTaskList(TaskList tasks) {
        showMessage(formatTaskList(tasks));
    }

    /**
     * Shows the prompt that asks what phrase to find.
     */
    public void showFindPrompt() {
        showMessage(formatFindPrompt());
    }

    /**
     * Shows tasks found by the latest find search.
     */
    public void showFindResults(ArrayList<FindResult> results) {
        showMessage(formatFindResults(results));
    }

    /**
     * Shows the message for a mark command without a valid task number.
     */
    public void showInvalidMarkNumber() {
        showMessage(formatInvalidMarkNumber());
    }

    /**
     * Shows the message for an unmark command without a valid task number.
     */
    public void showInvalidUnmarkNumber() {
        showMessage(formatInvalidUnmarkNumber());
    }

    /**
     * Shows the message for a command that refers to a task number not in the list.
     */
    public void showMissingTaskNumber() {
        showMessage(formatMissingTaskNumber());
    }

    /**
     * Shows the task that has just been marked as done.
     */
    public void showMarkedTask(Task task) {
        showMessage(formatMarkedTask(task));
    }

    /**
     * Shows the task that has just been marked as not done.
     */
    public void showUnmarkedTask(Task task) {
        showMessage(formatUnmarkedTask(task));
    }

    /**
     * Shows the task that has just been deleted and the new task count.
     */
    public void showDeletedTask(Task task, int taskCount) {
        showMessage(formatDeletedTask(task, taskCount));
    }

    /**
     * Shows the task that has just been added and the new task count.
     */
    public void showAddedTask(Task task, int taskCount) {
        showMessage(formatAddedTask(task, taskCount));
    }

    /**
     * Shows an error message followed by a separator line.
     */
    public void showError(String message) {
        showMessage(message);
    }

    /**
     * Shows a complete Larper message followed by a separator line.
     *
     * @param message Text to show before the separator line.
     */
    public void showMessage(String message) {
        System.out.println(message);
        showLine();
    }

    /**
     * Returns Larper's greeting and command prompt.
     */
    public static String formatWelcomeMessage() {
        return BANNER + "Larper has entered the chat.\n\n"
                + " Productivity expert (allegedly).\n"
                + " Let's get productive. Or at least look productive.\n\n"
                + " Drop a command.\n\n";
    }

    /**
     * Returns a compact guide to the supported commands.
     */
    public static String formatHelpMessage() {
        return " Commands Larper understands (allegedly):\n"
                + " todo DESCRIPTION\n"
                + " deadline DESCRIPTION /by DATE TIME\n"
                + " event DESCRIPTION /from START_DATE START_TIME /to END_DATE END_TIME\n"
                + " list\n"
                + " mark NUMBER / unmark NUMBER\n"
                + " delete NUMBER\n"
                + " find, then enter a search phrase\n"
                + " find tag TAG\n"
                + " tag NUMBER TAG / untag NUMBER TAG\n"
                + " exit";
    }

    /**
     * Returns Larper's exit message.
     */
    public static String formatExitMessage() {
        return " Aight, I'm clocking out. Continue larping productivity without me.";
    }

    /**
     * Returns all tasks in their current order.
     */
    public static String formatTaskList(TaskList tasks) {
        assert tasks != null : "Task list formatter should receive an existing task list.";
        String formattedTasks = IntStream.range(0, tasks.size())
                .mapToObj(index -> " " + (index + 1) + ". " + tasks.getTask(index + 1))
                .collect(Collectors.joining("\n"));
        if (formattedTasks.isEmpty()) {
            return " The agenda is empty. Suspiciously peaceful.";
        }
        return " The current agenda (very serious):\n" + formattedTasks;
    }

    /**
     * Returns the prompt that asks what phrase to find.
     */
    public static String formatFindPrompt() {
        return " Drop the keywords. I'll find the receipts.";
    }

    /**
     * Returns the message for a tag command without a valid task number.
     */
    public static String formatInvalidTagNumber() {
        return " Give me a real task number to label. I cannot curate thin air.";
    }

    /**
     * Returns tasks found by the latest find search.
     */
    public static String formatFindResults(ArrayList<FindResult> results) {
        assert results != null && !results.isEmpty() : "Find results formatter should receive matches.";
        String formattedResults = results.stream()
                .map(result -> " " + result)
                .collect(Collectors.joining("\n"));
        return " Caught in 4K. Here's what I found:\n" + formattedResults;
    }

    /**
     * Returns the task after tags have been added.
     *
     * @param taskNumber One-based task number that was updated.
     * @param task Updated task.
     * @return Message showing the updated task.
     */
    public static String formatTaggedTask(int taskNumber, Task task) {
        assert taskNumber >= 1 : "Tagged task number should be positive.";
        assert task != null : "Tagged task formatter should receive the updated task.";
        return " Labeled task " + taskNumber + ". Personal brand curated:\n " + task;
    }

    /**
     * Returns the task after tags have been removed.
     *
     * @param taskNumber One-based task number that was updated.
     * @param task Updated task.
     * @return Message showing the updated task.
     */
    public static String formatUntaggedTask(int taskNumber, Task task) {
        assert taskNumber >= 1 : "Untagged task number should be positive.";
        assert task != null : "Untagged task formatter should receive the updated task.";
        return " Rebranded task " + taskNumber + ". Removed that label:\n " + task;
    }

    /**
     * Returns the message for a mark command without a valid task number.
     */
    public static String formatInvalidMarkNumber() {
        return " Give me a real task number to mark. I cannot verify imaginary productivity.";
    }

    /**
     * Returns the message for an unmark command without a valid task number.
     */
    public static String formatInvalidUnmarkNumber() {
        return " Give me a real task number to unmark. I cannot retract mystery progress.";
    }

    /**
     * Returns the message for a command that refers to a task number not in the list.
     */
    public static String formatMissingTaskNumber() {
        return " That task literally does not exist.";
    }

    /**
     * Returns the task that has just been marked as done.
     */
    public static String formatMarkedTask(Task task) {
        assert task != null : "Marked task formatter should receive the marked task.";
        assert task.isDone() : "Marked task formatter should receive a done task.";
        return " Wait. Actual productivity? Locked in:\n " + task;
    }

    /**
     * Returns the task that has just been marked as not done.
     */
    public static String formatUnmarkedTask(Task task) {
        assert task != null : "Unmarked task formatter should receive the unmarked task.";
        assert !task.isDone() : "Unmarked task formatter should receive a not-done task.";
        return " False alarm. Back to larping productivity:\n " + task;
    }

    /**
     * Returns the task that has just been deleted and the new task count.
     */
    public static String formatDeletedTask(Task task, int taskCount) {
        assert task != null : "Deleted task formatter should receive the removed task.";
        assert taskCount >= 0 : "Task count should not be negative after deletion.";
        return " Removed from the record. We control the narrative:\n " + task + "\n" + formatTaskCount(taskCount);
    }

    /**
     * Returns the task that has just been added and the new task count.
     */
    public static String formatAddedTask(Task task, int taskCount) {
        assert task != null : "Added task formatter should receive the added task.";
        assert taskCount >= 1 : "Task count should include the newly added task.";
        return " Say less. Adding it to the agenda so we can feel productive:\n "
                + task + "\n" + formatTaskCount(taskCount);
    }

    /**
     * Closes the scanner used to read console input.
     */
    public void close() {
        scanner.close();
    }

    private static String formatTaskCount(int taskCount) {
        assert taskCount >= 0 : "Task count should never be negative.";
        String commitmentWord = taskCount == 1 ? "commitment" : "commitments";
        return " Agenda now has " + taskCount + " public " + commitmentWord + ".";
    }
}

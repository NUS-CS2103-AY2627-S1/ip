package kibo.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

import kibo.exception.KiboException;
import kibo.task.Task;
import kibo.task.TaskList;

/**
 * Handles all console input and output for Kibo.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final String BANNER = " _  __ _ _           \n"
            + "| |/ /(_) |__   ___  \n"
            + "| ' / | | '_ \\ / _ \\\n"
            + "| . \\ | | |_) | (_) |\n"
            + "|_|\\_\\|_|_.__/ \\___/\n";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Kibo's welcome message.
     */
    public void showWelcome() {
        System.out.print(BANNER);
        System.out.println(getWelcomeMessage());
        showSeparator();
    }

    /**
     * Returns Kibo's greeting.
     *
     * @return welcome message.
     */
    public String getWelcomeMessage() {
        return joinLines(
                "Hey! I'm Kibo, your pocket cheerleader.",
                "Small steps, brighter days!",
                "Ready? Try 'list' or 'todo read book'.");
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} if a command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return command text.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the line used to separate Kibo messages.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays a response using the console's standard indentation.
     *
     * @param message response to display.
     */
    public void showMessage(String message) {
        System.out.println(" " + message.replace("\n", "\n "));
    }

    /**
     * Returns the formatted task list.
     *
     * @param tasks tasks to display.
     * @return task-list message.
     */
    public String getTaskListMessage(TaskList tasks) {
        return getNumberedTasksMessage("Here's your lineup. One step at a time!", tasks);
    }

    /**
     * Returns tasks that match a search keyword.
     *
     * @param matchingTasks matching tasks to display.
     * @return matching-task message.
     */
    public String getMatchingTasksMessage(TaskList matchingTasks) {
        return getNumberedTasksMessage(
                "Let's see what matches your search:", matchingTasks);
    }

    /**
     * Returns tasks scheduled on a particular date.
     *
     * @param scheduledTasks tasks scheduled on the date.
     * @param date date whose schedule is being displayed.
     * @return dated schedule message.
     */
    public String getScheduleMessage(TaskList scheduledTasks, LocalDate date) {
        return getNumberedTasksMessage(
                "Let's plan your day! Schedule for " + date.format(DATE_FORMAT) + ":",
                scheduledTasks);
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task added task.
     * @param taskCount number of tasks after adding.
     * @return task-added message.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return joinLines(
                "On the list! Let's make it happen:",
                "  " + task,
                getTaskCountMessage(taskCount));
    }

    /**
     * Returns confirmation that a task was marked done.
     *
     * @param task marked task.
     * @return task-marked message.
     */
    public String getTaskMarkedMessage(Task task) {
        return joinLines(
                "Woohoo! One more task done:",
                "  " + task);
    }

    /**
     * Returns confirmation that a task was marked not done.
     *
     * @param task unmarked task.
     * @return task-unmarked message.
     */
    public String getTaskUnmarkedMessage(Task task) {
        return joinLines(
                "Back on the list. You've got this!",
                "  " + task);
    }

    /**
     * Returns confirmation that a task was removed.
     *
     * @param task removed task.
     * @param taskCount number of tasks after removal.
     * @return task-deleted message.
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return joinLines(
                "All cleared! I've removed this task:",
                "  " + task,
                getTaskCountMessage(taskCount));
    }

    /**
     * Returns a user-facing chatbot error.
     *
     * @param exception error to display.
     * @return error message.
     */
    public String getErrorMessage(KiboException exception) {
        return exception.getMessage();
    }

    /**
     * Returns Kibo's farewell message.
     *
     * @return farewell message.
     */
    public String getGoodbyeMessage() {
        return "Catch you soon! Keep taking those little steps.";
    }

    /**
     * Returns a friendly task count with the appropriate singular or plural noun.
     *
     * @param taskCount number of tasks currently stored.
     * @return task-count message.
     */
    private static String getTaskCountMessage(int taskCount) {
        return "You've got " + taskCount + (taskCount == 1 ? " task" : " tasks") + " on your list.";
    }

    /**
     * Joins any number of message lines using the UI's newline separator.
     *
     * @param lines message lines in display order.
     * @return lines joined into one message.
     */
    private static String joinLines(String... lines) {
        return String.join("\n", lines);
    }

    /**
     * Returns a heading followed by a numbered list of tasks.
     *
     * @param heading text shown before the tasks.
     * @param tasks tasks to number and display.
     * @return heading and numbered tasks as one message.
     */
    private static String getNumberedTasksMessage(String heading, TaskList tasks) {
        StringBuilder message = new StringBuilder(heading);
        for (int index = 0; index < tasks.size(); index++) {
            message.append("\n").append(index + 1).append(".").append(tasks.get(index));
        }
        return message.toString();
    }
}

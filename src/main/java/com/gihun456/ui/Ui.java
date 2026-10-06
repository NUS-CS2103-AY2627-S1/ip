package com.gihun456.ui;

import java.util.List;

import com.gihun456.ErrorMessages;
import com.gihun456.model.Task;

/**
 * Handles all user-visible output for the application.
 */
public class Ui {
    /**
     * Prints the welcome banner and greeting.
     */
    public void showGreeting() {
        System.out.println(UiMessages.BANNER);
        System.out.println(UiMessages.GREETING);
        showLine();
    }

    /**
     * Prints a separator line.
     */
    public void showLine() {
        System.out.println(UiMessages.SEP);
    }

    /**
     * Prints the goodbye message.
     */
    public void showFarewell() {
        System.out.println(UiMessages.FAREWELL);
    }

    /**
     * Prints an application error message.
     *
     * @param message Error details to display.
     */
    public void showError(String message) {
        System.out.println(ErrorMessages.ERROR_PREFIX + message);
    }

    /**
     * Prints a message for an empty task list.
     */
    public void showEmptyList() {
        System.out.println(UiMessages.EMPTY_STORAGE);
        showLine();
    }

    /**
     * Prints a message for the case where no matching tasks were found from the given keyword.
     */
    public void showNoMatchingTasks() {
        System.out.println(UiMessages.NO_MATCHING_TASKS);
        showLine();
    }

    /**
     * Prints all tasks in the list.
     *
     * @param tasks Tasks to display.
     * @param isMatching Boolean to determine which header to print.
     */
    public void showTaskList(List<Task> tasks, boolean isMatching) {
        if (isMatching) {
            System.out.println(UiMessages.LIST_MATCHING_TASKS);
        } else {
            System.out.println(UiMessages.LIST_TASKS);
        }

        for (int i = 0; i < tasks.size(); i++) {
            Task currentTask = tasks.get(i);
            System.out.println((i + 1) + ". " + currentTask.toString());
        }
        showLine();
    }

    /**
     * Prints a confirmation after adding a task.
     *
     * @param task Task that was added.
     * @param taskCount Updated number of tasks in the list.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(UiMessages.ADD_TASK);
        System.out.println(task.toString());
        System.out.println(UiMessages.getTaskCountMessage(taskCount));
        showLine();
    }

    /**
     * Prints a confirmation after removing a task.
     *
     * @param task Task that was removed.
     * @param taskCount Updated number of tasks left in the list.
     */
    public void showTaskRemoved(Task task, int taskCount) {
        System.out.println(UiMessages.REMOVE_TASK);
        System.out.println(task.toString());
        System.out.println(UiMessages.getTaskCountMessage(taskCount));
        showLine();
    }

    /**
     * Prints a confirmation after marking a task as done.
     *
     * @param task Task that was marked complete.
     */
    public void showTaskMarked(Task task) {
        System.out.println(UiMessages.MARK_TASK);
        System.out.println(task.toString());
        showLine();
    }

    /**
     * Prints a confirmation after marking a task as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(UiMessages.UNMARK_TASK);
        System.out.println(task.toString());
        showLine();
    }
}

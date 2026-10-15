package chudgpt.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import chudgpt.ChudGpt;
import chudgpt.exception.ChudException;
import chudgpt.task.Priority;
import chudgpt.task.Task;
import chudgpt.task.TaskList;

/** Creates application messages and handles console input and output. */
public class Ui {
    private final Scanner input = new Scanner(System.in);

    /**
     * Returns the welcome message.
     *
     * @return the welcome message.
     */
    public String getWelcomeMessage() {
        String logo;
        try {
            logo = getLogo();
        } catch (ChudException e) {
            logo = "OOPS!!! I can't find the logo :( I'm such a chud...";
        }
        return getDivider() + "\n" + logo + "\nHello! I'm ChudGPT.\nWhat can I do for you?\n" + getDivider();
    }

    /**
     * Returns the ChudGPT logo.
     *
     * @return the ChudGPT logo.
     * @throws ChudException if the logo resource cannot be read.
     */
    public String getLogo() throws ChudException {
        try (InputStream logoStream = ChudGpt.class.getResourceAsStream("/logo.txt")) {
            if (logoStream == null) {
                throw new ChudException("Could not find logo.txt on the classpath.");
            }
            return new String(logoStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ChudException(e.getMessage());
        }
    }

    /**
     * Returns a message listing the current tasks.
     *
     * @param tasks the current tasks in the list.
     * @return the task-list message.
     */
    public String getTaskListMessage(TaskList tasks) {
        return getTaskListMessage(tasks, false);
    }

    /**
     * Returns a task-list message in the requested display order.
     *
     * @param tasks current tasks.
     * @param isSortedByPriority whether to use a priority-sorted view.
     * @return task-list message with current underlying task numbers.
     */
    public String getTaskListMessage(TaskList tasks, boolean isSortedByPriority) {
        if (tasks.size() == 0) {
            return "You have no tasks in your list! Try adding some";
        }
        String taskDisplay = isSortedByPriority ? tasks.toPrioritySortedString() : tasks.toString();
        return "Here are the tasks in your list:\n" + taskDisplay;
    }

    /**
     * Returns a message listing the matched tasks.
     *
     * @param matches the matched tasks that were found.
     * @return the matched-task message.
     */
    public String getMatchesMessage(TaskList matches) {
        if (matches.size() == 0) {
            return "I couldn't find any tasks that contains that keyword.";
        }
        return "Here are the matching tasks in your list:\n" + matches;
    }

    /**
     * Returns the greeting message.
     *
     * @return the greeting message.
     */
    public String getGreetingMessage() {
        return "Hi! I'm ChudGPT. How can I help you?";
    }

    /**
     * Returns the goodbye message.
     *
     * @return the goodbye message.
     */
    public String getGoodbyeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Returns the message shown after adding a task.
     *
     * @param task the task that was added.
     * @param size the updated size of the task list.
     * @return the task-added message.
     */
    public String getTaskAddedMessage(Task task, int size) {
        return "Got it. I've added this task:\n  " + task + "\n" + getListSizeMessage(size);
    }

    /**
     * Returns the message shown after deleting a task.
     *
     * @param task the task that was deleted.
     * @param size the updated size of the task list.
     * @return the task-deleted message.
     */
    public String getTaskDeletedMessage(Task task, int size) {
        return "Got it. Noted. I've removed this task:\n  " + task + "\n" + getListSizeMessage(size);
    }

    /**
     * Returns the message shown after changing a task's completion status.
     *
     * @param task the task whose status was changed.
     * @return the task-status message.
     */
    public String getTaskStatusMessage(Task task) {
        return task.isCompleted()
                ? "Nice! I've marked this task as completed!\n  " + task
                : "OK, I've marked this task as incomplete.\n  " + task;
    }

    /**
     * Returns a confirmation for any priority assignment, including NONE and repeated assignments.
     *
     * @param task updated task.
     * @return priority-change message.
     */
    public String getTaskPriorityMessage(Task task) {
        if (task.getPriority() == Priority.NONE) {
            return "Got it. I've removed the priority from this task:\n  " + task;
        }
        return "Got it. I've set this task's priority to " + task.getPriority() + ":\n  " + task;
    }

    /** Returns the message showing the updated task-list size. */
    private String getListSizeMessage(int size) {
        return "Now you have " + size + " tasks in your list.";
    }

    /**
     * Returns the save message.
     *
     * @return the save message.
     */
    public String getSaveMessage() {
        return "I've saved your current list of tasks.";
    }

    /**
     * Returns an error message.
     *
     * @param errorMessage the message associated with the error.
     * @return the formatted error message.
     */
    public String getErrorMessage(String errorMessage) {
        return "OOPS!!! I've run into an error :( I'm such a chud...\nDetails:\n  " + errorMessage;
    }

    /**
     * Reads the next command from standard input.
     *
     * @return the next command entered by the user.
     */
    public String readCommand() {
        return input.nextLine();
    }

    /**
     * Returns the divider line.
     *
     * @return the divider line.
     */
    public String getDivider() {
        return "____________________________________________________________";
    }

    /**
     * Returns the error message shown when the save file cannot be loaded.
     *
     * @return the save-file loading error message.
     */
    public String getLoadErrorMessage() {
        return "OOPS!!! I couldn't retrieve the save file :( I'm such a chud...";
    }

    /**
     * Returns a warning describing malformed records skipped while loading.
     *
     * @param warnings line-specific warnings produced by storage.
     * @return formatted recovery warning.
     */
    public String getLoadWarningMessage(List<String> warnings) {
        assert warnings != null : "Load warnings should not be null";
        assert !warnings.isEmpty() : "A load warning message requires at least one warning";
        String details = warnings.stream()
                .map(warning -> "  " + warning)
                .collect(Collectors.joining("\n"));
        return "WARNING: Some saved tasks could not be loaded.\nDetails:\n" + details
                + "\nA backup will be created before the recovered task list is saved.";
    }

    /**
     * Displays a message in the console.
     *
     * @param message the message to display.
     */
    public void display(String message) {
        System.out.println(message);
    }
}

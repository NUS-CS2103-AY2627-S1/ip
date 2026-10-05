package oreo;

import oreo.task.Task;
import oreo.task.TaskList;

/**
 * Formats Oreo's messages independently of the interface displaying them.
 * This allows the same command behaviour to be used in a graphical interface.
 */
public class Ui {
    /** Whether the response currently being produced represents an error. */
    private boolean lastMessageWasError;

    /** Clears the error flag before Oreo begins preparing a new response. */
    public void beginResponse() {
        lastMessageWasError = false;
    }

    /** Returns whether the most recently prepared response describes an error. */
    public boolean wasLastMessageAnError() {
        return lastMessageWasError;
    }

    /** Returns Oreo's greeting shown when the application starts. */
    public String getWelcomeMessage() {
        return "Hello! I'm OREO, your personal cookie-themed chatbot: crisp, creamy, and ready to help."
                + System.lineSeparator() + "What can I do for you? Let's make today a little sweeter.";
    }

    /** Returns the farewell message. */
    public String getGoodbyeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /** Returns a formatted task list. */
    public String getTaskListMessage(TaskList tasks) {
        return tasks.toString().trim();
    }

    /** Returns the matching-task message. */
    public String getMatchingTasksMessage(String matchingTasks) {
        return matchingTasks.trim();
    }

    /** Returns a formatted list of tags and their associated tasks. */
    public String getTagListMessage(String tagList) {
        return tagList.trim();
    }

    /** Returns a confirmation that a task was added. */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return "Got it. I've added this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /** Returns a confirmation that a task was marked as done. */
    public String getTaskMarkedMessage(Task task) {
        return "Nice! I've marked this task as done:" + System.lineSeparator()
                + "  [X] " + task.getDescription();
    }

    /** Returns a confirmation that a task was marked as not done. */
    public String getTaskUnmarkedMessage(Task task) {
        return "OK, I've marked this task as not done yet:" + System.lineSeparator()
                + "  [ ] " + task.getDescription();
    }

    /** Returns a confirmation that tags were added to a task. */
    public String getTagsAddedMessage(Task task) {
        return "Got it. I've added these tags to this task:" + System.lineSeparator()
                + "  " + task;
    }

    /** Returns a confirmation that tags were removed from a task. */
    public String getTagsRemovedMessage(Task task) {
        return "Got it. I've removed these tags from this task:" + System.lineSeparator()
                + "  " + task;
    }

    /** Returns a confirmation that a task was deleted. */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return "Noted. I've removed this task:" + System.lineSeparator() + task
                + System.lineSeparator() + "Now you have " + taskCount + " tasks in the list.";
    }

    /** Returns an error message. */
    public String getErrorMessage(String message) {
        lastMessageWasError = true;
        return message;
    }
}

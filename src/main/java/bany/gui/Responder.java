package bany.gui;

import java.util.List;
import java.util.Map;

import bany.tasks.Task;

/** Builds user-facing response strings for the Bany GUI. */
public class Responder {

    /** Creates the builder for Bany's user-facing messages. */
    public Responder() {
    }

    /**
     * Builds Bany's welcome message.
     *
     * @return welcome message.
     */
    public String respondWelcome() {
        return """
                Hi, I'm Bany.
                Let's keep your tasks growing.
                """;
    }

    /**
     * Builds Bany's goodbye message.
     *
     * @return goodbye message.
     */
    public String respondGoodbye() {
        return "Until next time - your task list will be here when you're ready.";
    }

    /**
     * Builds confirmation after a task is added.
     *
     * @param task task that was added.
     * @param size number of tasks currently stored.
     * @return task-added response.
     */
    public String respondAddTask(Task task, int size) {
        return String.format("Added to your task list:%n  %s%n"
                + "Your list now holds %d %s.", task, size, size == 1 ? "task" : "tasks");
    }

    /**
     * Builds an error response for a blank task description.
     *
     * @return invalid-description message.
     */
    public String respondInvalidTaskDescription() {

        return "I need a task description before I can add it.";
    }

    /**
     * Builds an error response when required task tags are missing.
     *
     * @return invalid-tag message.
     */
    public String respondInvalidTaskInitiation() {
        return "I couldn't schedule that task because its required tags are missing or invalid.";
    }

    /**
     * Builds an error response when a date-time has an unsupported format.
     *
     * @return invalid-date-time message.
     */
    public String respondInvalidDateTime() {
        return "Use the date-time format dd-MM-yyyy HH:mm.";
    }

    /**
     * Builds an error response when an event ends before it starts.
     *
     * @return invalid-event-range message.
     */
    public String respondInvalidEventRange() {
        return "An event must end after it begins.";
    }

    /**
     * Builds an error response for a repeated critical tag.
     *
     * @param tagName repeated tag name.
     * @return duplicate-tag message.
     */
    public String respondDuplicateTag(String tagName) {
        return String.format("Use the tag /%s only once.", tagName);
    }

    /**
     * Builds a warning when task tags are extra or out of order.
     *
     * @return tag warning message.
     */
    public String respondTagWarning() {
        return "I found extra or out-of-order tags. The task was still added.";
    }

    /**
     * Builds a warning when a reschedule command has extra or misordered tags.
     *
     * @return tag warning message for a reschedule command.
     */
    public String respondRescheduleTagWarning() {
        return "I found extra or out-of-order tags. The task was still rescheduled.";
    }

    /**
     * Builds an error response for a task index outside the current task list.
     *
     * @param type action that was attempted.
     * @param size number of tasks currently stored.
     * @return out-of-bounds response.
     */
    public String respondOutOfBoundIndex(String type, int size) {
        return String.format("I can't %s that task. Choose a valid task number from 1 to %d.",
                type, size);
    }

    /**
     * Builds confirmation after a task is marked as done.
     *
     * @param task task that was marked.
     * @return mark-task response.
     */
    public String respondMarkTask(Task task) {
        return String.format("Checked off:%n   %s", task);
    }

    /**
     * Builds confirmation after a task is marked as not done.
     *
     * @param task task that was unmarked.
     * @return unmark-task response.
     */
    public String respondUnmarkTask(Task task) {
        return String.format("Moved back to active tasks:%n   %s", task);
    }

    /**
     * Builds confirmation after a task is deleted.
     *
     * @param task task that was deleted.
     * @param size number of tasks remaining.
     * @return task-deleted response.
     */
    public String respondDeleteTask(Task task, int size) {
        String remainingTasks = size == 0 ? "Your list is now empty."
                : String.format("Your list now holds %d %s.", size, size == 1 ? "task" : "tasks");

        return String.format("Removed from your task list:%n   %s%n"
                + "%s", task, remainingTasks);
    }

    /**
     * Builds confirmation after a task's tag has been updated.
     *
     * @param task task whose tag was updated.
     * @return task-rescheduled response.
     */
    public String respondRescheduleTask(Task task) {
        return String.format("Schedule refreshed for this task:%n   %s", task);
    }

    /**
     * Builds a response containing all tasks in their current storage order.
     *
     * @param listOfTasks tasks to display.
     * @return formatted task-list response.
     */
    public String respondList(List<Task> listOfTasks) {
        if (listOfTasks.isEmpty()) {
            return "Your list is empty.";
        }
        StringBuilder response = new StringBuilder("Your task list:");
        int count = 1;
        for (Task task : listOfTasks) {
            response.append(System.lineSeparator())
                    .append(count++)
                    .append('.')
                    .append(' ')
                    .append(task);
        }
        return response.toString();
    }

    /**
     * Builds a generic invalid-command response.
     *
     * @return invalid-command message.
     */
    public String respondInvalidCommand() {
        return "I didn't recognize that command. Please try again.";
    }

    /**
     * Builds an invalid-command response with an optional suggestion.
     *
     * @param closestCommand closest recognized command, or {@code null}.
     * @return invalid-command response.
     */
    public String respondInvalidCommand(String closestCommand) {
        if (closestCommand == null) {
            return respondInvalidCommand();
        }
        return String.format("I didn't recognize that command. Did you mean %s?", closestCommand);
    }

    /**
     * Builds a message when a search matches no tasks.
     *
     * @return no-match message.
     */
    public String respondNoSuchTask() {
        return "No tasks on your list match that search.";
    }

    /**
     * Builds a response containing search matches and their original list numbers.
     * Entries are displayed in the supplied map's iteration order, without renumbering.
     *
     * @param matchedTasks matches keyed by their one-based positions in the complete task list,
     *     in display order.
     * @return formatted matching-tasks response.
     */
    public String respondMatchedTasks(Map<Integer, Task> matchedTasks) {
        StringBuilder response = new StringBuilder("Tasks matching your search:");
        for (Map.Entry<Integer, Task> match : matchedTasks.entrySet()) {
            response.append(System.lineSeparator())
                    .append(match.getKey())
                    .append(". ")
                    .append(match.getValue());
        }
        return response.toString();
    }

    /** Builds responses for task-file persistence errors. */
    public static class ErrorResponder {
        /** Creates the persistence-error response helper. */
        public ErrorResponder() {
        }

        /**
         * Returns the warning shown when startup data cannot be loaded.
         *
         * @return the startup warning and recovery-file location.
         */
        public static String respondStartupFileWarning() {
            return "I couldn't read the startup file. "
                    + "I'll begin with an empty task list. "
                    + "The original file is available at data/report.";
        }

        /**
         * Explains why commands are disabled when startup recovery cannot finish safely.
         *
         * @return the recovery failure message and suggested checks.
         */
        public static String respondStartupRecoveryError() {
            return "I couldn't archive the startup file or create data/bany.txt. "
                    + "Commands are disabled to protect your data. "
                    + "Check the data and report folders and their permissions, then restart Bany.";
        }

        /**
         * Builds an error response when the task file cannot be updated.
         *
         * @return file-update-error message.
         */
        public static String respondFileUpdateError() {
            return "I couldn't save your task list.";
        }

        /**
         * Builds an error response when the task file cannot be loaded.
         *
         * @return file-load-error message.
         */
        public static String respondFileLoadError() {
            return "I couldn't load your task list.";
        }
    }
}

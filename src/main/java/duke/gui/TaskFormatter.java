package duke.gui;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import duke.Deadline;
import duke.Event;
import duke.Task;
import duke.TaskMatch;

/**
 * Formats task snapshots and progress summaries for the desktop conversation.
 */
public final class TaskFormatter {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    private TaskFormatter() {
    }

    /**
     * Formats search results with actionable task numbers from the full list.
     *
     * @param matches Search results in full-list order.
     * @return Matching tasks or an explicit no-match message.
     */
    public static String renderMatches(List<TaskMatch> matches) {
        if (matches.isEmpty()) {
            return "No matching tasks found.\n• Try a different keyword.\n• Type list to see all tasks.";
        }
        StringBuilder result = new StringBuilder("Matching tasks:");
        for (TaskMatch match : matches) {
            result.append("\n\n").append(match.taskNumber()).append(". ").append(formatTask(match.task()));
        }
        return result.toString();
    }

    /**
     * Formats tasks with the one-based numbering used by the command interface.
     *
     * @param tasks Tasks to display.
     * @return A conversation-ready task list.
     */
    public static String renderTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Your task list is empty.\n• Add your first task: todo read a book";
        }

        StringBuilder result = new StringBuilder("Your tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            result.append("\n\n").append(i + 1).append(". ").append(formatTask(tasks.get(i)));
        }
        return result.toString();
    }

    /**
     * Formats one task with readable status and separate lines for dates or event times.
     *
     * @param task The task to display.
     * @return A concise display string without changing the terminal or storage format.
     */
    public static String formatTask(Task task) {
        String text = formatTitle(task);
        for (String detail : formatDetails(task)) {
            text += "\n" + detail;
        }
        return text;
    }

    /**
     * Formats the task status and description without any layout padding.
     *
     * @param task Task whose title should be displayed.
     * @return Literal status and description text.
     */
    public static String formatTitle(Task task) {
        return (task.isDone() ? "[Done] " : "[To do] ") + task.getDescription();
    }

    /**
     * Formats detail lines; the view, rather than leading spaces, controls their alignment.
     *
     * @param task Task whose dates or times should be displayed.
     * @return Immutable detail lines, or an empty list for a todo.
     */
    public static List<String> formatDetails(Task task) {
        if (task instanceof Deadline deadline) {
            return List.of("Due: " + deadline.getBy().format(DISPLAY_DATE));
        }
        if (task instanceof Event event) {
            return List.of("From: " + event.getFrom(), "To: " + event.getTo());
        }
        return List.of();
    }

    /**
     * Summarizes completed and total tasks for confirmations.
     *
     * @param tasks Tasks whose progress should be summarized.
     * @return A concise progress summary.
     */
    public static String formatProgressSummary(List<Task> tasks) {
        int completed = (int) tasks.stream().filter(Task::isDone).count();
        int total = tasks.size();
        if (total == 0) {
            return "No tasks yet. Add one whenever you're ready.";
        }
        String taskLabel = total == 1 ? "task" : "tasks";
        if (completed == total) {
            return "You've finished " + (total == 1 ? "your task" : "all " + total + " tasks")
                    + ". Nice work!";
        }
        return completed + " of " + total + " " + taskLabel + " done. One step at a time.";
    }

    /**
     * Returns the compact task count shown in the application header.
     *
     * @param tasks Tasks whose count should be displayed.
     * @return A compact count of total and completed tasks.
     */
    public static String formatHeaderStats(List<Task> tasks) {
        long completed = tasks.stream().filter(Task::isDone).count();
        return tasks.size() + " TASK" + (tasks.size() == 1 ? "" : "S") + "  ·  " + completed + " DONE";
    }
}

package sandrone.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import sandrone.task.Task;
import sandrone.task.TaskList;
import sandrone.task.TaskStatistics;

/**
 * Handles all console output shown to the user.
 */
public class Ui {
    private final Scanner scanner = new Scanner(System.in);
    private final StringBuilder output = new StringBuilder();

    /**
     * Returns whether another command is available from the user.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims one command from the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Closes the command input stream.
     */
    public void close() {
        scanner.close();
    }

    /** Displays Sandrone's greeting. */
    public void showWelcome() {
        writeLines(
                "Sandrone, Marionette.",
                "Where, when, how. Go.",
                "Facts are objective. Your records, however, require maintenance. State your task.");
    }

    /**
     * Displays a message enclosed by separator lines.
     */
    public void showMessage(String message) {
        writeLine(message);
    }

    /**
     * Displays the requested tasks.
     */
    public void showTaskList(TaskList tasks, LocalDate date, String dateText) {
        String listHeading = dateText.isEmpty()
                ? " Your records are below. Review them carefully."
                : " Your records for " + dateText + " are below. Review them carefully.";
        writeLine(listHeading);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.getTask(i);
            if (date == null || task.occursOn(date)) {
                writeLine(" " + (i + 1) + "." + task);
            }
        }
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        writeLine(" Matching records are below. Review them carefully.");
        for (int i = 0; i < matchingTasks.size(); i++) {
            writeLine(" " + (i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was added.
     */
    public void showTaskAdded(String command, int count) {
        String taskNoun = count == 1 ? "task" : "tasks";
        writeLines(
                " Recorded. Do try not to make me repeat the process.",
                " " + command,
                "You now have " + count + " " + taskNoun + " in the list");
    }

    /**
     * Displays confirmation that a task was marked or unmarked.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        writeLines(
                isDone ? " Status updated. An acceptable result:"
                        : " Status updated. The record has been reopened:",
                "   [" + task.getStatusIcon() + "] " + task.getDescription());
    }

    /**
     * Displays confirmation that a task was removed.
     */
    public void showTaskRemoved(Task task) {
        writeLines(
                " Got it, I have removed this task:",
                "   [" + task.getStatusIcon() + "] " + task.getDescription());
    }

    /** Displays a summary of the current task list. */
    public void showTaskStatistics(TaskStatistics statistics) {
        writeLines(
                "Task statistics",
                "Total tasks: " + statistics.getTotalTasks() + " / " + statistics.getTaskLimit(),
                "Completed: " + statistics.getCompletedTasks(),
                "Incomplete: " + statistics.getIncompleteTasks(),
                String.format(Locale.ROOT, "Completion rate: %.1f%%", statistics.getCompletionRate()),
                "",
                "By type",
                "Todos: " + statistics.getTodoCount(),
                "Deadlines: " + statistics.getDeadlineCount(),
                "Events: " + statistics.getEventCount(),
                "",
                "Scheduled today: " + statistics.getScheduledTodayCount(),
                "Remaining task slots: " + statistics.getRemainingTaskSlots());
    }

    /**
     * Returns all output produced since the previous call, for display in the GUI.
     */
    public String consumeOutput() {
        String outputText = output.toString();
        output.setLength(0);
        return outputText;
    }

    /** Writes one line to both the console and the GUI output buffer. */
    private void writeLine(String line) {
        output.append(line).append(System.lineSeparator());
        System.out.println(line);
    }

    /** Writes each supplied line to both the console and the GUI output buffer. */
    private void writeLines(String... lines) {
        for (String line : lines) {
            writeLine(line);
        }
    }
}

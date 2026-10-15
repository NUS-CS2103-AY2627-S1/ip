package stephen.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import stephen.task.Task;
import stephen.task.TaskList;

/** Reads user input and presents responses in Orbit's mission-control voice. */
public class Ui {
    private static final String DIVIDER = "\n________________________________________________";
    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another line of user input is available.
     *
     * @return {@code true} if another input line can be read
     */
    public boolean hasNextInput() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next line of user input.
     *
     * @return next command without surrounding whitespace
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays the startup greeting and its trailing divider. */
    public void showWelcome() {
        showLine("Orbit online.");
        showLine("Ready to plan your next move?");
        showDivider();
    }

    /** Displays the farewell between divider lines. */
    public void showGoodbye() {
        showDivider();
        showLine("Orbit signing off. Keep moving forward!");
        showDivider();
    }

    /**
     * Displays an error using the chatbot's standard prefix.
     *
     * @param message error message without the standard prefix
     */
    public void showError(String message) {
        showLine("Navigation alert: " + message);
    }

    /**
     * Displays all tasks with their one-based list numbers.
     *
     * @param tasks task list to display
     */
    public void showTaskList(TaskList tasks) {
        showLine("Current mission plan:");
        showNumberedTasks(tasks);
    }

    /**
     * Displays the confirmation and complete list after tasks are sorted.
     *
     * @param tasks sorted task list to display
     */
    public void showTasksSorted(TaskList tasks) {
        showLine("Mission plan sorted alphabetically:");
        showNumberedTasks(tasks);
    }

    /** Displays all tasks with their one-based list numbers. */
    private void showNumberedTasks(TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            showLine((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays the standard confirmation for a newly added task.
     *
     * @param task task that was added
     * @param taskCount number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        showLine("Mission logged. I've added this task:");
        showLine("  " + task);
        showRadarTaskCount(taskCount);
    }

    /**
     * Displays the standard confirmation for a deleted task.
     *
     * @param task task that was deleted
     * @param taskCount number of tasks after the deletion
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showLine("Course adjusted. I've removed this task:");
        showLine("  " + task);
        showRadarTaskCount(taskCount);
    }

    /** Displays the task count with grammatically correct singular or plural wording. */
    private void showRadarTaskCount(int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        showLine("You now have " + taskCount + " " + taskNoun + " on the radar.");
    }

    /**
     * Displays a task whose completion status was changed.
     *
     * @param task task whose status changed
     * @param isDone {@code true} if the task was marked done
     */
    public void showTaskMarked(Task task, boolean isDone) {
        if (isDone) {
            showLine("Milestone reached! This task is complete:");
        } else {
            showLine("Task reopened. It's back on the radar:");
        }
        showLine(task.toString());
    }

    /**
     * Displays dated tasks occurring on the requested date.
     *
     * @param tasks task list to search
     * @param date date whose tasks should be displayed
     */
    public void showSchedule(TaskList tasks, LocalDate date) {
        boolean hasMatches = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                if (!hasMatches) {
                    showLine("On the radar for "
                            + date.format(Task.DISPLAY_DATE_FORMAT) + ":");
                    hasMatches = true;
                }
                showLine((i + 1) + "." + tasks.get(i));
            }
        }
        if (!hasMatches) {
            showLine("Clear skies - no deadlines or events on "
                    + date.format(Task.DISPLAY_DATE_FORMAT) + ".");
        }
    }

    /**
     * Displays tasks whose descriptions match the search phrase.
     *
     * @param matches matching tasks in display order
     * @param keyword search phrase used to produce the matches
     */
    public void showFindResults(List<Task> matches, String keyword) {
        if (matches.isEmpty()) {
            showLine("Scan clear - no tasks match \"" + keyword + "\".");
            return;
        }
        String taskNoun = matches.size() == 1 ? "task" : "tasks";
        showLine("Scan found " + matches.size() + " matching " + taskNoun + ":");
        for (int i = 0; i < matches.size(); i++) {
            showLine((i + 1) + "." + matches.get(i));
        }
    }

    /**
     * Displays one line of normal output.
     *
     * @param message text to display
     */
    public void showLine(String message) {
        System.out.println(message);
    }

    /** Displays the standard divider, including its leading blank line. */
    public void showDivider() {
        showLine(DIVIDER);
    }
}

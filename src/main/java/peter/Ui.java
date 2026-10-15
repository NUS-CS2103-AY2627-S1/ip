package peter;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Builds Peter's response text for user-facing events, and reads console
 * input for the command-line entry point. Contains no formatting for visual
 * borders, so the same text can be shown in the console or in the GUI.
 */
public class Ui {
    private static final String BANNER = "       ____       _            \n"
            + "      |  _ \\ ___ | |_ ___ _ __ \n"
            + "      | |_) / _ \\| __/ _ \\ '__|\n"
            + "      |  __/  __/| ||  __/ |   \n"
            + "      |_|   \\___| \\__\\___|_|   \n";

    private final Scanner scanner;

    /** Creates a Ui that reads user input from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line of user input.
     *
     * @return the trimmed line entered by the user
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Closes the input scanner. Call once, when Peter is shutting down. */
    public void close() {
        scanner.close();
    }

    /**
     * Returns Peter's greeting banner.
     *
     * @return the greeting message
     */
    public String showWelcome() {
        return BANNER + "Your friendly neighbourhood Peter, at your service!\n"
                + "What can I swing by and help you with today?";
    }

    /**
     * Returns Peter's farewell message.
     *
     * @return the farewell message
     */
    public String showGoodbye() {
        return "Catch you on the web-swing! Later, pal.";
    }

    /**
     * Returns Peter's message about tasks recovered from storage.
     *
     * @param taskCount the number of tasks loaded from disk
     * @return the loading result message
     */
    public String showLoadingResult(int taskCount) {
        if (taskCount == 0) {
            return "Web's empty - starting fresh with a clean strand.";
        }
        return "Swung by the archive and pulled " + taskCount + " task(s) off the web.";
    }

    /**
     * Returns Peter's message for when saved tasks could not be loaded.
     *
     * @return the loading error message
     */
    public String showLoadingError() {
        return "My spidey-sense says that save file's a lost cause - starting fresh.";
    }

    /**
     * Returns all tasks in the current task list.
     *
     * @param tasks the tasks to display
     * @return the task list message
     */
    public String showTaskList(List<Task> tasks) {
        String header = "Here's everything currently stuck to the web:";
        if (tasks.isEmpty()) {
            return header;
        }
        String lines = IntStream.rangeClosed(1, tasks.size())
                .mapToObj(number -> number + "." + tasks.get(number - 1))
                .collect(Collectors.joining("\n"));
        return header + "\n" + lines;
    }

    /**
     * Returns confirmation that a task was marked as completed.
     *
     * @param task the task that was marked
     * @return the task-marked message
     */
    public String showTaskMarked(Task task) {
        return "Nice work, hero - that one's done:\n  " + task;
    }

    /**
     * Returns confirmation that a task was marked as incomplete.
     *
     * @param task the task that was unmarked
     * @return the task-unmarked message
     */
    public String showTaskUnmarked(Task task) {
        return "Gotcha, sticking that one back on the web as unfinished:\n  " + task;
    }

    /**
     * Returns confirmation that a task was removed.
     *
     * @param task the task that was removed
     * @param taskCount the new number of tasks
     * @return the task-removed message
     */
    public String showTaskRemoved(Task task, int taskCount) {
        return "Snipped that one clean off the web:\n  " + task
                + "\nThat's " + taskCount + " thing(s) left stuck to it.";
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task the task that was added
     * @param taskCount the new number of tasks
     * @return the task-added message
     */
    public String showTaskAdded(Task task, int taskCount) {
        return "On it! Webbed that one up for you:\n  " + task
                + "\nThat's " + taskCount + " thing(s) stuck to your web now.";
    }

    /**
     * Returns the schedule for a specific date: deadlines due, then events
     * starting, on that date.
     *
     * @param scheduledTasks the deadlines and events occurring on the requested date, in display order
     * @param requestedDate the date that was requested, shown for context only
     * @return the schedule message
     */
    public String showSchedule(List<Task> scheduledTasks, LocalDate requestedDate) {
        if (scheduledTasks.isEmpty()) {
            return "Quiet day - nothing on the web for then.";
        }
        String lines = scheduledTasks.stream()
                .map(task -> "  " + task)
                .collect(Collectors.joining("\n"));
        return "Here's what's on the web for that day, deadlines first:\n" + lines;
    }

    /**
     * Returns tasks matching a search keyword.
     *
     * @param matchingTasks the tasks that matched the search
     * @return the search-result message
     */
    public String showFoundTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return "Swung all over the city and came up empty on that search.";
        }
        String lines = IntStream.rangeClosed(1, matchingTasks.size())
                .mapToObj(number -> number + "." + matchingTasks.get(number - 1))
                .collect(Collectors.joining("\n"));
        return "Here's what stuck to the web when I searched for that:\n" + lines;
    }
}

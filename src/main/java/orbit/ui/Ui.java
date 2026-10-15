package orbit.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import orbit.task.Task;

/**
 * Creates Orbit's mission-control responses and handles console interaction.
 */
public class Ui {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = "  ___  ____  ____ ___ _____ \n"
            + " / _ \\|  _ \\| __ )_ _|_   _|\n"
            + "| | | | |_) |  _ \\| |  | |  \n"
            + "| |_| |  _ <| |_) | |  | |  \n"
            + " \\___/|_| \\_\\____/___| |_|  \n";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Orbit's welcome message.
     */
    public void showWelcome() {
        System.out.println(SEPARATOR + "\n" + BANNER
                + "Orbit online.\nMission control is ready. What shall we accomplish?\n" + SEPARATOR);
    }

    /**
     * Returns Orbit's greeting without console-specific decoration.
     *
     * @return greeting for a graphical UI
     */
    public String getWelcomeMessage() {
        return formatLines("Orbit online.", "Mission control is ready. What shall we accomplish?");
    }

    /**
     * Checks whether another command is available.
     *
     * @return true if another line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return full command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the horizontal separator used between responses.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays a response in the console.
     *
     * @param message response to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Creates Orbit's goodbye message.
     *
     * @return goodbye response
     */
    public String getGoodbyeMessage() {
        return "Orbit signing off. Clear skies!";
    }

    /**
     * Creates a list of all tasks with one-based numbering.
     *
     * @param tasks tasks to display
     * @return formatted task-list response
     */
    public String getTaskListMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Flight plan is clear. No missions are currently logged.";
        }
        return formatNumberedTasks("Here is your flight plan:", tasks);
    }

    /**
     * Creates a list of tasks whose descriptions match a search keyword.
     *
     * @param tasks matching tasks to display
     * @return formatted matching-task response
     */
    public String getMatchingTasksMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Scanner found no matching missions.";
        }
        return formatBulletedTasks("Scanner found these matching missions:", tasks);
    }

    /**
     * Creates a numbered schedule for a date.
     *
     * @param date date being viewed
     * @param tasks tasks occurring on the date
     * @return formatted schedule response
     */
    public String getScheduleMessage(LocalDate date, List<Task> tasks) {
        String formattedDate = date.format(DATE_FORMAT);
        if (tasks.isEmpty()) {
            return "No missions are scheduled for " + formattedDate + ".";
        }
        return formatBulletedTasks("Mission timeline for " + formattedDate + ":", tasks);
    }

    /**
     * Creates confirmation that a task was marked as done.
     *
     * @param taskDisplay updated task display text
     * @return task-marked response
     */
    public String getTaskMarkedMessage(String taskDisplay) {
        return formatLines("Mission accomplished! Marked as complete:", "  " + taskDisplay);
    }

    /**
     * Creates confirmation that a task was marked as not done.
     *
     * @param taskDisplay updated task display text
     * @return task-unmarked response
     */
    public String getTaskUnmarkedMessage(String taskDisplay) {
        return formatLines("Mission reopened and returned to the flight plan:", "  " + taskDisplay);
    }

    /**
     * Creates confirmation that a task was removed.
     *
     * @param task removed task
     * @param taskCount number of remaining tasks
     * @return task-deleted response
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return formatLines(
                "Mission scrubbed from the flight plan:",
                "  " + task,
                formatTaskCount(taskCount));
    }

    /**
     * Creates confirmation that a task was added.
     *
     * @param task added task
     * @param taskCount number of stored tasks
     * @return task-added response
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return formatLines(
                "Mission logged:",
                "  " + task,
                formatTaskCount(taskCount));
    }

    /**
     * Creates an error encountered while loading tasks.
     *
     * @param message explanation of the loading error
     * @return loading-error response
     */
    public String getLoadingErrorMessage(String message) {
        return "Navigation log could not be loaded: " + message;
    }

    /**
     * Creates an error encountered while saving tasks.
     *
     * @return saving-error response
     */
    public String getSavingErrorMessage() {
        return "Mission log could not be saved. Please try again.";
    }

    /**
     * Formats tasks under a heading using one-based numbering.
     *
     * @param heading text displayed before the tasks
     * @param tasks tasks to format
     * @return heading followed by numbered tasks
     */
    private static String formatNumberedTasks(String heading, List<Task> tasks) {
        StringBuilder message = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n")
                    .append(i + 1)
                    .append(".")
                    .append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Formats read-only search or schedule results as bullets rather than actionable task numbers.
     *
     * @param heading text displayed before the tasks
     * @param tasks tasks to format
     * @return heading followed by bulleted tasks
     */
    private static String formatBulletedTasks(String heading, List<Task> tasks) {
        StringBuilder message = new StringBuilder(heading);
        for (Task task : tasks) {
            message.append("\n- ").append(task);
        }
        return message.toString();
    }

    /**
     * Combines any number of response lines into one display message.
     *
     * @param lines response lines in display order
     * @return lines separated by newline characters
     */
    private static String formatLines(String... lines) {
        return String.join("\n", lines);
    }

    /**
     * Formats the current number of missions with correct singular or plural grammar.
     *
     * @param taskCount number of missions in the flight plan
     * @return formatted mission-count message
     */
    private static String formatTaskCount(int taskCount) {
        String noun = taskCount == 1 ? "mission" : "missions";
        return "Flight plan now contains " + taskCount + " " + noun + ".";
    }
}

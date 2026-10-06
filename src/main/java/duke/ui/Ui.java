package duke.ui;

import java.util.Arrays;
import java.util.Scanner;

import duke.exception.DukeException;
import duke.place.Place;
import duke.place.PlaceList;
import duke.task.Task;
import duke.task.TaskList;

/**
 * Handles all user interactions and text output for the Duke application.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner scanner;
    private String lastResponse = "";

    /**
     * Constructs a Ui instance initialized with standard console input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line of input from the user.
     *
     * @return The raw input string from the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints a standard horizontal divider line.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays one or more message lines to standard output and updates the response buffer.
     *
     * @param messages The message lines to display.
     */
    public void showMessages(String... messages) {
        assert messages != null : "Messages array must not be null";
        Arrays.stream(messages).forEach(System.out::println);
        lastResponse = String.join("\n", messages);
    }

    /**
     * Prints the welcome message upon application startup.
     */
    public void showWelcome() {
        showLine();
        showMessages("Hello! I'm TBC.", "What can I do for you?");
        showLine();
    }

    /**
     * Prints the exit goodbye message.
     */
    public void showGoodbye() {
        showMessages("Bye bye.");
    }

    /**
     * Prints an error message.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        showMessages(message);
    }

    /**
     * Prints a loading error message if tasks could not be loaded from file.
     */
    public void showLoadingError() {
        showMessages("Error while loading tasks: file could not be loaded.");
    }

    /**
     * Displays all tasks currently in the task list.
     *
     * @param tasks The TaskList containing tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            showMessages("There are no tasks in your list.");
            return;
        }
        String[] taskLines = new String[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            try {
                Task t = tasks.getTask(i);
                taskLines[i] = (i + 1) + ". [" + t.getTaskIcon() + "]["
                        + t.getStatusIcon() + "] " + t;
            } catch (DukeException e) {
                showError(e.getMessage());
                return;
            }
        }
        showMessages(taskLines);
    }

    /**
     * Displays confirmation that a task has been added.
     *
     * @param task       The added task.
     * @param totalTasks The new total count of tasks.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        showMessages(
                "Got it. I've added this task:",
                "  [" + task.getTaskIcon() + "][" + task.getStatusIcon() + "] " + task,
                "Now you have " + totalTasks + " tasks in the list."
        );
    }

    /**
     * Displays confirmation that a task has been removed.
     *
     * @param task       The removed task.
     * @param totalTasks The new total count of tasks.
     */
    public void showTaskDeleted(Task task, int totalTasks) {
        showMessages(
                "Noted. I've removed this task:",
                "  [" + task.getTaskIcon() + "][" + task.getStatusIcon() + "] " + task,
                "Now you have " + totalTasks + " tasks in the list."
        );
    }

    /**
     * Displays confirmation that a task has been marked as done.
     *
     * @param task The marked task.
     */
    public void showTaskMarked(Task task) {
        showMessages(
                "Nice! I've marked this task as done:",
                "  [" + task.getTaskIcon() + "][" + task.getStatusIcon() + "] " + task
        );
    }

    /**
     * Displays confirmation that a task has been marked as not done.
     *
     * @param task The unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        showMessages(
                "OK, I've marked this task as not done yet:",
                "  [" + task.getTaskIcon() + "][" + task.getStatusIcon() + "] " + task
        );
    }

    /**
     * Displays confirmation that the previous command has been undone.
     *
     * @param totalTasks The current total count of tasks after undoing.
     */
    public void showUndoSuccess(int totalTasks) {
        showMessages(
                "Got it. I've undone the previous command.",
                "Now you have " + totalTasks + " tasks in the list."
        );
    }

    /**
     * Displays the list of tasks matching a search keyword.
     *
     * @param matchingTasks The TaskList containing matched tasks.
     */
    public void showFoundTasks(TaskList matchingTasks) {
        String[] lines = new String[matchingTasks.size() + 1];
        lines[0] = "Here are the matching tasks in your list:";
        for (int i = 0; i < matchingTasks.size(); i++) {
            try {
                Task t = matchingTasks.getTask(i);
                lines[i + 1] = (i + 1) + ". [" + t.getTaskIcon() + "]["
                        + t.getStatusIcon() + "] " + t;
            } catch (DukeException e) {
                showError(e.getMessage());
                return;
            }
        }
        showMessages(lines);
    }

    /**
     * Displays confirmation that a place has been added.
     *
     * @param place       The added place.
     * @param totalPlaces The new total count of places.
     */
    public void showPlaceAdded(Place place, int totalPlaces) {
        showMessages(
                "Got it. I've added this place:",
                "  " + place,
                "Now you have " + totalPlaces + " places in the list."
        );
    }

    /**
     * Displays confirmation that a place has been removed.
     *
     * @param place       The removed place.
     * @param totalPlaces The new total count of places.
     */
    public void showPlaceDeleted(Place place, int totalPlaces) {
        showMessages(
                "Noted. I've removed this place:",
                "  " + place,
                "Now you have " + totalPlaces + " places in the list."
        );
    }

    /**
     * Displays all places in the place list.
     *
     * @param places The PlaceList containing places to display.
     */
    public void showPlacesList(PlaceList places) {
        if (places.size() == 0) {
            showMessages("There are no places in your list.");
            return;
        }
        String[] lines = new String[places.size() + 1];
        lines[0] = "Here are the places in your list:";
        for (int i = 0; i < places.size(); i++) {
            try {
                Place p = places.getPlace(i);
                lines[i + 1] = (i + 1) + ". " + p;
            } catch (DukeException e) {
                showError(e.getMessage());
                return;
            }
        }
        showMessages(lines);
    }

    /**
     * Displays places matching a search keyword.
     *
     * @param matchingPlaces The PlaceList containing matched places.
     */
    public void showFoundPlaces(PlaceList matchingPlaces) {
        if (matchingPlaces.size() == 0) {
            showMessages("There are no matching places found.");
            return;
        }
        String[] lines = new String[matchingPlaces.size() + 1];
        lines[0] = "Here are the matching places in your list:";
        for (int i = 0; i < matchingPlaces.size(); i++) {
            try {
                Place p = matchingPlaces.getPlace(i);
                lines[i + 1] = (i + 1) + ". " + p;
            } catch (DukeException e) {
                showError(e.getMessage());
                return;
            }
        }
        showMessages(lines);
    }

    /**
     * Returns the most recent message formatted by the UI.
     *
     * @return The latest response string.
     */
    public String getLastResponse() {
        return lastResponse;
    }
}

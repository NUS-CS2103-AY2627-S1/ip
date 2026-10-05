package kaykay.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import kaykay.model.Place;
import kaykay.model.PlaceList;
import kaykay.model.Task;
import kaykay.model.TaskList;

/**
 * Handles all console input and output for the Kaykay chatbot.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BANNER = "#   #   ###   #   #  #   #   ###   #   #\n"
            + "#  #   #   #   # #   #  #   #   #   # #\n"
            + "###    #####    #    ###    #####    #\n"
            + "#  #   #   #    #    #  #   #   #    #\n"
            + "#   #  #   #    #    #   #  #   #    #";

    /** Reads commands typed by the user. */
    private final Scanner scanner;

    /** Receives rendered output lines from the UI. */
    private final Consumer<String> output;

    /** Receives rendered error lines from the UI. */
    private final Consumer<String> errorOutput;

    /** Receives rendered success lines from the UI. */
    private final Consumer<String> successOutput;

    /** Whether console-style separators should be rendered. */
    private final boolean shouldShowSeparators;

    /** Creates a UI that reads from standard input and writes to standard output. */
    public Ui() {
        this(System.out::println, System.out::println, System.out::println, true);
    }

    /**
     * Creates a UI that reads from standard input and sends output to a consumer.
     *
     * @param output receiver for rendered output lines.
     */
    public Ui(Consumer<String> output) {
        this(output, output, output, true);
    }

    /**
     * Creates a UI that reads from standard input and configures separator rendering.
     *
     * @param output receiver for rendered output lines.
     * @param shouldShowSeparators whether console-style separators should be rendered.
     */
    public Ui(Consumer<String> output, boolean shouldShowSeparators) {
        this(output, output, output, shouldShowSeparators);
    }

    /**
     * Creates a UI that can route normal and error output to different consumers.
     *
     * @param output receiver for normal rendered output lines.
     * @param errorOutput receiver for rendered error lines.
     * @param shouldShowSeparators whether console-style separators should be rendered.
     */
    public Ui(Consumer<String> output, Consumer<String> errorOutput, boolean shouldShowSeparators) {
        this(output, errorOutput, output, shouldShowSeparators);
    }

    /**
     * Creates a UI that can route normal, error, and success output to different consumers.
     *
     * @param output receiver for normal rendered output lines.
     * @param errorOutput receiver for rendered error lines.
     * @param successOutput receiver for rendered success lines.
     * @param shouldShowSeparators whether console-style separators should be rendered.
     */
    public Ui(Consumer<String> output, Consumer<String> errorOutput, Consumer<String> successOutput,
            boolean shouldShowSeparators) {
        scanner = new Scanner(System.in);
        this.output = output;
        this.errorOutput = errorOutput;
        this.successOutput = successOutput;
        this.shouldShowSeparators = shouldShowSeparators;
    }

    /**
     * Checks whether another input line is available.
     *
     * @return true if the user or input stream has another line.
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims one command from standard input.
     *
     * @return the next command without leading or trailing whitespace.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Shows Kaykay's greeting. */
    public void showWelcome() {
        showLine(SEPARATOR);
        showLine(BANNER);
        showLine(SEPARATOR);
        showLine("Kaykay online!");
        showLine("What's our next mission?");
        showLine(SEPARATOR);
    }

    /** Shows the message used when saved application data cannot be loaded. */
    public void showLoadingError() {
        showError("I couldn't load your data. Please check the data file.");
    }

    /**
     * Shows all tasks with their one-based positions.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        showLine(SEPARATOR);
        showLine("Here are your current missions:");
        for (int i = 0; i < tasks.size(); i += 1) {
            showLine(String.format("%d. %s", i + 1, tasks.getTask(i)));
        }
        showLine(SEPARATOR);
    }

    /**
     * Shows matching tasks with positions relative to the search results.
     *
     * @param matchingTasks tasks that matched the user's search keyword.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showLine(SEPARATOR);
        showLine("Here's what I found:");
        for (int i = 0; i < matchingTasks.size(); i += 1) {
            showLine(String.format("%d. %s", i + 1, matchingTasks.get(i)));
        }
        showLine(SEPARATOR);
    }

    /**
     * Shows all saved places with their one-based positions.
     *
     * @param places places to display.
     */
    public void showPlaceList(PlaceList places) {
        showLine(SEPARATOR);
        showLine("Here are your logged locations:");
        for (int i = 0; i < places.size(); i += 1) {
            showLine(String.format("%d. %s", i + 1, places.getPlace(i)));
        }
        showLine(SEPARATOR);
    }

    /**
     * Shows places that matched a search keyword.
     *
     * @param matchingPlaces places that matched the user's search keyword.
     */
    public void showMatchingPlaces(List<Place> matchingPlaces) {
        showLine(SEPARATOR);
        showLine("Here are the matching locations:");
        for (int i = 0; i < matchingPlaces.size(); i += 1) {
            showLine(String.format("%d. %s", i + 1, matchingPlaces.get(i)));
        }
        showLine(SEPARATOR);
    }

    /**
     * Shows the confirmation after adding a place.
     *
     * @param place place that was added.
     * @param placeCount number of places after the addition.
     */
    public void showAddedPlace(Place place, int placeCount) {
        showLine(SEPARATOR);
        showSuccessLine("Location logged:");
        showSuccessLine(place.toString());
        showSuccessLine(formatPlaceCount(placeCount));
        showLine(SEPARATOR);
    }

    /**
     * Shows the confirmation after editing a place.
     *
     * @param place place containing the updated details.
     */
    public void showEditedPlace(Place place) {
        showLine(SEPARATOR);
        showSuccessLine("Location updated:");
        showSuccessLine(place.toString());
        showLine(SEPARATOR);
    }

    /**
     * Shows the confirmation after deleting a place.
     *
     * @param place place that was deleted.
     * @param placeCount number of places after the deletion.
     */
    public void showDeletedPlace(Place place, int placeCount) {
        showLine(SEPARATOR);
        showSuccessLine("Location removed:");
        showSuccessLine("  " + place);
        showSuccessLine(formatPlaceCount(placeCount));
        showLine(SEPARATOR);
    }

    /** Returns a grammatically correct saved-place count. */
    private static String formatPlaceCount(int placeCount) {
        String placeWord = placeCount == 1 ? "place" : "places";
        return String.format("Now you have %d %s saved.", placeCount, placeWord);
    }

    /**
     * Shows the standard confirmation after adding a task.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     */
    public void showAddedTask(Task task, int taskCount) {
        showLine(SEPARATOR);
        showSuccessLine("Mission added:");
        showSuccessLine(task.toString());
        showSuccessLine(formatTaskCount(taskCount));
        showLine(SEPARATOR);
    }

    /**
     * Shows the confirmation after scheduling an event.
     *
     * @param event event that was scheduled.
     * @param taskCount number of tasks after the addition.
     */
    public void showScheduledEvent(Task event, int taskCount) {
        showLine(SEPARATOR);
        showSuccessLine("Event scheduled:");
        showSuccessLine(event.toString());
        showSuccessLine(formatTaskCount(taskCount));
        showLine(SEPARATOR);
    }

    /**
     * Shows that a task with identical details already exists.
     *
     * @param task existing task that matches the requested task.
     * @param taskNumber one-based mission number of the existing task.
     */
    public void showDuplicateTask(Task task, int taskNumber) {
        showLine(SEPARATOR);
        showLine(String.format("Those task details already exist as mission %d:", taskNumber));
        showLine(task.toString());
        showLine(SEPARATOR);
    }

    /**
     * Shows that a place with identical details is already saved.
     *
     * @param place existing place that matches the requested place.
     * @param placeNumber one-based number of the existing place.
     */
    public void showDuplicatePlace(Place place, int placeNumber) {
        showLine(SEPARATOR);
        showLine(String.format("Those place details already exist as place %d:", placeNumber));
        showLine(place.toString());
        showLine(SEPARATOR);
    }

    /**
     * Shows the standard confirmation after deleting a task.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after the deletion.
     */
    public void showDeletedTask(Task task, int taskCount) {
        showLine(SEPARATOR);
        showSuccessLine("Mission removed:");
        showSuccessLine("  " + task);
        showSuccessLine(formatTaskCount(taskCount));
        showLine(SEPARATOR);
    }

    /**
     * Shows the standard confirmation after marking or unmarking a task.
     *
     * @param task task whose status changed.
     * @param isMarked whether the task was marked as done.
     */
    public void showMarkedTask(Task task, boolean isMarked) {
        showLine(SEPARATOR);
        if (isMarked) {
            showSuccessLine("Mission complete! Nicely done.");
        } else {
            showSuccessLine("Mission reopened. Let's get back to it.");
        }
        showSuccessLine(task.toString());
        showLine(SEPARATOR);
    }

    /**
     * Shows that a mission already has the requested completion status.
     *
     * @param task task whose status did not need to change.
     * @param isMarked whether the mission was already complete.
     */
    public void showUnchangedTaskStatus(Task task, boolean isMarked) {
        showLine(SEPARATOR);
        if (isMarked) {
            showLine("This mission is already complete:");
        } else {
            showLine("This mission is already open:");
        }
        showLine(task.toString());
        showLine(SEPARATOR);
    }

    /**
     * Shows an error surrounded by the chatbot's standard separator.
     *
     * @param message error message to display.
     */
    public void showError(String message) {
        showLine(SEPARATOR);
        errorOutput.accept("Mission snag: " + message);
        showLine(SEPARATOR);
    }

    /** Shows Kaykay's farewell. */
    public void showFarewell() {
        showLine(SEPARATOR);
        showLine("All changes saved. Kaykay signing off!");
        showLine(SEPARATOR);
    }

    /** Shows the farewell used when invalid startup data was left unchanged. */
    public void showProtectedDataFarewell() {
        showLine(SEPARATOR);
        showLine("Kaykay signing off. Your existing data file was not changed.");
        showLine(SEPARATOR);
    }

    /** Sends one rendered success line to the configured success receiver. */
    private void showSuccessLine(String line) {
        successOutput.accept(line);
    }

    /** Returns a grammatically correct task count. */
    private static String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return String.format("Now you have %d %s in the list.", taskCount, taskWord);
    }

    /** Sends one rendered line to the configured output receiver. */
    private void showLine(String line) {
        if (shouldShowSeparators || !line.equals(SEPARATOR)) {
            output.accept(line);
        }
    }
}

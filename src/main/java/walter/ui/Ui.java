package walter.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import walter.place.Place;
import walter.task.Deadline;
import walter.task.Task;

/**
 * Handles Walter's terminal input and presents all user-facing output.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final DateTimeFormatter DATE_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final String BANNER = """
            ██╗    ██╗ █████╗ ██╗  ████████╗███████╗██████╗
            ██║    ██║██╔══██╗██║  ╚══██╔══╝██╔════╝██╔══██╗
            ██║ █╗ ██║███████║██║     ██║   █████╗  ██████╔╝
            ██║███╗██║██╔══██║██║     ██║   ██╔══╝  ██╔══██╗
            ╚███╔███╔╝██║  ██║███████╗██║   ███████╗██║  ██║
             ╚══╝╚══╝ ╚═╝  ╚═╝╚══════╝╚═╝   ╚══════╝╚═╝  ╚═╝
            """;

    private final Scanner scanner;
    private final PrintStream output;
    private final boolean isTerminal;

    /**
     * Creates a terminal UI that reads standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out;
        isTerminal = true;
    }

    /**
     * Creates an output-only UI for presenting one command response.
     *
     * @param output Destination for user-facing output.
     */
    public Ui(PrintStream output) {
        scanner = null;
        this.output = output;
        isTerminal = false;
    }

    /**
     * Reports whether another command is available.
     *
     * @return {@code true} if standard input contains another line.
     */
    public boolean hasNextCommand() {
        return scanner != null && scanner.hasNextLine();
    }

    /**
     * Reads the next raw command line.
     *
     * @return Next line from standard input without parsing or normalization.
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This UI does not read commands.");
        }
        return scanner.nextLine();
    }

    /**
     * Displays Walter's banner, greeting, and an optional loading warning.
     *
     * @param loadWarning Warning to display after the greeting, or {@code null} for none.
     */
    public void showWelcome(String loadWarning) {
        showSeparator();
        output.print(BANNER);
        output.println("Jesse, focus. I'm Walter.");
        output.println("Tell me what needs doing. We'll keep the operation precise.");
        if (loadWarning != null) {
            output.println(loadWarning);
        }
        showSeparator();
    }

    /**
     * Displays one output separator.
     */
    public void showSeparator() {
        output.println(SEPARATOR);
    }

    /**
     * Displays Walter's exit message and closing separator.
     */
    public void showGoodbye() {
        output.println("All right. The operation is closed. Stay focused, Jesse.");
        if (isTerminal) {
            showSeparator();
        }
    }

    /**
     * Displays an expected user-facing error.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        output.println(message + " Stay sharp, Jesse.");
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks Tasks to display in numbered order.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            output.println("The board is clean. Nothing needs doing.");
            return;
        }

        output.println("Here are your tasks:");
        showNumberedTasks(tasks);
    }

    /**
     * Displays Deadline tasks matching a date, or the existing no-match message.
     *
     * @param date Date used in the heading or no-match message.
     * @param deadlines Matching deadlines in display order.
     */
    public void showDeadlinesOn(LocalDate date, List<Deadline> deadlines) {
        String displayDate = date.format(DATE_DISPLAY_FORMATTER);
        if (deadlines.isEmpty()) {
            output.println("No deadlines are scheduled for " + displayDate + ".");
            return;
        }

        output.println("Here is the schedule for " + displayDate + ":");
        showNumberedTasks(deadlines);
    }

    /**
     * Displays tasks whose descriptions matched a find keyword.
     *
     * @param tasks Complete task list.
     * @param matchingIndices Zero-based indices of matching tasks.
     */
    public void showFindResults(List<Task> tasks, List<Integer> matchingIndices) {
        if (matchingIndices.isEmpty()) {
            output.println("I found no tasks matching that keyword.");
            return;
        }

        output.println("Here's what I found:");
        for (int taskIndex : matchingIndices) {
            output.println((taskIndex + 1) + ". " + tasks.get(taskIndex));
        }
    }

    /**
     * Displays saved places in insertion order using one-based numbering.
     *
     * @param places Places to display.
     */
    public void showPlaces(List<Place> places) {
        if (places.isEmpty()) {
            output.println("The location list is empty.");
            return;
        }

        output.println("Here are the recorded locations:");
        for (int i = 0; i < places.size(); i++) {
            output.println((i + 1) + ". " + places.get(i));
        }
    }

    /**
     * Displays tasks in their existing order using one-based numbering.
     *
     * @param tasks Tasks to display.
     */
    private void showNumberedTasks(List<? extends Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks after the addition.
     */
    public void showAddedTask(Task task, int taskCount) {
        output.println("Good. That's on the list now:");
        output.println(task);
        showTaskCount(taskCount);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task Task that was deleted.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showDeletedTask(Task task, int taskCount) {
        output.println("Done. I've removed this from the list:");
        output.println(task);
        showTaskCount(taskCount);
    }

    /**
     * Displays confirmation that a place was saved.
     *
     * @param place Place that was saved.
     * @param placeCount Number of places after the addition.
     */
    public void showAddedPlace(Place place, int placeCount) {
        output.println("Good. I've recorded this location:");
        output.println(place);
        showPlaceCount(placeCount);
    }

    /**
     * Displays confirmation that a place was deleted.
     *
     * @param place Place that was deleted.
     * @param placeCount Number of places after the deletion.
     */
    public void showDeletedPlace(Place place, int placeCount) {
        output.println("Done. I've removed this location:");
        output.println(place);
        showPlaceCount(placeCount);
    }

    /**
     * Displays confirmation that a task was marked done.
     *
     * @param task Task whose status was changed.
     */
    public void showMarkedTask(Task task) {
        output.println("Done. Consider this one handled:");
        output.println(task);
    }

    /**
     * Displays confirmation that a task was marked not done.
     *
     * @param task Task whose status was changed.
     */
    public void showUnmarkedTask(Task task) {
        output.println("Understood. This goes back into the mix:");
        output.println(task);
    }

    /**
     * Displays the current task count with the correct singular or plural noun.
     *
     * @param taskCount Number of tasks to display.
     */
    private void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        output.println("The list now contains " + taskCount + " " + taskWord + ".");
    }

    /**
     * Displays the current place count with the correct singular or plural noun.
     */
    private void showPlaceCount(int placeCount) {
        String placeWord = placeCount == 1 ? "place" : "places";
        output.println("The location list now contains " + placeCount + " " + placeWord + ".");
    }
}

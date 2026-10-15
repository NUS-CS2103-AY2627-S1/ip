package ev.ui;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ev.DateTimes;
import ev.task.Task;
import ev.task.TaskList;

/**
 * Puts EV's replies into words and keeps the latest one.
 *
 * <p>This class only decides what the chatbot says. Where the words end up is left to
 * whoever is driving it: a caller takes the text with {@link #takeResponse()} and shows
 * it, and {@link ConsoleUi} additionally prints it as it is produced.
 */
public class Ui {

    private final StringBuilder response = new StringBuilder();

    /**
     * Returns everything said since the last call, and forgets it.
     *
     * @return the replies produced so far, or an empty string if there were none.
     */
    public String takeResponse() {
        String taken = response.toString().trim();
        response.setLength(0);
        return taken;
    }

    /** Greets the user. */
    public void showWelcome() {
        show("Hi Peter.\nEV online. What do you need?");
    }

    /** Says goodbye to the user. */
    public void showFarewell() {
        show("Signing off, Peter.");
    }

    /**
     * Shows something that went wrong.
     *
     * @param message the message of the exception, written for the user to read.
     */
    public void showError(String message) {
        show(message);
    }

    /**
     * Warns that part of the save file could not be read.
     *
     * @param count how many lines were skipped.
     * @param file the save file they were skipped in.
     */
    public void showSkippedLines(int count, Path file) {
        show("Skipped " + count + " unreadable line(s) in " + file + ".\n"
                + "The rest loaded. Next change rewrites the file.");
    }

    /**
     * Confirms that a task was added.
     *
     * @param task the task that was added.
     * @param tasks the list it went into, used to report the new size.
     */
    public void showAdded(Task task, TaskList tasks) {
        show("Added.\n  " + task + "\n" + tasks.describeSize() + ".");
    }

    /**
     * Confirms that a task was removed.
     *
     * @param task the task that was removed.
     * @param tasks the list it came out of, used to report the new size.
     */
    public void showRemoved(Task task, TaskList tasks) {
        show("Removed.\n  " + task + "\n" + tasks.describeSize() + ".");
    }

    /**
     * Confirms that the done status of a task changed.
     *
     * @param task the task in its new state.
     * @param isDone true if the task was just marked as done.
     */
    public void showMarked(Task task, boolean isDone) {
        String message = isDone
                ? "Done."
                : "Back to not done.";
        show(message + "\n  " + task);
    }

    /**
     * Confirms that one detail of a task changed.
     *
     * @param task the task in its new state.
     */
    public void showUpdated(Task task) {
        show("Updated.\n  " + task);
    }

    /**
     * Shows the whole list, numbered from 1, or says so when there is nothing in it.
     *
     * @param tasks the list to show.
     */
    public void showTasks(TaskList tasks) {
        showSelected(tasks, task -> true,
                "Your list:",
                "Nothing on your list.");
    }

    /**
     * Shows the tasks that happen on one date.
     *
     * <p>Each task keeps the number it has in the full list, so it can be marked or
     * deleted straight away without listing everything first.
     *
     * @param date the date being asked about.
     * @param tasks the list to look through.
     */
    public void showTasksOn(LocalDate date, TaskList tasks) {
        showSelected(tasks, task -> task.occursOn(date),
                "On " + DateTimes.format(date) + ":",
                "Nothing on " + DateTimes.format(date) + ".");
    }

    /**
     * Shows the tasks whose description contains the given text.
     *
     * <p>As with {@link #showTasksOn}, each task keeps the number it has in the full
     * list, so a task can be marked or deleted straight after finding it.
     *
     * @param keyword the text that was searched for.
     * @param tasks the list to look through.
     */
    public void showMatchingTasks(String keyword, TaskList tasks) {
        showSelected(tasks, task -> task.hasKeyword(keyword),
                "Matches:",
                "No match for \"" + keyword + "\".");
    }

    /**
     * Shows the tasks a listing is interested in, keeping the number each one has in the
     * full list so that it can be marked or deleted straight away.
     *
     * @param tasks the list to look through.
     * @param isWanted decides which tasks belong in this listing.
     * @param heading the line shown above the tasks, when there are any.
     * @param noneFound the whole reply, when no task qualifies.
     */
    private void showSelected(TaskList tasks, Predicate<Task> isWanted,
            String heading, String noneFound) {
        String listing = numberedListing(tasks, isWanted);
        show(listing.isEmpty() ? noneFound : heading + listing);
    }

    /**
     * Records one reply. Subclasses override this to also send it somewhere.
     *
     * @param message the text of the reply, which may span several lines.
     */
    protected void show(String message) {
        if (response.length() > 0) {
            response.append("\n");
        }
        response.append(message);
    }

    /**
     * Returns the wanted tasks as numbered lines, one per line.
     *
     * <p>The number is the position in the full list rather than in the result, so a task
     * found by a search can be marked or deleted straight away.
     *
     * @param tasks the list to look through.
     * @param isWanted decides which tasks belong in the listing.
     * @return the lines, each starting with a line break, or an empty string if none qualify.
     */
    private String numberedListing(TaskList tasks, Predicate<Task> isWanted) {
        return IntStream.range(0, tasks.size())
                .filter(index -> isWanted.test(tasks.get(index)))
                .mapToObj(index -> "\n" + (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining());
    }
}

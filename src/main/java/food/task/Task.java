package food.task;

import food.exception.FoodInputException;
import food.exception.FoodStorageException;

/**
 * A single item on the user's list. Subclasses add the details specific to each kind of task.
 *
 * <p>This class holds what every task has regardless of kind: a description, whether it is done,
 * and the line the user originally typed. Keeping that original line is what lets a task be
 * written to disk and read back without a separate save format having to be invented for each
 * subclass; see {@link #toSaveFormat} and {@link #fromSaveFormat}.
 */
public class Task {
    /** The description shown to the user, e.g. "read book". */
    private String title;
    /** Whether the user has marked this task done; shown as the "X" in the display form. */
    private boolean isCompleted = false;
    /** The line the user typed, kept so the task can be saved and rebuilt verbatim. */
    private final String input;

    /**
     * Creates a task. Called through {@code super(...)} by each subclass once it has picked the
     * description out of the user's line.
     *
     * @param title the description to show the user.
     * @param input the untouched line the user typed, used later when saving.
     * @throws FoodInputException if the description is empty or only spaces, which almost always
     *                            means the user left the description off.
     */
    public Task(String title, String input) throws FoodInputException {
        if (title.isBlank()) {
            throw new FoodInputException("An empty order? I can't cook nothing, chef.");
        }
        this.input = input;
        this.title = title;
    }

    /**
     * Returns the task as one line for the save file, e.g. "1 | todo read book".
     *
     * <p>The completed flag is written as 0 or 1 in front of the original input, because the
     * input alone does not record whether the task was later marked done.
     *
     * @return the line to write to the save file.
     */
    public String toSaveFormat() {
        return String.format("%d | %s", this.isCompleted ? 1 : 0, this.input);
    }

    /**
     * Rebuilds a Task from a line written by {@link #toSaveFormat}, e.g. "1 | todo read book".
     *
     * <p>The original user input was saved, so the normal constructors can parse it again.
     * A line those constructors reject means the save file is damaged, not that the user typed
     * something wrong, so the FoodInputException they throw is translated into a
     * FoodStorageException before it leaves this method.
     *
     * @param line one line of the save file.
     * @return the reconstructed task, with its completed flag restored.
     * @throws FoodStorageException if the line is not in the expected format.
     */
    public static Task fromSaveFormat(String line) throws FoodStorageException {
        // Limit of 2 so a " | " inside the task description is not split away.
        String[] parts = line.split(" \\| ", 2);
        if (parts.length != 2) {
            throw new FoodStorageException("Save file is corrupted: " + line);
        }
        String input = parts[1];
        String command = input.split(" ")[0];

        Task task;
        try {
            task = switch (command) {
                case "todo" -> new Todo(input);
                case "deadline" -> new Deadline(input);
                case "event" -> new Event(input);
                default -> throw new FoodStorageException("Save file is corrupted: " + line);
            };
        } catch (FoodInputException e) {
            throw new FoodStorageException("Save file is corrupted: " + line, e);
        }

        if (parts[0].equals("1")) {
            task.markComplete();
        }
        return task;
    }

    /**
     * Returns whether this task's description contains the given keyword, ignoring case.
     *
     * <p>The task answers this itself so that the description stays private: callers can search
     * without being handed the field to compare on.
     *
     * @param keyword the text to look for.
     * @return true if the description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        return this.title.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns whether this task and another were created from the same command line, i.e. the
     * user is adding something already on the list. Whether either is done is ignored, since
     * "read book" is the same task whether or not it has been finished.
     *
     * @param other the task to compare with.
     * @return true if both were typed identically.
     */
    public boolean isSameAs(Task other) {
        return this.input.equals(other.input);
    }

    /**
     * Rejects a command line that gives the same parameter twice, e.g. "deadline x /by 1 /by 2".
     * Left unchecked, the regex would silently swallow the second copy into the first value and
     * produce a confusing date error instead.
     *
     * @param input the full command line.
     * @param flag  the parameter marker to look for, e.g. "/by".
     * @throws FoodInputException if the marker appears more than once.
     */
    protected static void rejectRepeated(String input, String flag) throws FoodInputException {
        if (input.split(" " + flag + " ", -1).length > 2) {
            throw new FoodInputException(String.format("One %s per ticket, chef.", flag));
        }
    }

    /** Marks this task done. Marking an already-done task again changes nothing. */
    public void markComplete() {
        this.isCompleted = true;
    }

    /** Marks this task not done, undoing {@link #markComplete}. */
    public void markIncomplete() {
        this.isCompleted = false;
    }

    /**
     * Returns the status and description, e.g. "[X] read book" when done and "[] read book" when
     * not. Each subclass prepends its own symbol to this.
     *
     * @return the shared part of the display form.
     */
    @Override
    public String toString() {
        String status = "";
        if (this.isCompleted) {
            status = "X";
        }
        return String.format("[%s] %s", status, this.title);
    }
}

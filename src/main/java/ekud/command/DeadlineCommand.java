package ekud.command;

import ekud.EkudException;
import ekud.parser.Parser;
import ekud.storage.Storage;
import ekud.task.Deadline;
import ekud.task.Task;
import ekud.task.TaskDateTime;

/**
 * Adds a {@link Deadline} to the task list.
 */
public class DeadlineCommand implements TaskCreatingCommand {
    private static final String FLAG_BY = "/by";

    private final String description;
    private final TaskDateTime by;

    /**
     * Creates a command that will add a deadline.
     *
     * @param description text of the deadline to add
     * @param by          due date or date-time
     */
    private DeadlineCommand(String description, TaskDateTime by) {
        this.description = description;
        this.by = by;
    }

    /**
     * Parses {@code deadline DESCRIPTION /by WHEN}.
     * {@code WHEN} must be a supported date or date-time, for example
     * {@code 2019-12-02} or {@code 2/12/2019 1800}.
     *
     * @param arguments text after the command word
     * @return a deadline command
     * @throws EkudException if the description, {@code /by}, or date/time is missing or invalid
     */
    public static DeadlineCommand parse(String arguments) throws EkudException {
        if (arguments.isBlank()) {
            throw new EkudException("The description of a deadline cannot be empty.");
        }
        int byIndex = Parser.indexOfFlag(arguments, FLAG_BY);
        if (byIndex < 0) {
            throw new EkudException(
                    "Please provide a deadline using /by, e.g. deadline return book /by 2019-12-02.");
        }
        String description = arguments.substring(0, byIndex).trim();
        String byText = arguments.substring(byIndex + FLAG_BY.length()).trim();
        if (description.isEmpty()) {
            throw new EkudException("The description of a deadline cannot be empty.");
        }
        if (byText.isEmpty()) {
            throw new EkudException(
                    "Please provide a date/time after /by, e.g. deadline return book /by 2019-12-02.");
        }
        return new DeadlineCommand(description, TaskDateTime.parse(byText));
    }

    /**
     * Builds the deadline without adding it to the list.
     * {@link Storage} uses this when loading a saved {@code deadline} line.
     *
     * @return a new incomplete deadline
     */
    @Override
    public Task createTask() {
        return new Deadline(description, by);
    }
}

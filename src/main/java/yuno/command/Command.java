package yuno.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.InvalidTaskNumberException;
import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.TaskList;
import yuno.ui.Ui;
import yuno.util.DateTimeFormats;

/**
 * Represents a user command and executes its associated behavior.
 */
public abstract class Command {
    /** Stores the arguments supplied after the command keyword. */
    private final String commandArguments;

    /**
     * Creates a command with the specified arguments.
     *
     * @param commandArguments Arguments supplied after the command keyword.
     */
    public Command(String commandArguments) {
        this.commandArguments = commandArguments;
    }

    /**
     * Returns the arguments supplied after the command keyword.
     *
     * @return Arguments supplied for this command.
     */
    protected String getCommandArguments() {
        return commandArguments;
    }

    /**
     * Returns the date represented by the specified user input.
     *
     * @param dateText Date text entered by the user.
     * @return Parsed date.
     * @throws InvalidCommandFormatException If the text does not follow the required input format.
     */
    protected LocalDate parseInputDate(String dateText) throws InvalidCommandFormatException {
        try {
            return LocalDate.parse(dateText, DateTimeFormats.DATE_INPUT_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new InvalidCommandFormatException(
                    "Memorize the date format before you even type. It's supposed to be yyyy-MM-dd.");
        }
    }

    /**
     * Returns the date-time represented by the specified user input.
     * Date-only input is interpreted as midnight at the start of that date.
     *
     * @param dateOrDateTimeText Date or date-time text entered by the user.
     * @return Parsed date-time.
     * @throws InvalidCommandFormatException If the text does not follow the required input format.
     */
    protected LocalDateTime parseInputDateTime(String dateOrDateTimeText)
            throws InvalidCommandFormatException {
        try {
            return LocalDateTime.parse(dateOrDateTimeText, DateTimeFormats.DATE_TIME_INPUT_FORMATTER);
        } catch (DateTimeParseException dateTimeException) {
            try {
                return LocalDate.parse(
                        dateOrDateTimeText, DateTimeFormats.DATE_INPUT_FORMATTER).atStartOfDay();
            } catch (DateTimeParseException dateException) {
                throw new InvalidCommandFormatException(
                        "Memorize the date format before you even type. "
                                + "It's either yyyy-MM-dd HHmm or yyyy-MM-dd.");
            }
        }
    }

    /**
     * Returns the task number represented by this command's arguments.
     *
     * @return Task number entered by the user.
     * @throws InvalidTaskNumberException If the command arguments are not an integer.
     */
    protected int parseTaskNumber() throws InvalidTaskNumberException {
        try {
            return Integer.parseInt(getCommandArguments());
        } catch (NumberFormatException exception) {
            throw new InvalidTaskNumberException(
                    "Did you even give me an integer? Please don't waste my time!");
        }
    }

    /**
     * Executes this command using the specified task list, user interface, and storage.
     *
     * @param taskList Task list to read from or modify.
     * @param ui User interface used to display results.
     * @param storage Storage used to save task list changes.
     * @return Result indicating whether the chatbot should continue accepting commands.
     * @throws YunoException If the command cannot be completed due to invalid input or task data.
     */
    public abstract CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException;
}

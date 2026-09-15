package gepit.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import gepit.GepitException;
import gepit.task.Deadline;
import gepit.task.Event;
import gepit.task.Todo;

/**
 * Parses user input into commands and task information.
 */
public class Parser {

    /**
     * Returns the command word from the specified user input.
     *
     * @param input User input to parse.
     * @return Command word.
     */
    public static String getCommand(String input) {
        return input.strip().split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
    }

    /**
     * Returns the argument following the command word.
     *
     * @param input User input to parse.
     * @return Argument following the command.
     * @throws GepitException If the command has no argument.
     */
    public static String getArgument(String input) throws GepitException {
        String[] parts = input.strip().split("\\s+", 2);

        if (parts.length < 2 || parts[1].isBlank()) {
            throw new GepitException(
                    "Oi, this command needs more information.");
        }

        return parts[1].strip();
    }

    /**
     * Parses a user-facing task number.
     *
     * @param input gepit.task.Task number to parse.
     * @return Parsed task number.
     * @throws GepitException If the input is not a valid integer.
     */
    public static int parseTaskNumber(String input)
            throws GepitException {
        try {
            int taskNumber = Integer.parseInt(input.strip());
            if (taskNumber <= 0) {
                throw new GepitException("Task numbers start from 1.");
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new GepitException(
                    input + " isn't a valid task number");
        }
    }

    /**
     * Creates a todo from its description.
     *
     * @param input gepit.task.Todo description.
     * @return Parsed todo.
     */
    public static Todo parseTodo(String input) throws GepitException {
        String description = validateDescription(input);
        return new Todo(description);
    }

    /**
     * Parses a deadline command argument.
     *
     * @param input gepit.task.Deadline description and date.
     * @return Parsed deadline.
     * @throws GepitException If the deadline format is invalid.
     */
    public static Deadline parseDeadline(String input)
            throws GepitException {
        String[] parts = input.strip().split("\\s+/by\\s+", -1);

        if (parts.length != 2
                || parts[0].isBlank()
                || parts[1].isBlank()) {
            throw new GepitException(
                    "A deadline should look like:"
                            + "\ndeadline return book /by 2026-09-10");
        }

        String description = validateDescription(parts[0]);

        try {
            LocalDate due = LocalDate.parse(parts[1].strip());
            return new Deadline(description, due);
        } catch (DateTimeParseException e) {
            throw new GepitException(
                    "Deadline dates should use yyyy-MM-dd, "
                            + "e.g. 2026-09-10");
        }
    }

    /**
     * Parses an event command argument.
     *
     * @param input gepit.task.Event description, start date, and end date.
     * @return Parsed event.
     * @throws GepitException If the event format is invalid.
     */
    public static Event parseEvent(String input)
            throws GepitException {
        String[] parts = input.strip().split("\\s+/from\\s+", -1);

        if (parts.length != 2 || parts[0].isBlank()) {
            throw new GepitException(
                    "An event should look like:"
                            + "\nevent meeting /from 2026-09-10 "
                            + "/to 2026-09-11");
        }

        String description = validateDescription(parts[0]);
        String[] dates = parts[1].split("\\s+/to\\s+", -1);

        if (dates.length < 2
                || dates.length > 2
                || dates[0].isBlank()
                || dates[1].isBlank()) {
            throw new GepitException(
                    "An event should look like:"
                            + "\nevent meeting /from 2026-09-10 "
                            + "/to 2026-09-11");
        }

        try {
            LocalDate start = LocalDate.parse(dates[0].strip());
            LocalDate end = LocalDate.parse(dates[1].strip());

            if (!end.isAfter(start)) {
                throw new GepitException(
                        "An event must end after it starts.");
            }

            return new Event(description, start, end);
        } catch (DateTimeParseException e) {
            throw new GepitException(
                    "Event dates should use yyyy-MM-dd, "
                            + "e.g. event meeting /from 2026-09-10 "
                            + "/to 2026-09-11");
        }
    }

    /**
     * Represents the parsed arguments of an update command.
     *
     * @param taskNumber Task number to update.
     * @param description New task description.
     */
    public record UpdateArguments(int taskNumber, String description) {
    }

    /**
     * Parses an update command argument.
     *
     * @param input Task number followed by the new description.
     * @return Parsed update arguments.
     * @throws GepitException If the task number or description is invalid.
     */
    public static UpdateArguments parseUpdate(String input)
            throws GepitException {
        String[] parts = input.strip().split("\\s+", 2);

        if (parts.length < 2 || parts[1].isBlank()) {
            throw new GepitException(
                    "An update should look like:"
                            + "\nupdate 1 new description");
        }

        int taskNumber = parseTaskNumber(parts[0]);
        String description = validateDescription(parts[1]);

        return new UpdateArguments(taskNumber, description);
    }

    private static String validateDescription(String input)
            throws GepitException {
        String description = input.strip();
        if (description.isEmpty()) {
            throw new GepitException("A task description cannot be empty.");
        }
        if (description.contains(" | ")) {
            throw new GepitException(
                    "A task description cannot contain ' | '.");
        }
        return description;
    }
}

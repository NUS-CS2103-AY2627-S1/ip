package joebiden.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import joebiden.exception.JoeBidenException;
import joebiden.task.Deadline;
import joebiden.task.Event;




/**
 * Parses user input and converts command arguments into usable values and tasks.
 */
public class Parser {
    /**
     * Extracts the command from the user input.
     *
     * @param input Raw user input.
     * @return Command word in lowercase.
     */
    public static String getCommand(String input) {
        String[] parts = input.split(" ", 2);
        return parts[0].toLowerCase();
    }
    /**
     * Extracts the arguments from the user input.
     *
     * @param input Raw user input.
     * @return Command arguments, or an empty string if none are provided.
     */
    public static String getArguments(String input) {
        String[] parts = input.split(" ", 2);

        if (parts.length < 2) {
            return "";
        }

        return parts[1];
    }

    /**
     * Checks that a command does not contain any arguments.
     *
     * @param command Command being validated.
     * @param arguments Arguments supplied with the command.
     * @throws JoeBidenException If arguments are provided.
     */
    public static void validateNoArguments(String command, String arguments)
            throws JoeBidenException {

        if (!arguments.isBlank()) {
            throw new JoeBidenException(
                    "The " + command + " command does not take any arguments."
            );
        }
    }
    /**
     * Parses a task number from the given arguments.
     *
     * @param arguments Arguments containing the task number.
     * @return Parsed task number.
     * @throws JoeBidenException If the task number is missing or not numeric.
     */
    public static int getTaskNumber(String arguments) throws JoeBidenException {
        if (arguments.isBlank()) {
            throw new JoeBidenException(
                    "Please specify a task number."
            );
        }

        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new JoeBidenException(
                    "Task number must be a number."
            );
        }
    }
    /**
     * Parses event arguments and creates an Event.
     *
     * @param arguments Event description, start time, and end time.
     * @return Event created from the arguments.
     * @throws JoeBidenException If the event format or date format is invalid.
     */
    public static Event parseEvent(String arguments)
            throws JoeBidenException {

        if (arguments.isBlank()) {
            throw new JoeBidenException(
                    "The description of an event cannot be empty."
            );
        }

        String[] fromParts = arguments.split(" /from ", 2);

        if (fromParts.length < 2
                || fromParts[0].isBlank()
                || fromParts[1].isBlank()) {
            throw new JoeBidenException(
                    "Event must be in the format: event <task> /from <start> /to <end>"
            );
        }

        String description = fromParts[0];

        String[] toParts = fromParts[1].split(" /to ", 2);

        if (toParts.length < 2
                || toParts[0].isBlank()
                || toParts[1].isBlank()) {
            throw new JoeBidenException(
                    "Event must be in the format: event <task> /from <start> /to <end>"
            );
        }

        DateTimeFormatter inputFormat =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

        try {
            LocalDateTime from =
                    LocalDateTime.parse(toParts[0], inputFormat);

            LocalDateTime to =
                    LocalDateTime.parse(toParts[1], inputFormat);

            return new Event(description, from, to);

        } catch (DateTimeParseException e) {
            throw new JoeBidenException(
                    "Invalid date. Use format: yyyy-MM-dd HHmm"
            );
        }
    }
    /**
     * Parses and validates a task description.
     *
     * @param arguments Arguments containing the task description.
     * @return Validated task description.
     * @throws JoeBidenException If the description is empty.
     */
    public static String parseDescription(String arguments)
            throws JoeBidenException {

        if (arguments.isBlank()) {
            throw new JoeBidenException(
                    "The description cannot be empty."
            );
        }

        return arguments;
    }
    /**
     * Validates and returns the keyword used for finding tasks.
     *
     * @param arguments Keyword entered by the user.
     * @return Validated search keyword.
     * @throws JoeBidenException If the keyword is empty.
     */
    public static String parseFindKeyword(String arguments)
            throws JoeBidenException {
        if (arguments.isBlank()) {
            throw new JoeBidenException(
                    "Please specify a keyword to find."
            );
        }

        return arguments;
    }
    /**
     * Parses deadline arguments and creates a Deadline.
     *
     * @param arguments Deadline description and due date.
     * @return Deadline created from the arguments.
     * @throws JoeBidenException If the deadline format or date format is invalid.
     */
    public static Deadline parseDeadline(String arguments)
            throws JoeBidenException {

        if (arguments.isBlank()) {
            throw new JoeBidenException(
                    "The description of a deadline cannot be empty."
            );
        }

        String[] deadlineParts = arguments.split(" /by ", 2);

        if (deadlineParts.length < 2
                || deadlineParts[0].isBlank()
                || deadlineParts[1].isBlank()) {
            throw new JoeBidenException(
                    "Deadline must be in the format: deadline <task> /by <date>"
            );
        }

        String description = deadlineParts[0];

        DateTimeFormatter inputFormat =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

        try {
            LocalDateTime by =
                    LocalDateTime.parse(deadlineParts[1], inputFormat);

            return new Deadline(description, by);

        } catch (DateTimeParseException e) {
            throw new JoeBidenException(
                    "Invalid date. Use format: yyyy-MM-dd HHmm"
            );
        }
    }
}

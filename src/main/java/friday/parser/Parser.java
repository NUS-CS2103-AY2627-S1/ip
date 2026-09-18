package friday.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import friday.command.AddCommand;
import friday.command.ArchiveCommand;
import friday.command.Command;
import friday.command.DeleteCommand;
import friday.command.ExitCommand;
import friday.command.FindCommand;
import friday.command.ListArchiveCommand;
import friday.command.ListCommand;
import friday.command.MarkCommand;
import friday.command.UnarchiveCommand;
import friday.command.UnmarkCommand;
import friday.exception.FridayException;
import friday.model.Deadline;
import friday.model.Event;
import friday.model.Task;
import friday.model.ToDo;

/**
 * Converts raw user input into validated commands for Friday to execute.
 */
public class Parser {
    private static final DateTimeFormatter DATE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter EVENT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter EVENT_COMPACT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Creates a parser for Friday commands.
     */
    public Parser() {
    }

    /**
     * Parses one line of user input.
     *
     * @param input Raw command entered by the user.
     * @return A parsed executable command.
     * @throws FridayException If the input is not a supported valid command.
     */
    public Command parse(String input) throws FridayException {
        if (input == null || input.isBlank()) {
            throw new FridayException("Please enter a command.");
        }
        String command = normalizeWhitespace(input);
        if (command.equals("bye")) {
            return new ExitCommand();
        } else if (command.equals("list")) {
            return new ListCommand();
        } else if (command.equals("list-archive")) {
            return new ListArchiveCommand();
        } else if (isCommandWord(command, "archive")) {
            return parseArchive(command);
        } else if (isCommandWord(command, "unarchive")) {
            return parseUnarchive(command);
        } else if (command.equals("find") || command.startsWith("find ")) {
            return new FindCommand(parseFindKeyword(command));
        } else if (command.startsWith("delete ")) {
            return new DeleteCommand(parseTaskNumber(command.substring(7)));
        } else if (command.startsWith("mark ")) {
            return new MarkCommand(parseTaskNumber(command.substring(5)));
        } else if (command.startsWith("unmark ")) {
            return new UnmarkCommand(parseTaskNumber(command.substring(7)));
        }

        return new AddCommand(parseTask(command));
    }

    /**
     * Parses a task-creation command into its concrete task type.
     *
     * @param command Full task-creation command.
     * @return The parsed task.
     * @throws FridayException If the command is unknown or malformed.
     */
    private static Task parseTask(String command) throws FridayException {
        if (isCommandWord(command, "todo")) {
            return parseTodo(command);
        } else if (isCommandWord(command, "deadline")) {
            return parseDeadline(command);
        } else if (isCommandWord(command, "event")) {
            return parseEvent(command);
        }

        throw new FridayException("I'm sorry, but I don't know what that means :-(");
    }

    /**
     * Converts a command's task-number argument into an integer.
     *
     * @param taskNumberText User-supplied task number.
     * @return The parsed task number.
     * @throws FridayException If the argument is not an integer.
     */
    private static int parseTaskNumber(String taskNumberText) throws FridayException {
        if (!taskNumberText.matches("[1-9][0-9]*")) {
            throw new FridayException("Please enter a valid task number.");
        }
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new FridayException("Please enter a valid task number.");
        }
    }

    /**
     * Parses an archive command for one task or all active tasks.
     *
     * @param input Full archive command.
     * @return A command that archives one or all active tasks.
     * @throws FridayException If the command syntax is invalid.
     */
    private static Command parseArchive(String input) throws FridayException {
        String argument = input.substring("archive".length()).trim();
        if (argument.equals("all")) {
            return new ArchiveCommand();
        }
        if (argument.isEmpty() || argument.contains(" ") || argument.indexOf("/by") >= 0) {
            throw new FridayException("Use this format: archive <task number> or archive all");
        }
        return new ArchiveCommand(parseTaskNumber(argument));
    }

    /**
     * Parses an unarchive command for one task or all archived tasks.
     *
     * @param input Full unarchive command.
     * @return A command that restores one or all archived tasks.
     * @throws FridayException If the command syntax is invalid.
     */
    private static Command parseUnarchive(String input) throws FridayException {
        String argument = input.substring("unarchive".length()).trim();
        if (argument.equals("all")) {
            return new UnarchiveCommand();
        }
        if (argument.isEmpty() || argument.contains(" ")) {
            throw new FridayException("Use this format: unarchive <archive number> or unarchive all");
        }
        try {
            return new UnarchiveCommand(Integer.parseInt(argument));
        } catch (NumberFormatException e) {
            throw new FridayException("Please enter a valid archived task number.");
        }
    }

    /**
     * Extracts and validates the keyword from a find command.
     *
     * @param command Full find command.
     * @return The non-empty search keyword.
     * @throws FridayException If no keyword was supplied.
     */
    private static String parseFindKeyword(String command) throws FridayException {
        String keyword = command.substring(4).trim();
        if (keyword.isEmpty()) {
            throw new FridayException("Please provide a keyword to find.");
        }
        return keyword;
    }

    /**
     * Checks whether an input contains a complete command word.
     *
     * @param command Full user input.
     * @param keyword Command word to check.
     * @return Whether the input is exactly the word or starts with it and a space.
     */
    private static boolean isCommandWord(String command, String keyword) {
        return command.equals(keyword) || command.startsWith(keyword + " ");
    }

    /**
     * Parses a todo command and validates its description.
     *
     * @param command Full todo command.
     * @return A new todo task.
     * @throws FridayException If the description is empty.
     */
    private static Task parseTodo(String command) throws FridayException {
        String description = command.substring(4).trim();
        if (description.isEmpty()) {
            throw new FridayException("The description of a todo cannot be empty.");
        }
        return new ToDo(description);
    }

    /**
     * Parses a deadline command and its strict ISO date.
     *
     * @param command Full deadline command.
     * @return A new deadline task.
     * @throws FridayException If the format or date is invalid.
     */
    private static Task parseDeadline(String command) throws FridayException {
        String details = command.substring(8).trim();
        String[] parts = details.split(" /by ", 2);

        if (parts.length < 2) {
            throw new FridayException("Use this format: deadline <description> /by <yyyy-MM-dd date>");
        }

        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty()) {
            throw new FridayException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new FridayException("Use this format: deadline <description> /by <yyyy-MM-dd date>");
        }

        try {
            return new Deadline(description, LocalDate.parse(by, DATE_INPUT_FORMAT));
        } catch (DateTimeParseException e) {
            throw new FridayException("Use this format: deadline <description> /by <yyyy-MM-dd date>");
        }
    }

    /**
     * Parses an event command and its start/end date-times.
     *
     * @param command Full event command.
     * @return A new event task.
     * @throws FridayException If the format or date-times are invalid.
     */
    private static Task parseEvent(String command) throws FridayException {
        String details = command.substring(5).trim();
        String[] firstSplit = details.split(" /from ", 2);

        if (firstSplit.length < 2) {
            throw eventFormatException();
        }

        String description = firstSplit[0].trim();
        String[] secondSplit = firstSplit[1].split(" /to ", 2);

        if (secondSplit.length < 2) {
            throw eventFormatException();
        }

        String fromText = secondSplit[0].trim();
        String toText = secondSplit[1].trim();

        if (description.isEmpty()) {
            throw new FridayException("The description of an event cannot be empty.");
        }
        if (fromText.isEmpty() || toText.isEmpty()) {
            throw eventFormatException();
        }

        LocalDateTime from = parseEventDateTime(fromText);
        LocalDateTime to = parseEventDateTime(toText);
        if (!from.isBefore(to)) {
            throw new FridayException("The event must start before it ends.");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses one event date-time in either supported 24-hour format.
     *
     * @param value User-supplied date-time.
     * @return The parsed date-time.
     * @throws FridayException If neither supported format matches.
     */
    private static LocalDateTime parseEventDateTime(String value) throws FridayException {
        try {
            return LocalDateTime.parse(value, EVENT_INPUT_FORMAT);
        } catch (DateTimeParseException firstFailure) {
            try {
                return LocalDateTime.parse(value, EVENT_COMPACT_INPUT_FORMAT);
            } catch (DateTimeParseException secondFailure) {
                throw eventFormatException();
            }
        }
    }

    /**
     * Creates the standard event-format validation error.
     *
     * @return An exception describing the accepted event syntax.
     */
    private static FridayException eventFormatException() {
        return new FridayException("Use this format: event <description> /from <yyyy-MM-dd HH:mm> "
                + "/to <yyyy-MM-dd HH:mm>");
    }

    /**
     * Trims outer whitespace and treats repeated whitespace as one separator.
     *
     * @param input Raw user input.
     * @return Canonical command text.
     */
    private static String normalizeWhitespace(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }
}

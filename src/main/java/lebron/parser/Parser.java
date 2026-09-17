package lebron.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import lebron.command.ByeCommand;
import lebron.command.Command;
import lebron.command.DeadlineCommand;
import lebron.command.DeleteCommand;
import lebron.command.EventCommand;
import lebron.command.FindCommand;
import lebron.command.ListCommand;
import lebron.command.MarkCommand;
import lebron.command.RecurringCommand;
import lebron.command.TodoCommand;
import lebron.command.UnmarkCommand;
import lebron.exception.LeBronException;
import lebron.task.RecurrenceInterval;

/**
 * Deals with making sense of raw user input, turning it into an executable Command.
 */
public class Parser {
    // "uuuu" (not "yyyy") is required for ResolverStyle.STRICT to work with LocalDateTime;
    // STRICT rejects non-existent dates like Feb 30 instead of silently rolling them over
    // to Feb 28, which the default (SMART) resolver style does.
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    private Parser() {
    }

    /**
     * Bundles the description and due date/time parsed out of a "deadline" command.
     */
    private record DeadlineArgs(String description, LocalDateTime by) {
    }

    /**
     * Bundles the description and start/end date/time parsed out of an "event" command.
     */
    private record EventArgs(String description, LocalDateTime start, LocalDateTime end) {
    }

    /**
     * Bundles the description, next due date/time, and interval parsed out of a "recur" command.
     */
    private record RecurringArgs(String description, LocalDateTime nextDue, RecurrenceInterval interval) {
    }

    /**
     * Parses raw user input into an executable Command.
     *
     * @param input Raw user input.
     * @return The Command corresponding to the input.
     * @throws LeBronException if the input is not a recognised, well-formed command.
     */
    public static Command parse(String input) throws LeBronException {
        String trimmedInput = input.trim();
        CommandType commandType = getCommandType(trimmedInput);
        switch (commandType) {
            case BYE -> {
                return new ByeCommand();
            }
            case LIST -> {
                return new ListCommand();
            }
            case TODO -> {
                return new TodoCommand(parseTodoDescription(trimmedInput));
            }
            case DEADLINE -> {
                DeadlineArgs args = parseDeadlineArgs(trimmedInput);
                return new DeadlineCommand(args.description(), args.by());
            }
            case EVENT -> {
                EventArgs args = parseEventArgs(trimmedInput);
                return new EventCommand(args.description(), args.start(), args.end());
            }
            case MARK -> {
                return new MarkCommand(parseTaskNumber(trimmedInput));
            }
            case UNMARK -> {
                return new UnmarkCommand(parseTaskNumber(trimmedInput));
            }
            case DELETE -> {
                return new DeleteCommand(parseTaskNumber(trimmedInput));
            }
            case FIND -> {
                return new FindCommand(parseFindKeyword(trimmedInput));
            }
            case RECUR -> {
                RecurringArgs args = parseRecurringArgs(trimmedInput);
                return new RecurringCommand(args.description(), args.nextDue(), args.interval());
            }
            default -> throw new LeBronException("Whatchu tryna do youngblood?");
        }
    }

    /**
     * Determines the CommandType of a raw user input line.
     *
     * @param input Raw user input.
     * @return The CommandType corresponding to the input's first word.
     */
    private static CommandType getCommandType(String input) {
        String firstWord = input.split(" ")[0];
        switch (firstWord) {
            case "bye":
                return CommandType.BYE;
            case "list":
                return CommandType.LIST;
            case "mark":
                return CommandType.MARK;
            case "unmark":
                return CommandType.UNMARK;
            case "todo":
                return CommandType.TODO;
            case "deadline":
                return CommandType.DEADLINE;
            case "event":
                return CommandType.EVENT;
            case "delete":
                return CommandType.DELETE;
            case "find":
                return CommandType.FIND;
            case "recur":
                return CommandType.RECUR;
            default:
                return CommandType.UNKNOWN;
        }
    }

    /**
     * Extracts the text after a command keyword, e.g. extracting "read book" from
     * "todo read book" given the keyword "todo". Returns an empty String if the
     * keyword has no text after it.
     *
     * @param input Raw user input.
     * @param keyword The command keyword the input starts with, e.g. "todo".
     * @return The trimmed text after the keyword and the space following it.
     */
    private static String extractArgs(String input, String keyword) {
        return input.length() > keyword.length() ? input.substring(keyword.length() + 1).trim() : "";
    }

    /**
     * Extracts and validates the description from a "todo" command.
     *
     * @param input Raw user input, e.g. "todo read book".
     * @return The Todo's description.
     * @throws LeBronException if no description was given.
     */
    private static String parseTodoDescription(String input) throws LeBronException {
        String description = extractArgs(input, "todo");
        if (description.isEmpty()) {
            throw new LeBronException("Whatchu tryna to do?");
        }
        return description;
    }

    /**
     * Extracts and validates the description and due date/time from a "deadline" command.
     *
     * @param input Raw user input, e.g. "deadline return book by 2019-10-15 1800".
     * @return The parsed description and due date/time.
     * @throws LeBronException if no description/date was given, the "by" keyword is missing,
     *         or "by" appears more than once.
     */
    private static DeadlineArgs parseDeadlineArgs(String input) throws LeBronException {
        String fullDesc = extractArgs(input, "deadline");
        if (fullDesc.isEmpty()) {
            throw new LeBronException("Yo specify your deadline!");
        }
        if (!fullDesc.contains(" by ")) {
            throw new LeBronException("Stop playin with me!");
        }
        String[] parts = fullDesc.split(" by ");
        if (parts.length != 2) {
            throw new LeBronException("Only one 'by' per deadline, don't confuse me!");
        }
        String description = parts[0].trim();
        LocalDateTime by = LocalDateTime.parse(parts[1].trim(), INPUT_DATE_FORMAT);
        return new DeadlineArgs(description, by);
    }

    /**
     * Extracts and validates the description and start/end date/time from an "event" command.
     *
     * @param input Raw user input, e.g. "event project meeting from 2019-10-16 0900 to 2019-10-16 1100".
     * @return The parsed description and start/end date/time.
     * @throws LeBronException if no description/dates were given, the "from"/"to" keywords
     *         are missing or repeated, or the end date/time is not after the start date/time.
     */
    private static EventArgs parseEventArgs(String input) throws LeBronException {
        String fullDesc = extractArgs(input, "event");
        if (fullDesc.isEmpty()) {
            throw new LeBronException("What event you tryna go for? Quit playin!");
        }
        if (!fullDesc.contains(" from ") || !fullDesc.contains(" to ")) {
            throw new LeBronException("Tell me start and end!");
        }
        String[] parts = fullDesc.split(" from | to ");
        if (parts.length != 3) {
            throw new LeBronException("One 'from' and one 'to', don't confuse me!");
        }
        String description = parts[0].trim();
        LocalDateTime start = LocalDateTime.parse(parts[1].trim(), INPUT_DATE_FORMAT);
        LocalDateTime end = LocalDateTime.parse(parts[2].trim(), INPUT_DATE_FORMAT);
        if (!end.isAfter(start)) {
            throw new LeBronException("Your event can't end before it even starts!");
        }
        return new EventArgs(description, start, end);
    }

    /**
     * Extracts and validates the keyword from a "find" command.
     *
     * @param input Raw user input, e.g. "find book".
     * @return The search keyword.
     * @throws LeBronException if no keyword was given.
     */
    private static String parseFindKeyword(String input) throws LeBronException {
        String keyword = extractArgs(input, "find");
        if (keyword.isEmpty()) {
            throw new LeBronException("Whatchu tryna find?");
        }
        return keyword;
    }

    /**
     * Extracts and validates the description, interval, and next due date/time from a
     * "recur" command.
     *
     * @param input Raw user input, e.g. "recur project meeting every week from 2019-10-15 1800".
     * @return The parsed description, next due date/time, and interval.
     * @throws LeBronException if no description/interval/date was given, the "every"/"from"
     *         keywords are missing or "from" is repeated, or the interval is not "day",
     *         "week", or "month".
     */
    private static RecurringArgs parseRecurringArgs(String input) throws LeBronException {
        String fullDesc = extractArgs(input, "recur");
        if (fullDesc.isEmpty()) {
            throw new LeBronException("What's on repeat? Give me the details!");
        }
        if (!fullDesc.contains(" every ") || !fullDesc.contains(" from ")) {
            throw new LeBronException("Tell me how often and when it starts!");
        }
        String[] descAndRest = fullDesc.split(" every ", 2);
        String description = descAndRest[0].trim();
        String[] intervalAndDate = descAndRest[1].split(" from ");
        if (intervalAndDate.length != 2) {
            throw new LeBronException("Only one 'from', don't confuse me!");
        }
        RecurrenceInterval interval = RecurrenceInterval.fromKeyword(intervalAndDate[0].trim());
        LocalDateTime nextDue = LocalDateTime.parse(intervalAndDate[1].trim(), INPUT_DATE_FORMAT);
        return new RecurringArgs(description, nextDue, interval);
    }

    /**
     * Extracts and validates the task number from a "mark"/"unmark"/"delete" command.
     * Tolerant of repeated spaces between the command word and the number.
     *
     * @param input Raw user input, e.g. "mark 2".
     * @return The 1-based task number.
     * @throws LeBronException if no task number was given, more than one was given,
     *         or it is not a whole number.
     */
    private static int parseTaskNumber(String input) throws LeBronException {
        String[] tokens = input.split("\\s+");
        if (tokens.length != 2) {
            throw new LeBronException("Gimme exactly one task number, nothing more nothing less!");
        }
        try {
            return Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            throw new LeBronException("That's not a task number I recognize!");
        }
    }
}

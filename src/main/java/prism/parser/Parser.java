package prism.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import prism.PrismException;

/** Parses user commands and their task or date arguments. */
public class Parser {
    private static final DateTimeFormatter DATE_INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("[d/M/uuuu][uuuu-MM-dd]")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Identifies the commands understood by the application. */
    public enum CommandType {
        BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, DATE, FIND, SNOOZE
    }

    /** Identifies the command represented by a complete user input string. */
    public static CommandType parseCommandType(String fullCommand) throws PrismException {
        String trimmed = fullCommand.trim();
        if (trimmed.equals("bye")) {
            return CommandType.BYE;
        } else if (trimmed.equals("list")) {
            return CommandType.LIST;
        } else if (trimmed.matches("mark(\\s.*)?")) {
            return CommandType.MARK;
        } else if (trimmed.matches("unmark(\\s.*)?")) {
            return CommandType.UNMARK;
        } else if (trimmed.matches("delete(\\s.*)?")) {
            return CommandType.DELETE;
        } else if (trimmed.matches("todo(\\s.*)?")) {
            return CommandType.TODO;
        } else if (trimmed.matches("deadline(\\s.*)?")) {
            return CommandType.DEADLINE;
        } else if (trimmed.matches("event(\\s.*)?")) {
            return CommandType.EVENT;
        } else if (trimmed.matches("date(\\s.*)?")) {
            return CommandType.DATE;
        } else if (trimmed.matches("find(\\s.*)?")) {
            return CommandType.FIND;
        } else if (trimmed.matches("snooze(\\s.*)?")) {
            return CommandType.SNOOZE;
        } else {
            throw new PrismException("!!! I'm sorry, but I don't know what that means");
        }
    }

    /** Extracts the task number and new date from a snooze command. */
    public static String[] parseSnoozeArgs(String command) throws PrismException {
        if (!command.matches("^snooze\\s+\\d+\\s+/to\\s+.+$")) {
            throw new PrismException("!!! Snooze needs a task number and a '/to' date/time, e.g. "
                    + "'snooze 2 /to 2025-12-02 1800'.");
        }
        String[] parts = command.substring(7).split("\\s+/to\\s+", 2);
        return new String[]{parts[0].trim(), parts[1].trim()};
    }

    /** Parses a one-based task number and returns its zero-based list index. */
    public static int parseIndex(String command, String prefix) throws PrismException {
        if (!command.matches("^" + prefix + "\\s+\\d+$")) {
            throw new PrismException("!!! Please tell me which task number to " + prefix
                    + ", e.g. '" + prefix + " 2'.");
        }
        try {
            return Integer.parseInt(command.substring(prefix.length()).trim()) - 1;
        } catch (NumberFormatException e) {
            throw new PrismException("!!! Invalid task number provided.");
        }
    }

    /** Extracts and validates the description from a todo command. */
    public static String parseTodoDescription(String fullCommand) throws PrismException {
        // Extract everything after "todo" and trim standard whitespace
        assert fullCommand.startsWith("todo") : "Todo command must start with 'todo'";
        String description = fullCommand.substring(4).trim();

        if (description.isEmpty()) {
            throw new PrismException("!!! The description of a todo cannot be empty.");
        }

        return description;
    }

    /** Extracts and validates the keyword from a find command. */
    public static String parseFindKeyword(String fullCommand) throws PrismException {
        assert fullCommand.startsWith("find") : "Find command must start with 'find'";
        String keyword = fullCommand.substring(4).trim();

        if (keyword.isEmpty()) {
            throw new PrismException("!!! Please provide a keyword to search for.");
        }

        return keyword;
    }

    /** Extracts the description and due time from a deadline command. */
    public static String[] parseDeadlineArgs(String command) throws PrismException {
        if (!command.matches("^deadline\\s+.+\\s+/by\\s+.+$")) {
            throw new PrismException(
                    "!!! A deadline needs a description and a '/by' time, e.g. "
                            + "'deadline return book /by 2019-12-02 1800'.");
        }
        String[] parts = command.substring(9).split(" /by ", 2);
        assert parts.length == 2 : "Validated deadline command must have two parts";
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new PrismException("!!! A deadline needs both a description and a '/by' time.");
        }
        return new String[]{parts[0].trim(), parts[1].trim()};
    }

    /** Extracts the description, start time, and end time from an event command. */
    public static String[] parseEventArgs(String command) throws PrismException {
        if (!command.matches("^event\\s+.+\\s+/from\\s+.+\\s+/to\\s+.+$")) {
            throw new PrismException(
                    "!!! An event needs a description, a '/from' time, and a '/to' time.");
        }
        String[] parts = command.substring(6).split(" /from | /to ", 3);
        assert parts.length == 3 : "Validated event command must have three parts";
        if (parts.length < 3 || hasEmptyPart(parts)) {
            throw new PrismException("!!! An event needs a description, a '/from' time, and a '/to' time.");
        }
        return new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim()};
    }

    /** Returns whether any extracted event argument is empty after trimming. */
    private static boolean hasEmptyPart(String[] parts) {
        for (String part : parts) {
            if (part.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /** Parses the date argument from a date command. */
    public static LocalDate parseQueryDate(String command) throws PrismException {
        if (!command.matches("^date\\s+(\\d{4}-\\d{2}-\\d{2}|\\d{1,2}/\\d{1,2}/\\d{4})$")) {
            throw new PrismException("!!! Please specify a date in yyyy-MM-dd or d/M/yyyy format, "
                    + "e.g. 'date 2019-12-02'.");
        }
        try {
            return LocalDate.parse(command.substring(4).trim(), DATE_INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new PrismException("!!! That is not a valid calendar date. Please use yyyy-MM-dd, "
                    + "such as '2025-12-02'.");
        }
    }
}

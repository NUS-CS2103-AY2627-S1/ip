package puyo.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import puyo.PuyoException;
import puyo.command.AddCommand;
import puyo.command.ByeCommand;
import puyo.command.Command;
import puyo.command.DeleteCommand;
import puyo.command.FindCommand;
import puyo.command.ListCommand;
import puyo.command.MarkCommand;
import puyo.command.UnknownCommand;
import puyo.command.UnmarkCommand;
import puyo.command.ViewScheduleCommand;
import puyo.task.Deadline;
import puyo.task.Event;
import puyo.task.ToDo;

/**
 * Parses user input strings into executable {@code Command} objects.
 * Input validation updates were developed with ChatGPT assistance.
 */
public class Parser {

    /** Formatter for input dates in YYYY-MM-DD format. */
    public static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Formatter for input date and time in YYYY-MM-DD HHmm format. */
    public static final DateTimeFormatter INPUT_DATETIME = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Formatter for displaying date and time in user interface. */
    public static final DateTimeFormatter DISPLAY_DATETIME =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mma", Locale.ENGLISH);

    /** Formatter for saving date and time into storage files. */
    public static final DateTimeFormatter SAVE_DATETIME = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");

    /**
     * Parses the raw user input into a corresponding {@code Command}.
     *
     * @param input The raw input string from the user.
     * @return The parsed {@code Command} object to be executed.
     * @throws PuyoException If the input format is invalid or empty.
     */
    public static Command parse(String input) throws PuyoException {
        assert input != null : "Input string to parse should not be null";

        input = input.trim();
        if (input.isBlank()) {
            throw new PuyoException("Please enter a non-empty valid command!");
        }

        String[] parts = input.split("\\s+", 2);
        assert parts.length > 0 : "Split string array should contain at least one element";

        String firstWord = parts[0].toLowerCase(Locale.ROOT);
        // Normalize the command separator without changing the task description.
        input = firstWord + (parts.length == 2 ? " " + parts[1] : "");
        switch (firstWord) {
            case "bye":
                return new ByeCommand();
            case "list":
                return new ListCommand();
            case "mark":
                return parseIndexCommand("mark", input, 5);
            case "unmark":
                return parseIndexCommand("unmark", input, 7);
            case "delete":
                return parseDeleteCommand(input);
            case "todo":
                return parseTodoCommand(input);
            case "deadline":
                return parseDeadlineCommand(input);
            case "event":
                return parseEventCommand(input);
            case "find":
                return parseFindCommand(input);
            case "viewschedule":
                return parseViewScheduleCommand(input);
            default:
                return new UnknownCommand();
        }
    }

    /**
     * Parses commands that require a single task index (mark/unmark).
     *
     * @param type   The command type string ("mark" or "unmark").
     * @param input  The full user input string.
     * @param offset The character index offset to start parsing the task number.
     * @return The corresponding {@code MarkCommand} or {@code UnmarkCommand}.
     * @throws PuyoException If the index argument is invalid or missing.
     */
    private static Command parseIndexCommand(String type, String input, int offset) throws PuyoException {
        assert type.equals("mark") || type.equals("unmark") : "Command type must be mark or unmark";
        assert offset > 0 : "Offset must be positive";

        try {
            int number = Integer.parseInt(input.substring(offset).trim());
            if (number < 1) {
                throw new PuyoException("Task numbers start at 1! (e.g. " + type + " 1)");
            }
            return type.equals("mark") ? new MarkCommand(number - 1) : new UnmarkCommand(number - 1);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new PuyoException("Please provide a valid task number! (e.g. " + type + " 1)");
        }
    }

    /**
     * Parses a delete command input string.
     *
     * @param input The full user input string.
     * @return A {@code DeleteCommand} containing the target index.
     * @throws PuyoException If the index argument is invalid or missing.
     */
    private static Command parseDeleteCommand(String input) throws PuyoException {
        assert input.startsWith("delete") : "Input should start with 'delete'";

        try {
            int number = Integer.parseInt(input.substring(7).trim());
            if (number < 1) {
                throw new PuyoException("Task numbers start at 1! (e.g. delete 1)");
            }
            return new DeleteCommand(number - 1);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new PuyoException("Please provide a valid task number to delete! (e.g. delete 1)");
        }
    }

    /**
     * Parses a todo command input string.
     *
     * @param input The full user input string.
     * @return An {@code AddCommand} containing the created {@code ToDo} task.
     * @throws PuyoException If the description is empty.
     */
    private static Command parseTodoCommand(String input) throws PuyoException {
        assert input.startsWith("todo") : "Input should start with 'todo'";

        String desc = input.substring(4).trim();
        if (desc.isEmpty()) {
            throw new PuyoException("The description of a todo can't be empty!");
        }
        return new AddCommand(new ToDo(desc));
    }

    /**
     * Parses a deadline command input string.
     *
     * @param input The full user input string.
     * @return An {@code AddCommand} containing the created {@code Deadline} task.
     * @throws PuyoException If the arguments or date format are invalid.
     */
    private static Command parseDeadlineCommand(String input) throws PuyoException {
        assert input.startsWith("deadline") : "Input should start with 'deadline'";

        int byIndex = findFlag(input, "/by");
        if (byIndex == -1) {
            throw new PuyoException("Please enter a valid deadline by using '/by'!");
        }
        String name = input.substring(8, byIndex).trim();
        String byRaw = input.substring(byIndex + 3).trim();
        if (name.isEmpty() || byRaw.isEmpty()) {
            throw new PuyoException("The description or time of a deadline can't be empty!");
        }
        LocalDateTime by = parseDateTime(byRaw);
        if (by == null) {
            throw new PuyoException("Invalid date format! Use: yyyy-MM-dd or yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)");
        }
        return new AddCommand(new Deadline(name, by));
    }

    /**
     * Parses an event command input string.
     *
     * @param input The full user input string.
     * @return An {@code AddCommand} containing the created {@code Event} task.
     * @throws PuyoException If the arguments or date formats are invalid.
     */
    private static Command parseEventCommand(String input) throws PuyoException {
        assert input.startsWith("event") : "Input should start with 'event'";

        int fromIndex = findFlag(input, "/from");
        int toIndex = findFlag(input, "/to");

        if (fromIndex == -1 || toIndex == -1) {
            throw new PuyoException("Please enter a valid event timing by using '/from' and '/to'!");
        }
        if (fromIndex > toIndex) {
            throw new PuyoException("Please enter a valid event timing by putting '/from' before '/to'!");
        }

        String name = input.substring(5, fromIndex).trim();
        String from = input.substring(fromIndex + 5, toIndex).trim();
        String to = input.substring(toIndex + 3).trim();

        if (name.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new PuyoException("Event description, '/from', or '/to' cannot be empty!");
        }

        LocalDateTime fromDateTime = parseDateTime(from);
        LocalDateTime toDateTime = parseDateTime(to);
        if (fromDateTime == null || toDateTime == null) {
            throw new PuyoException("Invalid date format! Use: yyyy-MM-dd or yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)");
        }

        return new AddCommand(new Event(name, fromDateTime, toDateTime));
    }

    /**
     * Finds a flag without changing the character positions in the original input.
     *
     * @param input The command string to search.
     * @param flag The flag to find, ignoring letter case.
     * @return The flag's starting position, or {@code -1} if it is absent.
     */
    private static int findFlag(String input, String flag) {
        for (int i = 0; i <= input.length() - flag.length(); i++) {
            if (input.regionMatches(true, i, flag, 0, flag.length())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Parses a date or date-time string into a {@code LocalDateTime} object.
     *
     * @param raw The raw date string.
     * @return Parsed {@code LocalDateTime} object, or {@code null} if parsing
     *         fails.
     */
    public static LocalDateTime parseDateTime(String raw) {
        assert raw != null : "Raw date string to parse should not be null";

        raw = raw.trim();
        try {
            return LocalDateTime.parse(raw, INPUT_DATETIME);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(raw, INPUT_DATE).atStartOfDay();
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }

    /**
     * Parses a find command input string.
     *
     * @param input The full user input string.
     * @return A {@code FindCommand} with the specified search keyword.
     * @throws PuyoException If the search keyword is empty.
     */
    private static Command parseFindCommand(String input) throws PuyoException {
        assert input.startsWith("find") : "Input should start with 'find'";

        String keyword = input.substring(4).trim();
        if (keyword.isEmpty()) {
            throw new PuyoException("The search keyword cannot be empty!");
        }
        return new FindCommand(keyword);
    }

    /**
     * Parses a view schedule command input string.
     *
     * @param input The full user input string.
     * @return A {@code ViewScheduleCommand} object.
     * @throws PuyoException If the date format is invalid or missing.
     */
    private static Command parseViewScheduleCommand(String input) throws PuyoException {
        String[] parts = input.split("\\s+", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new PuyoException("Please provide a valid date! Format: viewschedule YYYY-MM-DD");
        }
        try {
            String dateString = parts[1].trim();
            LocalDate date = LocalDate.parse(dateString, INPUT_DATE);
            return new ViewScheduleCommand(date);
        } catch (DateTimeParseException e) {
            throw new PuyoException("Please provide a valid date! Format: viewschedule YYYY-MM-DD");
        }
    }
}

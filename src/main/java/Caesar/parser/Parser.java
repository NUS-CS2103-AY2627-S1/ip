package caesar.parser;

import caesar.Caesar;
import caesar.command.AddCommand;
import caesar.command.ClearMissedCommand;
import caesar.command.Command;
import caesar.command.DeleteCommand;
import caesar.command.ExitCommand;
import caesar.command.FindCommand;
import caesar.command.HelpCommand;
import caesar.command.ListCommand;
import caesar.command.MarkCommand;
import caesar.command.ReminderCommand;
import caesar.command.RescheduleCommand;
import caesar.command.UnmarkCommand;
import caesar.exception.CaesarException;
import caesar.task.Deadline;
import caesar.task.Event;
import caesar.task.Task;
import caesar.task.ToDo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Interprets user commands and creates tasks from command details.
 *
 * <p>Parser does not read from or write to the console. It converts raw input
 * into structured values so that {@link Caesar} can coordinate the user
 * interface and task operations.</p>
 */
public class Parser {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter SLASH_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String COMMAND_INSTRUCTIONS =
            "Type a command or click HELP to see available formats.";

    /** Returns the command instructions shown by the user interface. */
    public static String getCommandInstructions() {
        return COMMAND_INSTRUCTIONS;
    }

    /** Returns every command format displayed by the GUI help window. */
    public static List<String> getHelpCommands() {
        return List.of(
                "todo <description>",
                "deadline <description> /by <date>",
                "event <description> /from <start> /to <end>",
                "list",
                "list sorted",
                "find <keyword>",
                "help",
                "reminder",
                "clear missed",
                "mark <task number>",
                "unmark <task number>",
                "delete <task number>",
                "reschedule <number> <date> [<end date>]",
                "bye"
        );
    }

    /** Splits raw input into a command type and its optional details. */
    public ParsedCommand parse(String command) {
        String[] commandParts = command == null
                ? new String[]{""}
                : command.trim().split("\\s+", 2);
        String prefix = commandParts.length > 0 ? commandParts[0] : "";
        String details = commandParts.length > 1 ? commandParts[1] : null;
        return new ParsedCommand(Caesar.CommandType.fromString(prefix), details);
    }

    /** Parses raw input and creates the command that should execute it. */
    public Command parseCommand(String command) throws CaesarException {
        ParsedCommand parsedCommand = parse(command);
        String details = parsedCommand.getDetails();

        return switch (parsedCommand.getType()) {
            case TODO -> new AddCommand(new ToDo(requireDetails(details, "todo <description>")));
            case DEADLINE -> new AddCommand(createDeadline(requireDetails(
                    details, "deadline <description> /by <date or time>")));
            case EVENT -> new AddCommand(createEvent(requireDetails(
                    details, "event <description> /from <start> /to <end>")));
            case LIST -> new ListCommand("sorted".equals(details));
            case FIND -> new FindCommand(requireDetails(details, "find <keyword>"));
            case HELP -> new HelpCommand();
            case REMINDER -> {
                if (details != null) {
                    throw unknownCommand();
                }
                yield new ReminderCommand();
            }
            case CLEAR -> {
                if (details != null && !"missed".equalsIgnoreCase(details.trim())) {
                    throw unknownCommand();
                }
                yield new ClearMissedCommand();
            }
            case MARK -> new MarkCommand(parseTaskNumber(details, "mark <task number>"));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(details, "unmark <task number>"));
            case DELETE -> new DeleteCommand(parseTaskNumber(details, "delete <task number>"));
            case RESCHEDULE -> createReschedule(requireDetails(
                    details, "reschedule <number> <date> [<end date>]"));
            case BYE -> {
                if (details != null) {
                    throw unknownCommand();
                }
                yield new ExitCommand();
            }
            case UNKNOWN -> throw unknownCommand();
        };
    }

    /** Creates a deadline from details in the form {@code description /by date}. */
    public Task createDeadline(String details) throws CaesarException {
        String[] commandParts = details.split("/by", 2);
        if (commandParts.length != 2) {
            throw missingDetails("deadline <description> /by <date or time>");
        }

        String description = commandParts[0].trim();
        String by = commandParts[1].trim();
        String formattedBy = convertDate(by);
        if (description.isEmpty() || by.isEmpty()) {
            throw missingDetails("deadline <description> /by <date or time>");
        }
        return new Deadline(description, formattedBy);
    }

    /** Creates an event from details in the form {@code description /from start /to end}. */
    public Task createEvent(String details) throws CaesarException {
        String[] commandParts = details.split("/from", 2);
        if (commandParts.length != 2) {
            throw missingDetails("event <description> /from <start> /to <end>");
        }

        String[] timeParts = commandParts[1].split("/to", 2);
        if (timeParts.length != 2) {
            throw missingDetails("event <description> /from <start> /to <end>");
        }

        String description = commandParts[0].trim();
        String start = timeParts[0].trim();
        String end = timeParts[1].trim();
        String formattedStart = convertDate(start);
        String formattedEnd = convertDate(end);
        if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
            throw missingDetails("event <description> /from <start> /to <end>");
        }
        return new Event(description, formattedStart, formattedEnd);
    }

    /** Creates a reschedule command from one or two positional dates. */
    private Command createReschedule(String details) throws CaesarException {
        String[] commandParts = details.trim().split("\\s+");
        if (commandParts.length < 2 || commandParts.length > 3) {
            throw missingDetails("reschedule <number> <date> [<end date>]");
        }

        int taskNumber = parseTaskNumber(commandParts[0], "reschedule <number> <date> [<end date>]");
        String newStart = convertDate(commandParts[1]);
        if (commandParts.length == 2) {
            return new RescheduleCommand(taskNumber, newStart);
        }
        return new RescheduleCommand(taskNumber, newStart, convertDate(commandParts[2]));
    }

    /** Converts a date into the display format used by saved tasks. */
    private String convertDate(String time) throws CaesarException {
        String trimmed = time.trim();
        LocalDate date;

        try {
            date = LocalDate.parse(trimmed);
        } catch (DateTimeParseException firstException) {
            try {
                date = LocalDate.parse(trimmed, SLASH_DATE_FORMAT);
            } catch (DateTimeParseException secondException) {
                try {
                    date = LocalDate.parse(trimmed, DISPLAY_FORMAT);
                } catch (DateTimeParseException thirdException) {
                    throw new CaesarException(
                            "Invalid date format. Please use YYYY-MM-DD, DD/MM/YYYY, or MMM d yyyy.");
                }
            }
        }
        return date.format(DISPLAY_FORMAT);
    }

    /** Converts a command argument into a task number. */
    public int parseTaskNumber(String details, String format) throws CaesarException {
        if (details == null || details.trim().isEmpty()) {
            throw missingDetails(format);
        }

        try {
            return Integer.parseInt(details.trim());
        } catch (NumberFormatException exception) {
            throw new CaesarException("Please provide a valid task number.");
        }
    }

    /** Ensures a command has non-empty details and returns the trimmed value. */
    public String requireDetails(String details, String format) throws CaesarException {
        if (details == null || details.trim().isEmpty()) {
            throw missingDetails(format);
        }
        return details.trim();
    }

    /** Creates the standard invalid-command error. */
    public CaesarException unknownCommand() {
        return new CaesarException("I'm not quite sure I caught that command. "
                + ":(click HELP to find useful commands)");
    }

    /** Creates the standard error for a command that is missing required details. */
    private CaesarException missingDetails(String format) {
        return new CaesarException(
                "I'd love to organize that for you, but I just need more details. "
                        + "Try enter in this format: \n\n" + format);
    }

    /** A parsed command containing its type and the text after the command keyword. */
    public static final class ParsedCommand {
        private final Caesar.CommandType type;
        private final String details;

        private ParsedCommand(Caesar.CommandType type, String details) {
            this.type = type;
            this.details = details;
        }

        /** Returns the command keyword classification. */
        public Caesar.CommandType getType() {
            return type;
        }

        /** Returns the optional text following the command keyword. */
        public String getDetails() {
            return details;
        }
    }
}

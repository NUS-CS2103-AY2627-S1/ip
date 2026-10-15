package percy;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Makes sense of raw user input.
 *
 * <p>{@link #parse(String)} splits a line into a {@link CommandType} plus its
 * argument string. The {@code parseXxx} helpers then turn the arguments of a
 * specific command into the object it needs (a task, or a task index),
 * throwing {@link PercyException} with a friendly message when the input is
 * malformed. All parsing lives here so {@link Percy} only has to decide what
 * to do, not how to read the text.
 */
public class Parser {

    /** Keyword that separates a deadline's description from its due date. */
    private static final String BY = " /by ";
    /** Keyword that separates an event's description from its start date. */
    private static final String FROM = " /from ";
    /** Keyword that separates an event's start date from its end date. */
    private static final String TO = " /to ";

    /** The kinds of command Percy understands. */
    public enum CommandType {
        BYE, LIST, FIND, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, UPDATE, UNKNOWN
    }

    /**
     * A parsed command: its {@link CommandType} together with everything the
     * user typed after the command word (trimmed; may be an empty string).
     */
    public static class Command {
        private final CommandType type;
        private final String arguments;

        private Command(CommandType type, String arguments) {
            this.type = type;
            this.arguments = arguments;
        }

        /**
         * Returns the kind of command this is.
         *
         * @return the command type
         */
        public CommandType getType() {
            return type;
        }

        /**
         * Returns the text typed after the command word, trimmed. May be empty.
         *
         * @return the argument string
         */
        public String getArguments() {
            return arguments;
        }
    }

    /**
     * The changes requested by an {@code update} command. A {@code null} field
     * means "leave this as it is"; only the non-null fields are applied.
     */
    public static class UpdateSpec {
        private final int index;
        private final String description;
        private final LocalDate by;
        private final LocalDate from;
        private final LocalDate to;

        private UpdateSpec(int index, String description, LocalDate by, LocalDate from, LocalDate to) {
            this.index = index;
            this.description = description;
            this.by = by;
            this.from = from;
            this.to = to;
        }

        /** Returns the 0-based index of the task to update. */
        public int getIndex() {
            return index;
        }

        /** Returns the new description, or {@code null} to keep the current one. */
        public String getDescription() {
            return description;
        }

        /** Returns the new due date, or {@code null} to keep the current one. */
        public LocalDate getBy() {
            return by;
        }

        /** Returns the new start date, or {@code null} to keep the current one. */
        public LocalDate getFrom() {
            return from;
        }

        /** Returns the new end date, or {@code null} to keep the current one. */
        public LocalDate getTo() {
            return to;
        }
    }

    /** Splits raw input into a command word and its argument string. */
    public static Command parse(String input) {
        assert input != null : "input line should not be null";
        String trimmed = input.trim();
        int firstSpace = trimmed.indexOf(' ');

        String commandWord;
        String arguments;
        if (firstSpace == -1) {
            commandWord = trimmed;
            arguments = "";
        } else {
            commandWord = trimmed.substring(0, firstSpace);
            arguments = trimmed.substring(firstSpace + 1).trim();
        }

        return new Command(toCommandType(commandWord), arguments);
    }

    /** Maps a command word to its {@link CommandType}, or UNKNOWN if unrecognised. */
    private static CommandType toCommandType(String commandWord) {
        assert commandWord != null : "command word should not be null";
        return switch (commandWord) {
        case "bye" -> CommandType.BYE;
        case "list" -> CommandType.LIST;
        case "find" -> CommandType.FIND;
        case "mark" -> CommandType.MARK;
        case "unmark" -> CommandType.UNMARK;
        case "delete" -> CommandType.DELETE;
        case "todo" -> CommandType.TODO;
        case "deadline" -> CommandType.DEADLINE;
        case "event" -> CommandType.EVENT;
        case "update" -> CommandType.UPDATE;
        default -> CommandType.UNKNOWN;
        };
    }

    /**
     * Parses a 1-based task number from {@code arguments} and converts it to a
     * 0-based index. Range checking against the actual list is left to the caller.
     *
     * @throws PercyException if the number is missing or not an integer.
     */
    public static int parseIndex(String arguments) throws PercyException {
        assert arguments != null : "arguments should not be null";
        if (arguments.isEmpty()) {
            throw new PercyException("OOPS!!! Please specify which task number.");
        }
        try {
            return Integer.parseInt(arguments.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new PercyException("OOPS!!! Please enter a valid task number.");
        }
    }

    /**
     * Builds a {@link Todo} from a {@code todo} command's arguments.
     *
     * @throws PercyException if the description is empty.
     */
    public static Todo parseTodo(String arguments) throws PercyException {
        if (arguments.isEmpty()) {
            throw new PercyException("OOPS!!! The description of a todo cannot be empty.");
        }
        return new Todo(arguments);
    }

    /**
     * Builds a {@link Deadline} from a {@code deadline} command's arguments,
     * which must have the form {@code <description> /by <yyyy-mm-dd>}.
     *
     * @throws PercyException if the description or date is missing or invalid.
     */
    public static Deadline parseDeadline(String arguments) throws PercyException {
        if (arguments.isEmpty()) {
            throw new PercyException("OOPS!!! The description of a deadline cannot be empty.");
        }
        String[] parts = arguments.split(BY, 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new PercyException("OOPS!!! A deadline needs a description and a '/by' date.");
        }
        assert parts.length == 2 : "a validated deadline splits into exactly 2 parts";
        return new Deadline(parts[0].trim(), parseDate(parts[1]));
    }

    /**
     * Builds an {@link Event} from an {@code event} command's arguments, which
     * must have the form {@code <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>}.
     *
     * @throws PercyException if any part is missing or a date is invalid.
     */
    public static Event parseEvent(String arguments) throws PercyException {
        if (arguments.isEmpty()) {
            throw new PercyException("OOPS!!! The description of an event cannot be empty.");
        }
        String[] descriptionAndDates = arguments.split(FROM, 2);
        if (descriptionAndDates.length < 2 || descriptionAndDates[0].trim().isEmpty()) {
            throw eventFormatError();
        }
        String[] startAndEnd = descriptionAndDates[1].split(TO, 2);
        if (startAndEnd.length < 2 || startAndEnd[0].trim().isEmpty()
                || startAndEnd[1].trim().isEmpty()) {
            throw eventFormatError();
        }
        assert descriptionAndDates.length == 2 && startAndEnd.length == 2
                : "a validated event splits into a description and two dates";
        return new Event(descriptionAndDates[0].trim(),
                parseDate(startAndEnd[0]), parseDate(startAndEnd[1]));
    }

    /**
     * Parses an {@code update} command's arguments, of the form
     * {@code <number> [new description] [/by <date>] [/from <date>] [/to <date>]}.
     * At least one thing to change must be given.
     *
     * @throws PercyException if the number or a date is invalid, or nothing
     *     was asked to be changed
     */
    public static UpdateSpec parseUpdate(String arguments) throws PercyException {
        if (arguments.isBlank()) {
            throw updateUsageError();
        }
        String[] numberAndRest = arguments.trim().split(" ", 2);
        int index = parseIndex(numberAndRest[0]);
        String rest = numberAndRest.length > 1 ? numberAndRest[1].trim() : "";

        String[] segments = (" " + rest).split(" /");
        String description = segments[0].trim().isEmpty() ? null : segments[0].trim();
        LocalDate by = null;
        LocalDate from = null;
        LocalDate to = null;
        for (int i = 1; i < segments.length; i++) {
            String segment = segments[i].trim();
            if (segment.startsWith("by ")) {
                by = parseDate(segment.substring("by ".length()));
            } else if (segment.startsWith("from ")) {
                from = parseDate(segment.substring("from ".length()));
            } else if (segment.startsWith("to ")) {
                to = parseDate(segment.substring("to ".length()));
            } else {
                throw updateUsageError();
            }
        }

        if (description == null && by == null && from == null && to == null) {
            throw new PercyException(
                    "OOPS!!! Tell me what to change, e.g. 'update 2 /by 2019-12-01'.");
        }
        return new UpdateSpec(index, description, by, from, to);
    }

    /**
     * Parses an ISO {@code yyyy-mm-dd} date, ignoring surrounding spaces.
     *
     * @throws PercyException if the text is not a valid date in that format
     */
    private static LocalDate parseDate(String text) throws PercyException {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new PercyException(
                    "OOPS!!! Please enter dates in yyyy-mm-dd format, e.g. 2019-10-15.");
        }
    }

    private static PercyException eventFormatError() {
        return new PercyException(
                "OOPS!!! An event needs a description and '/from' and '/to' dates.");
    }

    private static PercyException updateUsageError() {
        return new PercyException("OOPS!!! Usage: update <number> [new description] "
                + "[/by <date>] [/from <date>] [/to <date>]");
    }
}

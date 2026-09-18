package meowmeow.parser;

import meowmeow.MeowmeowException;
import meowmeow.command.AddCommand;
import meowmeow.command.Command;
import meowmeow.command.DeleteCommand;
import meowmeow.command.ExitCommand;
import meowmeow.command.FindCommand;
import meowmeow.command.ListCommand;
import meowmeow.command.MarkCommand;
import meowmeow.command.PriorityCommand;
import meowmeow.task.Deadline;
import meowmeow.task.Event;
import meowmeow.task.TaskDateTime;
import meowmeow.task.TaskPriority;
import meowmeow.task.TaskStatus;
import meowmeow.task.Todo;

/**
 * Turns a full input line into a ready-to-run {@link Command}: it works out
 * which command the line invokes, reads that command's arguments (the task
 * number for "mark"/"unmark"/"delete", the description for "todo", the
 * "/by" of a deadline, the "/from" and "/to" of an event, the date of a
 * "list &lt;date&gt;" query, the keyword of a "find &lt;keyword&gt;" query)
 * and returns the matching {@code Command} object.
 *
 * <p>Every "I don't understand that" case is raised here as a
 * {@link MeowmeowException} carrying the exact message shown to the user, so
 * the command loop only has to catch it, not work out what went wrong.
 *
 * <p>All methods are {@code static}: a {@code Parser} holds no state, it is
 * just a home for these related parsing routines.
 */
public class Parser {

    /** Prevents instantiation - all behaviour is via the static methods. */
    private Parser() {
    }

    /**
     * Turns a full input line into the {@link Command} it asks for.
     *
     * <p>{@link CommandType#fromInput(String)} recognises which command the
     * line invokes (throwing for an unknown one); this method then reads
     * that command's arguments and builds the matching {@code Command}.
     *
     * @throws MeowmeowException if the line is not a known command, or its
     *     arguments do not make sense (missing, malformed, or a date
     *     Meowmeow does not recognise).
     */
    public static Command parse(String fullCommand) throws MeowmeowException {
        CommandType type = CommandType.fromInput(fullCommand);
        // fromInput() returns a matching command or throws for an unknown one;
        // it never returns null, so the switch below always has a real type.
        assert type != null : "CommandType.fromInput returns a command or throws, never null";
        String arguments = type.argumentsOf(fullCommand);
        switch (type) {
            case BYE:
                return new ExitCommand();
            case LIST:
                // "list" alone lists everything; "list <date>" filters by day.
                return arguments.isEmpty()
                        ? new ListCommand()
                        : new ListCommand(TaskDateTime.parse(arguments));
            case FIND:
                return new FindCommand(parseKeyword(arguments));
            case MARK:
                return new MarkCommand(parseTaskNumber(arguments, type), TaskStatus.DONE);
            case UNMARK:
                return new MarkCommand(parseTaskNumber(arguments, type), TaskStatus.NOT_DONE);
            case DELETE:
                return new DeleteCommand(parseTaskNumber(arguments, type));
            case PRIORITY:
                return parsePriority(arguments);
            case TODO:
                return new AddCommand(parseTodo(arguments));
            case DEADLINE:
                return new AddCommand(parseDeadline(arguments));
            case EVENT:
                return new AddCommand(parseEvent(arguments));
            default:
                // Unreachable: every CommandType constant is handled above.
                // Kept so the compiler warns if a new constant is added
                // without a case here.
                throw new IllegalStateException("Unhandled command: " + type);
        }
    }

    /**
     * Reads the 1-based task number for a "mark", "unmark" or "delete"
     * command. The number is not range-checked here - {@link meowmeow.task.TaskList TaskList} does
     * that when the task is actually looked up.
     *
     * @param arguments the text after the command keyword.
     * @param command   the command being run, named in the error message if
     *                  no number was given.
     * @throws MeowmeowException if no number was given, or the text is not a
     *     whole number.
     */
    private static int parseTaskNumber(String arguments, CommandType command) throws MeowmeowException {
        if (arguments.isEmpty()) {
            throw new MeowmeowException(" Tell me which task number to " + command.keyword() + ".");
        }
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException notANumber) {
            throw new MeowmeowException(" That's not a task number I recognise.");
        }
    }

    /**
     * Builds the {@link Todo} described by a "todo" command's arguments. The
     * arguments may also carry a "/p" or "/priority" flag anywhere in them,
     * e.g. {@code "borrow book /p high"} or {@code "/p high borrow book"}.
     *
     * @throws MeowmeowException if no description was given, the description
     *     contains "|", or the priority flag is malformed.
     */
    private static Todo parseTodo(String arguments) throws MeowmeowException {
        PriorityFlag flag = PriorityFlag.extractFrom(arguments);
        String description = flag.remainingArguments();
        if (description.isEmpty()) {
            throw new MeowmeowException(" Tell me what to add, e.g. \"todo borrow book\".");
        }
        requireNoFileSeparator(description);
        Todo todo = new Todo(description);
        todo.setPriority(flag.priority());
        return todo;
    }

    /**
     * Reads the keyword a "find" command should search task descriptions
     * for.
     *
     * @param arguments the text after the "find" keyword.
     * @return the search keyword.
     * @throws MeowmeowException if no keyword was given.
     */
    private static String parseKeyword(String arguments) throws MeowmeowException {
        if (arguments.isEmpty()) {
            throw new MeowmeowException(" Tell me what to search for, e.g. \"find book\".");
        }
        return arguments;
    }

    /**
     * Builds the {@link Deadline} described by a "deadline" command's
     * arguments, in the form {@code <description> /by <when>}, optionally
     * carrying a "/p" or "/priority" flag anywhere in the arguments.
     *
     * <p>The "/by" marker is searched for from the end and
     * case-insensitively, so the marker closest to the end is the real flag
     * even if the description text also contains "/by", and "/BY"/"/By" work
     * the same.
     *
     * @throws MeowmeowException if the "/by" marker, the description or the
     *     date is missing, the description contains "|", the date is not one
     *     Meowmeow recognises, or the priority flag is malformed.
     */
    private static Deadline parseDeadline(String arguments) throws MeowmeowException {
        PriorityFlag flag = PriorityFlag.extractFrom(arguments);
        String remaining = flag.remainingArguments();
        int byMarker = lastIndexOfIgnoreCase(remaining, "/by", remaining.length());
        String description = byMarker < 0 ? "" : remaining.substring(0, byMarker).trim();
        String by = byMarker < 0 ? "" : remaining.substring(byMarker + 3).trim();
        if (byMarker < 0 || description.isEmpty() || by.isEmpty()) {
            throw new MeowmeowException(
                    " Use \"deadline <description> /by <when>\", e.g.",
                    " \"deadline return book /by 2/12/2019 1800\".");
        }
        requireNoFileSeparator(description);
        // TaskDateTime.parse throws MeowmeowException if the text is not a
        // date Meowmeow recognises.
        Deadline deadline = new Deadline(description, TaskDateTime.parse(by));
        deadline.setPriority(flag.priority());
        return deadline;
    }

    /**
     * Builds the {@link Event} described by an "event" command's arguments,
     * in the form {@code <description> /from <start> /to <end>}, optionally
     * carrying a "/p" or "/priority" flag anywhere in the arguments.
     *
     * <p>Same end-first, case-insensitive marker search as
     * {@link #parseDeadline}: the rightmost "/to" is the real flag, and the
     * real "/from" is the rightmost one before it.
     *
     * @throws MeowmeowException if a marker, the description or an endpoint
     *     is missing, the description contains "|", an endpoint is not a
     *     date Meowmeow recognises, the end is before the start, or the
     *     priority flag is malformed.
     */
    private static Event parseEvent(String arguments) throws MeowmeowException {
        PriorityFlag flag = PriorityFlag.extractFrom(arguments);
        String remaining = flag.remainingArguments();
        int toMarker = lastIndexOfIgnoreCase(remaining, "/to", remaining.length());
        int fromMarker = toMarker < 0 ? -1 : lastIndexOfIgnoreCase(remaining, "/from", toMarker - 1);
        String description = fromMarker < 0 ? "" : remaining.substring(0, fromMarker).trim();
        String from = fromMarker < 0 ? "" : remaining.substring(fromMarker + 5, toMarker).trim();
        String to = toMarker < 0 ? "" : remaining.substring(toMarker + 3).trim();
        if (fromMarker < 0 || description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new MeowmeowException(
                    " Use \"event <description> /from <start> /to <end>\", e.g.",
                    " \"event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600\".");
        }
        requireNoFileSeparator(description);
        TaskDateTime start = TaskDateTime.parse(from);
        TaskDateTime end = TaskDateTime.parse(to);
        if (!start.isNotAfter(end)) {
            throw new MeowmeowException(" An event can't end before it starts.");
        }
        Event event = new Event(description, start, end);
        event.setPriority(flag.priority());
        return event;
    }

    /**
     * Builds the {@link PriorityCommand} described by a "priority" command's
     * arguments, in the form {@code <task number> <level>}.
     *
     * @throws MeowmeowException if the task number or level is missing, the
     *     task number is not a whole number, or the level is not one
     *     Meowmeow recognises.
     */
    private static PriorityCommand parsePriority(String arguments) throws MeowmeowException {
        String[] parts = arguments.split("\\s+", 2);
        if (parts.length < 2 || parts[0].isEmpty()) {
            throw new MeowmeowException(
                    " Use \"priority <task number> <level>\", e.g. \"priority 2 high\".",
                    " Levels: " + TaskPriority.acceptedLevels() + ".");
        }
        int taskNumber = parseTaskNumber(parts[0], CommandType.PRIORITY);
        TaskPriority priority = PriorityFlag.parseLevel(parts[1].trim());
        return new PriorityCommand(taskNumber, priority);
    }

    /**
     * Rejects a description that contains "|", the character
     * {@link meowmeow.storage.Storage Storage} uses to separate fields on a
     * saved line. Letting it through would round-trip fine on this save, but
     * silently corrupt (truncate) the description the next time the file is
     * loaded, since the extra "|" would be misread as a field boundary.
     *
     * @param description the "todo"/"deadline"/"event" description to check.
     * @throws MeowmeowException if the description contains "|".
     */
    private static void requireNoFileSeparator(String description) throws MeowmeowException {
        if (description.contains("|")) {
            throw new MeowmeowException(" Sorry, task descriptions can't contain \"|\".");
        }
    }

    /**
     * Returns the case-insensitive equivalent of
     * {@code text.lastIndexOf(marker, fromIndex)}: finds the marker regardless
     * of how the user capitalised it, while still returning an index into the
     * original (not lowercased) text.
     */
    private static int lastIndexOfIgnoreCase(String text, String marker, int fromIndex) {
        return text.toLowerCase().lastIndexOf(marker.toLowerCase(), fromIndex);
    }
}

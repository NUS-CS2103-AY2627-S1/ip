package wodan.parser;

import java.time.LocalDate;

import wodan.WodanException;
import wodan.command.AddCommand;
import wodan.command.Command;
import wodan.command.DeleteCommand;
import wodan.command.ExitCommand;
import wodan.command.FindCommand;
import wodan.command.ListCommand;
import wodan.command.MarkCommand;
import wodan.command.OnCommand;
import wodan.command.TagCommand;
import wodan.command.UnmarkCommand;
import wodan.task.Deadline;
import wodan.task.Event;
import wodan.task.TaskDateTime;
import wodan.task.TaskList;
import wodan.task.Todo;

/**
 * Interprets a user command line: the command word, its arguments, and task details.
 */
public class Parser {
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";
    private static final String BY_SPLIT_PATTERN = "\\s+" + BY_MARKER + "(?:\\s+|$)";
    private static final String FROM_SPLIT_PATTERN = "\\s+" + FROM_MARKER + "(?:\\s+|$)";
    private static final String TO_SPLIT_PATTERN = "\\s+" + TO_MARKER + "(?:\\s+|$)";
    private static final String EVENT_USAGE = "Try: event meeting " + FROM_MARKER
            + " 2019-12-02 1400 " + TO_MARKER + " 2019-12-02 1600";
    private static final String DEADLINE_USAGE = "Try: deadline return book " + BY_MARKER
            + " 2019-12-02";
    private static final String TODO_USAGE = "Try: todo borrow book";
    private static final String EVENT_RANGE_ERROR =
            "An event cannot end before or at the same time it starts.";

    /**
     * Prevents instantiation; command parsing is done through static methods.
     */
    private Parser() {
    }

    /**
     * Returns a command object for {@code fullCommand}.
     *
     * @param fullCommand One line typed by the user.
     * @return The command to execute.
     * @throws WodanException If the line is empty, unknown, or has invalid arguments.
     */
    public static Command parse(String fullCommand) throws WodanException {
        String[] words = splitCommandLine(fullCommand);
        CommandWord commandWord = CommandWord.parse(words[0]);
        String arguments = words.length > 1 ? words[1] : "";
        switch (commandWord) {
            case TODO:
                return new AddCommand(parseTodo(arguments));
            case DEADLINE:
                return new AddCommand(parseDeadline(arguments));
            case EVENT:
                return new AddCommand(parseEvent(arguments));
            case LIST:
                return new ListCommand();
            case MARK:
                return new MarkCommand(arguments);
            case UNMARK:
                return new UnmarkCommand(arguments);
            case DELETE:
                return new DeleteCommand(arguments);
            case ON:
                return new OnCommand(arguments);
            case FIND:
                return new FindCommand(arguments);
            case TAG:
                return new TagCommand(arguments);
            case BYE:
                return new ExitCommand();
            default:
                throw new WodanException(CommandWord.unknownCommandMessage());
        }
    }

    /**
     * Splits {@code fullCommand} into the command word and the remaining argument text.
     *
     * @param fullCommand One line typed by the user.
     * @return The first word, then the rest of the line if any.
     * @throws WodanException If the line is empty.
     */
    private static String[] splitCommandLine(String fullCommand) throws WodanException {
        String trimmed = fullCommand.trim();
        if (trimmed.isEmpty()) {
            throw new WodanException(
                    "Silence is not a command. Speak todo, deadline, event, list, mark, "
                            + "unmark, delete, on, find, tag, or bye.");
        }
        return trimmed.split("\\s+", 2);
    }

    /**
     * Returns a todo built from {@code arguments}.
     *
     * @param arguments Text after the {@code todo} command word.
     * @return The todo to add.
     * @throws WodanException If the description is missing or contains {@code |}.
     */
    private static Todo parseTodo(String arguments) throws WodanException {
        rejectUnexpectedMarkers(arguments,
                "A todo has no date. Leave out /by, /from, and /to. " + TODO_USAGE,
                BY_MARKER, FROM_MARKER, TO_MARKER);
        String description = normalizeSpaces(arguments);
        if (description.isEmpty()) {
            throw new WodanException(
                    "A todo needs a quest name. " + TODO_USAGE);
        }
        rejectFileDelimiter(description);
        return new Todo(description);
    }

    /**
     * Returns a deadline built from {@code arguments}.
     *
     * @param arguments Text after the {@code deadline} command word, including {@code /by}.
     * @return The deadline to add.
     * @throws WodanException If the description, {@code /by} time, or date is invalid.
     */
    private static Deadline parseDeadline(String arguments) throws WodanException {
        rejectUnexpectedMarkers(arguments,
                "A deadline uses /by, not /from or /to. " + DEADLINE_USAGE,
                FROM_MARKER, TO_MARKER);
        rejectDuplicateMarker(arguments, BY_MARKER,
                "Use /by only once. " + DEADLINE_USAGE);

        String[] deadlineParts = arguments.split(BY_SPLIT_PATTERN, 2);
        String description = normalizeSpaces(deadlineParts[0]);
        String by = deadlineParts.length > 1 ? normalizeSpaces(deadlineParts[1]) : "";
        if (description.startsWith(BY_MARKER)) {
            throw new WodanException(
                    "A deadline needs a quest name before " + BY_MARKER
                            + ". " + DEADLINE_USAGE);
        }
        if (description.isEmpty()) {
            throw new WodanException(
                    "A deadline needs a quest name and " + BY_MARKER
                            + " <when>. " + DEADLINE_USAGE);
        }
        if (deadlineParts.length < 2) {
            throw new WodanException(
                    "A deadline must include " + BY_MARKER
                            + " <when>. " + DEADLINE_USAGE);
        }
        if (by.isEmpty()) {
            throw new WodanException(
                    "The ravens need a time after " + BY_MARKER
                            + ". " + DEADLINE_USAGE);
        }
        rejectFileDelimiter(description);
        rejectFileDelimiter(by);
        TaskDateTime dueAt = TaskDateTime.parse(by);
        return new Deadline(description, dueAt);
    }

    /**
     * Returns an event built from {@code arguments}.
     *
     * @param arguments Text after the {@code event} command word, including {@code /from} and {@code /to}.
     * @return The event to add.
     * @throws WodanException If the description, times, or date range is invalid.
     */
    private static Event parseEvent(String arguments) throws WodanException {
        rejectUnexpectedMarkers(arguments,
                "An event uses /from and /to, not /by. " + EVENT_USAGE,
                BY_MARKER);
        rejectDuplicateMarker(arguments, FROM_MARKER,
                "Use /from only once. " + EVENT_USAGE);
        rejectDuplicateMarker(arguments, TO_MARKER,
                "Use /to only once. " + EVENT_USAGE);

        String[] fromParts = arguments.split(FROM_SPLIT_PATTERN, 2);
        String description = normalizeSpaces(fromParts[0]);
        rejectInvalidEventDescription(description, fromParts.length);

        EventTimeRange times = splitEventTimes(fromParts[1].trim());
        rejectInvalidEventTimes(times);

        String from = normalizeSpaces(times.from);
        String to = normalizeSpaces(times.to);
        rejectFileDelimiter(description);
        rejectFileDelimiter(from);
        rejectFileDelimiter(to);
        TaskDateTime startAt = TaskDateTime.parse(from);
        TaskDateTime endAt = TaskDateTime.parse(to);
        if (!Event.isValidRange(startAt, endAt)) {
            throw new WodanException(EVENT_RANGE_ERROR);
        }
        return new Event(description, startAt, endAt);
    }

    /**
     * Rejects an event with a missing name or a missing {@code /from} marker.
     *
     * @param description Text before {@code /from}.
     * @param fromPartCount Number of pieces after splitting on {@code /from}.
     * @throws WodanException If the description or {@code /from} is missing.
     */
    private static void rejectInvalidEventDescription(String description, int fromPartCount)
            throws WodanException {
        if (description.startsWith(FROM_MARKER)) {
            throw new WodanException(
                    "An event needs a quest name before " + FROM_MARKER + ". " + EVENT_USAGE);
        }
        if (description.isEmpty()) {
            throw new WodanException(
                    "An event needs a name, " + FROM_MARKER + " <start>, and " + TO_MARKER + " <end>. "
                            + EVENT_USAGE);
        }
        if (fromPartCount < 2) {
            throw new WodanException(
                    "An event must include " + FROM_MARKER + " <start> and " + TO_MARKER + " <end>. "
                            + EVENT_USAGE);
        }
    }

    /**
     * Splits the text after {@code /from} into start and end values.
     *
     * @param rest Text after the {@code /from} marker.
     * @return The start text, end text, and whether {@code /to} was present.
     */
    private static EventTimeRange splitEventTimes(String rest) {
        if (isToMarkerWithoutFrom(rest)) {
            return new EventTimeRange("", rest.substring(TO_MARKER.length()).trim(), true);
        }
        String[] toParts = rest.split(TO_SPLIT_PATTERN, 2);
        boolean hasToMarker = toParts.length >= 2;
        String to = hasToMarker ? toParts[1].trim() : "";
        return new EventTimeRange(toParts[0].trim(), to, hasToMarker);
    }

    /**
     * Returns {@code true} if {@code rest} is a {@code /to} marker with no start time before it.
     *
     * @param rest Text after {@code /from}.
     * @return Whether the start time is missing because {@code /to} comes next.
     */
    private static boolean isToMarkerWithoutFrom(String rest) {
        return rest.equals(TO_MARKER)
                || rest.startsWith(TO_MARKER + " ")
                || rest.startsWith(TO_MARKER + "\t");
    }

    /**
     * Rejects an event with a missing start, a missing {@code /to}, or a missing end.
     *
     * @param times Split start and end text.
     * @throws WodanException If a required time part is missing.
     */
    private static void rejectInvalidEventTimes(EventTimeRange times) throws WodanException {
        if (times.from.isEmpty()) {
            throw new WodanException(
                    "The ravens need a start time after " + FROM_MARKER + ". " + EVENT_USAGE);
        }
        if (!times.hasToMarker) {
            throw new WodanException(
                    "An event must include " + TO_MARKER + " <end>. " + EVENT_USAGE);
        }
        if (times.to.isEmpty()) {
            throw new WodanException(
                    "The ravens need an end time after " + TO_MARKER + ". " + EVENT_USAGE);
        }
    }

    /**
     * Returns the 1-based list number for a {@code delete} command.
     *
     * @param arguments Text after the {@code delete} command word.
     * @param tasks Current task list, used to check that the number is in range.
     * @return A 1-based task number that exists in {@code tasks}.
     * @throws WodanException If the number is missing, not an integer, or out of range.
     */
    public static int parseDeleteNumber(String arguments, TaskList tasks) throws WodanException {
        rejectExtraTokens(arguments, "delete takes only a quest number. Try: delete 1");
        return parseTaskNumber(arguments, tasks,
                "Which quest is too burdensome? Try: delete 1",
                "' is not a quest number. Try: delete 1",
                "There are no quests to delete yet. Add one with todo, deadline, or event.");
    }

    /**
     * Returns the 1-based list number for a {@code mark} command.
     *
     * @param arguments Text after the {@code mark} command word.
     * @param tasks Current task list, used to check that the number is in range.
     * @return A 1-based task number that exists in {@code tasks}.
     * @throws WodanException If the number is missing, not an integer, or out of range.
     */
    public static int parseMarkNumber(String arguments, TaskList tasks) throws WodanException {
        rejectExtraTokens(arguments, "mark takes only a quest number. Try: mark 1");
        return parseTaskNumber(arguments, tasks,
                "Which quest should the ravens mark? Try: mark 1",
                "' is not a quest number. Try: mark 1",
                "There are no quests to mark yet. Add one with todo, deadline, or event.");
    }

    /**
     * Returns the 1-based list number for an {@code unmark} command.
     *
     * @param arguments Text after the {@code unmark} command word.
     * @param tasks Current task list, used to check that the number is in range.
     * @return A 1-based task number that exists in {@code tasks}.
     * @throws WodanException If the number is missing, not an integer, or out of range.
     */
    public static int parseUnmarkNumber(String arguments, TaskList tasks) throws WodanException {
        rejectExtraTokens(arguments, "unmark takes only a quest number. Try: unmark 1");
        return parseTaskNumber(arguments, tasks,
                "Which quest should the ravens unmark? Try: unmark 1",
                "' is not a quest number. Try: unmark 1",
                "There are no quests to unmark yet. Add one with todo, deadline, or event.");
    }

    /**
     * Returns the calendar date for an {@code on} command.
     *
     * @param arguments Text after the {@code on} command word.
     * @return The date to search.
     * @throws WodanException If the date is missing or cannot be parsed.
     */
    public static LocalDate parseOnDate(String arguments) throws WodanException {
        if (arguments.trim().isEmpty()) {
            throw new WodanException(
                    "Which day should the ravens search? Try: on 2019-12-02");
        }
        return TaskDateTime.parse(normalizeSpaces(arguments)).toLocalDate();
    }

    /**
     * Returns the keyword for a {@code find} command.
     *
     * @param arguments Text after the {@code find} command word.
     * @return The trimmed keyword.
     * @throws WodanException If the keyword is missing.
     */
    public static String parseFindKeyword(String arguments) throws WodanException {
        String keyword = normalizeSpaces(arguments);
        if (keyword.isEmpty()) {
            throw new WodanException(
                    "Which word should the ravens seek? Try: find book");
        }
        return keyword;
    }

    /**
     * Returns the 1-based list number for a {@code tag} command.
     *
     * @param arguments Text after the {@code tag} command word.
     * @param tasks Current task list, used to check that the number is in range.
     * @return A 1-based task number that exists in {@code tasks}.
     * @throws WodanException If the number is missing, not an integer, or out of range.
     */
    public static int parseTagNumber(String arguments, TaskList tasks) throws WodanException {
        String[] parts = arguments.trim().split("\\s+", 2);
        return parseTaskNumber(parts[0], tasks,
                "Which quest should the ravens brand? Try: tag 1 school",
                "' is not a quest number. Try: tag 1 school",
                "There are no quests to tag yet. Add one with todo, deadline, or event.");
    }

    /**
     * Returns the category name for a {@code tag} command.
     *
     * @param arguments Text after the {@code tag} command word.
     * @return The trimmed category name.
     * @throws WodanException If the name is missing or contains {@code |}.
     */
    public static String parseTagCategory(String arguments) throws WodanException {
        String[] parts = arguments.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new WodanException(
                    "Name the brand. Try: tag 1 school");
        }
        String category = normalizeSpaces(parts[1]);
        rejectFileDelimiter(category);
        return category;
    }

    /**
     * Returns {@code text} with leading and trailing spaces removed and inner gaps collapsed.
     *
     * @param text Raw argument text.
     * @return A single-spaced string, which may be empty.
     */
    private static String normalizeSpaces(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    /**
     * Returns how many times {@code token} appears as a whole word in {@code text}.
     *
     * @param text Argument text to scan.
     * @param token Whole token to count, such as {@code /by}.
     * @return The number of matching tokens.
     */
    private static int countToken(String text, String token) {
        if (text.isBlank()) {
            return 0;
        }
        int count = 0;
        for (String part : text.trim().split("\\s+")) {
            if (part.equals(token)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Rejects {@code arguments} that contain any of {@code markers} as whole tokens.
     *
     * @param arguments Text after the command word.
     * @param message Error to show if a marker is present.
     * @param markers Tokens that must not appear.
     * @throws WodanException If a forbidden marker is present.
     */
    private static void rejectUnexpectedMarkers(String arguments, String message, String... markers)
            throws WodanException {
        for (String marker : markers) {
            if (countToken(arguments, marker) > 0) {
                throw new WodanException(message);
            }
        }
    }

    /**
     * Rejects {@code arguments} that contain {@code marker} more than once.
     *
     * @param arguments Text after the command word.
     * @param marker Token that may appear at most once.
     * @param message Error to show if the marker is repeated.
     * @throws WodanException If the marker appears more than once.
     */
    private static void rejectDuplicateMarker(String arguments, String marker, String message)
            throws WodanException {
        if (countToken(arguments, marker) > 1) {
            throw new WodanException(message);
        }
    }

    /**
     * Rejects {@code arguments} that contain more than one token.
     *
     * @param arguments Text after the command word.
     * @param message Error to show if extra tokens are present.
     * @throws WodanException If more than one token is present.
     */
    private static void rejectExtraTokens(String arguments, String message) throws WodanException {
        String trimmed = arguments.trim();
        if (!trimmed.isEmpty() && trimmed.split("\\s+").length > 1) {
            throw new WodanException(message);
        }
    }

    /**
     * Rejects values that contain {@code |}, which is reserved as the save-file delimiter.
     *
     * @param value User-provided task text to check.
     * @throws WodanException If {@code value} contains {@code |}.
     */
    private static void rejectFileDelimiter(String value) throws WodanException {
        if (value.contains("|")) {
            throw new WodanException(
                    "A quest cannot contain '|'. The ravens use that mark in the save file.");
        }
    }

    /**
     * Returns a 1-based task number parsed from {@code arguments} that exists in {@code tasks}.
     *
     * @param arguments Text after the command word.
     * @param tasks Current task list, used to check that the number is in range.
     * @param missingMessage Error if {@code arguments} is blank.
     * @param notANumberSuffix Error suffix if {@code arguments} is not an integer.
     * @param emptyListMessage Error if {@code tasks} has no items.
     * @return A 1-based task number that exists in {@code tasks}.
     */
    private static int parseTaskNumber(String arguments, TaskList tasks, String missingMessage,
            String notANumberSuffix, String emptyListMessage) throws WodanException {
        String trimmed = arguments.trim();
        if (trimmed.isEmpty()) {
            throw new WodanException(missingMessage);
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new WodanException(
                    "'" + trimmed + notANumberSuffix);
        }
        if (tasks.isEmpty()) {
            throw new WodanException(emptyListMessage);
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new WodanException(
                    "There is no quest " + taskNumber + ". The ravens watch over " + tasks.size()
                            + (tasks.size() == 1 ? " quest" : " quests")
                            + ". Try a number from 1 to " + tasks.size() + ".");
        }
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "parseTaskNumber must return a number that exists in the list";
        return taskNumber;
    }

    /**
     * Start and end text taken from an event command after {@code /from}.
     */
    private static class EventTimeRange {
        private final String from;
        private final String to;
        private final boolean hasToMarker;

        private EventTimeRange(String from, String to, boolean hasToMarker) {
            this.from = from;
            this.to = to;
            this.hasToMarker = hasToMarker;
        }
    }
}

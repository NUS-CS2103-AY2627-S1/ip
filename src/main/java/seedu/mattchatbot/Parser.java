package seedu.mattchatbot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import seedu.mattchatbot.task.Deadline;
import seedu.mattchatbot.task.Event;
import seedu.mattchatbot.task.Task;
import seedu.mattchatbot.task.TaskUpdate;
import seedu.mattchatbot.task.Todo;

/**
 * Turns the raw text the user types into things the rest of the code can act
 * on: a command, a task, a task number, a date.
 * <p>
 * Extracted so that the input syntax -- which word is the command, where the
 * /by and /from markers go, what a valid date looks like -- is described in
 * one place, separate from what the chatbot then does about it.
 */
public class Parser {

    /** Keyword separating a deadline's description from its due date. */
    private static final String KEYWORD_BY = "/by";

    /** Keyword separating an event's description from its start time. */
    private static final String KEYWORD_FROM = "/from";

    /** Keyword separating an event's start time from its end time. */
    private static final String KEYWORD_TO = "/to";

    /**
     * The character the save file keeps between fields, which a description
     * therefore cannot contain.
     * <p>
     * Taken from the save format rather than written out again, so the two
     * cannot drift apart. Without this rule a description holding the
     * character saves and lists perfectly well, then splits into too many
     * fields when read back, and the entry is dropped as damaged -- a task
     * lost with nothing at the time to suggest anything was wrong.
     */
    private static final String RESERVED_CHARACTER = Task.FIELD_SEPARATOR.trim();

    /**
     * Every marker an update command may carry.
     * <p>
     * Listed together because an update, unlike the commands that build a
     * task, does not know in advance which markers it will meet: it has to
     * find whichever come next, in whatever order they were typed.
     */
    private static final String[] UPDATE_MARKERS = {KEYWORD_BY, KEYWORD_FROM, KEYWORD_TO};

    /** Not meant to be instantiated: every member of Parser is static. */
    private Parser() {
    }

    /**
     * Reads the command word, i.e. the first word of the input.
     *
     * @param input one non-empty line as the user typed it
     * @return the command it names
     * @throws MattChatBotException if no command uses that word
     */
    public static Command parseCommand(String input) throws MattChatBotException {
        return Command.fromKeyword(splitOffCommandWord(input)[0]);
    }

    /**
     * Reads everything after the command word.
     *
     * @param input one non-empty line as the user typed it
     * @return the rest of the line, or an empty string if there is none
     */
    public static String parseArgument(String input) {
        String[] parts = splitOffCommandWord(input);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Splits the input into its command word and the rest.
     * <p>
     * Done on the first space only, so that a description which happens to
     * begin with a command word is not mistaken for that command.
     *
     * @param input one non-empty line as the user typed it
     * @return one or two parts: the command word, then the remainder
     */
    private static String[] splitOffCommandWord(String input) {
        String[] parts = input.split("\\s+", 2);
        // parseCommand reads parts[0] without checking, which is safe only
        // because split always yields at least one part for a non-blank input.
        assert parts.length >= 1 : "splitting a non-blank line always yields a command word";
        return parts;
    }

    /**
     * Checks a description the user typed, and tidies its spacing.
     * <p>
     * Runs of spaces are squeezed to one, so that entries differing only in
     * how they were spaced do not read as different entries, and so that what
     * is saved matches what was shown.
     *
     * @param description the description as the user typed it
     * @return the description with its spacing tidied
     * @throws MattChatBotException if it holds the save file's separator
     */
    private static String checkDescription(String description) throws MattChatBotException {
        if (description.contains(RESERVED_CHARACTER)) {
            throw new MattChatBotException("A description cannot contain \""
                    + RESERVED_CHARACTER + "\", because that is what separates the fields"
                    + " of the save file. Please write it another way.");
        }
        return description.replaceAll("\\s+", " ");
    }

    /**
     * Rejects a marker the user has given more than once.
     *
     * @param text   the text being read
     * @param marker the marker to count, e.g. {@code /by}
     * @param from   where to start looking, so an earlier marker is not counted
     * @throws MattChatBotException if the marker appears again after that point
     */
    private static void checkGivenOnce(String text, String marker, int from)
            throws MattChatBotException {
        int first = text.indexOf(marker, from);
        if (first >= 0 && text.indexOf(marker, first + marker.length()) >= 0) {
            throw new MattChatBotException("You have given " + marker + " more than once."
                    + " Please say it just the once, so I know which one you mean.");
        }
    }

    /**
     * Builds a todo from the text after the command word.
     *
     * @param argument the description, as the user typed it
     * @return the todo
     * @throws MattChatBotException if the description is missing
     */
    public static Todo parseTodo(String argument) throws MattChatBotException {
        if (argument.isEmpty()) {
            throw new MattChatBotException("A todo wants a description. Perhaps: "
                    + Command.TODO.getKeyword() + " borrow book");
        }
        return new Todo(checkDescription(argument));
    }

    /**
     * Builds a deadline from text of the form {@code <description> /by <when>}.
     *
     * @param argument the description and due date, as the user typed them
     * @return the deadline
     * @throws MattChatBotException if a part is missing or the date is not understood
     */
    public static Deadline parseDeadline(String argument) throws MattChatBotException {
        String example = "Perhaps: " + Command.DEADLINE.getKeyword()
                + " return book /by 2019-10-15";
        checkGivenOnce(argument, KEYWORD_BY, 0);
        int byAt = argument.indexOf(KEYWORD_BY);
        if (byAt < 0) {
            throw new MattChatBotException(
                    "A deadline wants a /by, so I know when it falls due. " + example);
        }
        String description = argument.substring(0, byAt).trim();
        String by = argument.substring(byAt + KEYWORD_BY.length()).trim();
        if (description.isEmpty()) {
            throw new MattChatBotException(
                    "A deadline wants a description before the /by. " + example);
        }
        if (by.isEmpty()) {
            throw new MattChatBotException(
                    "A deadline wants a date or time after the /by. " + example);
        }
        return new Deadline(checkDescription(description), DateTimes.parse(by));
    }

    /**
     * Builds an event from text of the form
     * {@code <description> /from <start> /to <end>}.
     *
     * @param argument the description and time range, as the user typed them
     * @return the event
     * @throws MattChatBotException if a part is missing or a date is not understood
     */
    public static Event parseEvent(String argument) throws MattChatBotException {
        String example = "Perhaps: " + Command.EVENT.getKeyword()
                + " project meeting /from 2019-10-15 1400 /to 2019-10-15 1600";
        checkGivenOnce(argument, KEYWORD_FROM, 0);
        int fromAt = argument.indexOf(KEYWORD_FROM);
        if (fromAt < 0) {
            throw new MattChatBotException(
                    "An event wants a /from, so I know when it begins. " + example);
        }
        // Look for /to only after /from, so that a description mentioning "/to"
        // does not get mistaken for the end time.
        // Counted only after the /from, so that a description mentioning "/to"
        // is not mistaken for a second end time.
        checkGivenOnce(argument, KEYWORD_TO, fromAt + KEYWORD_FROM.length());
        int toAt = argument.indexOf(KEYWORD_TO, fromAt + KEYWORD_FROM.length());
        if (toAt < 0) {
            throw new MattChatBotException(
                    "An event wants a /to, so I know when it concludes. " + example);
        }
        String description = argument.substring(0, fromAt).trim();
        String from = argument.substring(fromAt + KEYWORD_FROM.length(), toAt).trim();
        String to = argument.substring(toAt + KEYWORD_TO.length()).trim();
        if (description.isEmpty()) {
            throw new MattChatBotException(
                    "An event wants a description before the /from. " + example);
        }
        if (from.isEmpty()) {
            throw new MattChatBotException(
                    "An event wants a start time after the /from. " + example);
        }
        if (to.isEmpty()) {
            throw new MattChatBotException(
                    "An event wants an end time after the /to. " + example);
        }
        return new Event(checkDescription(description), DateTimes.parse(from),
                DateTimes.parse(to));
    }

    /**
     * Converts a task number the user typed into a list position.
     * <p>
     * Only converts: whether the position exists is TaskList's business, so
     * this does not need to know how many tasks there are.
     *
     * @param argument the task number as the user typed it, 1-based
     * @param command  the command being run, so the message can name it
     * @return the matching 0-based index
     * @throws MattChatBotException if the number is missing or is not a number
     */
    public static int parseTaskNumber(String argument, Command command)
            throws MattChatBotException {
        String keyword = command.getKeyword();
        if (argument.isEmpty()) {
            throw new MattChatBotException("Which entry shall I " + keyword
                    + "? Perhaps: " + keyword + " 2");
        }
        try {
            return Integer.parseInt(argument) - 1;
        } catch (NumberFormatException e) {
            throw new MattChatBotException("\"" + argument
                    + "\" is not a number I recognise. Perhaps: " + keyword + " 2");
        }
    }

    /**
     * Reads the keyword given to the find command.
     *
     * @param argument the keyword as the user typed it
     * @return the keyword
     * @throws MattChatBotException if no keyword was given
     */
    public static String parseKeyword(String argument) throws MattChatBotException {
        if (argument.isEmpty()) {
            throw new MattChatBotException("What shall I look for? Perhaps: "
                    + Command.FIND.getKeyword() + " book");
        }
        return argument;
    }

    /**
     * Reads which task an update command refers to.
     *
     * @param argument everything the user typed after the command word
     * @return the matching 0-based index
     * @throws MattChatBotException if the number is missing or is not a number
     */
    public static int parseUpdateTarget(String argument) throws MattChatBotException {
        if (argument.isEmpty()) {
            String keyword = Command.UPDATE.getKeyword();
            throw new MattChatBotException("Which entry shall I " + keyword
                    + "? Perhaps: " + keyword + " 2 new description");
        }
        return parseTaskNumber(splitOffCommandWord(argument)[0], Command.UPDATE);
    }

    /**
     * Reads the parts an update command asks to change.
     * <p>
     * Whatever sits between the task number and the first marker is the new
     * description, and each marker's value runs up to the next marker or the
     * end of the line. Parts that were not typed stay null, which is how a
     * user changes an event's end time without restating its start.
     *
     * @param argument everything the user typed after the command word
     * @return the requested changes, with every date already parsed
     * @throws MattChatBotException if nothing was asked for, or a date is unusable
     */
    public static TaskUpdate parseUpdateChanges(String argument) throws MattChatBotException {
        String changes = parseArgument(argument);
        for (String marker : UPDATE_MARKERS) {
            checkGivenOnce(changes, marker, 0);
        }
        int firstMarker = indexOfNextMarker(changes, 0);
        String description = firstMarker < 0 ? changes : changes.substring(0, firstMarker);
        TaskUpdate update = new TaskUpdate(
                checkedOrNull(description),
                parseMarkerValue(changes, KEYWORD_BY),
                parseMarkerValue(changes, KEYWORD_FROM),
                parseMarkerValue(changes, KEYWORD_TO));
        if (update.isEmpty()) {
            String keyword = Command.UPDATE.getKeyword();
            throw new MattChatBotException("You have not said what to change. Perhaps: " + keyword
                    + " 2 new description, or " + keyword + " 2 /by 2019-12-01");
        }
        return update;
    }

    /**
     * Returns where the next update marker starts, at or after a position.
     *
     * @param text the text being read
     * @param from where to start looking
     * @return the earliest marker's position, or -1 if none is left
     */
    private static int indexOfNextMarker(String text, int from) {
        return Arrays.stream(UPDATE_MARKERS)
                .mapToInt(marker -> text.indexOf(marker, from))
                .filter(at -> at >= 0)
                .min()
                .orElse(-1);
    }

    /**
     * Reads one marker's value, or null if the marker was not typed.
     *
     * @param changes the text after the task number
     * @param marker  the marker to look for, e.g. {@code /by}
     * @return the moment it names, or null if the marker is absent
     * @throws MattChatBotException if the marker was given nothing, or an unreadable date
     */
    private static LocalDateTime parseMarkerValue(String changes, String marker)
            throws MattChatBotException {
        int at = changes.indexOf(marker);
        if (at < 0) {
            return null;
        }
        int valueStart = at + marker.length();
        int nextMarker = indexOfNextMarker(changes, valueStart);
        String value = nextMarker < 0
                ? changes.substring(valueStart)
                : changes.substring(valueStart, nextMarker);
        if (value.isBlank()) {
            throw new MattChatBotException("The " + marker + " wants a date after it. Perhaps: "
                    + Command.UPDATE.getKeyword() + " 2 " + marker + " 2019-12-01");
        }
        return DateTimes.parse(value);
    }

    /**
     * Returns a checked, tidied description, or null if none was given.
     *
     * @param text the text before the first marker
     * @return the description, or null if nothing was there to change
     * @throws MattChatBotException if it holds the save file's separator
     */
    private static String checkedOrNull(String text) throws MattChatBotException {
        String trimmed = text.trim();
        return trimmed.isEmpty() ? null : checkDescription(trimmed);
    }

    /**
     * Reads the date given to the on command.
     *
     * @param argument the date as the user typed it
     * @return the date
     * @throws MattChatBotException if the date is missing or not understood
     */
    public static LocalDate parseOnDate(String argument) throws MattChatBotException {
        if (argument.isEmpty()) {
            throw new MattChatBotException("Which date, may I ask? Perhaps: "
                    + Command.ON.getKeyword() + " 2019-10-15");
        }
        return DateTimes.parseDate(argument);
    }
}

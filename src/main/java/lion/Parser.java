package lion;

/**
 * Extracts command types and task details from user input.
 */
public class Parser {
    /** Length of the "mark " command word, including its one trailing space. */
    public static final int MARK_PREFIX_LENGTH = 5;
    /** Length of the "unmark "/"delete " command words, including their trailing space. */
    public static final int UNMARK_OR_DELETE_PREFIX_LENGTH = 7;

    /** Length of the "todo" command word, before any trailing whitespace is trimmed. */
    private static final int TODO_PREFIX_LENGTH = 4;
    /** Length of the "deadline " command word, including its one trailing space. */
    private static final int DEADLINE_PREFIX_LENGTH = 9;
    /** Length of the "event" command word, before any trailing whitespace is trimmed. */
    private static final int EVENT_PREFIX_LENGTH = 5;
    /** Length of the "find" command word, before any trailing whitespace is trimmed. */
    private static final int FIND_PREFIX_LENGTH = 4;

    /** Creates a parser for Lion commands. */
    public Parser() {
    }

    /**
     * Identifies the command type represented by the input.
     *
     * @param input complete command entered by the user.
     * @return matching command type, or {@link CommandType#UNKNOWN}.
     */
    public CommandType getCommandType(String input) {
        return CommandType.from(input);
    }

    /**
     * Extracts and trims the description from a todo command.
     *
     * @param input todo command entered by the user.
     * @return todo description, possibly empty.
     */
    public String getTodoDescription(String input) {
        // Skipping exactly TODO_PREFIX_LENGTH characters assumes the command
        // word is "todo", which is only guaranteed once getCommandType has
        // classified the input as TODO, which Lion.java always checks before
        // calling this method.
        assert getCommandType(input) == CommandType.TODO
                : "getTodoDescription should only be called for todo commands";

        return input.substring(TODO_PREFIX_LENGTH).trim();
    }

    /**
     * Separates a deadline command into its description and due date.
     *
     * <p>Tolerates any amount of whitespace around {@code /by}, and splits on
     * only its first occurrence, so a date/description that happens to
     * contain the literal text "/by" again is preserved rather than
     * mis-split.
     *
     * @param input deadline command containing a {@code /by} separator.
     * @return description at index 0 and deadline text at index 1.
     * @throws LionException if {@code /by} is missing, or the description or date is empty.
     */
    public String[] getDeadlineParts(String input) throws LionException {
        // Skipping exactly DEADLINE_PREFIX_LENGTH characters assumes the
        // command word is "deadline ", which only holds once getCommandType
        // has classified the input as DEADLINE.
        assert getCommandType(input) == CommandType.DEADLINE
                : "getDeadlineParts should only be called for deadline commands";

        // input.length() can be as short as "deadline" (8 chars) once
        // getCommandType has classified it as DEADLINE, which is shorter than
        // DEADLINE_PREFIX_LENGTH (9, "deadline " with its trailing space) —
        // substring() on it directly would throw StringIndexOutOfBoundsException.
        String details = input.length() > DEADLINE_PREFIX_LENGTH
                ? input.substring(DEADLINE_PREFIX_LENGTH).trim()
                : "";

        if (!details.contains("/by")) {
            throw new LionException(
                    "A deadline needs a '/by' date, e.g. 'deadline return book /by 2/12/2019 1800'.");
        }

        String[] parts = details.split("\\s*/by\\s*", 2);
        String description = parts[0].trim();
        String byText = parts.length > 1 ? parts[1].trim() : "";

        if (description.isEmpty()) {
            throw new LionException("A deadline needs a description before '/by'.");
        }
        if (byText.isEmpty()) {
            throw new LionException("A deadline needs a date after '/by'.");
        }

        return new String[] { description, byText };
    }

    /**
     * Separates an event command into its description, start, and end.
     *
     * <p>Tolerates any amount of whitespace around {@code /from} and
     * {@code /to}, and splits on only their first occurrence, mirroring
     * {@link #getDeadlineParts(String)}.
     *
     * @param input event command containing {@code /from} and {@code /to}.
     * @return description, start, and end in that order.
     * @throws LionException if {@code /from} or {@code /to} is missing, or any of the
     *     description, start, or end is empty.
     */
    public String[] getEventParts(String input) throws LionException {
        // Skipping exactly EVENT_PREFIX_LENGTH characters assumes the command
        // word is "event", which only holds once getCommandType has
        // classified the input as EVENT.
        assert getCommandType(input) == CommandType.EVENT
                : "getEventParts should only be called for event commands";

        String details = input.length() > EVENT_PREFIX_LENGTH
                ? input.substring(EVENT_PREFIX_LENGTH).trim()
                : "";

        if (!details.contains("/from") || !details.contains("/to")) {
            throw new LionException("An event needs both '/from' and '/to', e.g. "
                    + "'event party /from 2/12/2019 1800 /to 2/12/2019 2000'.");
        }

        String[] fromParts = details.split("\\s*/from\\s*", 2);
        String description = fromParts[0].trim();
        String afterFrom = fromParts.length > 1 ? fromParts[1] : "";

        String[] toParts = afterFrom.split("\\s*/to\\s*", 2);
        String from = toParts[0].trim();
        String to = toParts.length > 1 ? toParts[1].trim() : "";

        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new LionException("An event needs a description, a start, and an end — none can be empty.");
        }

        return new String[] { description, from, to };
    }

    /**
     * Converts a one-based task number in a command to an array index.
     *
     * @param input command containing the task number.
     * @param prefixLength number of command-prefix characters to skip.
     * @return zero-based task index.
     * @throws LionException if no number is given, or the given text is not a whole number.
     */
    public int getTaskIndex(String input, int prefixLength) throws LionException {
        // Callers are expected to pass one of this class's own prefix-length
        // constants. Any other value would mean a caller is using this shared
        // helper incorrectly.
        assert prefixLength == MARK_PREFIX_LENGTH || prefixLength == UNMARK_OR_DELETE_PREFIX_LENGTH
                : "prefixLength must match the 'mark '/'unmark '/'delete ' prefix";

        // input.length() can be shorter than prefixLength (e.g. "mark" is 4
        // chars but MARK_PREFIX_LENGTH is 5, "mark " with its trailing
        // space) — substring() on it directly would throw
        // StringIndexOutOfBoundsException.
        String number = input.length() > prefixLength ? input.substring(prefixLength).trim() : "";

        if (number.isEmpty()) {
            throw new LionException("Which task? Give Lion a task number, e.g. 'mark 2'.");
        }

        try {
            return Integer.parseInt(number) - 1;
        } catch (NumberFormatException e) {
            throw new LionException("'" + number + "' isn't a valid task number — please use a whole number.");
        }
    }

    /**
     * Extracts and trims the keyword from a find command.
     *
     * @param input find command entered by the user.
     * @return search keyword, possibly empty.
     */
    public String getFindKeyword(String input) {
        // Skipping exactly FIND_PREFIX_LENGTH characters assumes the command
        // word is "find", which only holds once getCommandType has
        // classified the input as FIND.
        assert getCommandType(input) == CommandType.FIND
                : "getFindKeyword should only be called for find commands";

        return input.substring(FIND_PREFIX_LENGTH).trim();
    }
}

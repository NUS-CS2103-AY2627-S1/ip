package dennis.parser;

/**
 * Represents a command supported by the Dennis application.
 */
public enum CommandType {
    /** Quit the program ({@code bye}). */
    BYE,
    /** Show every task ({@code list}). */
    LIST,
    /** Mark a task as done ({@code mark <n>}). */
    MARK,
    /** Mark a task as not done ({@code unmark <n>}). */
    UNMARK,
    /** Remove a task ({@code delete <n>}). */
    DELETE,
    /** Add a plain task ({@code todo <description>}). */
    TODO,
    /** Add a task with a due date ({@code deadline <description> /by <date>}). */
    DEADLINE,
    /** Add a task with a start and end date ({@code event ... /from ... /to ...}). */
    EVENT,
    /** List the tasks on a given date ({@code on <date>}). */
    ON,
    /** Add a task with a completion window ({@code within ... /from ... /to ...}). */
    WITHIN,
    /** Find tasks whose description contains a keyword ({@code find <keyword>}). */
    FIND,
    /** Anything that does not match a known command word. */
    UNKNOWN;

    /**
     * Determines the command type from a line of user input.
     *
     * @param input user input to examine
     * @return the matching command type, or UNKNOWN
     */
    public static CommandType from(String input) {
        if (input == null || input.isBlank()) {
            return UNKNOWN;
        }

        String commandWord = input.trim().split("\\s+", 2)[0];

        // The blank check above guarantees input.trim() has at least one
        // non-space character, so the split always yields a non-empty first
        // token for the switch to match.
        assert !commandWord.isEmpty() : "non-blank input must yield a command word";

        return switch (commandWord) {
            case "bye" -> BYE;
            case "list" -> LIST;
            case "mark" -> MARK;
            case "unmark" -> UNMARK;
            case "delete" -> DELETE;
            case "todo" -> TODO;
            case "deadline" -> DEADLINE;
            case "event" -> EVENT;
            case "on" -> ON;
            case "within" -> WITHIN;
            case "find" -> FIND;
            default -> UNKNOWN;
        };
    }
}

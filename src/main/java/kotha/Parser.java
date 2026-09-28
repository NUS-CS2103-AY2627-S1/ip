package kotha;

/** Identifies the type of command entered by the user. */
public class Parser {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String CONSTRAINT_COMMAND = "constraint";
    private static final String DELETE_COMMAND = "delete";

    /** The command types understood by Kotha. */
    public enum CommandType {
        BYE, LIST, FIND, MARK, UNMARK, TODO, DEADLINE, EVENT, CONSTRAINT, DELETE, UNKNOWN
    }

    /**
     * Determines the command type from the first word of the input.
     *
     * @param input the String input from the user.
     */
    public CommandType parse(String input) {
        if (input == null) {
            return CommandType.UNKNOWN;
        }
        String command = input.trim();
        if (command.equals(BYE_COMMAND)) {
            return CommandType.BYE;
        }
        if (command.equals(LIST_COMMAND)) {
            return CommandType.LIST;
        }
        if (hasCommandWord(command, FIND_COMMAND)) {
            return CommandType.FIND;
        }
        if (hasCommandWord(command, MARK_COMMAND)) {
            return CommandType.MARK;
        }
        if (hasCommandWord(command, UNMARK_COMMAND)) {
            return CommandType.UNMARK;
        }
        if (hasCommandWord(command, TODO_COMMAND)) {
            return CommandType.TODO;
        }
        if (hasCommandWord(command, DEADLINE_COMMAND)) {
            return CommandType.DEADLINE;
        }
        if (hasCommandWord(command, EVENT_COMMAND)) {
            return CommandType.EVENT;
        }
        if (hasCommandWord(command, CONSTRAINT_COMMAND)) {
            return CommandType.CONSTRAINT;
        }
        if (hasCommandWord(command, DELETE_COMMAND)) {
            return CommandType.DELETE;
        } else {
            return CommandType.UNKNOWN;
        }
    }

    private boolean hasCommandWord(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }
}

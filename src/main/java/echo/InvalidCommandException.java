package echo;

/**
 * Custom exception meant to be thrown when the user inputs an invalid command.
 */
public class InvalidCommandException extends IllegalArgumentException {
    /**
     * Retrieves the invalid input message specific to each type of command
     *
     * @param command the command that was incorrectly used
     */
    public static String getInvalidCommandMessage(String command) {
        switch (CommandWord.fromString(command)) {
            case MARK -> {
                return "ERROR: INVALID INPUT FOR mark. EXAMPLE USAGE: mark 2";
            }
            case UNMARK -> {
                return "ERROR: INVALID INPUT FOR unmark. EXAMPLE USAGE: unmark 2";
            }
            case TODO -> {
                return "ERROR: INVALID INPUT FOR todo. EXAMPLE USAGE: todo Example";
            }
            case DEADLINE -> {
                return "ERROR: INVALID INPUT FOR deadline. EXAMPLE USAGE: deadline Example /2023-12-13";
            }
            case EVENT -> {
                return "ERROR: INVALID INPUT FOR event. EXAMPLE USAGE: event Example /2023-12-13 /2023-12-14";
            }
            case DELETE -> {
                return "ERROR: INVALID INPUT FOR delete. EXAMPLE USAGE: delete 2";
            }
            case FIND -> {
                return "ERROR: INVALID INPUT FOR find. EXAMPLE USAGE: find book";
            }
            case UNDO -> {
                return "ERROR: NO ACTIONS CAN BE UNDONE AT THIS POINT.";
            }
            default -> {
                return "ERROR: COMMAND NOT FOUND IN DATABASE.";
            }
        }
    }
}

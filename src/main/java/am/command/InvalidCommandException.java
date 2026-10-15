package am.command;

/** Indicates that a recognized command has invalid arguments or formatting. */
public class InvalidCommandException extends RuntimeException {
    /** Creates an exception with a user-facing explanation. */
    public InvalidCommandException(String message) {
        super(message);
    }

    /**
     * Creates the common syntax error with an example for the recognized command.
     *
     * @param commandType recognized command name, optionally qualified by the edited task type
     * @return syntax error containing a complete example command
     */
    public static InvalidCommandException forSyntax(String commandType) {
        String example = switch (commandType) {
            case "bye" -> "bye";
            case "list" -> "list";
            case "past" -> "past";
            case "find" -> "find milk";
            case "todo" -> "todo buy milk";
            case "deadline" -> "deadline report /by 2026-09-12 1800";
            case "event" -> "event meeting /from 2026-09-12 1400 /to 2026-09-12 1600";
            case "edit" -> "edit 1 /name buy milk";
            case "edit deadline" -> "edit 1 /by 2026-09-12 1800";
            case "edit event" -> "edit 1 /from 2026-09-12 1400 /to 2026-09-12 1600";
            case "mark" -> "mark 1";
            case "unmark" -> "unmark 1";
            case "delete" -> "delete 1";
            default -> throw new IllegalArgumentException("Unsupported command: " + commandType);
        };
        return new InvalidCommandException("You messed up the command.\nExample: " + example);
    }
}

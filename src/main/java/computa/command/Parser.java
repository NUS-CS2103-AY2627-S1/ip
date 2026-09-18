package computa.command;

/** Converts complete user input into a command object when it is supported. */
public final class Parser {
    /** Prevents instantiation of this stateless command parser. */
    private Parser() {
        // Utility class; do not instantiate.
    }

    /**
     * Recognizes the first command migrated to the command framework.
     *
     * @param fullCommand complete line entered by the user.
     * @return an {@link ExitCommand} for {@code bye}, or {@code null} until
     *         later increments migrate the remaining commands.
     */
    public static Command parse(String fullCommand) {
        if ("bye".equals(fullCommand)) {
            return new ExitCommand();
        }
        return null;
    }
}

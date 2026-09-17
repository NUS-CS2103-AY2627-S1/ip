package lion;

/**
 * Represents the commands that lion.Lion understands.
 */
public enum CommandType {
    /** Displays all saved tasks. */
    LIST("Shows every task in your pride."),
    /** Adds a task without date or time details. */
    TODO("Adds a plain task with no date or time."),
    /** Adds a task with a due date and time. */
    DEADLINE("Adds a task with a deadline to beat."),
    /** Adds a task with start and end details. */
    EVENT("Adds a task with a start and end time."),
    /** Marks a task as complete. */
    MARK("Marks a task as done."),
    /** Marks a task as incomplete. */
    UNMARK("Marks a task as not done."),
    /** Removes a task. */
    DELETE("Removes a task from your pride."),
    /** Finds tasks whose descriptions contain a keyword. */
    FIND("Hunts down tasks matching a keyword."),
    /** Displays the list of available commands. */
    HELP("Shows this list of commands."),
    /** Exits the application. */
    BYE("Sends Lion off to rest."),
    /** Represents input that does not match a supported command. */
    UNKNOWN("Represents input that does not match a supported command.");

    private final String description;

    CommandType(String description) {
        this.description = description;
    }

    /**
     * Returns a one-line, user-facing description of this command.
     *
     * @return description shown to users, e.g. by the {@code help} command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Identifies the command represented by the first word of the user's input.
     *
     * @param input full line entered by the user.
     * @return the matching command, or {@link #UNKNOWN} if there is no match.
     */
    public static CommandType from(String input) {
        // input.trim() would throw NullPointerException anyway, but the assertion
        // documents that every caller is expected to supply a non-null line
        // (MainWindow always passes text-field text, never null).
        assert input != null : "command input must not be null";

        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return UNKNOWN;
        }

        String commandWord = trimmedInput.split("\\s+", 2)[0];
        try {
            return CommandType.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}

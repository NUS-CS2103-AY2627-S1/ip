package sine.ui;

/**
 * Provides shared text displayed by Sine's user interfaces.
 */
public final class Messages {
    /** Lists every supported command using its expected argument format. */
    public static final String COMMAND_HELP = "Here's a list of commands I can do!\n"
            + " - todo DESCRIPTION\n"
            + " - deadline DESCRIPTION /by YYYY-MM-DD\n"
            + " - event DESCRIPTION /from START /to END\n"
            + " - list\n"
            + " - find KEYWORD\n"
            + " - mark TASK_NUMBER\n"
            + " - unmark TASK_NUMBER\n"
            + " - delete TASK_NUMBER\n"
            + " - undo (or UNDO)\n"
            + " - bye";

    private Messages() {
    }
}

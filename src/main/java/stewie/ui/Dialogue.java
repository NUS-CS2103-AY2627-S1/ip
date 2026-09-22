package stewie.ui;

/**
 * Provides shared dialogue for Stewie's theatrical, dryly sarcastic task assistant persona.
 * Common replies keep the console and graphical conversations consistent.
 */
public final class Dialogue {
    /** Introduces Stewie and invites a task command. */
    public static final String GREETING =
            "Ah, there you are. I'm Stewie.\nTell me your tasks. Clearly, this operation requires supervision.";

    /** Says farewell with a reluctant hint of affection. */
    public static final String GOODBYE =
            "Very well. Do come back. I mean, someone must supervise your progress.";

    /** Confirms that a task was added. */
    public static final String ADDED =
            "Consider it recorded. A small triumph for competent administration.";

    /** Confirms that requested task details were updated. */
    public static final String UPDATED =
            "Revised to your specifications. Yes, even that detail.";

    /** Introduces the current task list. */
    public static final String LIST =
            "Behold, your agenda. Let us examine the scale of this undertaking.";

    /** Explains that the displayed list has no tasks. */
    public static final String EMPTY =
            "No tasks to show. How suspiciously serene. Try adding a task or checking your search.";

    private Dialogue() {
        // Shared dialogue does not require an instance.
    }
}

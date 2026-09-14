package penguin;

/**
 * Stores the chatbot identity and wording shared by console and GUI clients.
 */
public final class PenguinIdentity {
    /** Name used by the chatbot when addressing the user. */
    public static final String CHATBOT_NAME = "Pip";

    /** Short description displayed beneath the chatbot name. */
    public static final String CHATBOT_DESCRIPTION =
            "Your cheerful productivity penguin";

    /** Greeting displayed when the chatbot starts. */
    public static final String GREETING_MESSAGE =
            "Chilly greetings! I'm " + CHATBOT_NAME + ", your productivity penguin.\n"
                    + "Let's tackle today's tasks one small waddle at a time!";

    /** Title displayed in the application window. */
    public static final String WINDOW_TITLE =
            CHATBOT_NAME + " — Your Productivity Penguin";

    /** Prompt displayed in the GUI message field. */
    public static final String INPUT_PROMPT =
            "Tell " + CHATBOT_NAME + " what needs doing…";

    /**
     * Prevents instantiation of this identity utility class.
     */
    private PenguinIdentity() {
    }
}

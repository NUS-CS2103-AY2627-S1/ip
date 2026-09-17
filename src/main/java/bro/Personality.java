package bro;

/** Defines Bro's user-facing identity and signature phrases. */
public final class Personality {
    /** The name shown for the chatbot. */
    public static final String PERSONA_NAME = "Bro";

    /** The short role description shown in the graphical interface. */
    public static final String PERSONA_TAGLINE = "Your laid-back productivity wingman";

    /** The greeting shown when a new session starts. */
    public static final String PERSONA_WELCOME_MESSAGE =
            "Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?";

    /** The sign-off shown when a session ends. */
    public static final String PERSONA_GOODBYE_MESSAGE = "Catch you later, bro. Keep crushing that to-do list!";

    private Personality() {
    }
}

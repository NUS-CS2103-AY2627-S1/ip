package arthur.messages;

/**
 * An abstract response type. It is serialized using {@link Message#toString()}.
 */
public abstract class Message {
    private static final String[] QUOTES = {
        "There's an infinite number of monkeys outside who want to talk to us about this script for Hamlet "
                + "they've worked out.",
        "Is there any tea on this spaceship?",
        "This must be Thursday. I never could get the hang of Thursdays.",
        "I'd far rather be happy than right any day.",
        "All I want is a normal cup of tea. It is asking for a tea-total and absolute miracle?",
        "If I could just get some tea, then I might be able to cope with the end of the universe."
    };

    protected String getRandomQuote() {
        return QUOTES[(int) (Math.random() * QUOTES.length)];
    }


    @Override
    public abstract String toString();

    /**
     * Returns true if the program should return after this message.
     */
    public abstract boolean isFinal();

    /**
     * Returns the JavaFX CSS class name for this message.
     *
     * @return The class name for this message.
     */
    public String getClassName() {
        return "";
    }
}

package arthur.messages;

/**
 * Represents the message displayed when the application starts.
 */
public class WelcomeMessage extends Message {
    private static final String MESSAGE = """
            I'm Dent, Arthur Dent. Would it save you a lot of time
            if I just gave up and went mad now?
            """;

    @Override
    public String toString() {
        return MESSAGE;
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}

package orangutan;

/**
 * Supplies exceptions pertaining to the Orangutan Chatbot.
 */
public class OrangutanException extends Exception {
    /**
     * Constructs a new instance of OrangutanException.
     * @param msg Exception message describing the problem.
     */
    public OrangutanException(String msg) {
        super(msg);
    }

    /**
     * Returns localized message.
     */
    @Override
    public String toString() {
        return getLocalizedMessage();
    }
}

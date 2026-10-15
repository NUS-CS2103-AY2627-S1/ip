package odysseus;

/** An error caused by invalid user input to Odysseus. */
public class OdysseusException extends Exception {

    /**
     * Constructs an {@link OdysseusException} with the given detail message.
     *
     * @param msg string representing details of the error
     */
    public OdysseusException(String msg) {
        super(msg);
    }
}

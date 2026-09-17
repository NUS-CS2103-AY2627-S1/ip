package jarvis;

/** Represents an input error reported by the Jarvis chatbot. */
public class JarvisException extends Exception {
    /** Creates an exception with a message suitable for displaying to the user. */
    public JarvisException(String message) {
        super(message);
    }
}

package sylveon;

/** Represents an error caused by an invalid sylveon.Sylveon command. */
public class SylveonException extends Exception {
    public SylveonException(String message) {
        super(message);
    }
}

package otaku.exception;

/** Represents an input error that Otaku can explain to the user. */
public class OtakuException extends Exception {
    /** Creates an exception with a user-friendly explanation. */
    public OtakuException(String message) {
        super(message);
    }
}

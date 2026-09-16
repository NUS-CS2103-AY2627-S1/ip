package miku;

/** Represents an input error that Miku can explain to the user. */
public class MikuException extends Exception {
    /** Creates an input error with the message to show to the user. */
    public MikuException(String message) {
        super(message);
    }
}

package fein;

/** Represents an input error that Fein can explain to the user. */
public class FeinException extends Exception {
    /** Creates an input error with the given user-facing message. */
    public FeinException(String message) {
        super(message);
    }
}

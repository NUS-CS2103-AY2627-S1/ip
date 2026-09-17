package bro.exception;

/** Represents invalid user input that Bro can explain and recover from. */
public class BroException extends Exception {
    /** Creates an exception with the message that should be shown to the user. */
    public BroException(String message) {
        super(message);
    }
}

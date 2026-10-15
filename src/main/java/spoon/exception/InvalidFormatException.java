package spoon.exception;

/**
 * Thrown when the argument given by the user is not in the proper format (e.g. integer, string etc.).
 */
public class InvalidFormatException extends SpoonException {

    // Constructor
    public InvalidFormatException(String format) {
        super("Oops, that appears to be the wrong format! Please enter a " + format + ".");
    }
}

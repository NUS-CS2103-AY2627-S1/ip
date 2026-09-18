package vega;

/** Represents an error caused by an invalid command entered into Vega. */
public class VegaException extends Exception {
    /**
     * Creates an exception that explains how the user can correct an input.
     *
     * @param message Explanation shown to the user.
     */
    public VegaException(String message) {
        super(message);
    }
}

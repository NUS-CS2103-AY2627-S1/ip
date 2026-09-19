package chai;

/** Represents an input error that Chai can explain to the user. */
public class ChaiException extends Exception {

    /**
     * Creates an input error with the given explanation.
     *
     * @param message Explanation to show the user.
     */
    public ChaiException(String message) {
        super(message);
    }
}

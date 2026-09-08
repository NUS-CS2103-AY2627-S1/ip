package crack;

/**
 * Thrown when Crack cannot do what the user asked, for a reason worth telling them about.
 */
public class CrackException extends Exception {
    /**
     * Creates an exception carrying a message written for the user to read.
     *
     * @param message What went wrong, in Crack's own words.
     */
    public CrackException(String message) {
        super(message);
    }
}

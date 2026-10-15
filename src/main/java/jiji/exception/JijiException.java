package jiji.exception;

/**
 * Base checked exception class for all domain-specific exceptions in Jiji.
 */
public class JijiException extends Exception {

    /**
     * Constructs a new JijiException with the specified error message.
     *
     * @param message The detailed error message.
     */
    public JijiException(String message) {
        super(message);
    }
}

package cheecken;

/**
 * Reports an unrecognised command.
 */
public class CheeckenUnknownException extends RuntimeException {
    /**
     * Creates an exception with guidance for the invalid command.
     */
    public CheeckenUnknownException() {
        super("""
                What do you want? I only understand sentences starting with `todo`, `event` and `deadline.`
                (e.g. `event her wedding /from later /to forever`)"""
        );
    }
}

package xiaozhi.exception;

/**
 * Signals a problem XiaoZhi encountered while parsing or executing a user
 * command, e.g. malformed input or an out-of-range task number, that should
 * be reported to the user rather than treated as an unexpected bug.
 */
public class XiaoZhiException extends Exception {

    /**
     * Creates a XiaoZhiException with a message meant to be shown to the user as-is.
     *
     * @param message Description of what went wrong, suitable for display to the user.
     */
    public XiaoZhiException(String message) {
        super(message);
    }
}

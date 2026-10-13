package jimbo;

/**
 * For non-fatal exceptions that happen when
 * Jimbo is running.
 */
public class JimboException extends Exception {
    public JimboException(String msg) {
        super(msg);
    }
}

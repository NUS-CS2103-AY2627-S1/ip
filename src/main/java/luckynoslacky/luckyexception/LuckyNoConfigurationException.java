package luckynoslacky.luckyexception;

/**
 * Indicates that an application configuration value is invalid.
 */
public class LuckyNoConfigurationException extends RuntimeException {
    /**
     * Creates a configuration error with its underlying cause.
     *
     * @param message explanation of the configuration error
     * @param cause underlying parsing or validation failure
     */
    public LuckyNoConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

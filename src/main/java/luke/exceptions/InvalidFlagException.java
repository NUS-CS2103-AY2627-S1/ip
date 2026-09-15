package luke.exceptions;

/** Represents a problem with command flags such as /by, /from, or /to. */
public class InvalidFlagException extends UserInputException {
    private InvalidFlagException(String message) {
        super(message);
    }

    /**
     * Creates an error for a flag that appears more than once.
     *
     * @param flag flag keyword without the leading slash
     * @return formatted flag exception
     */
    public static InvalidFlagException duplicate(String flag) {
        return new InvalidFlagException("Duplicate flag: /%s".formatted(flag));
    }

    /**
     * Creates an error for a flag keyword Luke does not know.
     *
     * @param flag flag keyword without the leading slash
     * @return formatted flag exception
     */
    public static InvalidFlagException unidentified(String flag) {
        return new InvalidFlagException("Unidentified flag: /%s".formatted(flag));
    }

    /**
     * Creates an error for a required flag that was not provided.
     *
     * @param flag required flag keyword without the leading slash
     * @return formatted flag exception
     */
    public static InvalidFlagException missingRequired(String flag) {
        return new InvalidFlagException("Missing required flag: /%s".formatted(flag));
    }

    /**
     * Creates an error for a flag that has no value after it.
     *
     * @param flag flag keyword without the leading slash
     * @return formatted flag exception
     */
    public static InvalidFlagException missingValue(String flag) {
        return new InvalidFlagException("Missing value for flag: /%s".formatted(flag));
    }

    /**
     * Creates an error for a known flag used with the wrong command or task type.
     *
     * @param flag flag keyword without the leading slash
     * @return formatted flag exception
     */
    public static InvalidFlagException unsupported(String flag) {
        return new InvalidFlagException("Unsupported flag: /%s".formatted(flag));
    }

    /**
     * Creates an error for an unsupported value used with a known flag.
     *
     * @param flag flag keyword without the leading slash
     * @param value unsupported value supplied for the flag
     * @return formatted flag exception
     */
    public static InvalidFlagException unsupportedValue(String flag, String value) {
        return new InvalidFlagException("Unsupported value for /%s: %s".formatted(flag, value));
    }
}

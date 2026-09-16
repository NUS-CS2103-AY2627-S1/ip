package zucc.task;

import java.util.Locale;

import zucc.ZuccException;

/**
 * Represents the optional importance assigned to a task when it is created.
 */
public enum Priority {
    NONE("0"),
    HIGH("1"),
    MEDIUM("2"),
    LOW("3");

    /** Error shown when a command contains an unsupported priority. */
    private static final String INVALID_PRIORITY_ERROR =
            "Zucc's algorithm can't rank that priority. Use high, medium, low, 1, 2, or 3.";

    /** Stable value written to the task data file. */
    private final String storageCode;

    /**
     * Creates a priority with its persistent representation.
     *
     * @param storageCode value written to storage.
     */
    Priority(String storageCode) {
        this.storageCode = storageCode;
    }

    /**
     * Parses a user-entered priority name or number.
     *
     * @param value priority supplied after {@code /priority}.
     * @return corresponding priority.
     * @throws ZuccException if the value is blank or unsupported.
     */
    public static Priority fromUserInput(String value) throws ZuccException {
        if (value == null) {
            return NONE;
        }

        return switch (value.strip().toLowerCase(Locale.ENGLISH)) {
            case "high", "1" -> HIGH;
            case "medium", "2" -> MEDIUM;
            case "low", "3" -> LOW;
            default -> throw new ZuccException(INVALID_PRIORITY_ERROR);
        };
    }

    /**
     * Parses a priority code read from storage.
     *
     * @param storageCode stored priority code.
     * @return corresponding priority.
     * @throws ZuccException if the code is invalid.
     */
    static Priority fromStorageCode(String storageCode) throws ZuccException {
        return switch (storageCode) {
            case "0" -> NONE;
            case "1" -> HIGH;
            case "2" -> MEDIUM;
            case "3" -> LOW;
            default -> throw new ZuccException("Invalid stored priority.");
        };
    }

    /** Returns the stable value used in task storage. */
    String getStorageCode() {
        return storageCode;
    }
}

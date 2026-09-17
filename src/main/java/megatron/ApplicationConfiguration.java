package megatron;

import java.util.List;
import java.util.Objects;

/**
 * Stores the application settings that are derived from command-line arguments.
 */
public final class ApplicationConfiguration {
    private static final String DEFAULT_STORAGE_FILE_PATH = "data/megatron.csv";

    private final String storageFilePath;

    private ApplicationConfiguration(String storageFilePath) {
        this.storageFilePath = storageFilePath;
    }

    /**
     * Creates application settings from command-line arguments.
     *
     * @param arguments The command-line arguments in their original order.
     * @return The application settings.
     */
    public static ApplicationConfiguration fromArguments(List<String> arguments) {
        Objects.requireNonNull(arguments);
        String storageFilePath = arguments.isEmpty()
                ? DEFAULT_STORAGE_FILE_PATH
                : arguments.get(0);
        return new ApplicationConfiguration(storageFilePath);
    }

    /**
     * Creates application settings from command-line arguments.
     *
     * @param arguments The command-line arguments in their original order.
     * @return The application settings.
     */
    public static ApplicationConfiguration fromArguments(String[] arguments) {
        Objects.requireNonNull(arguments);
        return fromArguments(List.of(arguments));
    }

    /**
     * Returns the file path that Megatron uses to store tasks.
     *
     * @return The task storage file path.
     */
    public String getStorageFilePath() {
        return storageFilePath;
    }
}

package baron.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** Collects storage warnings so the user interface can display them at startup. */
public final class StorageWarningHandler extends Handler {
    private static final StorageWarningHandler INSTANCE = new StorageWarningHandler();
    private final List<String> warnings = new ArrayList<>();

    private StorageWarningHandler() {
        setLevel(Level.WARNING);
        Logger.getLogger(Storage.class.getName()).addHandler(this);
    }

    /** Initializes the shared warning handler before storage starts logging. */
    public static void initialize() {}

    /** Returns the shared handler, initializing it if necessary. */
    public static StorageWarningHandler getInstance() {
        return INSTANCE;
    }

    /** Returns a snapshot of warnings recorded by storage. */
    public List<String> getWarnings() {
        return List.copyOf(warnings);
    }

    /** Clears warnings from a previous application initialization. */
    public void clearWarnings() {
        warnings.clear();
    }

    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record)) {
            warnings.add(record.getMessage());
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}

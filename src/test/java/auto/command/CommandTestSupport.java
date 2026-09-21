package auto.command;

import java.nio.file.Path;

import auto.storage.Storage;

/** Provides storage helpers for command tests. */
abstract class CommandTestSupport {
    protected Storage writableStorage(Path directory) {
        return new Storage(directory.resolve("tasks.txt").toString());
    }

    /** Uses an existing directory as the data file to force every save to fail. */
    protected Storage failingStorage(Path directory) {
        return new Storage(directory.toString());
    }
}

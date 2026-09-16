package miku.command;

import java.util.Objects;

import miku.storage.Storage;
import miku.task.TaskList;
import miku.ui.ResponseFormatter;

/** Provides the collaborators available while a command is executed. */
public record CommandContext(TaskList tasks, ResponseFormatter responseFormatter, Storage storage) {
    /** Validates the collaborators required to execute a command. */
    public CommandContext {
        Objects.requireNonNull(tasks);
        Objects.requireNonNull(responseFormatter);
        Objects.requireNonNull(storage);
    }
}

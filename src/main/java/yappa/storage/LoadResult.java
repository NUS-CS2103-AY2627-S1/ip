package yappa.storage;

import java.util.List;

import yappa.task.TaskList;

/**
 * Represents the result of loading tasks from storage.
 *
 * @param tasks    Tasks successfully loaded.
 * @param warnings Warnings produced while loading invalid records.
 */
public record LoadResult(TaskList tasks, List<String> warnings) {

    /**
     * Creates a load result with an immutable copy of the warnings.
     */
    public LoadResult {
        warnings = List.copyOf(warnings);
    }
}

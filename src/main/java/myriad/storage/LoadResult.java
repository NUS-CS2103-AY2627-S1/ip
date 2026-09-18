package myriad.storage;

import java.util.List;

import myriad.task.Task;

/**
 * The outcome of one {@link Storage#load()} call: the tasks that were read
 * successfully, and a description of each line that had to be skipped.
 * {@link Storage} has to report both, and a small record is the simplest way
 * to return two values — the alternative, keeping the skipped lines in a
 * {@code Storage} field for the caller to fetch afterwards, would give
 * {@code Storage} state that lasts beyond the call and only makes sense
 * right after one.
 *
 * @param tasks        the tasks read successfully, in file order.
 * @param skippedLines one already-formatted description per unreadable
 *                     line ("line 3: unknown task type ..."), empty if
 *                     every line loaded.
 */
public record LoadResult(List<Task> tasks, List<String> skippedLines) {
}

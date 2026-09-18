package tally.storage;

import java.util.List;
import java.util.Optional;

import tally.task.Task;

/**
 * What reading the data file produced.
 *
 * <p>A line that does not become a task is skipped rather than costing the user
 * every other task on the tally, so a read can both succeed and have something to
 * report. The note carries that report when there is one: lines that could not be
 * read at all, and lines naming a task an earlier line had already named.
 *
 * @param tasks the tasks the file held, in the order they were written.
 * @param note what did not become a task, or empty when every line did.
 */
public record LoadResult(List<Task> tasks, Optional<String> note) {
}

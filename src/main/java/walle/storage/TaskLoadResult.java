package walle.storage;

import java.util.ArrayList;

import walle.task.Task;

/**
 * The result of loading tasks from the memory file: the tasks that parsed
 * successfully, and how many lines had to be skipped because their content
 * wasn't as expected (e.g. a wrong number of fields, or an unreadable date).
 *
 * @param tasks        the tasks read from the memory file.
 * @param skippedLines the number of lines that couldn't be parsed and were skipped.
 */
public record TaskLoadResult(ArrayList<Task> tasks, int skippedLines) {
}

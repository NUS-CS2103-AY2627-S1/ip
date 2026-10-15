package dude.task;

/**
 * Associates a task with its stable position in a task list.
 *
 * @param index Zero-based index in the source task list.
 * @param task Matching task.
 */
public record TaskMatch(int index, Task task) {
}

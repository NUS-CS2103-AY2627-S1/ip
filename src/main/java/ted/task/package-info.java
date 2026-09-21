/**
 * The tasks Ted keeps track of, and the list holding them.
 * <p>
 * {@link ted.task.Task} is the shared parent of the three kinds a user can
 * create ({@link ted.task.Todo}, {@link ted.task.Deadline} and
 * {@link ted.task.Event}), and defines both how a task shows itself to the
 * user and how it is written to the save file.
 * {@link ted.task.TaskList} holds them in the order they were added and owns
 * the rule that a task number must point at a real task.
 */
package ted.task;

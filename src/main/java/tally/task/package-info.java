/**
 * The tasks themselves, and the tally holding them.
 *
 * <p>{@link tally.task.Task} is what every task has in common: a description
 * and whether it is done. {@link tally.task.Todo}, {@link tally.task.Deadline},
 * {@link tally.task.Event} and {@link tally.task.Window} add whatever times their
 * kind carries, each rendering itself for the screen and for the data file, and
 * each saying for itself which days it takes up. {@link tally.task.TaskList} holds
 * them in the order they were added.
 */
package tally.task;

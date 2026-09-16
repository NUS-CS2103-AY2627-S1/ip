/**
 * The tasks the user keeps track of, and the list holding them.
 *
 * <p>{@link lebronjames.task.Task} is the abstract parent of
 * {@link lebronjames.task.Todo}, {@link lebronjames.task.Deadline}, and
 * {@link lebronjames.task.Event}, each of which knows how to render itself both
 * for the screen and for the save file.
 * {@link lebronjames.task.TaskDateTime} keeps all date parsing and formatting
 * in one place, and {@link lebronjames.task.TaskList} owns the list itself.
 */
package lebronjames.task;

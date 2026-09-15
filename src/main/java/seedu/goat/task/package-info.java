/**
 * The kinds of task GOAT tracks, and the collection that holds them.
 * <p>
 * {@link seedu.goat.task.Task} is the abstract base; {@link seedu.goat.task.Todo},
 * {@link seedu.goat.task.Deadline} and {@link seedu.goat.task.Event} differ in the dates
 * they carry and in how they display and encode themselves. Asking a task whether it
 * falls on a date, rather than testing its type, keeps callers free of any knowledge of
 * these subclasses.
 */
package seedu.goat.task;

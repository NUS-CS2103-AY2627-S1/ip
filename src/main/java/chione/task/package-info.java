/**
 * The things Chione keeps track of.
 *
 * <p>{@link chione.task.Task} is what every task has in common: a description,
 * and whether it is done. The three kinds the user can add extend it --
 * {@link chione.task.Todo} with no date, {@link chione.task.Deadline} with a
 * date it is due by, and {@link chione.task.Event} with a range it runs over.
 *
 * <p>Each of them knows two things no other package has to know: how to render
 * itself for the user, and how to render itself for the save file. Adding a
 * fourth kind of task means writing those two methods and nothing else.
 *
 * <p>{@link chione.task.TaskList} holds them, and guards its own bounds so that
 * a task number that points at nothing is refused where the length is known.
 */
package chione.task;

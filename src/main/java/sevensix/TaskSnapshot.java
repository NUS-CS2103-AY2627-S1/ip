package sevensix;

import java.util.ArrayList;
import java.util.List;

/** Holds an isolated task-list state for undo and failed-command recovery. */
public final class TaskSnapshot {
    /** Internal copies are never exposed to callers. */
    private final List<Task> tasks;

    /** Captures task values independently of the live list.
     *
     * @param tasks the task state to capture.
     */
    public TaskSnapshot(Iterable<Task> tasks) {
        this.tasks = copyTasks(tasks);
    }

    /** Returns a fresh, mutable copy of the captured state.
     *
     * @return tasks that can be restored without modifying this snapshot.
     */
    public List<Task> restore() {
        return copyTasks(tasks);
    }

    /** Copies a collection without retaining any mutable task references.
     *
     * @param tasks the tasks to copy.
     * @return independent task copies in the same order.
     */
    private static List<Task> copyTasks(Iterable<Task> tasks) {
        List<Task> copies = new ArrayList<>();
        for (Task task : tasks) {
            copies.add(copyTask(task));
        }
        return copies;
    }

    /** Copies the task's schedule and completion state.
     *
     * @param task the task to copy.
     * @return an independent task with identical values.
     */
    public static Task copyTask(Task task) {
        Task copy = copyTaskDetails(task);
        if (task.isDone()) {
            copy.markAsDone();
        }
        return copy;
    }

    /** Copies task-specific values before completion status is restored.
     *
     * @param task the task whose values are needed.
     * @return an incomplete task of the same type.
     */
    private static Task copyTaskDetails(Task task) {
        if (task instanceof Deadline deadline) {
            return new Deadline(deadline.getDescription(), deadline.getBy(), deadline.getByTime());
        } else if (task instanceof Event event) {
            return new Event(event.getDescription(), event.getFrom(), event.getFromTime(),
                    event.getTo(), event.getToTime());
        } else {
            return new Todo(task.getDescription());
        }
    }
}

package sage.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Owns the collection of tasks used by Sage.
 *
 * <p>This class currently specializes {@link ArrayList} so existing task
 * operations remain simple while the collection gains a clear responsibility
 * boundary for future task-list behavior.</p>
 */
public class TaskList extends ArrayList<Task> {
    /** Creates an empty task list. */
    public TaskList() {
        super();
    }

    /**
     * Creates a task list containing the tasks loaded from storage.
     *
     * @param tasks tasks to copy into this list
     */
    public TaskList(Collection<Task> tasks) {
        super(tasks);
    }

    /** Returns whether this list already contains a task with identical details.
     *
     * @param candidate task to compare against this list
     * @return whether an equivalent task is already present
     */
    public boolean containsEquivalent(Task candidate) {
        for (Task task : this) {
            if (task.getClass() != candidate.getClass()
                    || !task.getDescription().equals(candidate.getDescription())) {
                continue;
            }
            if (task instanceof Deadline && ((Deadline) task).getBy().equals(((Deadline) candidate).getBy())) {
                return true;
            }
            if (task instanceof Event && ((Event) task).getFrom().equals(((Event) candidate).getFrom())
                    && ((Event) task).getTo().equals(((Event) candidate).getTo())) {
                return true;
            }
            if (task instanceof Todo) {
                return true;
            }
        }
        return false;
    }
}

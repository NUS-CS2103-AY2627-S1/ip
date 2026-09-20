package atom.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import atom.exception.AtomTaskNotFoundException;

/**
 * A class to store and operate on tasks in a list.
 */
public class TaskList {
    /** The list of tasks */
    private final ArrayList<Task> tasks;

    /**
     * Instantiates a <code>TaskList</code>.
     *
     * @param tasks The list of tasks to be instantiated with.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to be added.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a task from the list.
     *
     * @param idx The index of the task to be removed.
     * @return The removed task.
     * @throws AtomTaskNotFoundException If the index is out of bounds.
     */
    public Task remove(int idx) throws AtomTaskNotFoundException {
        checkTaskIndex(idx);
        return tasks.remove(idx);
    }

    /**
     * Marks a task in the list as done.
     *
     * @param idx The index of the task to be marked.
     * @throws AtomTaskNotFoundException If the index is out of bounds.
     */
    public void mark(int idx) throws AtomTaskNotFoundException {
        get(idx).mark();
    }

    /**
     * Marks a task in the list as undone.
     *
     * @param idx The index of the task to be unmarked.
     * @throws AtomTaskNotFoundException If the index is out of bounds.
     */
    public void unmark(int idx) throws AtomTaskNotFoundException {
        get(idx).unmark();
    }

    /**
     * Retrieves a task by index.
     *
     * @param idx The index of the task to be retrieved.
     * @return The retrieved task.
     * @throws AtomTaskNotFoundException If the index is out of bounds.
     */
    public Task get(int idx) throws AtomTaskNotFoundException {
        checkTaskIndex(idx);
        return tasks.get(idx);
    }

    /**
     * @return The size of the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Finds the all tasks which contains the
     * matching keyword in the description.
     *
     * @param keyword The keyword.
     * @return A <code>TaskList</code> containing all matching tasks.
     */
    public TaskList find(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getDescription().contains(keyword)) {
                matchingTasks.add(t);
            }
        }
        return new TaskList(matchingTasks);
    }

    /** Returns deadlines and events occurring on the given date, in time order. */
    public TaskList schedule(LocalDate date) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline deadline && deadline.getDeadline().toLocalDate().equals(date)) {
                matchingTasks.add(task);
            } else if (task instanceof Event event
                    && !date.isBefore(event.getStartTime().toLocalDate())
                    && !date.isAfter(event.getEndTime().toLocalDate())) {
                matchingTasks.add(task);
            }
        }
        matchingTasks.sort(Comparator.comparing(this::scheduledTime));
        return new TaskList(matchingTasks);
    }

    private LocalDateTime scheduledTime(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getDeadline();
        }
        return ((Event) task).getStartTime();
    }

    /**
     * Checks if the task at the specified index exists.
     *
     * @param idx The index of the task to be retrieved.
     * @throws AtomTaskNotFoundException If the index is out of bounds.
     */
    private void checkTaskIndex(int idx) throws AtomTaskNotFoundException {
        if (idx < 0 || idx >= size()) {
            throw new AtomTaskNotFoundException(idx + 1);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (Task s : tasks) {
            sb.append(i).append(". ").append(s).append("\n");
            i++;
        }
        // delete the last line break
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}

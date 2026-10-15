package jeremy.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jeremy.exception.JeremyException;

/**
 * Contains the task list and the operations to add, delete, and
 * mark/unmark tasks in it.
 */
public class TaskList {
    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    public void add(Task task) {
        assert task != null : "Cannot add a null task";
        tasks.add(task);
    }

    /** Returns whether a task with the same type and details is already stored. */
    public boolean containsEquivalent(Task candidate) {
        assert candidate != null : "Candidate task must not be null";
        for (Task task : tasks) {
            if (hasSameDetails(task, candidate)) {
                return true;
            }
        }
        return false;
    }

    public Task delete(int index) throws JeremyException {
        checkIndex(index, "delete");
        return tasks.remove(index - 1);
    }

    public Task markDone(int index) throws JeremyException {
        checkIndex(index, "mark");
        Task task = tasks.get(index - 1);
        task.markAsDone();
        return task;
    }

    public Task markNotDone(int index) throws JeremyException {
        checkIndex(index, "unmark");
        Task task = tasks.get(index - 1);
        task.markAsNotDone();
        return task;
    }

    public Task get(int index) throws JeremyException {
        checkIndex(index, "access");
        return tasks.get(index - 1);
    }

    public int size() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Returns a read-only view of the current tasks. */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    public List<Task> find(String keyword) {
        assert keyword != null : "Search keyword must not be null";
        List<Task> matchingTasks = new ArrayList<>();
        String lowerCaseKeyword = keyword.toLowerCase();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowerCaseKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    private void checkIndex(int index, String actionWord) throws JeremyException {
        if (tasks.isEmpty()) {
            throw new JeremyException("Your list is empty — nothing to " + actionWord + ".");
        }
        if (index < 1 || index > tasks.size()) {
            throw new JeremyException(
                    "That task number doesn't exist. You have " + tasks.size()
                            + " task(s) — pick between 1 and " + tasks.size() + ".");
        }
    }

    private boolean hasSameDetails(Task first, Task second) {
        if (!first.getClass().equals(second.getClass())
                || !first.getDescription().equals(second.getDescription())) {
            return false;
        }
        if (first instanceof Deadline && second instanceof Deadline) {
            return ((Deadline) first).getBy().equals(((Deadline) second).getBy());
        }
        if (first instanceof Event && second instanceof Event) {
            Event firstEvent = (Event) first;
            Event secondEvent = (Event) second;
            return firstEvent.getFrom().equals(secondEvent.getFrom())
                    && firstEvent.getTo().equals(secondEvent.getTo());
        }
        return true;
    }
}

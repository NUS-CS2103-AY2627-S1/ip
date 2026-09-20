package todd;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Owns the collection of tasks and the operations that change or search it. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list containing tasks loaded from storage. */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "initial task list must not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns a read-only snapshot for displaying or saving the current tasks. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /** Adds a task only when its type, description, and dates do not match an existing task. */
    public void add(Task task) throws TodException {
        assert task != null : "task to add must not be null";
        for (Task existing : tasks) {
            if (hasSameDetails(existing, task)) {
                String type = task instanceof Deadline ? "deadline" : task instanceof Event ? "event" : "task";
                throw new TodException("Wait, that " + type + " is already in the list.");
            }
        }
        tasks.add(task);
    }

    /** Compares stored task details exactly, ignoring whether either task is completed. */
    private boolean hasSameDetails(Task first, Task second) {
        if (first.getClass() != second.getClass() || !first.getDescription().equals(second.getDescription())) {
            return false;
        }
        if (first instanceof Deadline) {
            return ((Deadline) first).getBy().equals(((Deadline) second).getBy());
        }
        if (first instanceof Event) {
            Event firstEvent = (Event) first;
            Event secondEvent = (Event) second;
            return firstEvent.getFrom().equals(secondEvent.getFrom())
                    && firstEvent.getTo().equals(secondEvent.getTo());
        }
        return true;
    }

    /** Removes and returns the task at the specified zero-based index. */
    public Task delete(int index) {
        assertValidIndex(index);
        return tasks.remove(index);
    }

    /** Marks one task and prevents an already-completed task from being marked again. */
    public Task mark(int index) throws TodException {
        assertValidIndex(index);
        Task task = tasks.get(index);
        if (task.isDone()) {
            throw new TodException("Gang, task " + (index + 1) + " is already done. Take the win.");
        }
        task.markAsDone();
        return task;
    }

    /** Unmarks one task and prevents an incomplete task from being unmarked again. */
    public Task unmark(int index) throws TodException {
        assertValidIndex(index);
        Task task = tasks.get(index);
        if (!task.isDone()) {
            throw new TodException("Gang, I think task " + (index + 1) + " is already unmarked.");
        }
        task.markAsUndone();
        return task;
    }

    /**
     * Finds one-based task numbers for deadlines due and events taking place on a date.
     * Original numbers are retained so they can be used with mark, unmark, or delete.
     */
    public List<Integer> findTaskNumbersOn(LocalDate date) {
        assert date != null : "search date must not be null";
        ArrayList<Integer> taskNumbers = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (occursOn(tasks.get(i), date)) {
                taskNumbers.add(i + 1);
            }
        }
        return taskNumbers;
    }

    /** Returns original one-based task numbers whose descriptions contain the keyword. */
    public List<Integer> findTaskNumbers(String keyword) {
        assert keyword != null : "search keyword must not be null";
        ArrayList<Integer> taskNumbers = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().contains(keyword)) {
                taskNumbers.add(i + 1);
            }
        }
        return taskNumbers;
    }

    /**
     * Finds incomplete deadlines and events within numberOfDays calendar dates, counting startDate as day one.
     * Original one-based task numbers are returned for use with other commands.
     */
    public List<Integer> findUpcomingTaskNumbers(LocalDate startDate, int numberOfDays) {
        assert startDate != null : "reminder start date must not be null";
        assert numberOfDays > 0 : "reminder window must contain at least one day";

        LocalDate endDate = startDate.plusDays(numberOfDays - 1L);
        ArrayList<Integer> taskNumbers = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (!task.isDone() && occursBetween(task, startDate, endDate)) {
                taskNumbers.add(i + 1);
            }
        }
        return taskNumbers;
    }

    /** Verifies the caller supplied an index that refers to an existing task. */
    private void assertValidIndex(int index) {
        assert index >= 0 && index < tasks.size() : "task index must refer to an existing task";
    }

    /** Returns whether a deadline or event belongs in a date search result. */
    private boolean occursOn(Task task, LocalDate date) {
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return deadline.getBy().toLocalDate().equals(date);
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            LocalDate from = event.getFrom().toLocalDate();
            LocalDate to = event.getTo().toLocalDate();
            return !date.isBefore(from) && !date.isAfter(to);
        }
        return false;
    }

    /** Returns whether a deadline or event intersects an inclusive date range. */
    private boolean occursBetween(Task task, LocalDate startDate, LocalDate endDate) {
        if (task instanceof Deadline) {
            LocalDate dueDate = ((Deadline) task).getBy().toLocalDate();
            return !dueDate.isBefore(startDate) && !dueDate.isAfter(endDate);
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            LocalDate eventStart = event.getFrom().toLocalDate();
            LocalDate eventEnd = event.getTo().toLocalDate();
            return !eventEnd.isBefore(startDate) && !eventStart.isAfter(endDate);
        }
        return false;
    }
}

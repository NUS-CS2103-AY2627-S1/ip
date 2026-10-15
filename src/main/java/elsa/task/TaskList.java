package elsa.task;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The tasks the user is keeping, and the operations that change them.
 *
 * <p>The list itself is private, so nothing outside this class can add to it,
 * reorder it or empty it behind its back. Everything the chatbot does to the
 * list goes through one of the methods here, which means the rules that apply to
 * every change, such as how a task number maps to a position, are stated once.
 *
 * <p>An ArrayList grows as tasks are added, so there is no fixed capacity to
 * track separately: size() is always exactly how many tasks there are.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list, as on a first run or after a failed load. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding tasks that have already been read from the disk.
     *
     * @param tasks the tasks to start with, in the order they should appear
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns how many tasks are in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether there are no tasks at all.
     *
     * @return true if the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the task at a position, counted from 0.
     *
     * @param index the position of the task
     * @return the task at that position
     */
    public Task get(int index) {
        assert isValidIndex(index) : index + " is not a position in " + size() + " task(s)";
        return tasks.get(index);
    }

    /**
     * Returns whether the list already holds a task describing the same thing.
     *
     * @param task the task to look for
     * @return true if a task the same as this one is already in the list
     */
    public boolean hasTask(Task task) {
        return tasks.stream().anyMatch(task::isSameTask);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at a position and returns it, so that the caller can show
     * the user which task was taken out.
     *
     * @param index the position of the task to remove
     * @return the task that was removed
     */
    public Task delete(int index) {
        assert isValidIndex(index) : index + " is not a position in " + size() + " task(s)";
        return tasks.remove(index);
    }

    /**
     * Marks the task at a position as done.
     *
     * @param index the position of the task
     * @return the task that was marked, so the caller can show it
     */
    public Task mark(int index) {
        assert isValidIndex(index) : index + " is not a position in " + size() + " task(s)";
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at a position as not done after all.
     *
     * @param index the position of the task
     * @return the task that was unmarked, so the caller can show it
     */
    public Task unmark(int index) {
        assert isValidIndex(index) : index + " is not a position in " + size() + " task(s)";
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the unfinished deadlines due on or before a number of days after
     * a date, overdue ones included, soonest first.
     *
     * <p>Only deadlines count. A todo has no date to be reminded of, and an
     * event is something that happens rather than something owed, which is also
     * why an event is never called overdue. A deadline already done needs no
     * reminder however close its date is.
     *
     * <p>The date is handed in rather than read from the clock, so that what
     * counts as due today, tomorrow or next week can be tested on any day.
     *
     * <p>Reminders due on the same day stay in list order, so the order shown
     * does not depend on how the sort happens to break a tie.
     *
     * @param today     the date to count from
     * @param daysAhead how many days after today still count as due soon
     * @return the matching deadlines, sorted by how soon they are due
     */
    public List<Reminder> getReminders(LocalDate today, int daysAhead) {
        LocalDate lastDay = today.plusDays(daysAhead);
        // A loop rather than a stream, because a task has to be checked for being
        // a deadline before it can be asked for its date, and the position has
        // to be kept alongside it; a stream would carry both only awkwardly.
        List<Reminder> reminders = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i) instanceof Deadline deadline
                    && !deadline.isDone && !deadline.getBy().isAfter(lastDay)) {
                long daysUntilDue = ChronoUnit.DAYS.between(today, deadline.getBy());
                reminders.add(new Reminder(i, deadline, daysUntilDue));
            }
        }
        reminders.sort(Comparator.comparingLong(Reminder::daysUntilDue)
                .thenComparingInt(Reminder::index));
        return reminders;
    }

    /**
     * Returns whether a position names a task that is actually in the list.
     *
     * <p>Written as a method so that the four methods above can assert the same
     * thing without repeating it. The call sits inside the assert, so it is not
     * made at all when assertions are switched off.
     *
     * @param index the position to check, counting from 0
     * @return true if a task sits at that position
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}

package caesar.task;

import caesar.exception.CaesarException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Stores the tasks managed by Caesar and exposes task-list operations.
 *
 * <p>This class deliberately does not know about the user interface or file
 * format. It owns collection rules, such as the maximum number of tasks and
 * the one-based numbering used by commands.</p>
 */
public class TaskList implements Iterable<Task> {
    private static final int MAX_TASKS = 100;
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
    private static final String INVALID_TASK_NUMBER =
            "I couldn't locate that specific item number on our list. "
                    + "Take a quick look at /list/ to check the numbering.";

    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>(MAX_TASKS);
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param initialTasks tasks loaded from storage
     * @throws CaesarException if there are more than 100 tasks
     */
    public TaskList(Collection<Task> initialTasks) throws CaesarException {
        if (initialTasks.size() > MAX_TASKS) {
            throw new CaesarException("Task file contains more than " + MAX_TASKS + " tasks.");
        }
        tasks = new ArrayList<>(initialTasks);
    }

    /** Adds a task when the list has not reached its capacity. */
    public void add(Task task) throws CaesarException {
        if (tasks.size() >= MAX_TASKS) {
            throw new CaesarException("You have too many tasks undone. Please finish some first before adding more");
        }
        tasks.add(task);
        // The capacity check above must protect the list's maximum-size invariant.
        assert tasks.size() <= MAX_TASKS : "Task list must not exceed its maximum capacity";
    }

    /** Removes and returns a task using the one-based number shown to users. */
    public Task delete(int taskNumber) throws CaesarException {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /** Inserts a task at a one-based position, preserving the displayed order. */
    public void insert(int taskNumber, Task task) throws CaesarException {
        if (tasks.size() >= MAX_TASKS) {
            throw new CaesarException("You have too many tasks undone. Please finish some first before adding more");
        }
        if (taskNumber < 1 || taskNumber > tasks.size() + 1) {
            throw new CaesarException(INVALID_TASK_NUMBER);
        }
        tasks.add(taskNumber - 1, task);
        // Insertion must preserve the one-based position shown to users.
        assert tasks.get(taskNumber - 1) == task : "Inserted task must occupy the requested position";
    }

    /** Returns tasks whose descriptions contain the supplied text. */
    public TaskList find(String description) throws CaesarException {
        String searchTerm = description.trim().toLowerCase(Locale.ENGLISH);
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ENGLISH).contains(searchTerm))
                .toList();
        if (matchingTasks.isEmpty()) {
            throw new CaesarException("I couldn't find any tasks containing that description. "
                    + "Try a different keyword or check your spelling.");
        }
        return new TaskList(matchingTasks);
    }

    /** Returns a task using the one-based number shown to users. */
    public Task get(int taskNumber) throws CaesarException {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /** Marks a task as done using the one-based number shown to users. */
    public void mark(int taskNumber) throws CaesarException {
        Task task = get(taskNumber);
        task.markAsDone();
        // Returning normally means the selected task is now complete.
        assert task.isDone() : "A successfully marked task must be complete";
    }

    /** Marks a task as not done using the one-based number shown to users. */
    public void unmark(int taskNumber) throws CaesarException {
        Task task = get(taskNumber);
        task.markAsNotDone();
        // Returning normally means the selected task is now pending.
        assert !task.isDone() : "A successfully unmarked task must be pending";
    }

    /** Returns a copy sorted with pending tasks before completed tasks. */
    public ArrayList<Task> sortedByStatus() {
        ArrayList<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(Comparator.comparing(Task::isDone));
        // Every completed task must appear after all pending tasks.
        for (int i = 1; i < sortedTasks.size(); i++) {
            assert !sortedTasks.get(i - 1).isDone() || sortedTasks.get(i).isDone()
                    : "Status-sorted tasks must place pending tasks first";
        }
        return sortedTasks;
    }

    /** Returns pending dated tasks split into overdue and upcoming reminders. */
    public ReminderTasks getReminderTasks(LocalDate today) {
        LocalDate lastUpcomingDate = today.plusDays(3);
        ArrayList<Task> missedTasks = new ArrayList<>();
        ArrayList<Task> upcomingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.isDone()) {
                continue;
            }

            LocalDate relevantDate = getRelevantDate(task);
            if (relevantDate == null) {
                continue;
            }

            if (relevantDate.isBefore(today)) {
                missedTasks.add(task);
            } else if (!relevantDate.isAfter(lastUpcomingDate)) {
                upcomingTasks.add(task);
            }
        }
        return new ReminderTasks(missedTasks, upcomingTasks);
    }

    /** Removes pending dated tasks whose relevant date is before today. */
    public List<RemovedTask> clearMissedTasks(LocalDate today) {
        ArrayList<RemovedTask> removedTasks = new ArrayList<>();
        for (int taskIndex = tasks.size() - 1; taskIndex >= 0; taskIndex--) {
            Task task = tasks.get(taskIndex);
            LocalDate relevantDate = getRelevantDate(task);
            if (!task.isDone() && relevantDate != null && relevantDate.isBefore(today)) {
                removedTasks.add(new RemovedTask(taskIndex, tasks.remove(taskIndex)));
            }
        }
        removedTasks.sort(Comparator.comparingInt(RemovedTask::getPosition));
        return List.copyOf(removedTasks);
    }

    /** Restores tasks removed by {@link #clearMissedTasks(LocalDate)}. */
    public void restoreRemovedTasks(List<RemovedTask> removedTasks) {
        for (RemovedTask removedTask : removedTasks) {
            tasks.add(removedTask.getPosition(), removedTask.getTask());
        }
    }

    /** Returns the date used to classify a deadline or event reminder. */
    private LocalDate getRelevantDate(Task task) {
        String dateText;
        if (task instanceof Deadline deadline) {
            dateText = deadline.getBy();
        } else if (task instanceof Event event) {
            dateText = event.getEnd();
        } else {
            return null;
        }

        try {
            return LocalDate.parse(dateText, DISPLAY_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /** Stores the two groups shown by the reminder report. */
    public static final class ReminderTasks {
        private final List<Task> missedTasks;
        private final List<Task> upcomingTasks;

        private ReminderTasks(List<Task> missedTasks, List<Task> upcomingTasks) {
            this.missedTasks = List.copyOf(missedTasks);
            this.upcomingTasks = List.copyOf(upcomingTasks);
        }

        /** Returns pending dated tasks whose relevant date has passed. */
        public List<Task> getMissedTasks() {
            return missedTasks;
        }

        /** Returns pending dated tasks due today or within the next three days. */
        public List<Task> getUpcomingTasks() {
            return upcomingTasks;
        }
    }

    /** Stores a removed task and its original zero-based list position. */
    public static final class RemovedTask {
        private final int position;
        private final Task task;

        private RemovedTask(int position, Task task) {
            this.position = position;
            this.task = task;
        }

        /** Returns the original position of the removed task. */
        int getPosition() {
            return position;
        }

        /** Returns the removed task. */
        Task getTask() {
            return task;
        }
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether the list contains no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Returns whether every task in the list is complete. */
    public boolean areAllDone() {
        return tasks.stream().allMatch(Task::isDone);
    }

    /** Returns the task descriptions in the same list-style form as the backing collection. */
    @Override
    public String toString() {
        return tasks.toString();
    }

    /** Allows storage code to read tasks without owning the collection and supports for-loops over tasks. */
    @Override
    public Iterator<Task> iterator() {
        return List.copyOf(tasks).iterator();
    }

    /** Validates that a one-based task number refers to an existing task. */
    private void validateTaskNumber(int taskNumber) throws CaesarException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new CaesarException(INVALID_TASK_NUMBER);
        }
    }
}

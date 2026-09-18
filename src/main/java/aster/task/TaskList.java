package aster.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Holds the tasks Aster is keeping track of.
 *
 * <p>The list grows as needed, so no capacity is assumed and the number of tasks is
 * always {@link #size()} rather than a separately tracked count. It holds every task
 * type, since each subclass is also a {@link Task}.
 *
 * <p>Owning the list here means nothing outside can reorder or empty it by accident:
 * the only way in is through the operations below, and {@link #asList()} hands out a
 * view that cannot be changed.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks, in the order given.
     *
     * <p>The tasks are copied, so whatever produced them cannot change this list
     * afterwards.
     *
     * @param tasks the tasks to start with, such as those read from the saved file.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at the given position and returns it.
     *
     * <p>Removing closes the gap, which is what renumbers the remaining tasks the next
     * time the list is shown.
     *
     * @param index the 0-based position of the task to remove.
     * @return the task that was removed.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index the 0-based position of the task.
     * @return the task at that position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns how many tasks the list holds.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks in order, for showing or saving them.
     *
     * @return a view of the tasks that cannot be changed through it.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     *
     * <p>Only the description is searched, so the type marker, the done status and
     * any date shown beside a task cannot be matched. Letter case is ignored, and is
     * folded in English so that a computer configured for another language finds the
     * same tasks. The keyword stands for itself, so punctuation in it is matched
     * literally rather than read as a pattern.
     *
     * <p>The matches keep the order they are held in, each matching task appears
     * once, and searching leaves the list exactly as it was.
     *
     * @param keyword the text to look for in each description.
     * @return a view of the matching tasks that cannot be changed through it.
     */
    public List<Task> find(String keyword) {
        String wanted = keyword.trim().toLowerCase(Locale.ENGLISH);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ENGLISH).contains(wanted))
                .toList();
    }

    /**
     * Returns how many tasks the list holds, how many are done, and how many there are of
     * each type.
     *
     * <p>The tasks are counted in a single pass, and counting leaves the list exactly as it
     * was.
     *
     * @return the statistics of the list as it stands.
     */
    public TaskStatistics getStatistics() {
        int completed = 0;
        int todos = 0;
        int deadlines = 0;
        int events = 0;
        for (Task task : tasks) {
            if (task.isDone()) {
                completed++;
            }
            if (task instanceof Todo) {
                todos++;
            } else if (task instanceof Deadline) {
                deadlines++;
            } else if (task instanceof Event) {
                events++;
            }
        }
        return new TaskStatistics(tasks.size(), completed, todos, deadlines, events);
    }
}

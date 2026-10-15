package computa;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/** Stores and provides access to the user's tasks. */
public class TaskList {
    private final List<Todo> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(List<Todo> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("A task list cannot be initialized with null");
        }
        this.tasks = tasks;
    }
    /**
     * Adds a new task to the active task list.
     *
     * @param task The Task object (Todo, Deadline, or Event) to be added.
     */
    public void addTask(Todo task) {
        if (task == null) {
            throw new IllegalArgumentException("The task list cannot contain a null task");
        }
        tasks.add(task);
    }
    /**
     * Deletes a task from the list based on its index number.
     *
     * @param index The 0-based index of the task to be removed.
     * @return The Task object that was successfully removed.
     * @throws Computa.ComputaException If the provided index is out of bounds.
     */
    public Todo deleteTask(int index) {
        return tasks.remove(index);
    }

    public Todo getTask(int index) {
        Todo task = tasks.get(index);
        assert task != null : "A valid task index must refer to a task";
        return task;
    }

    public int getSize() {
        return tasks.size();
    }

    /** Returns the task at a user-visible index, excluding recurring rules. */
    public Todo getVisibleTask(int index) {
        int visibleIndex = 0;
        for (Todo task : tasks) {
            if (!(task instanceof RecurringTask recurring) || recurring.isInstance()) {
                if (visibleIndex++ == index) {
                    return task;
                }
            }
        }
        throw new IndexOutOfBoundsException();
    }

    /** Returns the number of tasks shown to the user. */
    public int getVisibleSize() {
        int count = 0;
        for (Todo task : tasks) {
            if (!(task instanceof RecurringTask recurring) || recurring.isInstance()) {
                count++;
            }
        }
        return count;
    }

    /** Returns the visible tasks for display and searching. */
    public List<Todo> getVisibleTasks() {
        List<Todo> visibleTasks = new ArrayList<>();
        for (Todo task : tasks) {
            if (!(task instanceof RecurringTask recurring) || recurring.isInstance()) {
                visibleTasks.add(task);
            }
        }
        return visibleTasks;
    }

    /** Creates today's instances for recurring rules that are scheduled today. */
    public boolean generateRecurringTasks(LocalDate date) {
        boolean generated = false;
        for (Todo task : new ArrayList<>(tasks)) {
            if (task instanceof RecurringTask recurring && !recurring.isInstance()
                    && recurring.isDueToday(date)) {
                LocalDate occurrence = date.with(TemporalAdjusters.previousOrSame(recurring.getDay()));
                tasks.removeIf(existing -> existing instanceof RecurringTask instance
                        && instance.isInstance()
                        && instance.getDescription().equals(recurring.getDescription())
                        && instance.getDay() == recurring.getDay()
                        && instance.getOccurrenceDate().isBefore(occurrence)
                        && instance.isDone);
                addTask(new RecurringTask(recurring.getDescription(), recurring.getDay(), occurrence, false));
                recurring.markGenerated(occurrence);
                generated = true;
            }
        }
        return generated;
    }
}

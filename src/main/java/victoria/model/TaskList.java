package victoria.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Locale;

/** Stores and updates the tasks belonging to the user. */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();
    private final int capacity;

    /** Creates an empty task list with the given capacity. */
    public TaskList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Task list capacity cannot be negative");
        }
        this.capacity = capacity;
    }

    /** Adds a task when the list has capacity. */
    public boolean add(String description) {
        return add(new Todo(description));
    }

    /** Adds a task object when the list has capacity. */
    public boolean add(Task task) {
        if (task == null) {
            return false;
        }
        if (task.getDescription() == null || task.getDescription().isBlank()) {
            return false;
        }
        if (tasks.size() >= capacity) {
            return false;
        }
        tasks.add(task);
        assert tasks.size() <= capacity : "Adding a task must not exceed the list capacity";
        return true;
    }

    /** Marks a one-based task number as done, or returns null if it is invalid. */
    public Task markDone(int taskNumber) {
        Task task = get(taskNumber);
        if (task != null) {
            task.markDone();
        }
        return task;
    }

    /** Marks a one-based task number as not done, or returns null if it is invalid. */
    public Task markNotDone(int taskNumber) {
        Task task = get(taskNumber);
        if (task != null) {
            task.markNotDone();
        }
        return task;
    }

    /** Returns a task by its one-based number, or null if the number is invalid. */
    public Task getTask(int taskNumber) {
        return get(taskNumber);
    }

    /** Removes and returns a task by its one-based number, or null if invalid. */
    public Task delete(int taskNumber) {
        int index = taskNumber - 1;
        return index >= 0 && index < tasks.size() ? tasks.remove(index) : null;
    }

    /** Returns the number of tasks currently stored. */
    public int size() {
        return tasks.size();
    }

    /** Prints all stored tasks using one-based numbering. */
    public void printTasks() {
        if (tasks.isEmpty()) {
            System.out.println(" Your list is clear—enjoy the breathing room!");
            return;
        }
        System.out.println(" Here is your game plan:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /** Prints deadlines and events occurring on the supplied date. */
    public void printTasksOn(LocalDate date) {
        int matchingTaskNumber = 0;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean occursOnDate = task instanceof Deadline deadline && deadline.getDate().equals(date)
                    || task instanceof Event event
                    && !date.isBefore(event.getStartDate()) && !date.isAfter(event.getEndDate());
            if (occursOnDate) {
                if (matchingTaskNumber == 0) {
                    System.out.println(" Here is what's happening on " + date + ":");
                }
                matchingTaskNumber++;
                System.out.println(" " + (i + 1) + "." + task);
            }
        }
        if (matchingTaskNumber == 0) {
            System.out.println(" Nothing is scheduled for " + date + ". A free day—lovely!");
        }
    }

    /** Prints tasks whose descriptions contain the supplied keyword, ignoring case. */
    public void printTasksContaining(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        int matchingTaskCount = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                if (matchingTaskCount == 0) {
                    System.out.println(" I found these matching tasks:");
                }
                matchingTaskCount++;
                System.out.println(" " + (i + 1) + "." + tasks.get(i));
            }
        }
        if (matchingTaskCount == 0) {
            System.out.println(" No matching tasks yet. Try another keyword!");
        }
    }

    private Task get(int taskNumber) {
        int index = taskNumber - 1;
        return index >= 0 && index < tasks.size() ? tasks.get(index) : null;
    }
}

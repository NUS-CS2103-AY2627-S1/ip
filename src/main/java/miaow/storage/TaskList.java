package miaow.storage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import miaow.task.Deadline;
import miaow.task.Event;
import miaow.task.Task;

/**
 * Tasklist
 */
public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    /**
     * Adds task to task list
     * @param task The task to be added
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Task cannot be null."
            );
        }

        boolean duplicate = tasks.stream()
                .anyMatch(existing ->
                        existing.getDescription()
                                .equalsIgnoreCase(task.getDescription()));

        if (duplicate) {
            throw new IllegalArgumentException(
                    "A task with the same description already exists."
            );
        }

        tasks.add(task);
    }

    /**
     * Deletes task at index.
     *
     * @param index The index of task to be deleted.
     * @return The deleted task.
     */
    public Task deleteTask(int index) {
        if (!isValidIndex(index)) {
            throw new IndexOutOfBoundsException("Invalid task index: " + index);
        }
        return tasks.remove(index);
    }

    /**
     * Mark a task as done.
     * @param index The index of the task to be marked.
     */
    public void markTask(int index) {
        if (!isValidIndex(index)) {
            throw new IndexOutOfBoundsException("Invalid task index: " + index);
        }
        tasks.get(index).mark();
    }

    /**
     * Mark a task as undone.
     * @param index The index of the task to be unmarked.
     */
    public void unmarkTask(int index) {
        if (!isValidIndex(index)) {
            throw new IndexOutOfBoundsException("Invalid task index: " + index);
        }
        tasks.get(index).unmark();
    }

    /**
     * Returns the task at index.
     * @param index The index
     * @return The task at index
     */
    public Task getTask(int index) {
        if (!isValidIndex(index)) {
            throw new IndexOutOfBoundsException("Invalid task index: " + index);
        }
        return tasks.get(index);
    }

    /**
     * Returns size of task list
     * @return an integer value of size
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks if the list is empty
     * @return Whether the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Return the whole task list
     * @return ArrayList of tasks
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    //public ArrayList<Task> findTasksByKeyword(String keyword) {}

    //public ArrayList<Deadline> getDeadlinesOnDate(LocalDate date) {}

    //public ArrayList<Event> getEventsOnDate(LocalDate date) {}

    /**
     * Checks if index is out-of-bounds.
     * @param index the input index.
     * @return A boolean value checking whether the index is within bounds.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();    }

    public void clear() {
        this.tasks.clear();
    }

    private static Task createTask(String command) {
        if (command.startsWith("todo")) {
            String description = command.substring(5).trim();
            if (description.isEmpty()) {
                return null;
            }
            return new Task(description);
        } else if (command.startsWith("deadline")) {
            String content = command.substring(9).trim();
            String[] parts = content.split(" /by ", 2);
            if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                return null;
            }
            Deadline deadline = new Deadline(parts[0].trim());
            deadline.by(parts[1].trim());
            return deadline;
        } else if (command.startsWith("event")) {
            String content = command.substring(6).trim();
            String[] parts = content.split(" /from | /to ", 3);
            if (parts.length < 3 || parts[0].trim().isEmpty()
                    || parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
                return null;
            }
            Event event = new Event(parts[0].trim());
            event.from(parts[1].trim());
            event.to(parts[2].trim());
            return event;
        }
        return null;
    }

    /**
     * finds the list of tasks using a keyword
     * @param keyword The keyword
     * @return a list of tasks
     */
    public ArrayList<Task> findTasksByKeyword(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return matchingTasks;
        }

        String searchTerm = keyword.trim().toLowerCase();

        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase()
                        .contains(searchTerm))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Sorts tasks according to time
     */
    public void sortChronologically() {
        tasks.sort(Comparator.comparing(
                TaskList::getTaskDate,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));
    }

    private static LocalDate getTaskDate(Task task) {
        if (task instanceof Deadline deadline) {
           // LocalDate date = deadline.getByDate();

            return deadline.getByDate();
        }

        if (task instanceof Event event) {
            return event.getFromDateTime();
        }

        return null;
    }

    /**
     * displays the elements in the list
     * @return string of elements
     */
    @Override
    public String toString() {
        return tasks.stream()
                .map(Task::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}

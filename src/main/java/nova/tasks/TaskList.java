package nova.tasks;

import java.util.ArrayList;

/**
 * Represents the list of tasks managed by Nova.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks.
     *
     * @param tasks tasks to store in the task list
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the task list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes the task at the specified index.
     *
     * @param index index of the task to delete
     * @return deleted task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index index of the task
     * @return task at the specified index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "Task index should be within task list bounds";

        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the task list.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns all tasks in the task list.
     *
     * @return list of tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Marks the task at the specified index as done.
     *
     * @param index index of the task
     */
    public void mark(int index) {
        tasks.get(index).markDone();
    }

    /**
     * Marks the task at the specified index as not done.
     *
     * @param index index of the task
     */
    public void unmark(int index) {
        tasks.get(index).unmarkUndone();
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword keyword to search for
     * @return task list containing matching tasks
     */
    public TaskList find(String keyword) {
        TaskList matchingList = new TaskList();
        String lowerCaseKeyword = keyword.toLowerCase();

        tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase()
                        .contains(lowerCaseKeyword))
                .forEach(matchingList::add);

        return matchingList;
    }

    /**
     * Finds an existing event that clashes with the given event.
     *
     * @param newEvent event to check for clashes
     * @return clashing event, or null if there is no clash
     */
    public Event findClashingEvent(Event newEvent) {
        for (Task task : tasks) {
            if (!(task instanceof Event)) {
                continue;
            }

            Event existingEvent = (Event) task;

            boolean isClashing =
                    existingEvent.getFrom().isBefore(newEvent.getTo())
                            && newEvent.getFrom().isBefore(existingEvent.getTo());

            if (isClashing) {
                return existingEvent;
            }
        }

        return null;
    }

    /**
     * Returns a formatted representation of all tasks.
     *
     * @return formatted task list
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < tasks.size(); i++) {
            result.append(i + 1)
                    .append(".")
                    .append(tasks.get(i))
                    .append(System.lineSeparator());
        }

        return result.toString();
    }
}

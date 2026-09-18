package joebiden.task;

import java.time.LocalDate;
import java.util.ArrayList;

import joebiden.exception.JoeBidenException;

/**
 * Manages the collection of tasks in the chatbot.
 */
public class TaskList {

    private final ArrayList<Task> list;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        list = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks.
     *
     * @param tasks Tasks to initialize the list with.
     */
    public TaskList(ArrayList<Task> tasks) {
        list = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void addTask(Task task) {
        assert task != null : "Task to add should not be null";
        list.add(task);
    }

    /**
     * Deletes the task with the given task number.
     *
     * @param number Task number to delete.
     * @return The removed task.
     * @throws JoeBidenException If the task number does not exist.
     */
    public Task deleteTask(int number) throws JoeBidenException {
        validateTaskNumber(number);
        return list.remove(number - 1);
    }

    /**
     * Returns the task with the given task number.
     *
     * @param number Task number to retrieve.
     * @return The requested task.
     * @throws JoeBidenException If the task number does not exist.
     */
    public Task getTask(int number) throws JoeBidenException {
        validateTaskNumber(number);

        assert number >= 1 && number <= list.size()
                : "Validated task number should be within list bounds";

        return list.get(number - 1);
    }

    /**
     * Marks the specified task as completed.
     *
     * @param number Task number to mark.
     * @return Message describing the updated task.
     * @throws JoeBidenException If the task number does not exist.
     */
    public String markTask(int number) throws JoeBidenException {
        Task task = getTask(number);
        return task.markDone();
    }

    /**
     * Marks the specified task as not completed.
     *
     * @param number Task number to unmark.
     * @return Message describing the updated task.
     * @throws JoeBidenException If the task number does not exist.
     */
    public String unmarkTask(int number) throws JoeBidenException {
        Task task = getTask(number);
        return task.unmark();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return list.size();
    }

    /**
     * Returns a formatted representation of all tasks in the list.
     *
     * @return Formatted task list.
     */
    public String listTasks() {
        StringBuilder output = new StringBuilder(
                "Here are the tasks in your list:\n"
        );

        for (int i = 0; i < list.size(); i++) {
            output.append(i + 1)
                    .append(". ")
                    .append(list.get(i))
                    .append("\n");
        }

        return output.toString();
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return List of tasks.
     */
    public ArrayList<Task> getList() {
        return list;
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword Keyword to search for in task descriptions.
     * @return Formatted list of matching tasks.
     */
    public String findTasks(String keyword) {
        StringBuilder output = new StringBuilder(
                "Here are the matching tasks in your list:\n"
        );

        String lowerKeyword = keyword.toLowerCase();
        int count = 1;

        for (Task task : list) {
            if (task.getName().toLowerCase().contains(lowerKeyword)) {
                output.append(count)
                        .append(". ")
                        .append(task)
                        .append("\n");
                count++;
            }
        }

        return output.toString();
    }

    /**
     * Gets unfinished tasks and events that are due tomorrow.
     *
     * @return Formatted reminders for tomorrow, or an empty string if none exist.
     */
    public String getTomorrowReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        StringBuilder reminders = new StringBuilder();

        for (Task task : list) {
            if (task.isDone()) {
                continue;
            }

            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;

                if (deadline.getBy().toLocalDate().equals(tomorrow)) {
                    reminders.append(task).append("\n");
                }
            } else if (task instanceof Event) {
                Event event = (Event) task;

                if (event.getFrom().toLocalDate().equals(tomorrow)) {
                    reminders.append(task).append("\n");
                }
            }
        }

        if (reminders.length() == 0) {
            return "";
        }

        return "Hey! You have these tasks tomorrow:\n"
                + reminders;
    }

    /**
     * Validates that the specified task number exists.
     *
     * @param number Task number to validate.
     * @throws JoeBidenException If the task number does not exist.
     */
    private void validateTaskNumber(int number) throws JoeBidenException {
        if (number < 1 || number > list.size()) {
            throw new JoeBidenException(
                    "That task number does not exist."
            );
        }
    }
}

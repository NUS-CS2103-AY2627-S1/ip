package botavius.tasklist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import botavius.exception.BotaviusException;

/** Stores, displays, and updates the tasks in a Botavius session. */
public class TaskList {
    /** Stores tasks entered during the current session, in insertion order. */
    private static ArrayList<Task> storedTasks = new ArrayList<Task>();

    /** Creates a task list and reconstructs tasks from serialized data.
     *
     * @param taskData serialized task data, with one task per line
     */
    public TaskList(String taskData) {
        storedTasks = new ArrayList<Task>();
        if (taskData == null || taskData.isBlank()) {
            return;
        }
        String[] taskStrings = taskData.split("\n");
        for (int i = 0; i < taskStrings.length; ++i) {
            String[] data;
            if (taskStrings[i].length() < 7) {
                throw new BotaviusException("invalid task record.");
            }
            boolean done = taskStrings[i].charAt(4) == 'X';
            Task loadedTask = null;
            switch (taskStrings[i].charAt(1)) {
                case 'T':
                    loadedTask = new ToDo(taskStrings[i].substring(7));
                    break;
                case 'E':
                    data = taskStrings[i].split("from:");
                    if (data.length != 2) {
                        throw new BotaviusException("event start time not provided.");
                    }
                    String description = data[0].substring(7).strip();
                    data = data[1].split("to:");
                    if (data.length != 2) {
                        throw new BotaviusException("event end time not provided.");
                    }
                    loadedTask = new Event(description, data[0].strip(), data[1].strip());
                    break;
                case 'D':
                    data = taskStrings[i].split("by:");
                    if (data.length != 2) {
                        throw new BotaviusException("deadline not provided.");
                    }
                    loadedTask = new Deadline(data[0].substring(7).strip(), data[1].strip());
                    break;
                case 'A':
                    data = taskStrings[i].split("after:");
                    if (data.length != 2) {
                        throw new BotaviusException("do-after time not provided.");
                    }
                    loadedTask = new DoAfter(data[0].substring(7).strip(), data[1].strip());
                    break;
            default:
                    throw new BotaviusException("invalid task type.");
            }
            // Every supported task type must produce an object before it is stored.
            assert loadedTask != null : "Supported task type must create a task";
            storedTasks.add(loadedTask);
            if (done) {
                markTask(new String[]{"", Integer.toString(storedTasks.size() - 1)});
            }
        }
    }

    /** Returns all tasks serialized for saving, one task per line.
     *
     * @return all serialized tasks
     */
    public static String getTaskStrings() {
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < storedTasks.size(); ++i) {
            returnString
                    .append(storedTasks.get(i).toStorageString())
                    .append("\n");
        }
        return returnString.toString();
    }

    /** Returns a read-only view of the tasks for presentation-layer clients.
     *
     * @return tasks in insertion order
     */
    public static List<Task> getTasks() {
        return Collections.unmodifiableList(storedTasks);
    }
    /** Returns a numbered display of the tasks currently stored.
     *
     * @return the formatted task-list display
     */
    public static String listTasks() {
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < storedTasks.size(); ++i) {
            returnString
                    .append(Integer.toString(i + 1))
                    .append(": ")
                    .append(storedTasks.get(i).toString())
                    .append("\n");
        }
        return "Here are the tasks in your list:\n"
                + returnString
                .toString()
                .strip();
    }

    /**
     * Returns the numbered tasks whose descriptions contain the search text.
     * Searching the description prevents completion and task-type markers
     * from affecting the result.
     *
     * @param searchText text to look for in each task description
     * @return a formatted list of matching tasks, or a message when none match
     */
    public static String find(String searchText) {
        StringBuilder matchingTasks = new StringBuilder();
        int index = 1;
        for (int i = 0; i < storedTasks.size(); ++i) {
            if (storedTasks.get(i).getDescription().contains(searchText)) {
                matchingTasks
                        .append(Integer.toString(index))
                        .append(": ")
                        .append(storedTasks.get(i).toString())
                        .append("\n");
                index++;
            }
        }
        if (matchingTasks.length() == 0) {
            return "There are no matching tasks.";
        }
        return "Here are the matching tasks in your list:\n"
                + matchingTasks.toString().strip();
    }

    /**
     * Marks the task identified by the command parameters as completed.
     *
     * @param parameters command words, with the task number at position 1
     * @return a confirmation message containing the updated task
     * @throws BotaviusException if the task number does not identify a task
     */
    public static String markTask(String[] parameters) {
        int taskIndex = Integer.parseInt(parameters[1]) - 1;
        if (taskIndex >= storedTasks.size() || taskIndex < 0) {
            throw new BotaviusException("Task index doesn't exist");
        }
        Task t = storedTasks.get(taskIndex);
        t.setDone(true);
        return "Nice! I've marked this task as done: "
                + t.toString();
    }

    /**
     * Marks the task identified by the command parameters as not completed.
     *
     * @param parameters command words, with the task number at position 1
     * @return a confirmation message containing the updated task
     * @throws BotaviusException if the task number does not identify a task
     */
    public static String unmarkTask(String[] parameters) {
        int taskIndex = Integer.parseInt(parameters[1]) - 1;
        if (taskIndex >= storedTasks.size() || taskIndex < 0) {
            throw new BotaviusException("Task index doesn't exist");
        }
        Task t = storedTasks.get(taskIndex);
        t.setDone(false);
        return "OK, I've marked this task as not done yet: "
                + t.toString();
    }

    /**
     * Creates and stores a deadline task from parsed command parameters.
     *
     * @param namedParameters command parameters containing {@code /task} and
     *                        {@code /by} values
     * @return a confirmation message describing the new task
     * @throws BotaviusException if required task information is missing
     */
    public static String deadline(Map<String, String> namedParameters) {
        Deadline newTask = new Deadline(
                namedParameters.get("/task").substring(8),
                namedParameters.get("/by"));
        storedTasks.add(newTask);
        return "Got it. I've added this task:\n"
                + newTask.toString()
                + "\nNow you have "
                + storedTasks.size()
                + " tasks in the list.";
    }

    /**
     * Creates and stores an event task from parsed command parameters.
     *
     * @param namedParameters command parameters containing {@code /task},
     *                        {@code /from}, and {@code /to} values
     * @return a confirmation message describing the new task
     * @throws BotaviusException if required task information is missing
     */
    public static String event(Map<String, String> namedParameters) {
        Event newTask = new Event(
                namedParameters.get("/task").substring(5),
                namedParameters.get("/from"),
                namedParameters.get("/to"));
        storedTasks.add(newTask);
        return "Got it. I've added this task:\n"
                + newTask.toString()
                + "\nNow you have "
                + storedTasks.size()
                + " tasks in the list.";
    }

    /** Creates and stores a do-after task from parsed command parameters.
     *
     * @param namedParameters command parameters containing {@code /task} and
     *                        {@code /after} values
     * @return a confirmation message describing the new task
     */
    public static String doAfter(Map<String, String> namedParameters) {
        DoAfter newTask = new DoAfter(
                namedParameters.get("/task").substring(8),
                namedParameters.get("/after"));
        storedTasks.add(newTask);
        return "Got it. I've added this task:\n"
                + newTask.toString()
                + "\nNow you have "
                + storedTasks.size()
                + " tasks in the list.";
    }

    /**
     * Creates and stores a to-do task from parsed command parameters.
     *
     * @param namedParameters command parameters containing a {@code /task}
     *                        value
     * @return a confirmation message describing the new task
     * @throws BotaviusException if the task description is missing
     */
    public static String todo(Map<String, String> namedParameters) {
        ToDo newTask = new ToDo(
                namedParameters.get("/task").substring(4));
        storedTasks.add(newTask);
        return "Got it. I've added this task:\n"
                + newTask.toString()
                + "\nNow you have "
                + storedTasks.size()
                + " tasks in the list.";
    }

    /**
     * Deletes the task identified by the command parameters.
     *
     * @param parameters command words, with the task number at position 1
     * @return a confirmation message containing the deleted task
     * @throws BotaviusException if the task number does not identify a task
     */
    public static String delete(String[] parameters) {
        int taskIndex = Integer.parseInt(parameters[1]) - 1;
        if (taskIndex >= storedTasks.size() || taskIndex < 0) {
            throw new BotaviusException("Task index doesn't exist");
        }
        String returnstring =
                "Noted. I've removed this task: "
                        + storedTasks.get(taskIndex).toString()
                        + "\nNow you have "
                        + storedTasks.size()
                        + " tasks in the list.";
        storedTasks.remove(taskIndex);
        return returnstring;
    }
}

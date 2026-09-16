package nova;

import java.io.IOException;
import java.time.LocalDateTime;

import nova.exception.NovaException;
import nova.parser.Parser;
import nova.storage.Storage;
import nova.tasks.Deadline;
import nova.tasks.Event;
import nova.tasks.Task;
import nova.tasks.TaskList;
import nova.tasks.ToDo;

/**
 * Main class for the NOVA chatbot.
 * Coordinates parsing, task management, and storage.
 */
public class Nova {

    private final Storage storage;
    private TaskList tasks;
    private boolean lastResponseWasError = false;
    private boolean lastResponseWasWarning = false;

    /**
     * Creates a NOVA chatbot and loads saved tasks from the given file.
     *
     * @param filePath path to the task storage file
     */
    public Nova(String filePath) {
        storage = new Storage(filePath);

        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            tasks = new TaskList();
        }
    }

    /**
     * Processes one user command and returns NOVA's response.
     *
     * @param input user input
     * @return NOVA's response
     */
    public String getResponse(String input) {
        String command = Parser.getCommandWord(input);
        lastResponseWasError = false;
        lastResponseWasWarning = false;

        try {
            switch (command) {
                case "bye":
                    return "Bye! Hope to see you again soon!";
                case "list":
                    return getListResponse();
                case "todo":
                    return addTodo(input);
                case "deadline":
                    return addDeadline(input);
                case "event":
                    return addEvent(input);
                case "mark":
                    return markTask(input);
                case "unmark":
                    return unmarkTask(input);
                case "delete":
                    return deleteTask(input);
                case "find":
                    return findTasks(input);
                default:
                    throw new NovaException(
                            "NOVA doesn't recognise that command.");
            }
        } catch (NovaException e) {
            lastResponseWasError = true;
            return e.getMessage();
        } catch (IOException e) {
            lastResponseWasError = true;
            return "NOVA couldn't save your tasks.";
        }
    }

    /**
     * Returns the current task list.
     *
     * @return task list response
     */
    private String getListResponse() {
        return "Here are the tasks in your list:\n" + tasks;
    }

    /**
     * Adds a todo task.
     *
     * @param input user input
     * @return response message
     * @throws NovaException if the input is invalid
     * @throws IOException if the task cannot be saved
     */
    private String addTodo(String input)
            throws NovaException, IOException {

        String description = Parser.parseTodoDescription(input);

        Task task = new ToDo(description);

        tasks.add(task);
        storage.save(tasks.getTasks());

        return "Got it. I've added this task:\n"
                + "  " + task
                + "\nNow you have "
                + tasks.size()
                + " tasks in the list.";
    }

    /**
     * Adds a deadline task.
     *
     * @param input user input
     * @return response message
     * @throws NovaException if the input is invalid
     * @throws IOException if the task cannot be saved
     */
    private String addDeadline(String input)
            throws NovaException, IOException {

        String[] deadlineParts = Parser.parseDeadlineDetails(input);

        String description = deadlineParts[0];

        LocalDateTime by = Parser.parseDateTime(deadlineParts[1]);

        Task task = new Deadline(description, by);

        tasks.add(task);
        storage.save(tasks.getTasks());

        return "Got it. I've added this deadline:\n"
                + "  " + task
                + "\nNow you have "
                + tasks.size()
                + " tasks in the list.";
    }

    /**
     * Adds an event task.
     *
     * @param input user input
     * @return response message
     * @throws NovaException if the input is invalid
     * @throws IOException if the task cannot be saved
     */
    private String addEvent(String input)
            throws NovaException, IOException {

        String[] eventParts = Parser.parseEventDetails(input);
        String description = eventParts[0];

        LocalDateTime from = Parser.parseDateTime(eventParts[1]);
        LocalDateTime to = Parser.parseDateTime(eventParts[2]);

        if (to.isBefore(from)) {
            throw new NovaException(
                    "Your event cannot end before it starts.");
        }

        Event event = new Event(description, from, to);
        Event clashingEvent = tasks.findClashingEvent(event);

        tasks.add(event);
        storage.save(tasks.getTasks());

        String response =
                "Got it. I've added this event:\n"
                        + "  " + event
                        + "\nNow you have "
                        + tasks.size()
                        + " tasks in the list.";

        if (clashingEvent != null) {
            lastResponseWasWarning = true;

            response += "\nWarning! This event clashes with:\n"
                    + "  " + clashingEvent;
        }

        return response;
    }

    /**
     * Marks a task as done.
     *
     * @param input user input
     * @return response message
     * @throws NovaException if the task number is invalid
     * @throws IOException if the task cannot be saved
     */
    private String markTask(String input)
            throws NovaException, IOException {

        int taskNumber = Parser.parseTaskNumber(input, 4);
        int index = taskNumber - 1;

        validateTaskIndex(index);

        tasks.mark(index);
        storage.save(tasks.getTasks());

        return "Nice! I've marked this task as done:\n"
                + "  " + tasks.get(index);
    }

    /**
     * Marks a task as not done.
     *
     * @param input user input
     * @return response message
     * @throws NovaException if the task number is invalid
     * @throws IOException if the task cannot be saved
     */
    private String unmarkTask(String input)
            throws NovaException, IOException {

        int taskNumber = Parser.parseTaskNumber(input, 6);
        int index = taskNumber - 1;

        validateTaskIndex(index);

        tasks.unmark(index);
        storage.save(tasks.getTasks());

        return "OK, I've marked this task as not done yet:\n"
                + "  " + tasks.get(index);
    }

    /**
     * Deletes a task.
     *
     * @param input user input
     * @return response message
     * @throws NovaException if the task number is invalid
     * @throws IOException if the task cannot be saved
     */
    private String deleteTask(String input)
            throws NovaException, IOException {

        int taskNumber = Parser.parseTaskNumber(input, 6);
        int index = taskNumber - 1;

        validateTaskIndex(index);

        Task removedTask = tasks.delete(index);

        storage.save(tasks.getTasks());

        return "Noted. I've removed this task:\n"
                + "  " + removedTask
                + "\nNow you have "
                + tasks.size()
                + " tasks in the list.";
    }

    /**
     * Finds tasks containing the given keyword.
     *
     * @param input user input
     * @return response message
     * @throws NovaException if the input is invalid
     */
    private String findTasks(String input)
            throws NovaException {

        String keyword = Parser.parseFindKeyword(input);

        TaskList matchingTasks = tasks.find(keyword);

        if (matchingTasks.size() == 0) {
            return "I couldn't find any tasks containing \""
                    + keyword
                    + "\".";
        }

        return "Here are the matching tasks in your list:\n"
                + matchingTasks;
    }

    /**
     * Checks whether the given task index exists.
     *
     * @param index task index
     * @throws NovaException if the index is invalid
     */
    private void validateTaskIndex(int index)
            throws NovaException {

        if (index < 0 || index >= tasks.size()) {
            throw new NovaException(
                    "That task number does not exist.");
        }
    }

    /**
     * Returns whether the previous response was an error.
     *
     * @return true if the previous response was an error
     */
    public boolean wasLastResponseError() {
        return lastResponseWasError;
    }

    /**
     * Returns whether the previous response contained a warning.
     *
     * @return true if the previous response contained a warning
     */
    public boolean wasLastResponseWarning() {
        return lastResponseWasWarning;
    }
}

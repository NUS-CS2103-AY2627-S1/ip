package jarvis.backend;

import java.util.List;

import jarvis.classes.Deadline;
import jarvis.classes.Event;
import jarvis.classes.Task;
import jarvis.classes.ToDo;
import jarvis.exceptions.InvalidTaskNumberException;
import jarvis.exceptions.JarvisException;
import jarvis.storage.Storage;

/**
 * Main controller for the Jarvis graphical user and CLI interface.
 */
public class JarvisController {

    private final TaskList taskList;
    private final Storage storage = new Storage("./data/jarvis.txt");

    /**
     * Creates a Jarvis controller and loads saved tasks.
     */
    public JarvisController() {
        taskList = new TaskList(Storage.loadTasks());
    }

    /**
     * Creates and stores a to-do task.
     *
     * @param userInput User command containing the task description.
     * @throws JarvisException If the command is incomplete.
     */
    public void createToDoTask(String userInput) throws JarvisException {
        ToDo toDoTask = Parser.parseToDo(userInput);
        taskList.addTask(toDoTask);
    }

    /**
     * Creates and stores a deadline task.
     *
     * @param userInput User command containing the task and deadline.
     * @throws JarvisException If the command is invalid.
     */
    public void createDeadlineTask(String userInput) throws JarvisException {
        Deadline deadlineTask = Parser.parseDeadline(userInput);
        taskList.addTask(deadlineTask);
    }

    /**
     * Creates and stores an event task.
     *
     * @param userInput User command containing the task and event times.
     * @throws JarvisException If the command is invalid.
     */
    public void createEventTask(String userInput) throws JarvisException {
        Event eventTask = Parser.parseEvent(userInput);
        taskList.addTask(eventTask);
    }

    /**
     * Marks a task as done or undone.
     *
     * @param userInput User command containing the task number.
     * @param status New completion status.
     * @throws JarvisException If the command or task number is invalid.
     */
    public void markTaskAs(
            String userInput,
            Task.CompletionStatus status) throws JarvisException {

        int taskNumber = Parser.parseTaskNumber(userInput);
        int taskIndex = taskNumber - 1;

        validateTaskNumber(taskNumber);

        taskList.setCompletionStatus(taskIndex, status);
    }

    /**
     * Deletes a task.
     *
     * @param userInput User command containing the task number.
     * @throws JarvisException If the command or task number is invalid.
     */
    public Task deleteTask(String userInput) throws JarvisException {
        int taskNumber = Parser.parseTaskNumber(userInput);
        int taskIndex = taskNumber - 1;

        validateTaskNumber(taskNumber);

        return taskList.deleteTask(taskIndex);
    }

    /**
     * Finds tasks containing a keyword.
     *
     * @param userInput User command containing the keyword.
     * @return Matching tasks.
     * @throws JarvisException If the find command is invalid.
     */
    public List<Task> filterTasks(String userInput)
            throws JarvisException {

        String taskKeyWord = Parser.parseTaskKeyWord(userInput);
        return taskList.filterTasks(taskKeyWord);
    }

    /**
     * Returns all tasks.
     *
     * @return Current tasks.
     */
    public List<Task> getTasks() {
        return taskList.getTasks();
    }

    /**
     * Returns a task by zero-based taskNumber.
     *
     * @param index Zero-based task taskNumber.
     * @return Task at the specified taskNumber.
     * @throws InvalidTaskNumberException If the number is invalid.
     */
    public Task getTask(int index) throws InvalidTaskNumberException {
        return taskList.getTask(index);
    }

    /**
     * Returns the number of tasks.
     *
     * @return Number of tasks.
     */
    public int size() {
        return taskList.size();
    }

    /**
     * Validates a one-based task number.
     *
     * @param taskNumber One-based task number.
     * @throws InvalidTaskNumberException If the number is invalid.
     */
    private void validateTaskNumber(int taskNumber)
            throws InvalidTaskNumberException {

        if (taskList.size() == 0) {
            throw new InvalidTaskNumberException(
                    "You have no tasks to mark, unmark, or delete.\n"
                            + "Please add a task first."
            );
        }

        if (taskNumber < 1 || taskNumber > taskList.size()) {
            throw new InvalidTaskNumberException(
                    "Error: The task number you inputted is invalid.\n"
                            + String.format(
                            "Please re-enter with a valid number ranging from "
                                    + "1 to %d",
                            taskList.size())
            );
        }
    }
}


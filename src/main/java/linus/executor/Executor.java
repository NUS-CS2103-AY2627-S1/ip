package linus.executor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import linus.invalidtaskexception.InvalidTaskException;
import linus.storage.Storage;
import linus.task.Deadline;
import linus.task.Event;
import linus.task.Task;
import linus.task.ToDo;
import linus.validator.Validator;

/**
 * Represents an executor that carries out specified actions on a tasklist.
 */
public class Executor {
    private final List<Task> taskList;
    private final Storage storage;

    /**
     * Creates a new Executor.
     *
     * @param taskList The tasklist to be actioned on by the executor.
     * @param storage Local hard drive storage location of the tasklist.
     */
    public Executor(List<Task> taskList, Storage storage) {
        this.taskList = taskList;
        this.storage = storage;
    }

    /**
     * Executes the action based on the command from user input.
     *
     * @param parsedInput Parsed user input from the parser.
     * @return Message that describes the status of the execution of the command.
     * @throws InvalidTaskException If addTask() does not recognise input, throw InvalidTaskException.
     */
    public String execute(List<String> parsedInput) throws InvalidTaskException, IOException {
        assert parsedInput != null : "Parsed input to execute is null";
        assert !parsedInput.isEmpty() : "No parsed input provided for execution";
        String command = parsedInput.getFirst();
        if (command.equals("list")) {
            return this.listAll();
        }
        String description = parsedInput.get(Validator.TASK_DESCRIPTION_INDEX);
        switch (command) {
            case "mark":
                assert parsedInput.size() == 2 : "Invalid format for mark command";
                return this.mark(description);
            case "unmark":
                assert parsedInput.size() == 2 : "Invalid format for unmark command";
                return this.unmark(description);
            case "delete":
                assert parsedInput.size() == 2 : "Invalid format for delete command";
                return this.delete(description);
            case "find":
                assert parsedInput.size() == 2 : "Invalid format for find command";
                return this.find(description);
            case "todo":
                // Fallthrough
            case "deadline":
                // Fallthrough
            case "event":
                return this.addTask(command, description, parsedInput);
            default:
                // Should not reach here
                return "Here's a tech tip! Enter the command with a valid format! :-(";
        }
    }

    /**
     * Lists all the tasks in the tasklist.
     *
     * @return String representation of all tasks in the tasklist.
     */
    private String listAll() {
        return IntStream.range(0, this.taskList.size())
                .mapToObj(i -> (i + 1) + ". " + this.taskList.get(i))
                .reduce("Here are the tasks in your list:", (result, task) -> result + "\n" + task);
    }

    /**
     * Marks the selected task as completed.
     *
     * @param index The task number as a string to be marked as completed.
     * @return Message to indicate successful marking of task.
     */
    private String mark(String index) throws IOException {
        assert this.taskList != null : "The tasklist is null and has not been initialised";
        Task task = this.getTask(index);

        assert task != null : "The task to be marked is null";
        boolean wasDone = task.isDone();
        task.mark();

        try {
            assert this.storage != null : "The storage is null and has not been initialised";
            this.storage.saveFile(this.taskList);
        } catch (IOException e) {
            if (!wasDone) {
                task.unmark();
            }
            throw e;
        }

        return "Yay! I've marked this task as done: \n" + task;
    }

    /**
     * Marks the selected task as not completed.
     *
     * @param index The task number as a string to be marked as incomplete.
     * @return Message to indicate successful unmarking of task.
     */
    private String unmark(String index) throws IOException {
        assert this.taskList != null : "The tasklist is null and has not been initialised";
        Task task = this.getTask(index);

        assert task != null : "The task to be unmarked is null";
        boolean wasDone = task.isDone();
        task.unmark();

        try {
            assert this.storage != null : "The storage is null and has not been initialised";
            this.storage.saveFile(this.taskList);
        } catch (IOException e) {
            if (wasDone) {
                task.mark();
            }
            throw e;
        }

        return "Fine, I've marked this task as not done yet: \n" + task;
    }

    /**
     * Deletes the selected task from the tasklist.
     *
     * @param index The task number as a string to be deleted from the task list.
     * @return Message to indicate successful deletion of task.
     */
    private String delete(String index) throws IOException {
        assert this.taskList != null : "The tasklist is null and has not been initialised";
        Task task = this.getTask(index);

        assert task != null : "The task to be deleted is null";
        int taskIndex = Integer.parseInt(index) - 1;
        this.taskList.remove(taskIndex);

        try {
            assert this.storage != null : "The storage is null and has not been initialised";
            this.storage.saveFile(this.taskList);
        } catch (IOException e) {
            this.taskList.add(taskIndex, task);
            throw e;
        }

        return "Noted. I've removed this task: \n" + task + "\nNow you have "
                + this.taskList.size() + " tasks in the list.";
    }

    /**
     * Searches the tasklist to find tasks with the specified keyword in the task description.
     *
     * @param keyword The string to search for in the task description of all tasks.
     * @return Message containing all the tasks whose description contains keyword.
     */
    private String find(String keyword) {
        String matchingTasks = IntStream.range(0, this.taskList.size())
                .filter(i -> this.taskList.get(i).getDescription().contains(keyword))
                .mapToObj(i -> (i + 1) + ". " + this.taskList.get(i))
                .reduce("", (result, task) -> result + "\n" + task);
        if (matchingTasks.isEmpty()) {
            return "Here's a tech tip! There are no matching tasks in your list.";
        }
        return "Here are the matching tasks in your list:" + matchingTasks;
    }

    /**
     * Adds the specified task to the TaskList.
     *
     * @param command The string that describes which type of task to add.
     * @param description The description of the task.
     * @param parsedInput The parsed plaintext input from the user.
     * @return Message to indicate successful addition of the task.
     * @throws InvalidTaskException If the command is not recognised. Should not reach this point.
     */
    private String addTask(String command, String description, List<String> parsedInput) throws InvalidTaskException,
            IOException {
        assert parsedInput != null : "Parsed input to execute is null";
        assert !parsedInput.isEmpty() : "No parsed input provided for execution";
        assert parsedInput.size() >= 2 : "Invalid format for todo/deadline/event add task command";

        Task task;
        switch (command) {
            case "todo":
                assert parsedInput.size() == 2 : "Invalid number of details provided for a todo command";
                task = new ToDo(false, description);
                break;
            case "deadline":
                assert parsedInput.size() == 3 : "Invalid number of details provided for a deadline command";
                LocalDate deadline = LocalDate.parse(parsedInput.getLast(), Validator.DATE_FORMAT);
                task = new Deadline(false, description, deadline);
                break;
            case "event":
                assert parsedInput.size() == 4 : "Invalid number of details provided for an event command";
                LocalDate startDate = LocalDate.parse(parsedInput.get(Validator.TASK_START_DATE_INDEX),
                        Validator.DATE_FORMAT);
                LocalDate endDate = LocalDate.parse(parsedInput.getLast(), Validator.DATE_FORMAT);
                task = new Event(false, description, startDate, endDate);
                break;
            default:
                // Should not be reached.
                throw new InvalidTaskException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }

        assert this.taskList != null : "The tasklist is null and has not been initialised";
        if (this.taskList.contains(task)) {
            return "Here's a tech tip! This task already exists in the tasklist.";
        }
        this.taskList.add(task);

        assert this.storage != null : "The storage is null and has not been initialised";
        this.storage.saveFile(this.taskList);

        return "Sure. I've added this task: \n" + task + "\n"
                + "Now you have " + this.taskList.size() + " tasks in the list.";
    }

    private Task getTask(String index) {
        int taskId = Integer.parseInt(index) - 1;
        return this.taskList.get(taskId);
    }
}

package aegis;

import java.io.IOException;

import aegis.task.Task;
import aegis.ui.TextUi;

/**
 * Performs the chatbot tasks depending on the command type.
 * Accepts multiple distinct user command types.
 */
public class Command {
    private final String commandName;
    private final Task task;
    private final int index;
    private final String keyword;

    /**
     * Constructs a Command object identified by its action string,
     * optional task, task index, and optional command.
     */

    public Command(String commandName, Task task, int index, String keyword) {

        assert commandName != null : "Command name should be assigned by Parser";

        this.commandName = commandName;
        this.task = task;
        this.index = index;
        this.keyword = keyword;
    }

    /**
     * Checks if the command is equal to "bye" to end the conversation.
     */
    public boolean hasEnded() {
        return commandName.equals("bye");
    }

    /**
     * Checks whether this command's task index points to an existing task.
     */
    private void checkIndexValidity(TaskList tasks) throws AegisException {
        if (index < 0 || index >= tasks.size()) {
            throw new AegisException("That task number is hiding outside the list.");
        }
    }

    /**
     * Adds this command's parsed task and saves the updated task list.
     */
    private String executeAdd(TaskList tasks, TextUi ui, Storage storage) throws AegisException, IOException {
        if (tasks.containsSameDetails(task)) {
            throw new AegisException("This task is already in your log. No clone army today.");
        }
        tasks.add(task);
        storage.saveToFile(tasks);
        return ui.getTaskAddedMessage(task, tasks.size());
    }

    /**
     * Deletes this command's indexed task and saves the updated task list.
     */
    private String executeDelete(TaskList tasks, TextUi ui, Storage storage) throws AegisException, IOException {
        checkIndexValidity(tasks);
        Task taskToDelete = tasks.get(index);
        tasks.delete(index);
        storage.saveToFile(tasks);
        return ui.getDeletedTaskMessage(taskToDelete, tasks.size());
    }

    /**
     * Marks this command's indexed task as done and saves the updated task list.
     */
    private String executeMark(TaskList tasks, TextUi ui, Storage storage) throws AegisException, IOException {
        checkIndexValidity(tasks);
        Task taskToMark = tasks.get(index);
        tasks.mark(index);
        storage.saveToFile(tasks);
        return ui.getTaskStatusMessage(taskToMark, true);
    }

    /**
     * Marks this command's indexed task as not done and saves the updated task list.
     */
    private String executeUnmark(TaskList tasks, TextUi ui, Storage storage) throws AegisException, IOException {
        checkIndexValidity(tasks);
        Task taskToUnmark = tasks.get(index);
        tasks.unmark(index);
        storage.saveToFile(tasks);
        return ui.getTaskStatusMessage(taskToUnmark, false);
    }


    /**
     * Executes the task based on the corresponding command.
     *
     * @param tasks TaskList class for the current chat session.
     * @param ui UI message builder.
     * @param storage Storage class.
     */
    public String execute(TaskList tasks, TextUi ui, Storage storage) throws AegisException, IOException {
        assert tasks != null : "Command execution requires a task list";
        assert ui != null : "Command execution requires a UI message builder";
        assert storage != null : "Command execution requires storage";
        return switch (commandName) {
            case "todo", "deadline", "event" -> executeAdd(tasks, ui, storage);
            case "delete" -> executeDelete(tasks, ui, storage);
            case "mark" -> executeMark(tasks, ui, storage);
            case "unmark" -> executeUnmark(tasks, ui, storage);
            case "list" -> ui.getTaskListMessage(tasks);
            case "bye" -> ui.getEndMessage();
            case "find" -> ui.getFindMessage(tasks, keyword);
            case "sort" -> ui.getSortedListMessage(tasks, storage);
            default -> ui.getDefaultMessage();

        };
    }

    public String getCommandName() {
        return commandName;
    }

    public Task getTask() {
        return task;
    }

}

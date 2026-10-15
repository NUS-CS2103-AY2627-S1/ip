package stephen.command;

import java.io.IOException;

import stephen.exception.ChatbotException;
import stephen.storage.Storage;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Represents one parsed user command that can be executed by the application. */
public abstract class Command {
    /** Creates a command. */
    protected Command() {
    }

    /**
     * Executes this command using the application's components.
     *
     * @param tasks task list on which the command operates
     * @param ui console UI used to display command results
     * @param storage persistence service used by commands that change tasks
     * @throws ChatbotException if the command cannot complete
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws ChatbotException;

    /**
     * Returns whether this command should end the application.
     *
     * @return {@code true} if the application should stop after this command
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the current tasks while converting file failures into application errors.
     *
     * @param tasks task list to persist
     * @param storage persistence service to use
     * @throws ChatbotException if the tasks cannot be saved
     */
    protected void saveTasks(TaskList tasks, Storage storage) throws ChatbotException {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            throw new ChatbotException("I couldn't save your tasks. Please check the data folder.");
        }
    }
}

package shan.command;

import shan.ResponseSink;
import shan.exception.ShanException;
import shan.storage.Storage;
import shan.task.TaskList;

/**
 * Represents a user command that can act on Shan's collaborators.
 */
public abstract class Command {
    /**
     * Constructs a command.
     */
    protected Command() {
    }

    /**
     * Executes this command.
     *
     * @param tasks   Task list on which the command operates.
     * @param storage Storage used to persist task changes.
     * @return Message produced by the command.
     * @throws ShanException If the command cannot be completed.
     */
    public abstract String execute(TaskList tasks, Storage storage) throws ShanException;

    /**
     * Executes this command and sends its result to a response sink.
     *
     * @param tasks        Task list on which the command operates.
     * @param responseSink Receives the message produced by the command.
     * @param storage      Storage used to persist task changes.
     * @throws ShanException If the command cannot be completed.
     */
    public void execute(TaskList tasks, ResponseSink responseSink, Storage storage) throws ShanException {
        responseSink.showMessage(execute(tasks, storage));
    }

    /**
     * Returns whether Shan should exit after executing this command.
     *
     * @return {@code true} when this command exits Shan.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns whether this command changes task-list state and can be undone.
     *
     * @return {@code true} when successful execution should create an undo point.
     */
    public boolean isUndoable() {
        return false;
    }
}

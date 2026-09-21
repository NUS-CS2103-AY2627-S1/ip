package answerme.command;

import answerme.exception.AnswerMeException;
import answerme.storage.Storage;
import answerme.task.TaskList;
import answerme.ui.Ui;

/**
 * Provides the base implementation for commands used by AnswerMe.
 */
public abstract class Command {
    /**
     * Creates a user command.
     */
    protected Command() {
    }

    /**
     * Executes the current command using the current task list, user interface
     * and storage.
     *
     * @param taskList The current list of tasks.
     * @param ui The user interface used to display results.
     * @param storage The storage manager used to save task changes.
     * @throws AnswerMeException If the command cannot be executed.
     */
    public abstract void execute(TaskList taskList, Ui ui, Storage storage)
            throws AnswerMeException;

    /**
     * Returns whether this command exits the application.
     *
     * @return {@code true} if this command exits the application, otherwise {@code false}.
     */
    public boolean isExit() {
        return false;
    }
}

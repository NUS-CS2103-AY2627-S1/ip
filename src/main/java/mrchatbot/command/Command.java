package mrchatbot.command;

import mrchatbot.exception.MrChatbotException;
import mrchatbot.storage.Storage;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /**
     * Executes this command using the current task list, UI, and storage.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MrChatbotException;

    /**
     * Returns true if this command should end the chatbot session.
     */
    public boolean isExit() {
        return false;
    }
}

package dukey.command;

import dukey.exception.DukeyException;
import dukey.storage.Storage;
import dukey.task.TaskList;
import dukey.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    public static final String STYLE_DEFAULT = "default-response-label";
    public static final String STYLE_ADD = "add-response-label";
    public static final String STYLE_DELETE = "delete-response-label";
    public static final String STYLE_ERROR = "error-response-label";
    public static final String STYLE_EXIT = "exit-response-label";
    public static final String STYLE_SEARCH = "search-response-label";
    public static final String STYLE_STATS = "stats-response-label";
    public static final String STYLE_STATUS = "status-response-label";

    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws DukeyException;

    /**
     * Returns the CSS class used for this command's GUI response bubble.
     *
     * @return CSS class representing this command's response type.
     */
    public String getResponseStyleClass() {
        return STYLE_DEFAULT;
    }

    public boolean isExit() {
        return false;
    }
}

package echo.command;

import java.util.List;

import echo.EchoException;
import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Represents a deadline task command.
 */
public class DeadlineCommand extends TaskCreationCommand {
    /**
     * Creates a deadline command.
     *
     * @param description deadline description.
     * @param dueDate validated deadline date.
     */
    public DeadlineCommand(String description, String dueDate) {
        super(Type.DEADLINE, List.of(description, dueDate));
    }

    /**
     * Creates and displays a deadline task.
     */
    @Override
    public String execute(TaskManager taskManager, Ui ui) throws EchoException {
        return showAdded(taskManager.addDeadline(getDescription(), getArgument(1)), taskManager, ui);
    }
}

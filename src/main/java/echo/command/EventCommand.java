package echo.command;

import java.util.List;

import echo.EchoException;
import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Represents an event task command.
 */
public class EventCommand extends TaskCreationCommand {
    /**
     * Creates an event command.
     *
     * @param description event description.
     * @param startDate validated event start date.
     * @param endDate validated event end date.
     */
    public EventCommand(String description, String startDate, String endDate) {
        super(Type.EVENT, List.of(description, startDate, endDate));
    }

    /**
     * Creates and displays an event task.
     */
    @Override
    public String execute(TaskManager taskManager, Ui ui) throws EchoException {
        return showAdded(taskManager.addEvent(getDescription(), getArgument(1), getArgument(2)),
                taskManager, ui);
    }
}

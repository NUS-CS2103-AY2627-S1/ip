package zen.command;

import java.time.LocalDate;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.TaskList;
import zen.ui.Ui;

/** Displays dated tasks that occur on the requested date. */
public class OccurCommand extends Command {
    private final String arguments;

    /**
     * Creates a command using the supplied date argument.
     *
     * @param arguments date whose tasks should be displayed
     */
    public OccurCommand(String arguments) {
        this.arguments = arguments;
    }

    /** Displays tasks that occur on the requested date. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException {
        LocalDate date = Parser.parseDate(arguments);
        ui.printTaskListOccurring(tasks.getAllTasksOnDate(date), date);
    }
}

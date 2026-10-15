package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.TaskList;
import zen.ui.Ui;

/** Adds an event task. */
public class EventCommand extends AddCommand {
    private final String arguments;

    /**
     * Creates a command using the supplied event details.
     *
     * @param arguments description, start, and end details to parse
     */
    public EventCommand(String arguments) {
        this.arguments = arguments;
    }

    /** Adds the event described by this command. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException {
        addTask(Parser.parseEvent(arguments), tasks, ui, storage);
    }
}

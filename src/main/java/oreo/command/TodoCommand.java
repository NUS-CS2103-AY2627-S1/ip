package oreo.command;

import oreo.core.OreoException;
import oreo.core.Parser;
import oreo.model.Task;
import oreo.model.Todo;

/** Command that adds a to-do task. */
public class TodoCommand extends AddTaskCommand {
    private final String description;

    /** Creates a to-do command. */
    public TodoCommand(String description, Parser parser) {
        super(parser);
        this.description = description;
    }

    /** Parses the command argument into a to-do task. */
    @Override
    protected Task createTask() throws OreoException {
        return new Todo(parser.todoDescription(description));
    }
}

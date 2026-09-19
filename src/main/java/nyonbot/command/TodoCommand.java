package nyonbot.command;

import java.util.HashMap;

import nyonbot.Logic.Result;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;
import nyonbot.model.ToDo;

/**
 * Creates a new Todo Task.
 */
public class TodoCommand extends Command {
    private TaskList list;

    /**
     * Creates a TodoCommand with parsed arguments and a TaskList.
     *
     * @param arguments parsed command arguments.
     * @param list TaskList to add to.
     */
    public TodoCommand(HashMap<String, String> arguments, TaskList list) {
        super(arguments);
        this.list = list;
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() throws NyonException {
        assert arguments != null;
        String description = arguments.get(DESCRIPTION_KEY);
        if (description == null || description.isBlank()) {
            throw new NyonException("cannot add a missing description");
        }
        Task todo = new ToDo(description);
        list.add(todo);
        return new Result(String.format(
                "I've added this task:\n%s\nThere are %s tasks in your list",
                todo, list.size()),
            false,
            true
        );
    }
}

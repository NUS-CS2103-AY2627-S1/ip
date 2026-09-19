package nyonbot.command;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import nyonbot.Logic.Result;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

/**
 * Lists all tasks in the tasklist.
 */
public class ListCommand extends Command {
    private TaskList list;

    /**
     * Creates a List command with parsed arguments and a TaskList to read.
     *
     * @param arguments parsed command arguments.
     * @param list      TaskList to read.
     */
    public ListCommand(HashMap<String, String> arguments, TaskList list) {
        super(arguments);
        this.list = list;
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() {
        String output = IntStream.range(0, list.size())
                .mapToObj(index -> formatTask(index, list.get(index)))
                .collect(Collectors.joining(System.lineSeparator()));
        if (output.isEmpty()) {
            output = "No tasks nyon...";
        } else {
            output = "\n" + output + "\n";
        }
        return new Result(output);
    }

    private String formatTask(int index, Task task) {
        return String.format("%d. %s", index + 1, task);
    }
}

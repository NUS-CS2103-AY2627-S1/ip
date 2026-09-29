package emma.command;

import java.util.function.Predicate;

import emma.Storage;
import emma.Task;
import emma.TaskList;

/**
 * Shows only the tasks that pass a test worked out while the command was read.
 */
public class FilterCommand implements Command {

    private final Predicate<Task> matches;

    /**
     * Creates the command.
     *
     * @param matches the test a task must pass to be shown.
     */
    public FilterCommand(Predicate<Task> matches) {
        this.matches = matches;
    }

    @Override
    public String execute(TaskList tasks, Storage storage) {
        String matched = tasks.format(matches);
        if (matched.isEmpty()) {
            return "Nothing matches that filter.";
        }
        return "Here's what matches:\n" + matched;
    }
}

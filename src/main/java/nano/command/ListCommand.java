package nano.command;

import nano.Storage;
import nano.task.TaskList;


/**
 * Lists all tasks currently stored by Nano.
 */
public class ListCommand implements Command {

    @Override
    public String execute(TaskList tasks, Storage storage) {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:\n");

        for (int i = 0; i < tasks.size(); i++) {
            response.append(i + 1)
                    .append(".")
                    .append(tasks.get(i))
                    .append("\n");
        }
        return response.toString();
    }
}

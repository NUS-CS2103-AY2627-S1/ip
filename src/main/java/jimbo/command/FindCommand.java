package jimbo.command;

import jimbo.JimboException;
import jimbo.Storage;
import jimbo.TaskList;
import jimbo.ui.Ui;
import jimbo.task.Task;

/**
 * Command for finding tasks that contain a specified text.
 */
public class FindCommand extends Command {
    String toFind;

    /**
     *
     * @param toFind The text to find.
     */
    public FindCommand(String toFind) {
        this.toFind = toFind;
    }

    @Override
    public String execute(Ui ui, TaskList tasks, Storage storage) throws JimboException {
        assert tasks != null : "tasks should not be null";

        int counter = 1;
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getData().contains(toFind)) {
                response.append(counter + ". " + task + '\n');
                counter++;
            }
        }
        if (counter == 1) {
            return "nothing found :(";
        }

        assert !response.isEmpty() : "Response should not be empty if tasks are found";
        return "i found these hidden somewhere:\n" + response;
    }
}

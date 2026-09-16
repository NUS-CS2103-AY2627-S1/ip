package serangooner.command;

import java.util.Arrays;
import java.util.List;

import serangooner.storage.Storage;
import serangooner.task.TaskList;
import serangooner.ui.Ui;

/**
 * Shows every command the user can enter.
 */
public class HelpCommand extends Command {
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        List<String> commands = Arrays.stream(CommandType.values())
                .map(command -> command.getSyntax() + " - " + command.getDescription())
                .toList();
        return ui.formatHelp(commands);
    }
}

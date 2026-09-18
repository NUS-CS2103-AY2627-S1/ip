package chud.command;

import java.util.List;
import java.util.stream.Collectors;

import chud.exception.ChudException;
import chud.parser.Parser;
import chud.storage.Storage;
import chud.task.Task;
import chud.task.TaskList;
import chud.ui.Ui;

/** Lists the tasks whose description contains a given (not yet validated) keyword. */
public class FindCommand extends Command {
    private final String arguments;

    public FindCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        String keyword = Parser.parseFindKeyword(arguments);
        List<Task> matches = tasks.stream()
                .filter(task -> task.matchesKeyword(keyword))
                .collect(Collectors.toList());

        ui.showMessage("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            ui.showMessage((i + 1) + "." + matches.get(i));
        }
    }
}

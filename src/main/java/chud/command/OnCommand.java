package chud.command;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import chud.exception.ChudException;
import chud.parser.Parser;
import chud.storage.Storage;
import chud.task.Task;
import chud.task.TaskDateTime;
import chud.task.TaskList;
import chud.ui.Ui;

/** Lists the deadlines/events occurring on a given (not yet validated) date/time text. */
public class OnCommand extends Command {
    private final String arguments;

    /** Creates a command that will list tasks occurring on the raw (not yet validated) date text. */
    public OnCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        TaskDateTime queryDateTime = Parser.parseOnDate(arguments);
        LocalDate date = queryDateTime.getDate();
        List<Task> matches = tasks.stream()
                .filter(task -> task.occursOn(date))
                .collect(Collectors.toList());

        ui.showMessage("Here are the tasks occurring on " + queryDateTime + ":");
        for (int i = 0; i < matches.size(); i++) {
            ui.showMessage((i + 1) + "." + matches.get(i));
        }
    }
}

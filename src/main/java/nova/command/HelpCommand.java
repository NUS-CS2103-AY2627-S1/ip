package nova.command;

import java.util.List;

import nova.Storage;
import nova.TaskList;
import nova.Ui;

/** Lists the commands the chatbot understands. */
public class HelpCommand extends Command {
    private static final List<String> ENTRIES = List.of(
            "todo DESC                      - add a task with no date",
            "deadline DESC /by DATE         - add a task due by a date",
            "event DESC /from DATE /to DATE - add a task spanning a period",
            "list                           - show every task",
            "mark N / unmark N              - set a task done or not done",
            "delete N                       - remove a task",
            "find KEYWORD                   - show tasks whose description matches",
            "on DATE                        - show tasks falling on a date",
            "sort                           - reorder the list, soonest first",
            "help                           - show this list",
            "bye                            - exit",
            "",
            "DATE is 2019-12-02 or 2/12/2019, optionally followed by a",
            "time such as 1800.");

    /** Creates a command that shows the list of commands. */
    public HelpCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp(ENTRIES);
    }
}

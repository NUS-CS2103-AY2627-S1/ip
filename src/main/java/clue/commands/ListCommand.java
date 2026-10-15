package clue.commands;

import java.util.Map;

import clue.components.Parser;
import clue.components.TaskList;
import clue.ui.Ui;

/** Command that lists out all tasks within a TaskList */
public class ListCommand implements Parser.Command {
    public static final String NAME = "list";
    private final TaskList tasks;

    /** Creates a ListCommand which lists the given TaskList */
    public ListCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean run(Ui ui, Map<String, String> args) {
        tasks.list(ui);
        return true;
    }
}

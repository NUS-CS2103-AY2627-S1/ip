package clue.commands;

import java.util.Map;

import clue.components.Parser;
import clue.components.TaskList;
import clue.ui.Ui;

/**
 * Command that searches tasks fuzzily for keywords.
 */
public class FindCommand implements Parser.Command {
    public static final String NAME = "find";
    private final TaskList tasks;

    /** Creates a FindCommand operating on the given TaskList */
    public FindCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean run(Ui ui, Map<String, String> args) {
        if (args.isEmpty()) {
            ui.println("Please provide a keyword to search for!");
            return true;
        }

        String keyword = args.get(Parser.DEFAULT_ARGUMENT).trim();
        if (keyword.isEmpty()) {
            ui.println("Please provide a valid keyword to search for!");
            return true;
        }

        tasks.find(ui, keyword);
        return true;
    }
}

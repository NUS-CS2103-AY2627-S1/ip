package clue.commands;

import java.util.Map;

import clue.components.Parser;
import clue.ui.Ui;

/** A command to exit the application */
public class ByeCommand implements Parser.Command {
    public static final String NAME = "bye";

    @Override
    public boolean run(Ui ui, Map<String, String> args) {
        ui.println("Bye. Hope to see you again soon!");
        return false;
    }
}

package clue;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import clue.commands.ByeCommand;
import clue.commands.CreateDeadlineCommand;
import clue.commands.CreateEventCommand;
import clue.commands.CreateTodoCommand;
import clue.commands.DeleteCommand;
import clue.commands.FindCommand;
import clue.commands.ListCommand;
import clue.commands.MarkCommand;
import clue.commands.UnmarkCommand;
import clue.components.Parser;
import clue.components.Storage;
import clue.components.TaskList;
import clue.tasks.Task;
import clue.ui.Gui;
import clue.ui.Ui;
import javafx.application.Application;

/** Main class of the program. Registers the commands and runs the chatbot. */
public class Clue {
    private static final Storage storage = new Storage("./data.txt");
    private static final TaskList tasks = storage.load(new TaskList());

    private static final Parser parser = new Parser();

    static void main(String[] args) {
        register();

        // GUI system
        try {
            Application.launch(Gui.class, args);
        } finally {
            if (!storage.save(tasks)) {
                System.out.println("An error occurred when trying to save!");
            }
        }
    }

    /** Returns the current Parser object. */
    public static Parser getParser() {
        return parser;
    }

    // ==================== Command Registration ====================

    static void register() {
        // Simple commands (no arguments required)
        parser.register(ByeCommand.NAME, new ByeCommand());
        parser.register(ListCommand.NAME, new ListCommand(tasks));

        // Single-argument commands with validation
        parser.register(MarkCommand.NAME, new MarkCommand(tasks));
        parser.register(UnmarkCommand.NAME, new UnmarkCommand(tasks));
        parser.register(DeleteCommand.NAME, new DeleteCommand(tasks));

        // Task creation commands
        parser.register(CreateTodoCommand.NAME, new CreateTodoCommand(tasks));
        parser.register(CreateDeadlineCommand.NAME, new CreateDeadlineCommand(tasks));
        parser.register(CreateEventCommand.NAME, new CreateEventCommand(tasks));

        // Find command
        parser.register(FindCommand.NAME, new FindCommand(tasks));
    }

    // ==================== Utility Methods ====================

    /** Parses a String into a LocalDateTime, giving feedback to the user */
    public static LocalDateTime parseDate(Ui ui, String unparsed) {
        if (unparsed == null || unparsed.trim().isEmpty()) {
            ui.println("Invalid date format! Please provide a valid date. Example: [2026-]12-31[ 12:30]");
            return null;
        }

        LocalDateTime out;
        try {
            out = LocalDateTime.parse(unparsed, Task.IN_FORMAT);
        } catch (DateTimeParseException e) {
            ui.println("Invalid date format! Example: [2026-]12-31[ 12:30]");
            return null;
        }
        return out;
    }
}

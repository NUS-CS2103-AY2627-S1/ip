package cbt;

import java.util.Optional;

import command.Command;
import command.CommandResult;
import exception.CbtException;
import parser.Parser;
import storage.Storage;
import task.TaskList;
import ui.Ui;

/** Starts CBT and coordinates the user interface, parser, and task list. */
public class Cbt {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final String startupWarning;

    /**
     * Creates the application with its user interface and persistent storage.
     *
     * @param ui User interface used to read commands and display results.
     * @param storage Storage used to load and save tasks.
     */
    public Cbt(Ui ui, Storage storage) {
        assert ui != null : "CBT requires a user interface";
        assert storage != null : "CBT requires persistent storage";
        this.ui = ui;
        this.storage = storage;
        this.tasks = storage.loadTasks();
        this.startupWarning = storage.getLoadWarning().orElse(null);
        assert tasks != null : "Storage must return a task list";
    }

    /**
     * Executes one command and returns the message that should be shown in the GUI.
     *
     * @param input command entered by the user.
     * @return result or error message produced by the command.
     */
    public String getResponse(String input) {
        return getResponseWithStatus(input).message();
    }

    /**
     * Executes one command and returns its message together with its error status.
     *
     * @param input command entered by the user.
     * @return response details used to style the GUI message.
     */
    public CbtResponse getResponseWithStatus(String input) {
        try {
            Command command = Parser.parseCommand(input);
            CommandResult result = command.execute(tasks);

            if (result.isTaskListChanged()) {
                storage.saveTasks(tasks);
            }

            return new CbtResponse(result.message(), false);
        } catch (CbtException exception) {
            return new CbtResponse(exception.getMessage(), true);
        }
    }

    /**
     * Returns a warning encountered while loading saved tasks.
     *
     * @return startup warning, or an empty value when loading completed normally.
     */
    public Optional<String> getStartupWarning() {
        return Optional.ofNullable(startupWarning);
    }

    /** Runs the application until an exit command is received. */
    public void run() {
        ui.showWelcome();
        if (startupWarning != null) {
            ui.showError(startupWarning);
            ui.showLine();
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showLine();

            try {
                Command command = Parser.parseCommand(input);
                CommandResult result = command.execute(tasks);

                ui.showResult(result);

                if (result.isTaskListChanged()) {
                    storage.saveTasks(tasks);
                }

                if (result.isExit()) {
                    ui.showLine();
                    break;
                }
            } catch (CbtException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showLine();
        }
    }

    /**
     * Starts CBT using the default console interface and data file.
     *
     * @param args Command-line arguments; unused.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("./data/CBT.txt");
        Cbt cbt = new Cbt(ui, storage);
        cbt.run();
    }
}

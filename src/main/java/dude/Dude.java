package dude;

import java.io.IOException;

import dude.command.core.Command;
import dude.command.core.CommandCreator;
import dude.command.core.CommandQueue;
import dude.command.core.CommandResult;
import dude.command.duplicate.DuplicateResolutionHandler;
import dude.exception.CommandExecutionException;
import dude.exception.UsageException;
import dude.parser.Parser;
import dude.storage.Storage;
import dude.task.TaskService;
import dude.ui.ConsoleDuplicateResolutionHandler;
import dude.ui.Ui;

/**
 * Coordinates DUDE's input, command, task, storage, and output components.
 */
public class Dude {
    private final Ui ui;
    private final TaskService receiver;
    private final CommandCreator commandCreator;
    private final CommandQueue commandQueue;

    /**
     * Creates an application using standard input and the default storage.
     */
    public Dude() {
        this(new Storage(), new Ui());
    }

    /**
     * Creates an application with supplied storage and user-interface collaborators.
     *
     * @param storage Storage used to load and save tasks.
     * @param ui      User-interface handler.
     */
    public Dude(Storage storage, Ui ui) {
        this.ui = ui;
        receiver = new TaskService(storage);
        commandQueue = new CommandQueue();
        DuplicateResolutionHandler resolutionHandler = new ConsoleDuplicateResolutionHandler(ui);
        commandCreator = new CommandCreator(receiver, resolutionHandler, commandQueue);
    }

    /**
     * Starts DUDE with its default collaborators.
     *
     * @param args Command-line arguments, which are not used.
     */
    static void main(String[] args) {
        new Dude().run();
    }

    /**
     * Starts the application and processes commands until exit or input exhaustion.
     */
    public void run() {
        ui.showWelcome();
        loadTasks();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                if (fullCommand == null) {
                    break;
                }
                ui.showLine();
                Command command = commandCreator.create(Parser.parse(fullCommand), ui);
                CommandResult result = commandQueue.execute(command);
                isExit = result.shouldExit();
            } catch (UsageException exception) {
                ui.showError(exception);
            } catch (CommandExecutionException exception) {
                ui.showSavingError();
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Loads persisted tasks, falling back to an empty receiver on failure.
     */
    private void loadTasks() {
        try {
            receiver.loadTasks();
        } catch (IOException exception) {
            ui.showLoadingError();
        }
    }
}

package megia;

import java.util.Optional;
import java.util.Properties;

import megia.exception.MegiaException;
import megia.exception.StorageException;
import megia.model.CommandResult;
import megia.model.TaskStorage;
import megia.service.CommandExecutor;
import megia.service.LocalStorageService;
import megia.service.LocalizationService;
import megia.service.PropertiesService;
import megia.service.TaskService;
import megia.ui.ConsoleUi;

/**
 * Coordinates the Megia command-line task manager.
 */
public final class Megia {
    private static final Properties PROPERTIES = PropertiesService.getProperties();

    private final ConsoleUi ui;
    private final LocalStorageService localStorageService;
    private final TaskStorage taskStorage;
    private final CommandExecutor commandExecutor;

    /**
     * Creates an application using the supplied UI, local storage, and task collection.
     *
     * @param ui Console interface used to interact with the user.
     * @param localStorageService Storage used to persist tasks.
     * @param taskStorage Task collection managed by the application.
     */
    public Megia(
            ConsoleUi ui,
            LocalStorageService localStorageService,
            TaskStorage taskStorage) {
        this.ui = ui;
        this.localStorageService = localStorageService;
        this.taskStorage = taskStorage;
        this.commandExecutor = new CommandExecutor(new TaskService(taskStorage, localStorageService));
    }

    /**
     * Starts the command-line application using the configured storage and system console.
     *
     * @param arguments Command-line arguments, which Megia does not currently use.
     */
    public static void main(String[] arguments) {
        LocalStorageService localStorageService = new LocalStorageService(
                PROPERTIES.getProperty("storage.task.path"));
        TaskStorage taskStorage;
        try {
            taskStorage = localStorageService.loadTaskData().orElse(new TaskStorage());
        } catch (StorageException exception) {
            System.err.println(LocalizationService.getException(
                    exception.getErrorCode(), exception.getMessageArguments()));
            return;
        }

        try (ConsoleUi ui = new ConsoleUi()) {
            Megia megia = new Megia(ui, localStorageService, taskStorage);
            megia.run();
        }
    }

    /**
     * Runs the application until the user exits or the input stream closes.
     */
    public void run() {
        boolean shouldExit = false;

        ui.showGreeting();

        while (!shouldExit) {
            try {
                Optional<String> rawInput = ui.readCommand();
                if (rawInput.isEmpty()) {
                    break;
                }

                CommandResult result = commandExecutor.execute(rawInput.get());
                if (result instanceof CommandResult.Exit) {
                    shouldExit = true;
                } else {
                    ui.showResult(result);
                }
            } catch (MegiaException exception) {
                ui.showError(exception);
            } catch (RuntimeException exception) {
                ui.showUnexpectedError(exception);
            }
        }

        ui.showFarewell();
        try {
            localStorageService.saveTaskData(taskStorage);
        } catch (StorageException exception) {
            ui.showError(exception);
        }
    }
}

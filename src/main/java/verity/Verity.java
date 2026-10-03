package verity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import verity.client.ClientList;
import verity.command.Command;
import verity.command.CommandContext;
import verity.exception.VerityException;
import verity.parser.Parser;
import verity.storage.ClientStorage;
import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Coordinates the chatbot's UI, storage, parser, and task list.
 */
public class Verity {
    private final Ui ui;
    private final Storage storage;
    private final ClientStorage clientStorage;
    private final Parser parser;

    private TaskList tasks;
    private ClientList clients;
    private boolean isInitialized;
    private boolean isExitRequested;
    private String initializationErrorMessage;
    private String commandType;

    /**
     * Creates a chatbot that stores its tasks at the specified path.
     *
     * @param dataFilePath Path of the task data file.
     */
    public Verity(Path dataFilePath) {
        this(dataFilePath, dataFilePath.resolveSibling("clients.txt"));
    }

    /**
     * Creates a chatbot that stores tasks and clients at specified paths.
     *
     * @param taskDataFilePath Path of the task data file.
     * @param clientDataFilePath Path of the client data file.
     */
    public Verity(Path taskDataFilePath, Path clientDataFilePath) {
        this.ui = new Ui();
        this.storage = new Storage(taskDataFilePath);
        this.clientStorage = new ClientStorage(clientDataFilePath);
        this.parser = new Parser();
        this.tasks = new TaskList();
        this.clients = new ClientList();
        this.isInitialized = false;
        this.isExitRequested = false;
        this.initializationErrorMessage = null;
        this.commandType = null;
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     */
    public void run() {
        System.out.println(ui.getGreeting());

        while (!isExitRequested) {
            String fullCommand;
            try {
                fullCommand = ui.readCommand();
            } catch (NoSuchElementException exception) {
                return;
            }
            System.out.println(getResponse(fullCommand));
        }
    }

    /**
     * Returns Verity's response to one user command.
     *
     * @param input User command to process.
     * @return Verity's response.
     */
    public String getResponse(String input) {
        commandType = null;
        isExitRequested = false;
        if (!initialize()) {
            return initializationErrorMessage;
        }

        Command command;
        try {
            command = parser.parse(input, tasks.size());
        } catch (VerityException exception) {
            return ui.getCommandErrorMessage(exception.getMessage());
        }

        List<String> taskSnapshot = tasks.getTasks().stream()
                .map(task -> task.serialize())
                .toList();
        List<String> clientSnapshot = getClientSnapshot();
        CommandContext commandContext =
                new CommandContext(tasks, clients, ui, storage, clientStorage);
        try {
            String response = command.execute(commandContext);
            commandType = command.getClass().getSimpleName();
            isExitRequested = command.isExit();
            return response;
        } catch (VerityException exception) {
            restoreState(taskSnapshot, clientSnapshot);
            return ui.getCommandErrorMessage(exception.getMessage());
        } catch (IOException exception) {
            restoreState(taskSnapshot, clientSnapshot);
            if (isClientCommand(command)) {
                return ui.getSavingChangesErrorMessage();
            }
            return ui.getSavingErrorMessage();
        }
    }

    /**
     * Returns the type of the most recently processed command.
     *
     * @return Most recent command type, or null if no command was processed.
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Returns whether the most recently processed command requested exit.
     *
     * @return True if the most recent command was an exit command.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Restores the in-memory task and client data after a command fails.
     *
     * @param taskSnapshot Serialized tasks from before command execution.
     * @param clientSnapshot Serialized clients from before command execution.
     */
    private void restoreState(List<String> taskSnapshot, List<String> clientSnapshot) {
        try {
            clients = parser.parseSavedClients(clientSnapshot);
            tasks = new TaskList(
                    parser.parseSavedTasks(taskSnapshot)
                            .toArray(Task[]::new));
            tasks.validateClientReferences(clients);
            assert tasks.size() == taskSnapshot.size()
                    : "Restored task count must match snapshot.";
        } catch (VerityException exception) {
            throw new IllegalStateException(
                    "Could not restore the task list.", exception);
        }
    }

    private List<String> getClientSnapshot() {
        List<String> clientSnapshot = new ArrayList<>();
        clientSnapshot.add("NEXT_ID\t" + clients.getNextId());
        clients.getClients().stream()
                .map(client -> client.serialize())
                .forEach(clientSnapshot::add);
        return clientSnapshot;
    }

    private boolean isClientCommand(Command command) {
        return command != null
                && command.getClass().getSimpleName().startsWith("Client");
    }

    /**
     * Loads saved tasks the first time Verity is used.
     *
     * @return True if initialization succeeded.
     */
    private boolean initialize() {
        if (isInitialized) {
            return true;
        }

        initializationErrorMessage = null;
        ClientList loadedClients;
        try {
            List<String> savedClientLines = clientStorage.loadClientLines();
            loadedClients = parser.parseSavedClients(savedClientLines);
        } catch (IOException exception) {
            initializationErrorMessage = ui.getClientLoadingErrorMessage();
            return false;
        } catch (VerityException exception) {
            initializationErrorMessage =
                    ui.getCorruptedClientDataErrorMessage(exception.getMessage());
            return false;
        }

        TaskList loadedTasks;
        try {
            List<String> savedTaskLines = storage.loadTaskLines();
            loadedTasks = new TaskList(
                    parser.parseSavedTasks(savedTaskLines)
                            .toArray(Task[]::new));
            loadedTasks.validateClientReferences(loadedClients);
        } catch (IOException exception) {
            initializationErrorMessage = ui.getLoadingErrorMessage();
            return false;
        } catch (VerityException exception) {
            initializationErrorMessage =
                    ui.getCorruptedDataErrorMessage(
                            exception.getMessage());
            return false;
        }

        clients = loadedClients;
        tasks = loadedTasks;
        isInitialized = true;
        return true;
    }

    /**
     * Starts Verity using the default data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Verity(Path.of("data", "verity.txt")).run();
    }
}

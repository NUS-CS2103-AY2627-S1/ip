package koara;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import koara.client.Client;
import koara.client.ClientList;
import koara.exception.KoaraException;
import koara.parser.Parser;
import koara.parser.Parser.ClientEdit;
import koara.storage.ClientStorage;
import koara.storage.Storage;
import koara.task.Task;
import koara.task.TaskList;
import koara.ui.Ui;

/**
 * Coordinates Koara's user interface, task list, parser, and storage.
 */
public class Koara {
    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String FIND_COMMAND = "find";
    private static final String CLIENT_LIST_COMMAND = "client list";
    private static final String CLIENT_ADD_COMMAND = "client add";
    private static final String CLIENT_EDIT_COMMAND = "client edit";
    private static final String CLIENT_FIND_COMMAND = "client find";
    private static final String CLIENT_DELETE_COMMAND = "client delete";
    private static final Path DATA_FILE_PATH = Path.of("data", "koara.txt");
    private static final String CLIENT_DATA_FILE_NAME = "clients.txt";

    private final Storage storage;
    private final ClientStorage clientStorage;
    private final TaskList tasks;
    private final ClientList clients;
    private final String startupError;

    /**
     * Creates Koara using its default data file.
     */
    public Koara() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates Koara using the specified data file.
     *
     * @param dataFilePath Path of the data file.
     */
    public Koara(Path dataFilePath) {
        assert dataFilePath != null : "Data file path must not be null";
        storage = new Storage(dataFilePath);
        clientStorage = new ClientStorage(dataFilePath.resolveSibling(CLIENT_DATA_FILE_NAME));
        TaskList loadedTasks;
        ClientList loadedClients;
        String loadError = null;
        try {
            loadedTasks = storage.load();
        } catch (KoaraException exception) {
            loadedTasks = new TaskList();
            loadError = exception.getMessage();
        }
        try {
            loadedClients = clientStorage.load();
        } catch (KoaraException exception) {
            loadedClients = new ClientList();
            loadError = combineErrors(loadError, exception.getMessage());
        }
        tasks = loadedTasks;
        clients = loadedClients;
        startupError = loadError;
    }

    /**
     * Starts an interactive session for managing tasks.
     */
    public void run() {
        try (Ui ui = new Ui()) {
            ui.showWelcome();
            if (startupError != null) {
                ui.showResponse(startupError);
                ui.showLine();
            }
            while (ui.hasNextCommand()) {
                String command = Parser.normalizeCommand(ui.readCommand());
                ui.showLine();
                ui.showResponse(getResponse(command));
                ui.showLine();
                if (command.equals(EXIT_COMMAND)) {
                    break;
                }
            }
        }
    }

    /**
     * Returns an initialization error that should be shown to the user, if any.
     *
     * @return Initialization error, or an empty value when loading succeeded.
     */
    public Optional<String> getStartupError() {
        return Optional.ofNullable(startupError);
    }

    /**
     * Executes a command and returns Koara's response.
     *
     * @param command Command entered by the user.
     * @return User-facing response to the command.
     */
    public String getResponse(String command) {
        return getCommandResult(command).message();
    }

    /**
     * Executes a command and returns its message together with its success status.
     *
     * @param command Command entered by the user.
     * @return Result containing the response text and whether it represents an error.
     */
    public CommandResult getCommandResult(String command) {
        assert command != null : "Command must not be null";
        String normalizedCommand = Parser.normalizeCommand(command);
        if (normalizedCommand.equals(EXIT_COMMAND)) {
            return new CommandResult(
                    "See ya later! Koara is always here for you—go get that W.", false);
        }

        try {
            return new CommandResult(executeCommand(normalizedCommand), false);
        } catch (KoaraException exception) {
            return new CommandResult(exception.getMessage(), true);
        }
    }

    /**
     * Describes the text and outcome of a processed command.
     *
     * @param message User-facing response text.
     * @param isError Whether the command failed validation or persistence.
     */
    public record CommandResult(String message, boolean isError) {
    }

    /**
     * Executes a non-exit command and persists any task-list changes.
     *
     * @param command Command entered by the user.
     * @return User-facing response to the command.
     * @throws KoaraException If the command is invalid or a change cannot be saved.
     */
    private String executeCommand(String command) throws KoaraException {
        assert command != null : "Command must not be null";
        assert !command.equals(EXIT_COMMAND) : "Exit command must be handled before execution";
        if (command.equals(LIST_COMMAND)) {
            return formatTaskList("Your game plan—steady lah:", tasks);
        }
        if (command.equals(CLIENT_LIST_COMMAND)) {
            return formatClientList("Your client lineup—steady lah:", clients);
        }
        if (Parser.matchesCommand(command, CLIENT_ADD_COMMAND)) {
            return addClient(command);
        }
        if (Parser.matchesCommand(command, CLIENT_EDIT_COMMAND)) {
            return editClient(command);
        }
        if (Parser.matchesCommand(command, CLIENT_FIND_COMMAND)) {
            return findClients(command);
        }
        if (Parser.matchesCommand(command, CLIENT_DELETE_COMMAND)) {
            return deleteClient(command);
        }
        if (Parser.matchesCommand(command, MARK_COMMAND)) {
            return markTask(command);
        }
        if (Parser.matchesCommand(command, UNMARK_COMMAND)) {
            return unmarkTask(command);
        }
        if (Parser.matchesCommand(command, DELETE_COMMAND)) {
            return deleteTask(command);
        }
        if (Parser.matchesCommand(command, FIND_COMMAND)) {
            return findTasks(command);
        }
        return addTask(command);
    }

    private String markTask(String command) throws KoaraException {
        int taskIndex = Parser.parseTaskIndex(command, MARK_COMMAND, tasks.getSize());
        boolean wasDone = tasks.get(taskIndex).isDone();
        tasks.mark(taskIndex);
        try {
            storage.save(tasks);
        } catch (KoaraException exception) {
            restoreTaskStatus(taskIndex, wasDone);
            throw exception;
        }
        return "Shiok! That's a W—task done:\n  " + tasks.get(taskIndex);
    }

    private String unmarkTask(String command) throws KoaraException {
        int taskIndex = Parser.parseTaskIndex(command, UNMARK_COMMAND, tasks.getSize());
        boolean wasDone = tasks.get(taskIndex).isDone();
        tasks.unmark(taskIndex);
        try {
            storage.save(tasks);
        } catch (KoaraException exception) {
            restoreTaskStatus(taskIndex, wasDone);
            throw exception;
        }
        return "No stress lah—this task is back on the radar:\n  " + tasks.get(taskIndex);
    }

    private String deleteTask(String command) throws KoaraException {
        int taskIndex = Parser.parseTaskIndex(command, DELETE_COMMAND, tasks.getSize());
        Task removedTask = tasks.delete(taskIndex);
        try {
            storage.save(tasks);
        } catch (KoaraException exception) {
            tasks.insert(taskIndex, removedTask);
            throw exception;
        }
        return "Clean slate energy—removed this task:\n  " + removedTask
                + "\nYou have " + formatTaskCount(tasks.getSize()) + " left. Keep cooking!";
    }

    private String findTasks(String command) throws KoaraException {
        String keyword = Parser.parseFindKeyword(command);
        TaskList matchingTasks = tasks.find(keyword);
        return formatTaskList("Found them—here's the matching lineup:", matchingTasks);
    }

    private String addTask(String command) throws KoaraException {
        Task task = Parser.parseTask(command);
        if (tasks.containsEquivalent(task)) {
            throw new KoaraException("That task is already on the board—no need to double-book it.");
        }
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (KoaraException exception) {
            tasks.delete(tasks.getSize() - 1);
            throw exception;
        }
        return "Ayo, locked in! Added this task:\n  " + task
                + "\nYou now have " + formatTaskCount(tasks.getSize()) + ". We move!";
    }

    private String addClient(String command) throws KoaraException {
        Client client = Parser.parseClient(command);
        if (clients.containsDuplicate(client, -1)) {
            throw new KoaraException("That client name or phone number is already in your lineup.");
        }
        clients.add(client);
        try {
            clientStorage.save(clients);
        } catch (KoaraException exception) {
            clients.delete(clients.getSize() - 1);
            throw exception;
        }
        return "Ayo, client locked in:\n  " + client
                + "\nYou now have " + formatClientCount(clients.getSize()) + ". Steady!";
    }

    private String editClient(String command) throws KoaraException {
        ClientEdit clientEdit = Parser.parseClientEdit(command, clients.getSize());
        if (clients.containsDuplicate(clientEdit.client(), clientEdit.clientIndex())) {
            throw new KoaraException("That client name or phone number already belongs to someone else.");
        }
        Client previousClient = clients.get(clientEdit.clientIndex());
        clients.update(clientEdit.clientIndex(), clientEdit.client());
        try {
            clientStorage.save(clients);
        } catch (KoaraException exception) {
            clients.update(clientEdit.clientIndex(), previousClient);
            throw exception;
        }
        return "Glow-up complete—updated this client:\n  " + clientEdit.client();
    }

    private String findClients(String command) throws KoaraException {
        String keyword = Parser.parseClientKeyword(command);
        ClientList matchingClients = clients.find(keyword);
        return formatClientList("Found them—here's the matching client lineup:", matchingClients);
    }

    private String deleteClient(String command) throws KoaraException {
        int clientIndex = Parser.parseClientIndex(command, CLIENT_DELETE_COMMAND, clients.getSize());
        Client removedClient = clients.delete(clientIndex);
        try {
            clientStorage.save(clients);
        } catch (KoaraException exception) {
            clients.insert(clientIndex, removedClient);
            throw exception;
        }
        return "Clean slate energy—removed this client:\n  " + removedClient
                + "\nYou now have " + formatClientCount(clients.getSize()) + ".";
    }

    /**
     * Formats a heading followed by a numbered list of tasks.
     *
     * @param heading Heading placed before the tasks.
     * @param taskList Tasks to format.
     * @return Formatted task-list response.
     */
    private static String formatTaskList(String heading, TaskList taskList) {
        assert heading != null : "Task list heading must not be null";
        assert taskList != null : "Task list must not be null";
        String formattedTasks = IntStream.range(0, taskList.getSize())
                .mapToObj(index -> "\n" + (index + 1) + "." + taskList.get(index))
                .collect(Collectors.joining());
        return heading + formattedTasks;
    }

    private static String formatClientList(String heading, ClientList clientList) {
        assert heading != null : "Client list heading must not be null";
        assert clientList != null : "Client list must not be null";
        String formattedClients = IntStream.range(0, clientList.getSize())
                .mapToObj(index -> "\n" + (index + 1) + ". " + clientList.get(index))
                .collect(Collectors.joining());
        return heading + formattedClients;
    }

    private static String combineErrors(String firstError, String secondError) {
        return firstError == null ? secondError : firstError + "\n" + secondError;
    }

    private static String formatClientCount(int clientCount) {
        return clientCount + (clientCount == 1 ? " client" : " clients");
    }

    private static String formatTaskCount(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }

    private void restoreTaskStatus(int taskIndex, boolean wasDone) {
        if (wasDone) {
            tasks.mark(taskIndex);
        } else {
            tasks.unmark(taskIndex);
        }
    }

    /**
     * Starts Koara using its default data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Koara(DATA_FILE_PATH).run();
    }
}

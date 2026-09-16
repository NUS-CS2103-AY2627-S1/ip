package kopi;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates the components of the Kopi chatbot.
 */
public class Kopi {
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String startupMessage;
    private final boolean storageLoaded;

    /** Creates a chatbot that stores its tasks at the given path. */
    public Kopi(Path dataPath) {
        parser = new Parser();
        storage = new Storage(dataPath);
        ui = new Ui();

        TaskList loadedTasks;
        String loadMessage = null;
        boolean loadedSuccessfully = false;
        try {
            loadedTasks = new TaskList(storage.load());
            loadedSuccessfully = true;
        } catch (KopiException e) {
            loadMessage = ui.getError(e.getMessage());
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
        startupMessage = loadMessage;
        storageLoaded = loadedSuccessfully;
    }

    /** Reads and executes commands until the user exits. */
    public void run() {
        ui.showWelcome();
        if (startupMessage != null) {
            ui.showResponse(startupMessage);
            ui.showLine();
        }
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            try {
                Command command = parser.parseCommand(input);
                ui.showResponse(execute(command, input));
                if (command == Command.BYE) {
                    ui.showLine();
                    return;
                }
            } catch (KopiException e) {
                ui.showResponse(ui.getError(e.getMessage()));
            }
            ui.showLine();
        }
        ui.showResponse(ui.getGoodbye());
        ui.showLine();
    }

    /** Executes one command and returns its user-facing response. */
    public String getResponse(String input) {
        try {
            return execute(parser.parseCommand(input), input);
        } catch (KopiException e) {
            return ui.getError(e.getMessage());
        }
    }

    /** Returns whether the input asks Kopi to exit. */
    public boolean isExitCommand(String input) {
        try {
            if (parser.parseCommand(input) != Command.BYE) {
                return false;
            }
            parser.validateNoArguments(input, "bye");
            return true;
        } catch (KopiException e) {
            return false;
        }
    }

    /** Returns any error raised while loading stored tasks. */
    public String getStartupMessage() {
        return startupMessage;
    }

    private String execute(Command command, String input) throws KopiException {
        switch (command) {
            case BYE:
                parser.validateNoArguments(input, "bye");
                return ui.getGoodbye();
            case LIST:
                parser.validateNoArguments(input, "list");
                return ui.getTaskList(tasks);
            case FIND:
                String keyword = parser.parseKeyword(input, "find");
                return ui.getMatches(tasks.find(keyword));
            case MARK:
                return markTask(input);
            case UNMARK:
                return unmarkTask(input);
            case DELETE:
                return deleteTask(input);
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                return addTask(input, command);
            default:
                throw new KopiException("I don't understand that command.");
        }
    }

    private String markTask(String input) throws KopiException {
        requireLoadedStorage();
        int index = parser.parseTaskNumber(input, "mark", tasks.size());
        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            storage.save(tasks.getAll());
        } catch (KopiException e) {
            if (!wasDone) {
                task.markAsNotDone();
            }
            throw e;
        }
        return ui.getMarked(task);
    }

    private String unmarkTask(String input) throws KopiException {
        requireLoadedStorage();
        int index = parser.parseTaskNumber(input, "unmark", tasks.size());
        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        try {
            storage.save(tasks.getAll());
        } catch (KopiException e) {
            if (wasDone) {
                task.markAsDone();
            }
            throw e;
        }
        return ui.getUnmarked(task);
    }

    private String deleteTask(String input) throws KopiException {
        requireLoadedStorage();
        int index = parser.parseTaskNumber(input, "delete", tasks.size());
        List<Task> updatedTasks = new ArrayList<>(tasks.getAll());
        Task removedTask = updatedTasks.remove(index);
        storage.save(updatedTasks);
        tasks.delete(index);
        return ui.getDeleted(removedTask, tasks.size());
    }

    private String addTask(String input, Command command) throws KopiException {
        requireLoadedStorage();
        Task task = parser.parseTask(input, command);
        if (tasks.hasDuplicateOf(task)) {
            throw new KopiException("That task is already in the list.");
        }
        List<Task> updatedTasks = new ArrayList<>(tasks.getAll());
        updatedTasks.add(task);
        storage.save(updatedTasks);
        tasks.add(task);
        return ui.getAdded(task);
    }

    private void requireLoadedStorage() throws KopiException {
        if (!storageLoaded) {
            throw new KopiException(
                    "I won't change tasks because the data file could not be loaded. "
                            + "Fix or remove it, then restart Kopi.");
        }
    }

    /** Starts Kopi using its default data file. */
    public static void main(String[] args) {
        new Kopi(Path.of("data", "kopi.txt")).run();
    }
}

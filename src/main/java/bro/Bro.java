package bro;

import bro.command.Command;
import bro.command.Parser;
import bro.exception.BroException;
import bro.storage.Storage;
import bro.task.Deadlines;
import bro.task.Events;
import bro.task.Task;
import bro.task.TaskList;
import bro.task.ToDos;
import bro.ui.Ui;

import java.util.Objects;

/** Coordinates Bro's user interface, command parser, task list, and storage. */
public class Bro {
    private static final String DEFAULT_FILE_PATH = "data/bro.txt";

    private final Storage storage;
    private final Ui ui;
    private final Parser parser;
    private TaskList tasks;
    private boolean isLoaded;

    /** Creates Bro for the graphical interface using the default storage path. */
    public Bro() {
        this(new Storage(DEFAULT_FILE_PATH), Ui.silent(), new Parser());
    }

    /** Creates Bro with storage at the supplied file path and the process console as its UI. */
    public Bro(String filePath) {
        this(new Storage(filePath), new Ui(), new Parser());
    }

    /** Creates Bro with the supplied collaborators. */
    Bro(Storage storage, Ui ui, Parser parser) {
        this.storage = Objects.requireNonNull(storage);
        this.ui = Objects.requireNonNull(ui);
        this.parser = Objects.requireNonNull(parser);
        this.tasks = new TaskList();
        this.isLoaded = false;
    }

    /** Runs Bro until the user signs off or closes the input. */
    public void run() {
        ui.showWelcome();
        loadTasks();

        String userInput;
        while ((userInput = ui.readCommand()) != null) {
            getResponse(userInput);
            if (isExitCommand(userInput)) {
                break;
            }
        }
        if (userInput == null) {
            ui.showGoodbye();
        }
    }

    /** Returns Bro's greeting and reports any recoverable storage-loading problem. */
    public String getWelcomeMessage() {
        String loadingError = loadTasks();
        return loadingError == null
                ? ui.getWelcomeMessage()
                : ui.getWelcomeMessage() + "\n" + loadingError;
    }

    /**
     * Processes one user message and returns the response shown by the console or GUI.
     *
     * @param userInput Raw command entered by the user.
     * @return Formatted response generated after processing the command.
     */
    public String getResponse(String userInput) {
        String loadingError = loadTasks();
        if (loadingError != null) {
            return loadingError;
        }
        if (isExitCommand(userInput)) {
            return ui.showGoodbye();
        }

        try {
            return handleCommand(parser.parseCommand(userInput));
        } catch (BroException exception) {
            return ui.showError(exception.getMessage());
        }
    }

    /** Returns whether the input is Bro's exit command. */
    public boolean isExitCommand(String userInput) {
        return userInput != null && userInput.trim().equalsIgnoreCase("bye");
    }

    /** Starts Bro with its default relative storage path. */
    public static void main(String[] args) {
        new Bro(DEFAULT_FILE_PATH).run();
    }

    /** Dispatches one parsed command, throwing a BroException for invalid input. */
    private String handleCommand(Command command) throws BroException {
        assert command != null : "A parsed command is required for dispatch";
        assert tasks != null : "The task list must be available before dispatch";

        String action = command.getAction();
        assert !action.isBlank() : "A parsed command must have an action";
        switch (action) {
        case "list":
            parser.ensureNoArguments(command, "list");
            return ui.showTaskList(tasks);
        case "find":
            return findTasks(command);
        case "mark":
            return changeTaskStatus(command, true);
        case "unmark":
            return changeTaskStatus(command, false);
        case "delete":
            return deleteTask(command);
        case "todo":
            return addTodo(command);
        case "deadline":
            return addDeadline(command);
        case "event":
            return addEvent(command);
        default:
            throw new BroException("I don't recognize that command. Try todo, deadline, event, "
                    + "list, find, mark, unmark, delete, or bye.");
        }
    }

    /** Finds tasks whose descriptions contain the requested keyword. */
    private String findTasks(Command command) throws BroException {
        String keyword = parser.requireArgument(command, "Use: find <keyword>.");
        return ui.showFindResults(tasks.findTasks(keyword));
    }

    /** Adds a todo after checking that its description is present. */
    private String addTodo(Command command) throws BroException {
        String description = parser.requireArgument(command, "I need a todo description. "
                + "Try: todo <description>.");
        ToDos newToDo = new ToDos(description);
        tasks.add(newToDo);
        storage.save(tasks);
        return ui.showTaskAdded(newToDo, tasks.size());
    }

    /** Adds a deadline after validating its description and /by component. */
    private String addDeadline(Command command) throws BroException {
        Deadlines newDeadline = parser.parseDeadline(command);
        tasks.add(newDeadline);
        storage.save(tasks);
        return ui.showTaskAdded(newDeadline, tasks.size());
    }

    /** Adds an event after validating its description, start, and end components. */
    private String addEvent(Command command) throws BroException {
        Events newEvent = parser.parseEvent(command);
        tasks.add(newEvent);
        storage.save(tasks);
        return ui.showTaskAdded(newEvent, tasks.size());
    }

    /** Marks or unmarks the requested task after validating its index. */
    private String changeTaskStatus(Command command, boolean isDone) throws BroException {
        String operation = isDone ? "mark" : "unmark";
        int index = parser.parseTaskIndex(command, operation, tasks.size());
        assert index >= 1 && index <= tasks.size() : "The parser must return a valid task number";

        Task chosenTask = tasks.getTask(index - 1);
        chosenTask.setDone(isDone);
        storage.save(tasks);
        return ui.showTaskStatusChanged(isDone, chosenTask);
    }

    /** Removes the requested task and reports the remaining list size. */
    private String deleteTask(Command command) throws BroException {
        int index = parser.parseTaskIndex(command, "delete", tasks.size());
        assert index >= 1 && index <= tasks.size() : "The parser must return a valid task number";

        Task removedTask = tasks.removeTask(index - 1);
        storage.save(tasks);
        return ui.showTaskDeleted(removedTask, tasks.size());
    }

    /** Loads stored tasks once, returning a formatted recoverable error when loading fails. */
    private String loadTasks() {
        if (isLoaded) {
            return null;
        }

        isLoaded = true;
        try {
            tasks = storage.load();
            assert tasks != null : "A successful storage load must return a task list";
            return null;
        } catch (BroException exception) {
            tasks = new TaskList();
            return ui.showError(exception.getMessage());
        }
    }
}

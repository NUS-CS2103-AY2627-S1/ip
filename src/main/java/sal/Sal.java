package sal;

import java.io.IOException;

/**
 * Entry point and orchestrator for the Sal chatbot.
 * Wires together {@link Ui}, {@link Storage}, {@link Parser}, and {@link TaskList}
 * so each class has a single responsibility.
 */
public class Sal {
    private static final String DATA_FILE_PATH = "data/sal.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private boolean isExit;

    /**
     * Creates Sal using the default data file path.
     * JavaFX needs this no-argument constructor to create the application.
     */
    public Sal() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates Sal with UI, storage, and a task list loaded from disk when possible.
     *
     * @param filePath Relative path to the task data file.
     */
    public Sal(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = loadTasks();
    }

    /**
     * Starts the interactive command loop until the user says bye.
     */
    public void run() {
        ui.showWelcome();
        isExit = false;
        while (!isExit) {
            String input = ui.readCommand();
            try {
                ui.showMessage(handleCommand(input));
            } catch (SalException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.close();
    }

    /**
     * Returns the greeting shown when the GUI opens.
     *
     * @return Welcome text without the ASCII banner.
     */
    public String getWelcomeMessage() {
        return ui.formatWelcome();
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input Raw user input from the GUI.
     * @return Reply to display in a dialog box.
     */
    public String getResponse(String input) {
        return getChatResponse(input).getMessage();
    }

    /**
     * Generates a GUI reply, including whether the command failed.
     *
     * @param input Raw user input from the GUI.
     * @return Reply text and an error flag for bubble styling.
     */
    public ChatResponse getChatResponse(String input) {
        try {
            return new ChatResponse(handleCommand(input.trim()), false);
        } catch (SalException e) {
            return new ChatResponse(e.getMessage(), true);
        }
    }

    /**
     * Returns whether the user has issued the bye command.
     *
     * @return {@code true} if the session should end.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Loads tasks from disk, or starts empty if loading fails.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError();
            return new TaskList();
        }
    }

    /**
     * Persists the current task list to disk.
     */
    private void saveTasks() throws SalException {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            throw new SalException("Could not save tasks to disk.");
        }
    }

    private String handleCommand(String input) throws SalException {
        String command = Parser.getCommandWord(input);
        switch (command) {
            case "bye":
                isExit = true;
                return ui.formatGoodbye();
            case "list":
                return ui.formatTaskList(tasks);
            case "mark":
                return markTask(input);
            case "unmark":
                return unmarkTask(input);
            case "todo":
                return addTask(Parser.parseTodo(input));
            case "deadline":
                return addTask(Parser.parseDeadline(input));
            case "event":
                return addTask(Parser.parseEvent(input));
            case "delete":
                return deleteTask(input);
            case "find":
                return ui.formatFoundTasks(tasks.find(Parser.parseFind(input)));
            case "tag":
                return tagTask(input);
            default:
                throw new SalException("Command not recognised.");
        }
    }

    private String markTask(String input) throws SalException {
        int index = Parser.parseTaskIndex(input, "mark", "Correct format: mark <task number>");
        Task task = tasks.markAsDone(index);
        saveTasks();
        return ui.formatMarked(task);
    }

    private String unmarkTask(String input) throws SalException {
        int index = Parser.parseTaskIndex(input, "unmark", "Correct format: unmark <task number>");
        Task task = tasks.markAsNotDone(index);
        saveTasks();
        return ui.formatUnmarked(task);
    }

    private String deleteTask(String input) throws SalException {
        int index = Parser.parseTaskIndex(input, "delete", "Correct format: delete <task number>");
        Task removed = tasks.delete(index);
        saveTasks();
        return ui.formatTaskDeleted(removed, tasks.size());
    }

    private String tagTask(String input) throws SalException {
        Parser.ParsedTag parsed = Parser.parseTag(input);
        Task task = tasks.tag(parsed.index, parsed.tagName);
        saveTasks();
        return ui.formatTagged(task);
    }

    private String addTask(Task task) throws SalException {
        // Parser already validated the command, so a null task would be a bug in our code.
        assert task != null : "Task parsed from a valid command should not be null";
        tasks.add(task);
        saveTasks();
        return ui.formatTaskAdded(task, tasks.size());
    }

    /**
     * Launches Sal using the default data file path.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Sal(DATA_FILE_PATH).run();
    }
}

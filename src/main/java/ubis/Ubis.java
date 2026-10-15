package ubis;

import java.nio.file.Path;

/**
 * Entry point of the Ubis chatbot application, coordinating storage, user interface, and command parsing.
 */
public class Ubis {
    private TaskList taskList;
    private Parser parser;
    private String startupWarning;

    private final Path savePath;

    /**
     * Constructs a new Ubis chatbot instance and loads saved tasks from storage.
     */
    public Ubis() {
        this(Storage.SAVE_PATH);
    }

    /**
     * Loads and saves tasks at the supplied path, allowing isolated application tests.
     *
     * @param savePath Task data file to use for this session.
     */
    Ubis(Path savePath) {
        this(Storage.loadWithReport(savePath), savePath);
    }

    /**
     * Constructs a chatbot from loaded tasks and their startup warning.
     */
    private Ubis(Storage.LoadResult loadResult, Path savePath) {
        this(loadResult.getTaskList(), savePath);
        this.startupWarning = loadResult.getWarning();
    }

    /**
     * Constructs a Ubis instance with a supplied task list.
     *
     * @param taskList Initial task list.
     */
    Ubis(TaskList taskList) {
        this(taskList, Storage.SAVE_PATH);
    }

    /**
     * Constructs a chatbot with supplied tasks and an isolated save destination.
     */
    Ubis(TaskList taskList, Path savePath) {
        this.taskList = taskList;
        this.savePath = savePath;
        this.parser = new Parser(this);
    }

    /**
     * Persists this session's tasks to the same path used to load them.
     *
     * @return True when the task list was saved successfully.
     */
    boolean saveTasks() {
        return Storage.save(taskList, savePath);
    }

    /**
     * Starts the console application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Ubis ubis = new Ubis();
        ubis.welcome();
    }

    /**
     * Generates a response string for a given user input.
     *
     * @param input Raw user input string.
     * @return Chatbot response string.
     */
    public String getResponse(String input) {
        try {
            return parser.handleInput(input);
        } catch (RuntimeException e) {
            System.err.println("Unexpected error while processing a command:");
            e.printStackTrace();
            return Ui.Message.UNEXPECTED_ERROR.getMessage();
        }
    }

    /**
     * Displays the welcome banner and repeatedly processes user commands until exit.
     */
    private void welcome() {
        Ui.welcome();
        if (startupWarning != null) {
            Ui.printMessage(startupWarning);
        }

        // Keep handling commands. Exits when user inputs "bye"
        while (true) {
            String input = parser.receiveInput();
            if (input == null) {
                exit();
                return;
            }
            String response = getResponse(input);
            if ("bye".equals(input.trim())) {
                exit();
                return;
            }
            Ui.printMessage(response);
        }
    }

    /**
     * Returns the chatbot's task list.
     *
     * @return Current task list.
     */
    public TaskList getTaskList() {
        return this.taskList;
    }

    /**
     * Returns the warning generated while loading saved tasks.
     *
     * @return Startup warning, or null when storage loaded normally.
     */
    public String getStartupWarning() {
        return startupWarning;
    }

    /**
     * Cleans up resources, prints a goodbye message, and terminates the application.
     */
    public void exit() {
        Ui.printMessage(Ui.Message.GOODBYE);
        parser.cleanup();
        System.exit(0);
    }
}

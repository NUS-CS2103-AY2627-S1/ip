package emma;

import java.util.List;

import emma.command.Command;

/**
 * A command-line chatbot that keeps track of the user's tasks.
 */
public class Emma {

    /** Where the tasks are kept when no other file is asked for. */
    public static final String DEFAULT_SAVE_PATH = "data/emma.json";

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean isExit = false;
    private boolean isError = false;

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * @param filePath where the tasks are saved between runs.
     */
    public Emma(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList(List.of());
    }

    /** Greets the user, then answers commands until "bye" or the end of the input. */
    public void run() {
        ui.showWelcome();
        String loadMessage = loadTasks();
        if (!loadMessage.isEmpty()) {
            ui.showResponse(loadMessage);
        }
        while (true) {
            String input = ui.readCommand();
            if (input == null) {
                break;
            }
            ui.showResponse(getResponse(input));
            if (isExit) {
                break;
            }
        }
        ui.close();
    }

    /**
     * Replaces the empty starting list with the saved tasks, if they can be read.
     *
     * @return an empty string, or what to tell the user when the saved tasks cannot be read.
     */
    public String loadTasks() {
        try {
            tasks = new TaskList(storage.load());
            return "";
        } catch (EmmaException e) {
            return e.getMessage() + "\nI'll start with an empty list.";
        }
    }

    /**
     * Works out Emma's reply to one line of input, without printing anything.
     *
     * @param input the line the user typed.
     * @return what Emma has to say about it, including any complaint about the command.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            String response = command.execute(tasks, storage);
            assert response != null : "every command must return something for Emma to say";
            isExit = command.isExit();
            isError = false;
            return response;
        } catch (EmmaException e) {
            isError = true;
            return e.getMessage();
        }
    }

    /**
     * Tells whether the last response was a complaint rather than a reply Emma carried out,
     * so that a front end can set it apart from the rest of the conversation.
     *
     * @return true if the last command could not be carried out.
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Tells whether the last command asked Emma to stop.
     *
     * @return true once "bye" has been given.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Starts Emma with the usual save file.
     *
     * @param args empty.
     */
    public static void main(String[] args) {
        new Emma(DEFAULT_SAVE_PATH).run();
    }
}

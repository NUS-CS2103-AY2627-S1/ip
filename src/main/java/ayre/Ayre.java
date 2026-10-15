package ayre;

import ayre.enums.AyreStatus;
import ayre.enums.ReplyType;
import ayre.exceptions.AyreException;
import ayre.storage.LoadResult;
import ayre.storage.Storage;

/**
 * Main Class that handles the high-level operation of the chatbot.
 */
public class Ayre {
    private static final String LOG_PATH = "./data/ayre.txt";
    private final String loadMessage;

    private final CommandExecutor executor;

    /**
     * Initializes UI, Storage, TaskList, CommandExecutor and ensure they can communicate.
     */
    public Ayre() {
        Storage store = new Storage(LOG_PATH);
        LoadResult res = store.load();
        this.loadMessage = res.loadMessage();
        LiveTaskList tasks = new LiveTaskList(res.tasks(), store);
        this.executor = new CommandExecutor(tasks);
    }

    /**
     * Getter for the message on loading from save file.
     * Allows the GUI to display the load result.
     *
     * @return Any accumulated messages from loading from save file.
     */
    public String getLoadMessage() {
        return this.loadMessage;
    }

    /**
     * Reads, executes, then returns the result of user input as a String.
     *
     * @param input User input through the GUI.
     * @return Result of executing the relevant command.
     */
    public AyreResponse getResponse(String input) {
        try {
            ParsedInput parsedInput = Parser.parseInput(input);
            CommandResult result = this.executor.execute(parsedInput.command(), parsedInput.args());
            return new AyreResponse(result.message(), ReplyType.NORMAL, result.status());
        } catch (AyreException e) {
            return new AyreResponse(e.getMessage(), ReplyType.ERROR, AyreStatus.CONTINUE);
        }
    }
}

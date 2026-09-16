package miku;

import java.io.IOException;
import java.util.List;

import miku.command.Command;
import miku.command.CommandContext;
import miku.parser.Parser;
import miku.storage.Storage;
import miku.task.TaskList;
import miku.ui.ResponseFormatter;

/** Coordinates Miku's task tracker and provides responses for its GUI. */
public class Miku {
    private final ResponseFormatter responseFormatter;
    private final Parser parser;
    private final Storage storage;
    private TaskList tasks;
    private boolean hasLoadingError;
    private boolean isExitRequested;

    /** Creates Miku's application collaborators. */
    public Miku() {
        this(new Storage());
    }

    /** Creates Miku with a supplied storage location. */
    Miku(Storage storage) {
        responseFormatter = new ResponseFormatter();
        parser = new Parser();
        this.storage = storage;
        tasks = loadTasks();
    }

    /** Returns Miku's greeting, including a storage warning when loading failed. */
    public String getWelcomeMessage() {
        if (hasLoadingError) {
            return responseFormatter.formatWelcome() + '\n' + responseFormatter.formatLoadingError();
        }
        return responseFormatter.formatWelcome();
    }

    /** Returns separate greeting and warning messages for the GUI. */
    public List<MikuResponse> getStartupResponses() {
        MikuResponse greeting = new MikuResponse(responseFormatter.formatWelcome(), false);
        if (hasLoadingError) {
            return List.of(greeting, new MikuResponse(responseFormatter.formatLoadingError(), true));
        }
        return List.of(greeting);
    }

    /** Processes one user command and returns the response that should appear in the chat. */
    public String getResponse(String input) {
        return processCommand(input).text();
    }

    /** Processes one user command and identifies errors for the GUI. */
    public MikuResponse processCommand(String input) {
        try {
            Command command = parser.parse(input == null ? "" : input.trim());
            assert command != null : "Parser must return a command for valid input.";
            CommandContext commandContext = new CommandContext(tasks, responseFormatter, storage);
            String response = command.execute(commandContext);
            assert response != null : "Commands must return a response for the chat interface.";
            isExitRequested = command.isExit();
            return new MikuResponse(response, false);
        } catch (MikuException exception) {
            return new MikuResponse(responseFormatter.formatError(exception.getMessage()), true);
        }
    }

    /** Returns whether the most recently successful command requested application exit. */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /** Loads persisted tasks and starts with an empty list if the save data is unavailable or invalid. */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (IOException | MikuException exception) {
            hasLoadingError = true;
            return new TaskList();
        }
    }
}

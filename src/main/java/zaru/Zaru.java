package zaru;

import java.nio.file.Path;

import zaru.command.Command;
import zaru.exception.ZaruException;
import zaru.parser.Parser;
import zaru.parser.Response;
import zaru.storage.Storage;
import zaru.task.TaskList;

/** Coordinates storage, command parsing, and task operations for the Zaru chatbot. */
public class Zaru {
    private final TaskList tasks;
    private final String startupError;

    /** Creates a chatbot instance and loads tasks from the default save file. */
    public Zaru() {
        this(Path.of("data", "zaru.txt"));
    }

    /**
     * Creates a chatbot instance and loads tasks from a specified save file.
     *
     * @param saveFile Save file to load and update.
     */
    public Zaru(Path saveFile) {
        Storage storage = new Storage(saveFile);
        tasks = new TaskList(storage);

        String loadingError = null;
        try {
            tasks.loadFromStorage();
        } catch (ZaruException e) {
            loadingError = "Zaru could not load the save file safely.\n%s\n"
                    .formatted(e.getMessage())
                    + "Fix the file or its permissions, then restart the app.";
        }
        startupError = loadingError;
    }

    /**
     * Parses and executes one user command.
     *
     * @param input Raw command entered by the user.
     * @return Response message produced by the command or error handling.
     */
    public Response getResponse(String input) {
        if (startupError != null) {
            return new Response(startupError, Response.ResponseType.ERROR, false);
        }

        try {
            Command command = Parser.parseMessage(input);
            String responseText = command.execute(tasks);

            return new Response(responseText, Response.ResponseType.STATUS, command.isExit());
        } catch (ZaruException e) {
            return new Response(e.getMessage(), Response.ResponseType.ERROR, false);
        }
    }
}

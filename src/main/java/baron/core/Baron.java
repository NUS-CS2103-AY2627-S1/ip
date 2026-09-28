package baron.core;

import java.nio.file.Path;

/**
 * Coordinates Baron command processing and persistent task storage.
 */
public class Baron {
    private final Parser parser;

    /**
     * Creates Baron using the given file for persistent task storage.
     *
     * @param path The path to the task data file.
     */
    public Baron(Path path) {
        Storage storage = new Storage(path);
        parser = new Parser(storage, storage.readTasks());
    }

    /**
     * Returns Baron's introductory message.
     *
     * @return The introductory message.
     */
    public String getIntro() {
        return Response.respondWithIntro();
    }

    /**
     * Returns Baron's response to the specified user message.
     *
     * @param message The user message to process.
     * @return Baron's response.
     */
    public String getResponse(String message) {
        return parser.parse(message);
    }
}

package orangutan;

import java.nio.file.Path;
import java.nio.file.Paths;

import orangutan.command.Context;
import orangutan.command.Parser;

/**
 * Main class for Orangutan Chatbot.
 */
public class Orangutan {
    private final Context context;
    private final Ui ui;
    private final Parser parser;

    /**
     * Initializes a new session.
     * Loads saved list (if any) from file, and prints welcome banner.
     *
     * @param path Path to store / load lists.
     * @param isGui Whether this instance is being started in GUI or CLI.
     */
    public Orangutan(String path, boolean isGui) {
        assert path != null : "orangutan says: path should not be null!!!";

        Path filePath = Paths.get(path);
        context = new Context(); // list: null, isRunLoop: false, filePath: null
        parser = new Parser(context);
        context.setFilePath(filePath);

        if (isGui) {
            ui = null;
        } else {
            ui = new Ui(System.out, System.in);
            ui.welcome(parser);
        }

    }

    /**
     * Returns Orangutan's reply to a user query.
     *
     * @param input Command inputted by user.
     * @return String response to the input.
     */
    public String getResponse(String input) {
        return parser.runCommand(input);
    }

    /**
     * Runs user input loop for CLI.
     * Continuously retrieves user input and outputs chatbot reply, until exit conditions are met
     * (usually via the bye command).
     */
    public void run() {
        while (context.isRun()) {
            ui.getInput(parser);
        }
    }

    /**
     * Returns whether Orangutan is currently accepting input.
     * @return Boolean value.
     */
    public boolean isRun() {
        return context.isRun();
    }

    /**
     * Runs an Orangutan session in CLI.
     */
    public static void main(String[] args) {
        new Orangutan("./data/orangutan.txt", false).run();
    }
}

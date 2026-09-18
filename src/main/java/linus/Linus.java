package linus;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

import linus.executor.Executor;
import linus.invalidtaskexception.InvalidTaskException;
import linus.parser.Parser;
import linus.storage.Storage;
import linus.task.Task;
import linus.ui.Ui;
import linus.validator.Validator;


/**
 * Represents the Linus chatbot which orchestrates the logical flow.
 */
public class Linus {
    private static final String BYE_COMMAND = "bye";
    private static final String STORAGE_FILEPATH = "data/tasklist.txt";
    private static final String INVALID_TASK_ID_ERROR = "Here's a tech tip! Enter a valid task ID :-(";
    private static final String INVALID_DATE_FORMAT_ERROR =
            "Here's a tech tip! Enter a valid date in the format \"yyyy-MM-dd\" :-(";

    private final Ui ui;
    private final Parser parser;
    private final Validator validator;
    private final Executor executor;

    /**
     * Creates a new Linus chatbot.
     * Constructs the Ui, Parser, Validator, Executor and Storage for the Linus chatbot.
     *
     * @param filePath Path that the storage file is located.
     * @throws IOException When the constructor is unable to create or load the storage due to input or output errors.
     * @throws InvalidTaskException When the constructor is unable to load the tasklist due to malformed content.
     */
    public Linus(String filePath) throws IOException, InvalidTaskException {
        this.ui = new Ui();
        this.parser = new Parser();
        Storage storage = new Storage(filePath);
        List<Task> taskList = storage.loadFile();
        this.validator = new Validator(taskList);
        this.executor = new Executor(taskList, storage);
    }

    /**
     * Creates and starts up the chatbot. This is the entry point for the CLI.
     * Prints and displays exceptions that occur to the command line.
     */
    static void main() {
        try {
            Linus chatbot = new Linus(STORAGE_FILEPATH);
            chatbot.run();
        } catch (IOException | InvalidTaskException e) {
            Ui.display(e.getMessage());
        }
    }

    /**
     * Starts up the chatbot by loading the Ui and scanning
     * for user input to execute commands.
     */
    private void run() {
        Ui.sayHello();
        while (true) {
            String input = this.ui.read();
            boolean isBye = processInput(input);
            if (isBye) {
                break;
            }
        }
        Ui.sayBye();
    }

    private boolean processInput(String input) {
        if (input.equals(BYE_COMMAND)) {
            return true;
        }
        String response = this.getResponse(input);
        Ui.display(response);
        return false;
    }

    /**
     * Generates a response for the user's chat message.
     * Parses, validates and executes the user's command.
     * If an exception occurs, returns the error message.
     *
     * @param input The plaintext String input from the user.
     * @return Response message after attempting to execute command.
     */
    public String getResponse(String input) {
        try {
            List<String> parsedInput = this.parser.parse(input);
            this.validator.validate(parsedInput);
            return this.executor.execute(parsedInput);
        } catch (IOException | InvalidTaskException e) {
            return e.getMessage();
        } catch (NumberFormatException e) {
            return INVALID_TASK_ID_ERROR;
        } catch (DateTimeParseException e) {
            return INVALID_DATE_FORMAT_ERROR;
        }
    }
}

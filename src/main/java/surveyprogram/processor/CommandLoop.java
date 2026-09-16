package surveyprogram.processor;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Repeatedly reads console input and delegates each command for processing.
 */
public class CommandLoop {
    private final InputStream inputStream;
    private final TaskCommandProcessor commandProcessor;

    /**
     * Creates a command loop that reads from the supplied stream.
     *
     * @param inputStream source of user commands
     * @param commandProcessor processor that handles each command
     */
    public CommandLoop(InputStream inputStream, TaskCommandProcessor commandProcessor) {
        this.inputStream = inputStream;
        this.commandProcessor = commandProcessor;
    }

    /** Runs until the user enters the exit command or the input stream ends. */
    public void run() {
        try (Scanner scanner = new Scanner(inputStream)) {
            CommandResult result;
            do {
                if (!scanner.hasNextLine()) {
                    break;
                }
                result = commandProcessor.process(scanner.nextLine());
                if (!result.response().isEmpty()) {
                    System.out.println(result.response());
                }
            } while (result.shouldContinue());
        }
    }
}

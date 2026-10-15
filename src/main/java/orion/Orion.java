package orion;

import java.util.Scanner;

import orion.exception.OrionException;
import orion.format.ResponseFormatter;
import orion.parser.Parser;
import orion.storage.Storage;
import orion.task.TaskList;

/**
 * Serves as the core logic hub for the Orion chatbot application.
 * It initializes the main components such as the response formatter, storage,
 * and task list, and processes incoming commands from either the CLI or GUI.
 */
public class Orion {
    private Storage storage;
    private TaskList tasks;
    private ResponseFormatter formatter;

    /**
     * Initializes the Orion chatbot with the specified storage file path.
     * It attempts to load existing tasks from the file, or creates an empty task list
     * if the file cannot be loaded.
     *
     * @param filePath The relative path to the file where tasks are saved.
     */
    public Orion(String filePath) {
        formatter = new ResponseFormatter();
        storage = new Storage(filePath);

        try {
            tasks = new TaskList(storage.load());
        } catch (OrionException e) {
            System.out.println(e.getMessage() + "\nInitialising an empty task list...");
            tasks = new TaskList();
        }
    }

    /**
     * Starts the main execution loop of the chatbot.
     * It displays the welcome message and continuously reads, parses,
     * and executes user commands until the exit command is given.
     */
    public void run() {
        String banner = "  ____       _\n"
                + " / __ \\_____(_)___  ____\n"
                + "/ /_/ / ___/ / __ \\/ __ \\\n"
                + "\\____/_/  /_/\\____/_/ /_/\n";
        String line = "____________________________________________________________";

        System.out.println(line);
        System.out.println(banner);
        System.out.println(formatter.getWelcomeMessage());
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.print("\n> ");
            String input = scanner.nextLine();

            if (Parser.isExit(input)) {
                isRunning = false;
            }

            System.out.println(line);
            System.out.println(Parser.parseAndExecute(input, tasks, formatter, storage));
            System.out.println(line);
        }

        scanner.close();
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input The user's input text.
     * @return A string representing Orion's response.
     * @throws OrionException If the user input is invalid or an error occurs during execution.
     */
    public String getResponse(String input) throws OrionException {
        return Parser.parseAndExecute(input, tasks, formatter, storage);
    }

    /**
     * Generates the welcome message upon application startup.
     *
     * @return A string representing Orion's welcome message.
     */
    public String getWelcomeMessage() {
        return formatter.getWelcomeMessage();
    }

    /**
     * Serves as the entry point for the Command Line Interface (CLI) mode.
     * Creates a new instance of the Orion chatbot and starts its text-based execution.
     *
     * @param args Command line arguments supplied by the operating system.
     */
    public static void main(String[] args) {
        new Orion("./data/orion.txt").run();
    }
}

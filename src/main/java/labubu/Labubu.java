package labubu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Starts the Labubu chatbot application.
 */
public class Labubu {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates Labubu and loads tasks from the specified save file.
     *
     * @param filePath Path to the save file.
     */
    public Labubu(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (IOException | IllegalArgumentException e) {
            System.out.printf("Oops! Save file corrupted or not found. Creating new save...");
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /**
     * Runs the Labubu command-line application.
     *
     */
    public void run() {
        System.out.println(ui.getIntro());

        Scanner scanner = new Scanner(System.in);

        Parser parser = new Parser(scanner, storage, tasks);
        boolean isTerminated;
        do {
            isTerminated = parser.parse();
        } while (!isTerminated);

        System.out.println(ui.getExit());
    }

    /**
     * Processes one command for the graphical user interface.
     *
     * @param command Command entered by the user.
     * @return Labubu's response without the command prompt.
     */
    public String processGuiCommand(String command) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try (Scanner scanner = new Scanner(command + System.lineSeparator())) {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new Parser(scanner, storage, tasks).parse();
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8).replaceFirst("^> ", "").trim();
    }

    /**
     * Starts the Labubu application using its default save file.
     *
     * @param args Command-line arguments, which are ignored.
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--cli")) {
            new Labubu("data/labubu.txt").run();
        } else {
            Main.launch(args);
        }
    }
}

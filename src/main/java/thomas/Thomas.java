package thomas;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import thomas.command.Command;
import thomas.command.ConfirmDeletePlaceCommand;
import thomas.command.DeletePlaceCommand;
import thomas.command.Parser;
import thomas.command.PlaceCommand;
import thomas.place.PlaceList;
import thomas.storage.PlaceStorage;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Starts Thomas, a command-line task manager app.
 * Users can manage tasks, adding Todos, Deadlines, and Events,
 * list these tasks, mark/unmark, delete, and with persistent disk storage.
 */
public class Thomas {
    private final Storage storage;
    private final PlaceStorage placeStorage;
    private final TaskList tasks;
    private final PlaceList places;
    private final Ui ui;
    private final Parser parser;
    private boolean isLastCommandExit = false;
    private Integer pendingPlaceDeletionIndex;

    /**
     * Creates Thomas and loads the saved tasks.
     */
    public Thomas() {
        this("data", "thomas.txt");
    }

    /**
     * Creates Thomas with a configurable storage path.
     *
     * @param filePath Folder and file path segments.
     */
    public Thomas(String... filePath) {
        this.ui = new Ui();
        this.parser = new Parser();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList(storage.load());
        this.placeStorage = new PlaceStorage(getPlaceFilePath(filePath));
        this.places = new PlaceList(placeStorage.load());
    }

    /**
     * Starts the command loop until the user exits.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        try (Scanner scanner = new Scanner(System.in)) {
            while (!isExit) {
                try {
                    String fullCommand = ui.readCommand(scanner);
                    if (fullCommand == null) {
                        break;
                    }
                    ui.showDivider();
                    Command command = parser.parse(fullCommand);
                    executeCommand(command);
                    isExit = command.isExit();
                } catch (ThomasException e) {
                    pendingPlaceDeletionIndex = null;
                    ui.showError(e.getMessage());
                } catch (NumberFormatException e) {
                    pendingPlaceDeletionIndex = null;
                    ui.showInvalidTaskNumber();
                } finally {
                    ui.showDivider();
                }
            }
        }
    }

    /**
     * Processes a command and returns the response as a string.
     * Used by the GUI to get responses without printing to System.out.
     *
     * @param userInput The command input from the user.
     * @return The response string.
     */
    public String getResponse(String userInput) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        isLastCommandExit = false;

        try {
            Command command = parser.parse(userInput);
            executeCommand(command);
            isLastCommandExit = command.isExit();
        } catch (ThomasException e) {
            pendingPlaceDeletionIndex = null;
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            pendingPlaceDeletionIndex = null;
            System.out.println("Please enter a valid task number.");
        } catch (Exception e) {
            pendingPlaceDeletionIndex = null;
            System.out.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }

        String response = outputStream.toString();
        return response.isEmpty() ? "Command executed." : response;
    }

    /**
     * Returns whether the last command executed was an exit command.
     *
     * @return {@code true} if the last command was an exit command.
     */
    public boolean isLastCommandExit() {
        return isLastCommandExit;
    }

    /**
     * Returns the task list for GUI display.
     *
     * @return The current task list.
     */
    public TaskList getTasks() {
        return tasks;
    }

    /** Returns the saved places for GUI display and tests. */
    public PlaceList getPlaces() {
        return places;
    }

    private void executeCommand(Command command) throws ThomasException {
        if (command instanceof ConfirmDeletePlaceCommand confirmation) {
            if (pendingPlaceDeletionIndex == null || pendingPlaceDeletionIndex != confirmation.getIndex()) {
                pendingPlaceDeletionIndex = null;
                throw new ThomasException("No matching place deletion is awaiting confirmation.");
            }
            pendingPlaceDeletionIndex = null;
            confirmation.execute(places, ui, placeStorage);
        } else if (command instanceof DeletePlaceCommand deletion) {
            pendingPlaceDeletionIndex = null;
            deletion.execute(places, ui, placeStorage);
            pendingPlaceDeletionIndex = deletion.getIndex();
        } else {
            pendingPlaceDeletionIndex = null;
            if (command instanceof PlaceCommand placeCommand) {
                placeCommand.execute(places, ui, placeStorage);
            } else {
                command.execute(tasks, ui, storage);
            }
        }
    }

    private String getPlaceFilePath(String... filePath) {
        Path taskFile = Paths.get("", filePath);
        Path parent = taskFile.getParent();
        return (parent == null ? Paths.get("places.txt") : parent.resolve("places.txt")).toString();
    }

    /**
     * Starts the Thomas application.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Thomas().run();
    }
}

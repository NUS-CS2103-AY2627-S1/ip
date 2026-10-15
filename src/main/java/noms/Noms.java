package noms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import noms.command.Command;
import noms.exception.NomsException;
import noms.parser.Parser;
import noms.storage.Storage;
import noms.task.TaskList;
import noms.ui.Ui;

/**
 * Entry point and top-level coordinator for the Noms task manager.
 *
 * Noms itself no longer contains any UI, parsing, or storage logic: it
 * simply wires together a {@link Ui}, a {@link Storage}, and a
 * {@link TaskList}, and drives the read-evaluate-respond loop, delegating
 * each concern to the class that owns it.
 */
public class Noms {
    private static final String SAVE_DIRECTORY = "data";
    private static final String SAVE_FILE_NAME = "noms.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /** Whether the most recent GUI command asked Noms to exit. */
    private boolean isExitRequested;

    /**
     * Creates a Noms instance backed by the default save file.
     */
    public Noms() {
        this(SAVE_DIRECTORY, SAVE_FILE_NAME);
    }

    /**
     * Creates a Noms instance backed by the save file in the given
     * directory, loading any previously saved tasks. If the file cannot be
     * read, Noms starts with an empty list and reports the problem rather
     * than crashing.
     *
     * @param directory folder containing the save file, relative to the project root
     * @param fileName name of the save file within that folder
     */
    public Noms(String directory, String fileName) {
        ui = new Ui();
        storage = new Storage(directory, fileName);

        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
            reportSkippedLines(storage.getSkippedLines());
        } catch (IOException e) {
            loaded = new TaskList();
            ui.showError("Noms couldn't load the saved menu, starting with an empty plate: " + e.getMessage());
        }
        tasks = loaded;
    }

    /**
     * Reports each save-file line skipped while loading, so the user knows
     * their data was not silently dropped.
     *
     * @param skippedLines the raw lines that could not be parsed, in load order.
     */
    private void reportSkippedLines(List<String> skippedLines) {
        for (String spoiledLine : skippedLines) {
            ui.showError("Noms found a spoiled entry in the save file and skipped it: " + spoiledLine);
        }
    }

    /**
     * Runs the main loop: greets the user, then repeatedly reads a command,
     * turns it into a {@link Command}, executes it, and continues until an
     * exit command runs or the input ends. Any command error is reported
     * and the loop keeps going.
     */
    public void run() {
        ui.showWelcome();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            try {
                isExit = executeCommand(fullCommand);
            } catch (NomsException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.close();
    }

    /**
     * Runs a single command entered in the GUI and returns Noms' reply as text.
     *
     * The GUI reuses the exact same parse-execute pipeline as the console loop.
     * Because the existing {@link Command}s report their results by printing
     * through {@link Ui} (i.e. to {@code System.out}), this method temporarily
     * captures standard output while the command runs, then returns that text
     * with the console dividers stripped so it reads cleanly in a chat bubble.
     *
     * @param input the raw command line entered in the GUI
     * @return Noms' response text and its presentation type
     */
    public NomsResponse getResponse(String input) {
        ui.resetErrorState();
        String output = captureConsoleOutput(() -> {
            try {
                isExitRequested = executeCommand(input);
            } catch (NomsException e) {
                ui.showError(e.getMessage());
            }
        });
        ResponseType responseType = ui.hasShownError()
                ? ResponseType.ERROR
                : ResponseType.NORMAL;
        return new NomsResponse(stripDividers(output), responseType);
    }

    /**
     * Parses and executes one command through the shared console and GUI
     * pipeline.
     *
     * @param input the raw command line to execute
     * @return whether the command requests that Noms exit
     * @throws NomsException if the command is invalid
     */
    private boolean executeCommand(String input) throws NomsException {
        Command command = Parser.parse(input);
        command.execute(tasks, ui, storage);
        return command.isExit();
    }

    /**
     * Returns whether the last command run through {@link #getResponse} asked
     * Noms to exit, so the GUI knows when to close the window.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns the greeting shown when the GUI window first opens.
     */
    public String getGreeting() {
        return "Hi! I'm Noms, your hungry little task monster. What's on the menu today?";
    }

    /**
     * Runs the given action with {@code System.out} redirected into a buffer
     * and returns whatever it printed. The original stream is always restored,
     * even if the action throws.
     *
     * @param action the code whose console output should be captured
     * @return everything the action printed to standard output
     */
    private static String captureConsoleOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    /**
     * Removes the console divider lines (rows of underscores) and trims blank
     * edges so console-formatted output reads cleanly in a GUI chat bubble.
     *
     * @param text the raw captured console output
     * @return the same text without divider lines or surrounding blank space
     */
    private static String stripDividers(String text) {
        return text.lines()
                .filter(line -> !line.matches("_+"))
                .collect(Collectors.joining("\n"))
                .strip();
    }

    /**
     * Launches Noms with the default save location and runs it until the
     * user exits.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        new Noms().run();
    }
}

package aster;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import aster.exception.AsterException;
import aster.parser.Parser;
import aster.storage.Storage;
import aster.task.TaskList;
import aster.ui.Ui;

/**
 * Entry point for the Aster chatbot.
 *
 * <p>Aster greets the user and reads commands until {@code bye}. The commands
 * {@code todo}, {@code deadline} and {@code event} add a task of the matching type,
 * {@code list} shows the stored tasks with their type and done status,
 * {@code find <keyword>} shows those whose description contains the keyword,
 * {@code stats} shows how many tasks there are, how many are done and how many of each
 * type, {@code mark <number>} and {@code unmark <number>} change the done status of one
 * task, leaving a task already in that state as it is, and {@code delete <number>}
 * removes one task. Anything else is refused with an explanation: unrecognized commands,
 * missing descriptions, missing, repeated or out-of-order {@code /by}, {@code /from}
 * and {@code /to} parts, dates not written as {@code yyyy-MM-dd}, events that end
 * before they start, and unusable task numbers. A refused command leaves the task list
 * unchanged. The tasks are saved whenever the list changes, and read back when Aster
 * next starts, so the list survives leaving and returning.
 *
 * <p>This class holds the conversation together and nothing else: the {@link Ui} does
 * the talking, the {@link Parser} works out what was asked, a {@code Command} carries
 * it out on the {@link TaskList}, and the {@link Storage} keeps the tasks between
 * visits.
 *
 * <p>Aster can be talked to in two ways. {@link #run()} holds a whole conversation in
 * the terminal, while {@link #startConversation()} and {@link #getResponse(String)}
 * answer one message at a time for a graphical interface. Both carry out a command
 * through the same step, so the two cannot come to behave differently.
 */
public class Aster {
    // Kept relative, and built from its parts rather than written with a separator, so
    // it means the same thing on every operating system.
    private static final Path DATA_FILE = Paths.get("data", "aster.txt");

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a chatbot that keeps its tasks in the usual file, {@code data/aster.txt}.
     *
     * <p>This is how a graphical interface creates Aster, since it has no file to pass
     * in.
     */
    public Aster() {
        this(DATA_FILE);
    }

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * @param dataFile the file to read the tasks from and write them back to.
     */
    public Aster(Path dataFile) {
        this.ui = new Ui();
        this.storage = new Storage(dataFile);
    }

    /**
     * Reads any saved tasks, greets the user, then carries out commands until
     * {@code bye} or the end of input.
     *
     * <p>If the saved tasks cannot be read, Aster explains why and stops before taking
     * any command, so that a file which may still be worth keeping is not written over.
     */
    public void run() {
        try {
            loadTasks();
        } catch (AsterException e) {
            ui.showLoadingError(e.getMessage());
            return;
        }

        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            if (Parser.isExit(fullCommand)) {
                break;
            }
            ui.showLine();
            carryOut(fullCommand, ui);
            ui.showLine();
        }
        ui.showGoodbye();
    }

    /**
     * Reads any saved tasks and returns the reply that opens the conversation.
     *
     * <p>If the saved tasks cannot be read, the reply explains why and ends the
     * conversation, so that no command is carried out and a file which may still be
     * worth keeping is not written over. This mirrors {@link #run()} stopping before it
     * takes any command. Such a reply contains an error; the greeting does not.
     *
     * @return the greeting, or the reason the conversation cannot start.
     */
    public Response startConversation() {
        List<String> lines = new ArrayList<>();
        Ui reply = new Ui(lines::add);
        try {
            loadTasks();
        } catch (AsterException e) {
            reply.showError(e.getMessage());
            return new Response(joinLines(lines), true, true);
        }
        reply.showGreeting();
        return new Response(joinLines(lines), false, false);
    }

    /**
     * Returns Aster's reply to one message.
     *
     * <p>The message is trimmed first, as the text interface trims each line it reads.
     * {@code bye} ends the conversation with a farewell. Anything else is carried out
     * exactly as {@link #run()} carries it out, and the lines it produces form the reply,
     * so a refused command is answered with its explanation rather than thrown.
     *
     * <p>Showing an empty task list is the only command that produces no line at all.
     * The text interface's dividers still mark that exchange, but a reply here would be
     * blank, so it says that the list is empty instead.
     *
     * <p>The reply contains an error whenever carrying out the command reported one. The
     * farewell never does.
     *
     * @param input the message exactly as the user typed it.
     * @return the reply, whether it ends the conversation, and whether it contains an error.
     * @throws IllegalStateException if the conversation has not started successfully.
     */
    public Response getResponse(String input) {
        if (tasks == null) {
            throw new IllegalStateException("The conversation has not started successfully.");
        }
        String fullCommand = input.trim();
        List<String> lines = new ArrayList<>();
        Ui reply = new Ui(lines::add);
        if (Parser.isExit(fullCommand)) {
            reply.showFarewell();
            return new Response(joinLines(lines), true, false);
        }
        boolean isError = carryOut(fullCommand, reply);
        if (lines.isEmpty()) {
            reply.showEmptyList();
        }
        return new Response(joinLines(lines), false, isError);
    }

    /**
     * Reads the saved tasks into the task list.
     *
     * <p>The task list is replaced only once the tasks have been read, so a failed read
     * leaves it as it was.
     *
     * @throws AsterException if the saved tasks cannot be read.
     */
    private void loadTasks() throws AsterException {
        tasks = new TaskList(storage.load());
    }

    /**
     * Carries out one command line, reporting its outcome through the given interface.
     *
     * <p>Every failure surfaces in this one place, so nothing else reports errors, and
     * the task list keeps its previous contents whenever a command is refused.
     *
     * <p>A change that was made but could not be saved is reported here too, after the
     * lines describing the change, so that reply also contains an error.
     *
     * @param fullCommand the trimmed command line, which is not {@code bye}.
     * @param target the interface to report the outcome through.
     * @return {@code true} if the reply contains an error, otherwise {@code false}.
     */
    private boolean carryOut(String fullCommand, Ui target) {
        assert tasks != null : "The saved tasks must be loaded before any command is carried out";
        assert !Parser.isExit(fullCommand) : "bye must be handled by the caller, not carried out";
        try {
            Parser.parse(fullCommand).execute(tasks, target, storage);
            return false;
        } catch (AsterException e) {
            target.showError(e.getMessage());
            return true;
        }
    }

    /**
     * Returns the lines of a reply as one message.
     *
     * @param lines the lines, in the order they were shown.
     * @return the lines separated by {@code \n}, with no line ending after the last.
     */
    private static String joinLines(List<String> lines) {
        return String.join("\n", lines);
    }

    /**
     * Starts the chatbot in the terminal.
     *
     * @param args command line arguments; not used.
     */
    public static void main(String[] args) {
        new Aster(DATA_FILE).run();
    }
}

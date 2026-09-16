package lebronjames;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import lebronjames.command.Command;
import lebronjames.parser.Parser;
import lebronjames.storage.Storage;
import lebronjames.task.Task;
import lebronjames.task.TaskList;
import lebronjames.ui.Ui;

/**
 * Runs the Lebron James chatbot application.
 *
 * <p>An instance holds the three collaborators the chatbot needs: a {@link Ui}
 * to word its replies, a {@link Storage} to remember tasks between runs, and a
 * {@link TaskList} holding the tasks themselves.
 *
 * <p>This class no longer knows what any individual command does. It takes a
 * line, asks {@link Parser} what it means, and tells the resulting
 * {@link Command} to execute itself, which keeps the logic short enough to read
 * at a glance.
 *
 * <p>Two front ends sit on top of this class. {@link #getResponse(String)}
 * answers one command and hands back the reply as text, which is what the
 * graphical interface in {@link lebronjames.gui} puts into a dialog bubble;
 * {@link #run()} loops over that same method reading from and printing to the
 * console. Keeping the chatbot itself free of any display code is what lets
 * both exist without duplicating the command handling.
 */
public class LebronJames {
    /**
     * Folder and file name of the save file, relative to the project root.
     * Kept as separate parts so {@link Storage} can join them with the
     * separator that suits the current operating system.
     */
    public static final String DATA_FOLDER = "data";
    public static final String DATA_FILE = "lebronjames.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private boolean isExit = false;
    private boolean isErrorReply = false;

    /**
     * Creates a chatbot that saves to, and loads from, the given file.
     *
     * <p>The greeting is worded before loading so that any problem with the
     * save file is reported as part of the start-up message, which
     * {@link #getWelcomeMessage()} then hands to whichever front end is in use.
     *
     * @param firstPathPart First name in the path to the save file.
     * @param remainingPathParts Remaining names in the path to the save file.
     */
    public LebronJames(String firstPathPart, String... remainingPathParts) {
        this.ui = new Ui();
        this.storage = new Storage(firstPathPart, remainingPathParts);
        ui.showWelcome();
        this.tasks = loadTasks();
    }

    /**
     * Starts the chatbot in the console, saving to and loading from the default
     * data file.
     *
     * <p>The graphical interface has its own entry point,
     * {@link Launcher#main(String[])}; this one is kept so the chatbot can
     * still be driven from a terminal.
     *
     * @param args Command line arguments, which the chatbot does not use.
     */
    public static void main(String[] args) {
        useUtf8Output();
        new LebronJames(DATA_FOLDER, DATA_FILE).run();
    }

    /**
     * Makes printed output UTF-8 regardless of the machine the app is run on.
     *
     * <p>By default System.out encodes using whatever character set the console
     * reports, so on a console that is not UTF-8 the basketball in every reply
     * arrives as a question mark. Replacing the stream once, here, keeps every
     * message readable without {@link lebronjames.ui.Ui} having to know
     * anything about character sets.
     *
     * <p>This is done in main rather than inside Ui so that tests can still
     * capture output with System.setOut in the ordinary way.
     */
    private static void useUtf8Output() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
    }

    /**
     * Returns the greeting shown before the user has typed anything, including
     * any warning about the save file.
     *
     * @return Start-up message.
     */
    public String getWelcomeMessage() {
        return ui.getResponse();
    }

    /**
     * Carries out one command and returns what the chatbot says in reply.
     *
     * <p>An unusable command is not a failure of the whole program, so the
     * explanation carried by the exception becomes the reply rather than
     * escaping to the caller.
     *
     * @param fullCommand Line of input, exactly as the user typed it.
     * @return Reply to show the user.
     */
    public String getResponse(String fullCommand) {
        isErrorReply = false;
        try {
            Command command = Parser.parse(fullCommand);

            // Parser signals every problem by throwing, so reaching this line
            // means a real command was understood.
            assert command != null : "Parser must return a command or throw";

            command.execute(tasks, ui, storage);
            isExit = command.isExit();
        } catch (LebronJamesException exception) {
            ui.showError(exception.getMessage());
            isErrorReply = true;
        }
        return ui.getResponse();
    }

    /**
     * Returns whether the reply just handed out was a complaint about the input
     * rather than an answer to it.
     *
     * <p>The graphical interface uses this to draw a rejected command
     * differently from an accepted one. Only the front end cares, which is why
     * the distinction is reported here rather than folded into the reply text,
     * where the console interface would be forced to show it too.
     *
     * @return Whether the last reply reported a problem.
     */
    public boolean isErrorReply() {
        return isErrorReply;
    }

    /**
     * Returns whether the last command carried out was one that ends the
     * program, so a front end knows when to shut itself down.
     *
     * @return Whether the user has said goodbye.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Reads and carries out commands from the console until the user says bye
     * or the input ends.
     */
    public void run() {
        System.out.println(Ui.SEPARATOR);
        System.out.println(getWelcomeMessage());
        System.out.println(Ui.SEPARATOR);
        while (!isExit && ui.hasNextCommand()) {
            String reply = getResponse(ui.readCommand());
            System.out.println(Ui.SEPARATOR);
            System.out.println(reply);
            System.out.println(Ui.SEPARATOR);
        }
    }

    /**
     * Loads the saved tasks and reports anything that went wrong.
     *
     * <p>Startup never fails because of the save file: if it cannot be read at
     * all, the chatbot warns the user and begins with an empty list.
     *
     * @return Tasks restored from disk, or an empty list if none could be read.
     */
    private TaskList loadTasks() {
        try {
            ArrayList<Task> savedTasks = storage.load();

            // A missing save file yields an empty list rather than null, which is
            // what lets start-up carry on without a special case for a first run.
            assert savedTasks != null : "Storage.load returns an empty list, never null";

            ui.showLoaded(savedTasks.size());
            return new TaskList(savedTasks);
        } catch (LebronJamesException exception) {
            ui.showLoadingError(exception.getMessage());
            isErrorReply = true;
            return new TaskList();
        }
    }
}

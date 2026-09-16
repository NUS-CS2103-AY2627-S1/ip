package serangooner;

import java.nio.file.Path;

import serangooner.command.Command;
import serangooner.storage.Storage;
import serangooner.task.TaskList;
import serangooner.ui.Console;
import serangooner.ui.Ui;

/**
 * Runs the Serangooner chatbot.
 * This class is the one place that holds the wording, the task list and the
 * save file together, so it alone decides when a change to the list is
 * written back to disk. A line typed by the user is handed to the
 * {@link Parser}, which returns the {@link Command} it asks for, and running
 * that command is all this class has left to do.
 * Answering one line at a time is kept apart from the loop that reads them,
 * so that a chat window can ask for the same answers without a console.
 */
public class Serangooner {
    private static final String DEFAULT_FILE_PATH = "data/serangooner.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final Storage.LoadResult loadResult;

    /**
     * Constructs a chatbot whose tasks are read from and written to the
     * save file the program normally uses.
     */
    public Serangooner() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a chatbot whose tasks are read from and written to the given file.
     * A file that cannot be read is not fatal: the chatbot starts with an
     * empty list and says why once the user has been greeted.
     *
     * @param filePath Relative path of the save file.
     */
    public Serangooner(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(Path.of(filePath));
        this.loadResult = storage.load();
        this.tasks = new TaskList(loadResult.tasks());
        assert tasks.size() == loadResult.tasks().size()
                : "the list starts out holding exactly what was loaded";
    }

    /**
     * Returns the greeting that opens a conversation.
     */
    public String getGreeting() {
        return ui.formatWelcome();
    }

    /**
     * Returns what to say about the save file that was read at startup, and an
     * empty string when there is nothing worth saying about it.
     */
    public String getLoadReport() {
        return ui.formatLoadReport(loadResult);
    }

    /**
     * Runs the command the given line asks for, and returns what to say back.
     * A line the chatbot cannot make sense of is answered rather than thrown,
     * so that one bad command never ends the conversation. The same goes for
     * a failure no one foresaw, which is owned up to as the chatbot's fault
     * rather than the user's.
     *
     * @param fullCommand Line as the user typed it.
     * @return What the chatbot says in reply.
     */
    public Response getResponse(String fullCommand) {
        try {
            Command command = Parser.parse(fullCommand);
            return new Response(command.execute(tasks, ui, storage), command.isExit(), false);
        } catch (SerangoonerException exception) {
            return new Response(exception.getMessage(), false, true);
        } catch (RuntimeException exception) {
            return new Response(ui.formatUnexpectedError(exception), false, true);
        }
    }

    /**
     * Greets the user, then runs each command typed until the user says bye.
     * Every line is answered the same way, so this loop never has to ask what
     * was typed: it reads, answers, and draws a divider, whether the command
     * succeeded or not.
     */
    public void run() {
        Console console = new Console();
        console.showBanner();
        console.showBlock(getGreeting());
        console.showBlock(getLoadReport());

        boolean isExit = false;
        while (!isExit && console.hasNextCommand()) {
            Response response = getResponse(console.readCommand());
            show(console, response);
            isExit = response.isExit();
        }
    }

    /**
     * Shows one reply on the console, framed by a divider.
     * A refused command is shown as an error, which the console answers with
     * a sound of its own.
     *
     * @param console Console to show the reply on.
     * @param response Reply to show.
     */
    private static void show(Console console, Response response) {
        if (response.isError()) {
            console.showError(response.text());
        } else {
            console.show(response.text());
        }
        console.showDivider();
    }

    /**
     * Starts the chatbot on the console, reading the default save file.
     *
     * @param args Command line arguments, which are ignored.
     */
    public static void main(String[] args) {
        new Serangooner().run();
    }

    /**
     * Holds what the chatbot says in reply to one line.
     *
     * @param text Words to show the user.
     * @param isExit True if the line ended the conversation.
     * @param isError True if the line was refused rather than carried out.
     */
    public record Response(String text, boolean isExit, boolean isError) { }
}

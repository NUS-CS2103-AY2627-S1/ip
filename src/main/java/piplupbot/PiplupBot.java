package piplupbot;

import java.nio.file.Path;

import piplupbot.command.Command;
import piplupbot.task.TaskList;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents a simple chatbot that keeps a list of tasks, in the console or in
 * a window.
 * It greets the user, stores three kinds of task -- {@code todo},
 * {@code deadline} and {@code event} -- lists the stored tasks on the
 * {@code list} command, marks a task as done on the {@code mark <number>}
 * command, reverses that on the {@code unmark <number>} command,
 * removes a task on the {@code delete <number>} command,
 * shows the tasks whose description contains some text on the
 * {@code find <keyword>} command,
 * puts the list into a given order on the {@code sort <key> [asc|desc]}
 * command,
 * and exits when the user types {@code bye}.
 *
 * <p>Every change to the list is written straight to the hard disk by
 * {@link Storage}, and read back when the program starts, so the tasks survive
 * the program being closed.</p>
 *
 * <p>This class is the conversation and nothing else. It hands each line to
 * {@link Parser}, asks the {@link Command} that comes back to carry itself out,
 * and reports when one of them says the conversation is over. What a command
 * <em>does</em> is that command's own business, holding the tasks is
 * {@link TaskList}'s, and saying things out loud is {@link Ui}'s.</p>
 *
 * <p>The bot does not keep a {@link Ui} of its own. It is handed one along with
 * each line, just as a {@link Command} is, and that is what lets one bot serve
 * two faces: {@link #run} drives it from the console with a {@link TextUi}, and
 * the window, which is itself a {@link GuiUi}, drives it by handing over
 * itself. {@link Launcher} decides which of the two the user gets. They differ
 * only in how a line reaches the bot and how the answer is shown -- never in
 * what the answer is, because both go through {@link #respondTo}.</p>
 *
 * <p>So nothing here knows how a command is spelled, what any of them do, how a
 * list is indexed, or how a reply is laid out on screen. The measure of that is
 * that this file no longer names a single command: teaching the bot a new one
 * leaves it untouched.</p>
 */
public class PiplupBot {
    /**
     * Where the tasks are kept unless the bot is told otherwise. The console and
     * the window both use it, which is what makes them share one list of tasks.
     *
     * <p>The path is relative, so it is resolved against whatever directory the
     * bot is started in -- normally the project root, giving
     * {@code ./data/piplupbot.txt}. The text-UI tests rely on this: they start
     * the program in a scratch directory of their own, so the file they write
     * is not the one holding real tasks, and no test-only setting is needed to
     * arrange it.</p>
     *
     * <p>The parts of the path are passed separately rather than as one string,
     * so the separator between them is never written down here: {@code Path.of}
     * joins them with whatever the machine uses, a backslash on Windows and a
     * forward slash elsewhere.</p>
     */
    public static final Path DEFAULT_FILE_PATH = Path.of("data", "piplupbot.txt");

    /** Where the tasks are kept between runs. */
    private final Storage storage;

    /** The tasks the user has stored, as read from {@link #storage} at startup. */
    private final TaskList tasks;

    /**
     * What to tell the user about the save file, or an empty array if it was
     * read without trouble.
     *
     * <p>The file is read in the constructor but the warning cannot be shown
     * there, because the bot should introduce itself before it starts
     * complaining. Keeping the lines here is what lets the reading happen at
     * the earliest moment and the telling at the right one.</p>
     */
    private final String[] loadWarningLines;

    /**
     * Whether a line handed to {@link #respondTo} has ended the conversation.
     *
     * <p>{@link #respondTo} used to hand this back as its return value. Keeping
     * it here instead lets that method only <em>do</em> things and
     * {@link #isExit} only <em>answer</em> a question, so neither name has to
     * cover both jobs. The price is that the bot now remembers something
     * between lines, which it did not have to before.</p>
     */
    private boolean isExit = false;

    /**
     * Creates a bot whose tasks are kept in the given file, reading back
     * whatever the last run left there.
     *
     * <p>Reading in the constructor means that by the time the object exists it
     * is already usable, so no method has to wonder whether the list has been
     * filled in yet. It is safe to do here because {@link Storage#load} does not
     * throw: a file that is missing, unreadable or damaged still yields a list
     * -- an empty one if need be -- along with something to tell the user.</p>
     *
     * @param filePath Where to keep the tasks, e.g.
     *                 {@code Path.of("data", "piplupbot.txt")}.
     */
    public PiplupBot(Path filePath) {
        this.storage = new Storage(filePath);

        Storage.LoadResult loaded = storage.load();
        // The loaded tasks are handed to TaskList's constructor, so the list
        // starts out holding exactly what the file held.
        this.tasks = new TaskList(loaded.tasks());
        this.loadWarningLines = loaded.warningLines();
    }

    /**
     * Greets the user, then tells them about any trouble reading the save file.
     *
     * @param ui Where to say it.
     */
    public void greet(Ui ui) {
        ui.showWelcome();

        // Anything wrong with the save file is said after the greeting rather
        // than before it, so the bot introduces itself first and the warning
        // reads as its own words rather than as a crash on startup.
        if (loadWarningLines.length > 0) {
            ui.show(loadWarningLines);
        }
    }

    /**
     * Carries out one line the user typed. Ask {@link #isExit} afterwards to
     * find out whether it ended the conversation.
     *
     * <p>This is the one step both faces take for every line, so a command
     * behaves the same whether it was typed in the console or in the window.
     * The spaces around the line are removed here rather than trusted to be
     * gone already: the console's reader trims every line, but the window's
     * text box does not.</p>
     *
     * @param input The line the user typed.
     * @param ui    Where to say what happened.
     */
    public void respondTo(String input, Ui ui) {
        String line = input.trim();
        if (line.isEmpty()) {
            // A blank line names no command, so there is nothing to report.
            return;
        }

        // One try/catch for the whole conversation: the parser and the
        // commands decide *what* went wrong and throw, while this block is
        // the single place that decides *how* the problem is shown. A new
        // command therefore gets its error reporting for free.
        try {
            // Reading the line and acting on it are two separate steps, and
            // this method takes no part in either. Parser.parse() turns the
            // line into a command -- or refuses it -- and the command carries
            // itself out, so nothing here names a single one of them.
            Command command = Parser.parse(line);
            command.execute(tasks, ui, storage);

            // Only the command knows whether it was the last one, so it is
            // asked rather than recognised: this method never mentions "bye".
            isExit = command.isExit();
        } catch (PiplupBotException e) {
            // The bot explains the problem and carries on with the next line,
            // instead of letting the error stop the conversation.
            ui.showError(e);
        }
    }

    /**
     * Returns whether the last line handed to {@link #respondTo} ended the
     * conversation. A blank line or one the bot could not carry out leaves the
     * answer as it was.
     *
     * @return {@code true} once the conversation is over.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Holds the conversation in the console, from the greeting to {@code bye}.
     * Each line is handled by {@link #respondTo}, so all this method does is
     * read lines and stop at the right moment.
     */
    public void run() {
        TextUi ui = new TextUi();
        greet(ui);

        // Keep reading commands until one of them says the conversation is over,
        // or until the input runs out (e.g. Ctrl-D / piped input).
        while (!isExit() && ui.hasNextCommand()) {
            respondTo(ui.readCommand(), ui);
        }
    }

    /**
     * Starts the bot in the console, with its usual save file.
     *
     * <p>This is the console whatever the arguments, so that starting this class
     * directly -- as the text-UI tests do, and as IntelliJ does from the run
     * button beside this method -- still gives the console it always gave.
     * {@link Launcher} is the way in that opens the window.</p>
     *
     * @param args Ignored; the bot takes its instructions from the conversation.
     */
    public static void main(String[] args) {
        new PiplupBot(DEFAULT_FILE_PATH).run();
    }
}

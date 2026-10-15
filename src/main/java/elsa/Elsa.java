package elsa;

import java.util.List;

import elsa.command.Command;
import elsa.command.RemindCommand;
import elsa.parser.Parser;
import elsa.storage.Storage;
import elsa.task.Reminder;
import elsa.task.TaskList;
import elsa.ui.Ui;

/**
 * Entry point of the Elsa chatbot.
 * Greets the user, stores todos, deadlines and events, lists them back on request,
 * marks them as done or not done, deletes them, lists those falling on a given
 * date, reminds the user of deadlines that are overdue or due soon, reports what
 * it cannot understand, and exits when the user types "bye".
 * The task list is saved to the hard disk every time it changes and is read back
 * at startup; see {@link Storage}.
 *
 * <p>The work is shared out: {@link Ui} handles what the user sees and types,
 * {@link Parser} works out what a typed line means, {@link TaskList} holds the
 * tasks, and {@link Storage} keeps them on the disk. What is left here is the
 * order those happen in, which is the one thing that has to know about them all.
 */
public class Elsa {
    /** Where the tasks are kept between runs, relative to the project root. */
    private static final String FILE_PATH = "data/elsa.txt";

    /** What the user sees and types. Never replaced once the session starts. */
    private final Ui ui;

    /** Reads and writes the saved tasks. */
    private final Storage storage;

    /**
     * The tasks being kept. Not final because it is replaced when the saved list
     * is read at startup, and again if that list turns out to be unreadable.
     */
    private TaskList tasks;

    /**
     * Whether the user has said goodbye.
     * The terminal learns this from the command it just ran, but the window only
     * gets the reply text back, so the answer is recorded here for it to ask
     * afterwards through {@link #isExiting()}.
     */
    private boolean isExiting = false;

    /**
     * Whether the last reply was a complaint rather than a confirmation.
     * Recorded for the same reason as {@link #isExiting}: the window is handed
     * the reply as text, which says nothing about whether the command worked,
     * and it needs to know so that it can mark a complaint as one. Asked
     * afterwards through {@link #wasLastReplyAnError()}.
     */
    private boolean isLastReplyAnError = false;

    /**
     * Creates a chatbot that keeps its tasks in the named file, with nothing in
     * its list yet.
     * Reading the saved tasks is left to {@link #run()} rather than done here, so
     * that the greeting is shown before any complaint about the saved file, and
     * so that making a chatbot does not by itself touch the disk.
     *
     * @param filePath where the tasks are kept, relative to where the program is run
     */
    public Elsa(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList();
    }

    /**
     * Creates a chatbot that keeps its tasks where this program usually keeps
     * them.
     * There are two ways to start the chatbot, the terminal and the window, and
     * both want the same file. Naming it once here means neither has to know the
     * path, and moving the file later is one change rather than two.
     */
    public Elsa() {
        this(FILE_PATH);
    }

    /**
     * Returns what the chatbot says before the user has typed anything, having
     * read the tasks saved by an earlier run.
     *
     * <p>This is the window's counterpart to the opening of {@link #run()}. The
     * terminal shows the greeting, any complaint about the saved file and any
     * reminders as separate blocks; the window has one dialog box to put them in,
     * so they are joined here with a blank line between each.
     *
     * @return the chatbot's opening message.
     */
    public String startSession() {
        String loadReport = loadTasks();
        String reminders = getStartupReminders();
        String message = ui.getGreetingMessage();
        if (!loadReport.isEmpty()) {
            message += "\n\n" + loadReport;
        }
        if (!reminders.isEmpty()) {
            message += "\n\n" + reminders;
        }
        return message;
    }

    /**
     * Returns what the chatbot says in reply to a line the user typed.
     *
     * <p>This is one turn of a session: read the line, carry out what it asks,
     * and say what happened. Both faces of the chatbot turn a line into a reply
     * through here, the window for each line sent and {@link #run()} for each
     * line typed, so that neither can be given behaviour the other lacks.
     *
     * <p>A line the chatbot cannot carry out is answered with the complaint
     * rather than thrown, because the window has nowhere to throw it to and the
     * user is owed an answer either way. The terminal shows that complaint the
     * same way it shows any other reply.
     *
     * @param input the line the user typed.
     * @return what the chatbot says back.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input.trim());
            String response = command.execute(tasks, ui, storage);
            assert response != null : "every command owes the user a reply";
            isExiting = command.isExit();
            isLastReplyAnError = false;
            return response;
        } catch (ElsaException e) {
            isLastReplyAnError = true;
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /**
     * Returns whether the last reply was a complaint rather than a confirmation.
     *
     * <p>This answers for {@link #getResponse} alone. The opening message from
     * {@link #startSession()} may carry a complaint about the saved file among
     * the greeting and the reminders, and a single answer could not describe a
     * message that is partly one thing and partly another.
     *
     * @return true if the last line could not be carried out.
     */
    public boolean wasLastReplyAnError() {
        return isLastReplyAnError;
    }

    /**
     * Returns whether the user has said goodbye.
     *
     * @return true once a command has ended the session.
     */
    public boolean isExiting() {
        return isExiting;
    }

    /**
     * Runs one session in the terminal: greets the user, reads the saved tasks,
     * then carries out commands until the user says "bye" or the input ends.
     *
     * <p>What is left here is the terminal's own share of a session, which is
     * the opening and the reading of lines. Turning one line into a reply is
     * {@link #getResponse}, which the window uses too.
     */
    public void run() {
        ui.show(ui.getWelcomeMessage());

        // Shown as its own block, after the greeting, so that a complaint about
        // the saved file does not arrive mixed into the welcome.
        String loadReport = loadTasks();
        if (!loadReport.isEmpty()) {
            ui.show(loadReport);
        }

        // After the complaint, so that the user hears about any damaged lines
        // before being reminded of tasks read from the same file.
        String reminders = getStartupReminders();
        if (!reminders.isEmpty()) {
            ui.show(reminders);
        }

        // Each command says whether the session should end, so the loop does not
        // need to know which one means goodbye. hasNextCommand() guards against
        // input ending without a "bye".
        while (!isExiting && ui.hasNextCommand()) {
            ui.show(getResponse(ui.readCommand()));
        }
    }

    /**
     * Reads the tasks saved by an earlier run into the list, and returns anything
     * the user should be told about the reading.
     * On the very first run there is no file yet, and load() returns an empty
     * list rather than treating that as a problem.
     *
     * @return what to tell the user, or the empty string if all went well
     */
    private String loadTasks() {
        try {
            Storage.LoadResult loaded = storage.load();
            tasks = loaded.tasks();
            if (loaded.problems().isEmpty()) {
                return "";
            }
            // The tasks that did load are kept, so the user is told what was lost
            // rather than the whole file being thrown away.
            return ui.getSkippedLinesMessage(loaded.problems(), storage.getFileName());
        } catch (ElsaException e) {
            // A file that cannot be understood is reported once, and the session
            // goes on with an empty list rather than refusing to start.
            tasks = new TaskList();
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /**
     * Returns the reminders to show when a session starts, or the empty string
     * when nothing needs the user's attention.
     *
     * <p>Both faces of the chatbot open with this, so they cannot disagree about
     * what to remind the user of. Unlike the "remind" command it says nothing at
     * all when there is nothing to say, so that a start with no pressing deadline
     * looks the same as it did before reminders existed.
     *
     * @return the reminders, or the empty string
     */
    private String getStartupReminders() {
        List<Reminder> reminders = tasks.getReminders(Dates.today(), RemindCommand.DAYS_AHEAD);
        if (reminders.isEmpty()) {
            return "";
        }
        return ui.getRemindersMessage(reminders, RemindCommand.DAYS_AHEAD);
    }

    /**
     * Starts the chatbot.
     *
     * <p>This starts the chatbot in a terminal. The window starts it through
     * {@link elsa.gui.Launcher} instead, and both share the data file named by
     * {@link #Elsa()}.
     *
     * @param args command line arguments, which the chatbot does not use
     */
    public static void main(String[] args) {
        new Elsa().run();
    }
}

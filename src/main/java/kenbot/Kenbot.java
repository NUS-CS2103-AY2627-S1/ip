package kenbot;

import kenbot.task.Deadline;
import kenbot.task.Event;
import kenbot.task.Task;
import kenbot.task.Todo;

/**
 * Runs the Kenbot task-tracking chatbot.
 *
 * <p>Kenbot owns the parts it needs and decides the order they work in:
 * {@link Ui} reads input and prints output, {@link Parser} works out what was
 * asked for, {@link TaskList} holds the tasks, and {@link Storage} keeps them
 * on the hard disk.</p>
 *
 * <p>Commands are read one line at a time until the user types {@code bye} or
 * the input runs out. Anything Kenbot cannot use is reported as a
 * {@link KenbotException} and shown in a single place, so one bad command does
 * not stop the program.</p>
 */
public class Kenbot {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private boolean isExit = false;
    private boolean isLastResponseError = false;
    private String startupNotice = "";

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * @param filePath where the tasks are saved, relative to the working folder
     */
    public Kenbot(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = loadTasks();
    }

    /** Greets the user, then carries out commands until there are no more. */
    public void run() {
        ui.showGreeting();
        if (!startupNotice.isEmpty()) {
            ui.showError(startupNotice);
        }

        // hasNextCommand() is checked first so running out of input ends the
        // loop quietly instead of throwing.
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (input.isEmpty()) {
                continue;
            }
            try {
                if (handleCommand(input)) {
                    break;
                }
            } catch (KenbotException e) {
                // Every error message in the program is shown here.
                ui.showError(e.getMessage());
            }
        }

        ui.close();
    }

    /**
     * Starts Kenbot with the usual save file.
     *
     * @param args command line arguments, which are not used
     */
    public static void main(String[] args) {
        new Kenbot("data/Kenbot.txt").run();
    }

    /**
     * Reads any saved tasks from the hard disk, ready for the user to work with.
     *
     * <p>A list that cannot be read is reported and then ignored, so a problem
     * with the save file leaves Kenbot usable rather than stopping it.</p>
     *
     * @return the saved tasks, or an empty list if there are none to load
     */
    private TaskList loadTasks() {
        try {
            Storage.LoadResult result = storage.load();
            if (result.skippedLines() > 0) {
                startupNotice = "Couldn't read " + result.skippedLines()
                        + " line(s) from your save file, so I skipped them.";
            }
            return new TaskList(result.tasks());
        } catch (KenbotException e) {
            startupNotice = e.getMessage();
            return new TaskList();
        }
    }

    /**
     * Returns anything that went wrong while loading, or an empty string.
     *
     * <p>Recorded rather than printed because loading happens in the
     * constructor, before either front end has anything to show it on, and
     * because the two show it differently: the console prints a block, the
     * window adds a message to the conversation. Printing it here would send it
     * to the terminal, which a window user may not even have open, so a save
     * file that could not be read would look exactly like an empty list.</p>
     *
     * @return the problem to tell the user about, or an empty string if loading
     *         went fine
     */
    public String getStartupNotice() {
        return startupNotice;
    }

    /**
     * Returns the words Kenbot opens with, for a front end that shows its own
     * greeting.
     *
     * @return the welcome message
     */
    public String getGreeting() {
        return ui.getGreeting();
    }

    /**
     * Returns whether the last command handled was a request to exit.
     *
     * <p>The console loop ends itself, but a window has to be told to close, so
     * this reports the decision {@link Parser} already made rather than having
     * the caller work out what {@code bye} means for a second time.</p>
     *
     * @return true if the last command was {@code bye}
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Answers one command without printing anything.
     *
     * <p>This is the entry point used by the graphical front end, which needs
     * the reply as a value it can put in a dialog box rather than as text on a
     * console. Errors come back the same way, because a window has no separate
     * error stream to send them to.</p>
     *
     * @param input the whole line the user typed
     * @return what Kenbot has to say in reply
     */
    public String getResponse(String input) {
        if (input.isBlank()) {
            isLastResponseError = true;
            return "Type something and I'll sort it.";
        }

        try {
            String message = respondTo(Parser.parse(input.trim()));
            isLastResponseError = false;
            return message;
        } catch (KenbotException e) {
            isLastResponseError = true;
            return e.getMessage();
        }
    }

    /**
     * Returns whether the last reply was a refusal rather than a result.
     *
     * <p>The console shows both the same way, but a window can tell them apart
     * visually, and only this class knows which it produced: by the time the
     * caller has the reply it is just a string. Recorded here rather than
     * returned alongside the text so that the console front end, which does not
     * care, is not made to handle a value it would ignore.</p>
     *
     * @return true if the last call to {@link #getResponse(String)} reported a
     *         problem instead of carrying a command out
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Carries out one command typed by the user and prints the reply.
     *
     * @param input the whole line the user typed, already trimmed and not empty
     * @return true if the user asked to exit, false to carry on
     * @throws KenbotException if the command is unknown, its details cannot be
     *         used, or the task list cannot be saved
     */
    private boolean handleCommand(String input) throws KenbotException {
        Parser.ParsedCommand parsed = Parser.parse(input);
        ui.show(respondTo(parsed));
        return isExit;
    }

    /**
     * Works out what one command should say, and saves the result.
     *
     * <p>Both front ends go through here, so the console and the window can
     * never disagree about what a command does. Working out what was asked for
     * is left to {@link Parser}; this method only decides what to do about it.
     * The switch below is an expression rather than a statement on purpose: an
     * expression has to cover every {@link CommandType}, so the compiler
     * reports any command added to that list but not handled here.</p>
     *
     * @param parsed the command and its argument
     * @return the reply to show the user
     * @throws KenbotException if the command's details cannot be used, or the
     *         task list cannot be saved
     */
    private String respondTo(Parser.ParsedCommand parsed) throws KenbotException {
        String argument = parsed.argument();
        isExit = parsed.command() == CommandType.BYE;

        String message = switch (parsed.command()) {
            case BYE -> "Peace! See you soon!";
            case LIST -> tasks.describe();
            case MARK -> markTask(argument);
            case UNMARK -> unmarkTask(argument);
            case TODO -> addTask(Todo.of(argument));
            case DEADLINE -> addTask(Deadline.of(argument));
            case EVENT -> addTask(Event.of(argument));
            case DELETE -> deleteTask(argument);
            case FIND -> tasks.find(argument);
            case TAG -> tagTask(argument);
            case UNTAG -> untagTask(argument);
        };

        // Both front ends show this without checking it first, and the window
        // would render an empty bubble with nothing to explain it. The switch
        // is exhaustive, so only a new command arm returning nothing could
        // break this.
        assert message != null && !message.isBlank()
                : "every command must produce something to show the user";

        // Saved after every command rather than only the ones that change the
        // list: rewriting a file this small costs nothing, and it leaves no way
        // for a change to go unsaved.
        storage.save(tasks);
        return message;
    }

    /**
     * Marks a task as done and describes what changed.
     *
     * @param argument the task number, as the user typed it
     * @return the confirmation to show the user
     * @throws KenbotException if the number is missing, not a number, or out of range
     */
    private String markTask(String argument) throws KenbotException {
        return "Nice, that's done:\n  " + tasks.mark(argument);
    }

    /**
     * Marks a task as not done and describes what changed.
     *
     * @param argument the task number, as the user typed it
     * @return the confirmation to show the user
     * @throws KenbotException if the number is missing, not a number, or out of range
     */
    private String unmarkTask(String argument) throws KenbotException {
        return "Alright, back on the list:\n  " + tasks.unmark(argument);
    }

    /**
     * Attaches tags to a task and describes the result.
     *
     * @param argument the task number and tags, as the user typed them
     * @return the confirmation to show the user
     * @throws KenbotException if the number or any of the tags cannot be used
     */
    private String tagTask(String argument) throws KenbotException {
        return "Tagged:\n  " + tasks.tag(argument);
    }

    /**
     * Removes tags from a task and describes the result.
     *
     * @param argument the task number and tags, as the user typed them
     * @return the confirmation to show the user
     * @throws KenbotException if the number or any of the tags cannot be used
     */
    private String untagTask(String argument) throws KenbotException {
        return "Untagged:\n  " + tasks.untag(argument);
    }

    /**
     * Stores a new task and describes what was added.
     *
     * @param task the task that was just created
     * @return the confirmation to show the user
     * @throws KenbotException if the same task is already in the list
     */
    private String addTask(Task task) throws KenbotException {
        tasks.add(task);
        return "Got it, that's on the list:\n  " + task + "\n" + describeCount();
    }

    /**
     * Returns the running total of tasks.
     *
     * <p>Said in one place because both adding and removing report it, and
     * pluralised because "1 tasks" reads as a bug even though it is only
     * wording.</p>
     *
     * @return the count, as a sentence
     */
    private String describeCount() {
        int count = tasks.size();
        return "That makes " + count + (count == 1 ? " task." : " tasks.");
    }

    /**
     * Removes a task and describes what was removed.
     *
     * @param argument the task number, as the user typed it
     * @return the confirmation to show the user
     * @throws KenbotException if the number is missing, not a number, or out of range
     */
    private String deleteTask(String argument) throws KenbotException {
        Task removed = tasks.delete(argument);
        return "Gone:\n  " + removed + "\n" + describeCount();
    }
}

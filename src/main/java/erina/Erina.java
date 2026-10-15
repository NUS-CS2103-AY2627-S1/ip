package erina;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import erina.task.Task;

/**
 * Entry point of the Erina chatbot.
 *
 * <p>Erina stores to-dos, deadlines and events, lists them with their
 * completion status, marks them done or not done, deletes them, explains
 * what went wrong when a command cannot be carried out, and stops on the
 * {@code bye} command. The task list is saved to the hard disk after
 * every change and loaded back on the next start, so tasks survive
 * between runs.
 *
 * <p>The instructions Erina accepts are listed in {@link Command}. This class
 * only coordinates: {@link Ui} talks to the user, {@link Parser} makes sense
 * of what they typed, {@link TaskList} holds the tasks and {@link Storage}
 * keeps them on disk.
 *
 * <p>Commands are carried out by {@link #getResponse(String)}, which returns
 * what Erina wants to say rather than printing it. Both front ends call it:
 * the console loop in {@link #run()} prints the result, and the GUI shows it
 * in a dialog box. Behaviour therefore cannot drift between the two.
 */
public class Erina {
    /** Where the task list is kept between runs, unless a caller says otherwise. */
    public static final Path DEFAULT_SAVE_FILE = Path.of("data", "erina.txt");

    /** Keeps the task list on disk between runs. */
    private final Storage storage;

    /** Talks to the user. */
    private final Ui ui;

    /** The tasks the user is keeping. */
    private TaskList tasks;

    /** Whether the user has asked to leave. */
    private boolean isExit;

    /** What went wrong while loading, or {@code null} if loading succeeded. */
    private String loadError;

    /**
     * Whether the most recent reply reports a problem rather than a result,
     * so that the GUI can make errors stand out.
     */
    private boolean isLastReplyError;

    /** Creates an Erina that saves its tasks to the {@link #DEFAULT_SAVE_FILE}. */
    public Erina() {
        this(DEFAULT_SAVE_FILE);
    }

    /**
     * Creates an Erina that saves its tasks to the given file.
     *
     * @param saveFile where to keep the task list between runs
     */
    public Erina(Path saveFile) {
        this.storage = new Storage(saveFile);
        this.ui = new Ui();

        // A save file that cannot be understood should not end the program:
        // remember the complaint, and carry on with an empty list. The file is
        // copied aside first, because the user's next change overwrites it.
        try {
            this.tasks = new TaskList(storage.load());
        } catch (ErinaException e) {
            this.loadError = respond(e.getMessage(), backUpUnreadableFile(),
                    "I shall start you on a fresh list instead.");
            this.tasks = new TaskList();
        }
    }

    /**
     * Returns the complaint about the save file, if there was one.
     *
     * @return the message to show at startup, or {@code null} if all was well
     */
    public String getLoadError() {
        return loadError;
    }

    /**
     * Returns whether the user has asked to leave.
     *
     * @return {@code true} once a {@code bye} command has been handled
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns whether the most recent reply from {@link #getResponse(String)}
     * reports a problem, such as a mistyped command.
     *
     * @return {@code true} if the last command could not be carried out
     */
    public boolean isLastReplyError() {
        return isLastReplyError;
    }

    /**
     * Starts Erina with the standard save file location.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        new Erina().run();
    }

    /** Greets the user, serves commands until they leave, then says goodbye. */
    public void run() {
        ui.showWelcome();
        if (loadError != null) {
            ui.reply(loadError.split("\n"));
        }

        // Read one command per line until the user asks to exit.
        while (true) {
            String input = ui.readCommand();
            if (input == null) {
                // Input ended without a bye command, so say goodbye anyway.
                ui.showGoodbye();
                break;
            }

            // A blank line is not worth an error message; just read the next one.
            if (input.trim().isEmpty()) {
                continue;
            }

            ui.reply(getResponse(input).split("\n"));
            if (isExit) {
                break;
            }
        }
    }

    /**
     * Carries out one command and returns what Erina wants to say about it.
     *
     * <p>Returning the reply instead of printing it is what lets the console
     * and the GUI share this method: each decides for itself how to show the
     * result. Failures are returned as their message rather than thrown, so a
     * bad command never ends the conversation.
     *
     * @param input one line as typed by the user
     * @return the reply, whose lines are separated by newlines
     */
    public String getResponse(String input) {
        isLastReplyError = false;
        if (input.isBlank()) {
            return "";
        }

        try {
            ParsedCommand parsed = Parser.parse(input);
            assert parsed.command() != null : "Parser.parse returns a command or throws";
            if (parsed.command() == Command.BYE) {
                Parser.checkNoArgument(Command.BYE, parsed.argument());
                isExit = true;
                return Ui.FAREWELL;
            }

            String response = handleCommand(parsed.command(), parsed.argument());

            // Saving after every successful command, in one place, keeps
            // the file in step with the list without each command having
            // to remember to save.
            storage.save(tasks.asList());
            return response;
        } catch (ErinaException e) {
            isLastReplyError = true;
            return e.getMessage();
        }
    }

    /**
     * Joins the lines of a reply into the single string the callers expect.
     *
     * <p>Declared with varargs so a reply reads as its lines, one per
     * argument, in the same shape as {@link Ui#reply(String...)}. Callers
     * holding an array of lines can pass it directly.
     *
     * @param lines the lines of the reply, in order
     * @return the lines separated by newlines
     */
    private static String respond(String... lines) {
        return String.join("\n", lines);
    }

    /**
     * Keeps a copy of a save file that could not be loaded, and says where.
     *
     * @return the sentence telling the user what happened to their file
     */
    private String backUpUnreadableFile() {
        try {
            return "I have kept a copy at " + storage.backUp() + ", should you wish to repair it.";
        } catch (ErinaException e) {
            return e.getMessage();
        }
    }

    /**
     * Carries out one command from the user.
     *
     * @param command  the command the user asked for
     * @param argument everything after the command word, possibly empty
     * @return what Erina wants to say about it
     * @throws ErinaException if the argument is missing or does not make sense
     */
    private String handleCommand(Command command, String argument) throws ErinaException {
        // getResponse deals with bye itself, because it also has to stop the loop.
        assert command != Command.BYE : "bye must be handled before reaching handleCommand";
        assert argument != null : "argument is empty rather than null when absent";

        switch (command) {
            case LIST:
                Parser.checkNoArgument(command, argument);
                return listTasks();
            case MARK:
                return setDone(argument, true);
            case UNMARK:
                return setDone(argument, false);
            case TODO:
                return addTask(Parser.parseTodo(argument));
            case DEADLINE:
                return addTask(Parser.parseDeadline(argument));
            case EVENT:
                return addTask(Parser.parseEvent(argument));
            case DELETE:
                return deleteTask(argument);
            case FIND:
                return findTasks(argument);
            case HELP:
                Parser.checkNoArgument(command, argument);
                return showHelp();
            default:
                // BYE is handled by getResponse, which has to stop the loop, and
                // unknown words never reach here: Command.fromKeyword rejects them.
                // So this is a programming error, not something to tell the user.
                throw new IllegalStateException("Unhandled command: " + command);
        }
    }

    /**
     * Lists every command Erina understands, with how to type it.
     *
     * <p>The list comes from {@link Command#describeAll()}, so it is always
     * in step with the commands that actually exist.
     *
     * @return the help text
     */
    private String showHelp() {
        return respond(
                "Here is everything I can do for you:",
                Command.describeAll(),
                "Kindly write dates as yyyy-mm-dd, like 2019-10-15.");
    }

    /**
     * Adds an already-built task to the list and confirms it to the user.
     *
     * @param task the task to add
     * @return the confirmation to show
     * @throws ErinaException if the list already holds the same task
     */
    private String addTask(Task task) throws ErinaException {
        // A second copy of a task is almost always a mistyped repeat, and
        // would leave the user unsure which one to mark or delete.
        if (tasks.contains(task)) {
            throw new ErinaException(respond(
                    "Pardon me, but that task is already in your list:",
                    "  " + task));
        }

        int sizeBefore = tasks.size();
        tasks.add(task);
        assert tasks.size() == sizeBefore + 1 : "adding a task must grow the list by one";
        return respond(
                "Noted. I've put this in order for you:",
                "  " + task,
                describeSize());
    }

    /**
     * Removes the task at the given position and confirms it to the user.
     *
     * @param argument the task number as typed by the user, counting from 1
     * @return the confirmation to show
     * @throws ErinaException if the number is missing, not a number, or does
     *                        not refer to an existing task
     */
    private String deleteTask(String argument) throws ErinaException {
        // Remove returns the task it took out, so it can be shown to the user
        // after it is no longer in the list.
        Task removed = tasks.remove(Parser.parseIndex(argument, tasks.size()));
        return respond(
                "Very well. I've struck this from your list:",
                "  " + removed,
                describeSize());
    }

    /**
     * Marks the task at the given position as done or not done.
     *
     * <p>If the task is already in the requested state, nothing changes and
     * the user is told so, rather than being shown a misleading confirmation.
     *
     * @param argument the task number as typed by the user, counting from 1
     * @param isDone   {@code true} to mark done, {@code false} to mark not done
     * @return the confirmation to show
     * @throws ErinaException if the number is missing, not a number, or does
     *                        not refer to an existing task
     */
    private String setDone(String argument, boolean isDone) throws ErinaException {
        Task task = tasks.get(Parser.parseIndex(argument, tasks.size()));

        if (task.isDone() == isDone) {
            String state = isDone ? "already marked as done:" : "already not done:";
            return respond("No change needed. That task is " + state, "  " + task);
        }

        if (isDone) {
            task.markAsDone();
            return respond("Splendid. I've marked this as done:", "  " + task);
        }
        task.markAsNotDone();
        return respond("Very well, I've reopened this task:", "  " + task);
    }

    /**
     * Shows the tasks whose descriptions contain the given keyword.
     *
     * <p>Matches are numbered from 1 by their position among the matches,
     * not their position in the full list, mirroring how the course sample
     * output presents them.
     *
     * @param argument the text to search for
     * @return the matching tasks, or a message saying there were none
     * @throws ErinaException if no text to search for was given
     */
    private String findTasks(String argument) throws ErinaException {
        if (argument.isEmpty()) {
            throw new ErinaException("Pardon me. Please tell me what to look for, "
                    + "like: find book");
        }

        List<Task> matches = tasks.find(argument);
        if (matches.isEmpty()) {
            return "Nothing in your list mentions \"" + argument + "\".";
        }
        return numberedList("These are the tasks that match:", matches);
    }

    /**
     * Shows every task added so far, numbered from 1.
     *
     * <p>Shows a prompt instead of an empty block when nothing has been added.
     *
     * @return the numbered task list, or a prompt if there is nothing to show
     */
    private String listTasks() {
        if (tasks.isEmpty()) {
            return "Your list is empty. A clean slate, how refreshing.";
        }
        return numberedList("Here is your list, in impeccable order:", tasks.asList());
    }

    /**
     * Says how many tasks the list now holds, as a grammatical sentence.
     *
     * @return for example {@code "Your list now holds 1 task."}
     */
    private String describeSize() {
        int size = tasks.size();
        return "Your list now holds " + size + (size == 1 ? " task." : " tasks.");
    }

    /**
     * Formats tasks as a heading followed by one numbered line per task.
     *
     * <p>Every command that shows several tasks goes through here, so they
     * all number and lay out tasks the same way. An IntStream over the
     * positions gives the number and the task together, which a plain
     * stream over the tasks cannot do.
     *
     * @param heading the line shown above the tasks
     * @param tasks   the tasks to show, in the order they should appear
     * @return the heading and the numbered tasks, one per line
     */
    private static String numberedList(String heading, List<Task> tasks) {
        Stream<String> numbered = IntStream.range(0, tasks.size())
                // Users count from 1, so display position i as i + 1.
                .mapToObj(i -> (i + 1) + "." + tasks.get(i));
        return respond(Stream.concat(Stream.of(heading), numbered).toArray(String[]::new));
    }
}

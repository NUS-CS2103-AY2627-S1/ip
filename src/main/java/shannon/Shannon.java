package shannon;

import java.util.List;

import shannon.exception.DuplicateTaskException;
import shannon.exception.InvalidTaskNumberException;
import shannon.exception.ShannonException;
import shannon.exception.StorageException;
import shannon.exception.TaskNotFoundException;
import shannon.exception.UnexpectedArgumentException;
import shannon.exception.UnknownCommandException;
import shannon.task.Task;

/**
 * The Shannon chatbot: a to-do list you talk to.
 * <p>
 * This class is only the conductor. It holds the collaborators that do the actual work and
 * decides, for each command, which of them to ask:
 * <ul>
 *   <li>{@link Ui} &mdash; the wording of every reply, and the terminal</li>
 *   <li>{@link Parser} &mdash; making sense of a typed line</li>
 *   <li>{@link TaskList} &mdash; the tasks, and the rules for reaching them</li>
 *   <li>{@link Storage} &mdash; loading from and saving to the file</li>
 * </ul>
 * They are instance fields rather than statics, so a Shannon is a thing you can make: two with
 * different save files would not interfere, and a test could point one at a scratch file.
 * <p>
 * There are two ways in, and they share all their logic. {@link #run()} is the terminal version:
 * read a line, print the reply, repeat. {@link #getResponse(String)} answers a single line and
 * hands the reply back as a {@link Response}, which is what the JavaFX window in
 * {@code shannon.gui} uses.
 * Because {@code run()} is written in terms of {@code getResponse}, the two interfaces cannot
 * drift apart.
 */
public class Shannon {

    /** Where the task list is saved by default, relative to the project root. */
    private static final String DATA_FILE_PATH = "./data/shannon.txt";

    /** The command word that ends the conversation. */
    private static final String EXIT_COMMAND = "bye";

    /** Builds the wording of every reply, and prints them when running in the terminal. */
    private final Ui ui;

    /** Reads and writes the save file. */
    private final Storage storage;

    /** The tasks. Not final: {@link #getStartupMessage()} replaces it with the restored list. */
    private TaskList tasks;

    /**
     * Sets up the chatbot without touching the disk or the screen.
     * <p>
     * The save file is deliberately <em>not</em> read here. Loading has to report what it found,
     * and those messages belong after the greeting, so reading the file is part of
     * {@link #getStartupMessage()}; the empty list built here is what the program falls back on
     * if that reading fails.
     *
     * @param filePath the save file to use, e.g. {@code ./data/shannon.txt}.
     */
    public Shannon(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList();
    }

    /**
     * Sets up the chatbot with the default save file.
     * A convenience for the GUI, which has no reason to choose a different file.
     */
    public Shannon() {
        this(DATA_FILE_PATH);
    }

    /**
     * Greets the user, restores the saved tasks, then answers commands until {@code bye}, or
     * until the input runs out. This is the terminal version of the chatbot.
     */
    public void run() {
        ui.showLine();
        ui.showLogo();
        ui.showMessage(getStartupMessage());
        ui.showLine();

        // The input can end without a bye: the user may press Ctrl-D, or a file piped in as
        // input may run out. Checking before each read avoids the crash that reading past the
        // end would cause.
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showLine();
            // The terminal shows every reply the same way, so it wants only the words; the
            // window also asks whether they are an error, and styles them accordingly.
            ui.showMessage(getResponse(input).text());
            ui.showLine();
            if (isExitCommand(input)) {
                return;
            }
        }

        // The input ended without a bye, so say goodbye anyway rather than stop mid-conversation.
        ui.showLine();
        ui.showMessage(ui.getGoodbyeMessage());
        ui.showLine();
    }

    /**
     * Returns the reply to one line the user typed.
     * <p>
     * This is the whole chatbot reduced to a single function: text in, text out. Nothing here
     * prints, so the caller is free to put the answer in a terminal, in a chat bubble, or in a
     * test assertion. Every handler reports problems by throwing a {@link ShannonException}, so
     * all failures are turned into a message in this one place instead of being scattered around.
     * <p>
     * Because that one place is here, this is also the only place that knows a reply is a
     * complaint rather than a confirmation, which is why a {@link Response} is returned instead
     * of a bare string: the caller should not have to read the words to find that out.
     *
     * @param input one line exactly as the user typed it.
     * @return what the chatbot says in response, and whether it is an error.
     */
    public Response getResponse(String input) {
        try {
            return Response.of(handleCommand(new Parser(input)));
        } catch (ShannonException e) {
            return Response.ofError(ui.getErrorMessage(e.getMessage()));
        }
    }

    /**
     * Returns the greeting together with a report of what was restored from the save file, and
     * replaces {@link #tasks} with those restored tasks.
     * <p>
     * Any problem loading is reported and then ignored: a chatbot that refuses to start because
     * of a damaged save file is less useful than one that starts empty and says so. The empty
     * list built by the constructor stays in place in that case, so there is always a list to use.
     *
     * @return everything the user should see before typing their first command.
     */
    public String getStartupMessage() {
        StringBuilder message = new StringBuilder(ui.getWelcomeMessage());
        try {
            tasks = new TaskList(storage.load());
            appendIfPresent(message,
                    ui.getLoadedMessage(tasks.size()),
                    ui.getSkippedLinesMessage(storage.getSkippedLineCount(), storage.getFilePath()));
        } catch (StorageException e) {
            appendIfPresent(message, ui.getErrorMessage(e.getMessage()));
        }
        // Whether the file was partly or wholly unreadable, say how what could not be loaded has
        // been kept safe, so the user knows it is not gone.
        appendIfPresent(message, ui.getBackupMessage(
                storage.getFilePath(), storage.getBackupFilePath(), storage.isSaveBlocked()));
        return message.toString();
    }

    /**
     * Returns whether a line the user typed asks to end the conversation.
     * The GUI needs to know this so it can close the window afterwards.
     * <p>
     * Only a {@code bye} with nothing after it counts. {@link #handleCommand(Parser)} refuses
     * {@code bye now} as a mistyped command, and this must agree with it, or the window would
     * close straight after showing that error.
     *
     * @param input one line exactly as the user typed it.
     * @return {@code true} if the line is the {@code bye} command on its own.
     */
    public boolean isExitCommand(String input) {
        Parser parser = new Parser(input);
        return EXIT_COMMAND.equals(parser.getCommand()) && !parser.hasArgument();
    }

    /**
     * Adds messages to what has been built so far, one per line, skipping the empty ones.
     * The {@code get...Message} methods return an empty string when they have nothing to report,
     * and this keeps those non-reports from leaving blank lines behind.
     * <p>
     * Varargs, so the start-up report can be listed in one call in the order it should read.
     *
     * @param message   what has been built so far.
     * @param additions the messages to add, in order; any of them may be empty.
     */
    private static void appendIfPresent(StringBuilder message, String... additions) {
        for (String addition : additions) {
            if (!addition.isEmpty()) {
                message.append("\n").append(addition);
            }
        }
    }

    /**
     * Carries out one command the user typed and returns the reply to it.
     * <p>
     * This is the whole vocabulary of the chatbot in one place: each branch names a command and
     * the one thing it does. Pulling the text apart has already happened in {@link Parser}, so
     * nothing here touches a string beyond comparing the command word.
     *
     * @param parser the line the user typed, already split up.
     * @return the reply to that command.
     * @throws UnknownCommandException if the command word is not one we know.
     * @throws ShannonException        if the command was understood but could not be carried out.
     */
    private String handleCommand(Parser parser) throws ShannonException {
        return switch (parser.getCommand()) {
            case EXIT_COMMAND -> sayGoodbye(parser);
            case "list" -> listTasks(parser);
            // find only reads the list, so unlike the commands below it does not save afterwards.
            case "find" -> ui.getFoundTasksMessage(tasks.find(parser.parseKeyword()));
            case "mark" -> markTasks(parser, true);
            case "unmark" -> markTasks(parser, false);
            case "delete" -> deleteTasks(parser);
            case "todo" -> addTask(parser.parseTodo());
            case "deadline" -> addTask(parser.parseDeadline());
            case "event" -> addTask(parser.parseEvent());
            default -> throw new UnknownCommandException(parser.getCommand());
        };
    }

    /**
     * Handles {@code bye}, which takes nothing after it.
     *
     * @param parser the line the user typed, already split up.
     * @return the goodbye to show the user.
     * @throws UnexpectedArgumentException if anything was typed after {@code bye}.
     */
    private String sayGoodbye(Parser parser) throws UnexpectedArgumentException {
        parser.checkNoArgument();
        return ui.getGoodbyeMessage();
    }

    /**
     * Handles {@code list}, which takes nothing after it.
     *
     * @param parser the line the user typed, already split up.
     * @return every task, numbered, or a note that the list is empty.
     * @throws UnexpectedArgumentException if anything was typed after {@code list}.
     */
    private String listTasks(Parser parser) throws UnexpectedArgumentException {
        parser.checkNoArgument();
        return ui.getTaskListMessage(tasks.asList());
    }

    /**
     * Stores an already-built task, writes the whole list to disk, and returns the confirmation.
     * Every {@code todo}/{@code deadline}/{@code event} command funnels through here so the
     * confirmation message and the save live in exactly one place.
     *
     * @param task the task to add.
     * @return the confirmation to show the user.
     * @throws DuplicateTaskException if the list already has a task with the same details.
     * @throws StorageException       if the task was added but could not be saved to disk.
     */
    private String addTask(Task task) throws ShannonException {
        tasks.add(task);
        String message = ui.getTaskAddedMessage(task, tasks.size());
        // Saved after the message is built, so a failure here replaces nothing the user has
        // already been told: the task really is in the list, and only the disk copy is missing.
        storage.save(tasks.asList());
        return message;
    }

    /**
     * Handles {@code delete <task number>...}, which may name more than one task.
     *
     * @param parser the line the user typed, already split up.
     * @return the confirmation to show the user.
     * @throws InvalidTaskNumberException if a task number is missing or is not a whole number.
     * @throws TaskNotFoundException      if a number does not match any task in the list.
     * @throws StorageException           if the shortened list could not be saved to disk.
     */
    private String deleteTasks(Parser parser) throws ShannonException {
        // The numbers reach TaskList as varargs, so the one call covers "delete 2" and
        // "delete 2 5 7". deleteTasks hands back what it removed, so the confirmation can still
        // show what went.
        List<Task> deletedTasks = tasks.deleteTasks(parser.parseTaskNumbers());
        String message = ui.getTaskDeletedMessage(deletedTasks, tasks.size());
        storage.save(tasks.asList());
        return message;
    }

    /**
     * Handles both {@code mark} and {@code unmark}, which differ only in the flag they set, and
     * which may name more than one task.
     *
     * @param parser the line the user typed, already split up.
     * @param isDone {@code true} for {@code mark}, {@code false} for {@code unmark}.
     * @return the confirmation to show the user.
     * @throws InvalidTaskNumberException if a task number is missing or is not a whole number.
     * @throws TaskNotFoundException      if a number does not match any task in the list.
     * @throws StorageException           if the changed list could not be saved to disk.
     */
    private String markTasks(Parser parser, boolean isDone) throws ShannonException {
        // Every number is checked before any task changes, so a line naming one bad number
        // leaves the whole list as it was.
        List<Task> markedTasks = tasks.getTasks(parser.parseTaskNumbers());
        for (Task task : markedTasks) {
            if (isDone) {
                task.markDone();
            } else {
                task.unmarkDone();
            }
        }

        String message = ui.getTaskMarkedMessage(markedTasks, isDone);
        storage.save(tasks.asList());
        return message;
    }

    /**
     * Starts the chatbot in the terminal, with the default save file.
     * The GUI has its own entry point in {@code shannon.gui.Launcher}.
     *
     * @param args command line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Shannon(DATA_FILE_PATH).run();
    }
}

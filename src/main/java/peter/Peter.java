package peter;

import java.time.LocalDate;

/**
 * The main class for Peter, a task manager chatbot with both a
 * command-line and a JavaFX interface.
 */
public class Peter {
    private static final String DATA_FILE_PATH = "./data/peter.txt";
    private static final String DIVIDER = "____________________________________________________________";

    private final Storage storage;
    private final Ui ui;
    private final String loadingMessage;
    private final boolean isLoadingError;
    private TaskList tasks;
    private boolean isExit = false;

    /**
     * Peter's response to one line of input, and whether it represents an
     * error (an unrecognised command or invalid input) rather than a normal
     * reply, so callers like the GUI can style the two differently.
     *
     * @param text the response text to show the user
     * @param isError whether this response is an error message
     */
    public record Response(String text, boolean isError) {
    }

    /** Creates Peter, loading any previously saved tasks from the default data file. */
    public Peter() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates Peter, loading any previously saved tasks from the given file.
     *
     * @param filePath the location of the task data file
     */
    public Peter(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        String message;
        boolean loadFailed;
        try {
            tasks = new TaskList(storage.load());
            message = ui.showLoadingResult(tasks.size());
            loadFailed = false;
        } catch (PeterException exception) {
            message = ui.showLoadingError();
            tasks = new TaskList();
            loadFailed = true;
        }
        loadingMessage = message;
        isLoadingError = loadFailed;
    }

    /** Runs Peter's command-line interface until the user exits. */
    public void run() {
        printBoxed(ui.showWelcome());
        printBoxed(loadingMessage);
        while (!isExit) {
            String input = ui.readCommand();
            printBoxed(getResponse(input).text());
        }
        ui.close();
    }

    /**
     * Processes one line of user input and returns Peter's response, for use
     * by the GUI. Also usable directly by any caller that only needs the
     * response text.
     *
     * @param input the raw line entered by the user
     * @return Peter's response, and whether it is an error message
     */
    public Response getResponse(String input) {
        try {
            Parser.ParsedInput parsedInput = Parser.parseInput(input);
            if (parsedInput.getCommand() == Command.BYE) {
                isExit = true;
            }
            return new Response(handleCommand(parsedInput), false);
        } catch (PeterException exception) {
            return new Response(exception.getMessage(), true);
        }
    }

    /**
     * Returns Peter's greeting message.
     *
     * @return the greeting message
     */
    public String getWelcomeMessage() {
        return ui.showWelcome();
    }

    /**
     * Returns the message produced while loading saved tasks at startup.
     *
     * @return the loading message
     */
    public String getLoadingMessage() {
        return loadingMessage;
    }

    /**
     * Returns whether saved tasks failed to load at startup, so the loading
     * message can be styled as an error rather than a normal reply.
     *
     * @return {@code true} if the loading message is an error message
     */
    public boolean isLoadingError() {
        return isLoadingError;
    }

    /**
     * Returns whether the most recent command requests that Peter exits.
     *
     * @return {@code true} once a {@code bye} command has been processed
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Handles one parsed command, mutating the task list and returning the
     * response to show the user.
     *
     * @param parsedInput the command and arguments to act on
     * @return the response to show the user
     * @throws PeterException if the command's arguments are invalid
     */
    private String handleCommand(Parser.ParsedInput parsedInput) throws PeterException {
        String arguments = parsedInput.getArguments();
        switch (parsedInput.getCommand()) {
            case BYE:
                return ui.showGoodbye();
            case LIST:
                return ui.showTaskList(tasks.getAll());
            case MARK: {
                int index = Parser.parseTaskIndex(arguments);
                Task task = tasks.mark(index);
                storage.save(tasks.getAll());
                return ui.showTaskMarked(task);
            }
            case UNMARK: {
                int index = Parser.parseTaskIndex(arguments);
                Task task = tasks.unmark(index);
                storage.save(tasks.getAll());
                return ui.showTaskUnmarked(task);
            }
            case DELETE: {
                int index = Parser.parseTaskIndex(arguments);
                Task removedTask = tasks.delete(index);
                storage.save(tasks.getAll());
                return ui.showTaskRemoved(removedTask, tasks.size());
            }
            case TODO:
                return addTaskAndRespond(Parser.parseTodo(arguments));
            case DEADLINE:
                return addTaskAndRespond(Parser.parseDeadline(arguments));
            case EVENT:
                return addTaskAndRespond(Parser.parseEvent(arguments));
            case SCHEDULE: {
                LocalDate requestedDate = Parser.parseDate(arguments);
                return ui.showSchedule(tasks.getScheduleForDate(requestedDate), requestedDate);
            }
            case FIND: {
                String keyword = Parser.parseFindKeyword(arguments);
                return ui.showFoundTasks(tasks.findTasks(keyword));
            }
            default:
                throw new PeterException("Not even my spidey-sense can make sense of that one, pal. Try again?");
        }
    }

    /**
     * Adds a task to the list, persists the updated list, and returns
     * Peter's confirmation message. Shared by the todo, deadline, and event
     * commands, which otherwise repeated this exact three-step sequence.
     *
     * @param task the task to add
     * @return the task-added confirmation message
     * @throws PeterException if an equal task is already in the list
     */
    private String addTaskAndRespond(Task task) throws PeterException {
        tasks.add(task);
        storage.save(tasks.getAll());
        return ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Prints a message between divider lines, for the command-line interface.
     *
     * @param message the message to print
     */
    private void printBoxed(String message) {
        System.out.println("     " + DIVIDER);
        System.out.println(message);
        System.out.println("     " + DIVIDER);
    }

    /**
     * Starts Peter's command-line interface.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Peter(DATA_FILE_PATH).run();
    }
}

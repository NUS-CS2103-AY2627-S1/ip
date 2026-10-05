package ev;

import ev.task.Task;

/**
 * Runs E.V., a Spiderman-themed chatbot that reads and executes user commands.
 */
public class EV {
    private static final String DEFAULT_DATA_PATH = "data/ev.txt";
    private final Ui ui;
    private final Storage storage;
    private final String loadError;
    private TaskList tasks;

    /**
     * Constructs E.V. backed by the default data file, loading any saved tasks.
     */
    public EV() {
        this.ui = new Ui();
        this.storage = new Storage(DEFAULT_DATA_PATH);

        TaskList loaded;
        String error;

        try {
            Storage.LoadResult result = storage.load();

            loaded = new TaskList(result.tasks());
            error = result.skippedLineCount() == 0
                    ? null
                    : ui.getSkippedLineWarning(result.skippedLineCount());
        } catch (EvException e) {
            loaded = new TaskList();
            error = e.getMessage();
        }

        this.tasks = loaded;
        this.loadError = error;
    }

    /**
     * Starts E.V.'s command line interface.
     *
     * @param args command line arguments; not used.
     */
    public static void main(String[] args) {
        new EV().runCommandLoop();
    }

    /**
     * Returns the greeting E.V. shows on startup, including any problem loading saved tasks.
     *
     * @return the startup message.
     */
    public String getGreeting() {
        String welcome = ui.getWelcome();
        return loadError == null ? welcome : welcome + "\n" + loadError;
    }

    /**
     * Returns E.V.'s reply to one line of user input.
     *
     * @param input the raw text the user entered.
     * @return the text E.V. replies with.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parseCommand(input);

            if (command == Command.BYE) {
                Parser.requireNoArguments("bye", Parser.parseArguments(input));
                return ui.getGoodbye();
            }

            String response = execute(command, Parser.parseArguments(input));

            if (command.isMutating()) {
                storage.save(tasks);
            }

            return response;
        } catch (EvException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns whether the given input ends the conversation.
     *
     * @param input the raw text the user entered.
     * @return true if the input is the bye command.
     */
    public boolean isExit(String input) {
        try {
            return Parser.parseCommand(input) == Command.BYE
                    && Parser.parseArguments(input).isEmpty();
        } catch (EvException e) {
            return false;
        }
    }

    /**
     * Reads and executes user commands on the console until the bye command is given.
     */
    private void runCommandLoop() {
        System.out.println(getGreeting());

        while (ui.hasCommand()) {
            String input = ui.readCommand();
            String response = getResponse(input);

            System.out.println(response);

            if (isExit(input)) {
                break;
            }
        }

        ui.close();
    }

    /**
     * Returns the result of carrying out a single command against the task registry.
     *
     * @param command the command to carry out.
     * @param arguments the text following the command word.
     * @return the message describing what happened.
     * @throws EvException if the command's arguments are invalid.
     */
    private String execute(Command command, String arguments) throws EvException {
        switch (command) {
            case LIST:
                Parser.requireNoArguments("list", arguments);
                return ui.getList(tasks);
            case MARK:
                return markTask(arguments);
            case UNMARK:
                return unmarkTask(arguments);
            case DELETE:
                return deleteTask(arguments);
            case TODO:
                return addTask(Parser.parseTodo(arguments));
            case DEADLINE:
                return addTask(Parser.parseDeadline(arguments));
            case EVENT:
                return addTask(Parser.parseEvent(arguments));
            case FIND:
                return ui.getFound(tasks, tasks.findPositions(Parser.parseSearchKeyword(arguments)));
            case SORT:
                Parser.requireNoArguments("sort", arguments);
                return ui.getSorted(tasks, tasks.sortedPositions());
            case HELP:
                Parser.requireNoArguments("help", arguments);
                return ui.getHelp();
            default:
                throw new EvException("E.V. does not know how to run that command yet.");
        }
    }

    /**
     * Adds a task to the registry and returns the confirmation message.
     *
     * @param task the task to add.
     * @return the confirmation message.
     */
    private String addTask(Task task) {
        assert task != null : "Parser must return a task or throw; never null.";

        tasks.add(task);
        return ui.getAdded(task, tasks.size());
    }

    /**
     * Marks the task named by the arguments as done.
     *
     * @param arguments the text following the command word.
     * @return the confirmation message.
     * @throws EvException if the arguments name no existing task.
     */
    private String markTask(String arguments) throws EvException {
        Task task = tasks.get(Parser.parseTaskIndex(arguments, tasks.size()));

        task.markAsDone();

        assert task.getStatusIcon().equals("X") : "A task just marked done must report the done icon.";

        return ui.getMarked(task);
    }

    /**
     * Marks the task named by the arguments as not done.
     *
     * @param arguments the text following the command word.
     * @return the confirmation message.
     * @throws EvException if the arguments name no existing task.
     */
    private String unmarkTask(String arguments) throws EvException {
        Task task = tasks.get(Parser.parseTaskIndex(arguments, tasks.size()));

        task.markAsNotDone();
        return ui.getUnmarked(task);
    }

    /**
     * Removes the task named by the arguments from the registry.
     *
     * @param arguments the text following the command word.
     * @return the confirmation message.
     * @throws EvException if the arguments name no existing task.
     */
    private String deleteTask(String arguments) throws EvException {
        Task removed = tasks.remove(Parser.parseTaskIndex(arguments, tasks.size()));

        return ui.getRemoved(removed, tasks.size());
    }
}

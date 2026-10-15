package percy;

/**
 * The core of the Percy task-tracking chatbot.
 *
 * <p>Percy's work is split across four helper classes; this class wires them
 * together:
 * <ul>
 *   <li>{@link Ui} &ndash; formats responses (and reads input in CLI mode)</li>
 *   <li>{@link Storage} &ndash; loads and saves tasks to disk</li>
 *   <li>{@link TaskList} &ndash; holds the tasks in memory</li>
 *   <li>{@link Parser} &ndash; turns raw input into structured commands</li>
 * </ul>
 *
 * <p>It can be driven two ways: {@link #run()} for the command-line loop, or
 * {@link #getResponse(String)} one command at a time from the JavaFX GUI
 * ({@link Main} / {@link MainWindow}).
 */
public class Percy {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean isExit = false;
    private boolean lastResponseWasError = false;

    /**
     * Creates a Percy instance that persists tasks to {@code filePath}.
     * If the existing save file cannot be read, Percy reports the problem and
     * starts with an empty task list rather than crashing.
     */
    public Percy(String filePath) {
        assert filePath != null : "save-file path should not be null";
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Runs the command-line read&ndash;evaluate&ndash;respond loop, printing each
     * response, until the user types {@code bye}.
     */
    public void run() {
        ui.showWelcome();
        System.out.print(ui.flush());

        while (!isExit) {
            String input = ui.readCommand();
            try {
                isExit = execute(Parser.parse(input));
            } catch (PercyException e) {
                ui.showError(e.getMessage());
            }
            System.out.print(ui.flush());
        }
    }

    /**
     * Handles one command entered in the GUI.
     *
     * @param input the raw command line typed by the user
     * @return Percy's response text
     */
    public String getResponse(String input) {
        lastResponseWasError = false;
        try {
            isExit = execute(Parser.parse(input));
        } catch (PercyException e) {
            lastResponseWasError = true;
            ui.showError(e.getMessage());
        }
        return ui.flush().strip();
    }

    /**
     * Returns whether the most recent {@link #getResponse} call reported an
     * error (e.g. an unknown command or bad argument). The GUI uses this to
     * highlight that reply so mistakes are easy to spot.
     *
     * @return true if the last response was an error message
     */
    public boolean wasLastResponseError() {
        return lastResponseWasError;
    }

    /**
     * Returns the greeting shown when the GUI first opens.
     *
     * @return the greeting text
     */
    public String getWelcome() {
        return "Hello! I'm Percy.\nWhat can I do for you?";
    }

    /**
     * Returns whether the user has ended the session with {@code bye}.
     *
     * @return true once Percy should exit
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Carries out a single parsed command: updates the task list, saves to
     * disk when the list changed, and asks the {@link Ui} to report the result.
     *
     * @return true if Percy should exit after this command.
     * @throws PercyException if the command's arguments are invalid.
     */
    private boolean execute(Parser.Command command) throws PercyException {
        assert command != null : "parsed command should not be null";
        switch (command.getType()) {
        case BYE:
            ui.showGoodbye();
            return true;
        case LIST:
            ui.showTaskList(tasks);
            return false;
        case FIND:
            ui.showMatchingTasks(tasks.find(requireKeyword(command.getArguments())));
            return false;
        case MARK:
            ui.showMarked(setDoneStatus(command.getArguments(), true));
            return false;
        case UNMARK:
            ui.showUnmarked(setDoneStatus(command.getArguments(), false));
            return false;
        case DELETE: {
            Task removed = tasks.remove(resolveIndex(command.getArguments()));
            storage.save(tasks);
            ui.showRemoved(removed, tasks.size());
            return false;
        }
        case TODO:
            return addTask(Parser.parseTodo(command.getArguments()));
        case DEADLINE:
            return addTask(Parser.parseDeadline(command.getArguments()));
        case EVENT:
            return addTask(Parser.parseEvent(command.getArguments()));
        case UPDATE:
            ui.showUpdated(applyUpdate(Parser.parseUpdate(command.getArguments())));
            return false;
        default:
            throw new PercyException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    /** Adds a task, persists the list, and reports it. Always keeps Percy running. */
    private boolean addTask(Task task) {
        assert task != null : "task to add should not be null";
        tasks.add(task);
        storage.save(tasks);
        ui.showAdded(task, tasks.size());
        return false;
    }

    /**
     * Marks the task referred to by {@code arguments} as done or not done, saves
     * the change, and returns the affected task.
     *
     * @throws PercyException if the task number is missing or out of range
     */
    private Task setDoneStatus(String arguments, boolean done) throws PercyException {
        Task task = tasks.get(resolveIndex(arguments));
        task.setDone(done);
        storage.save(tasks);
        return task;
    }

    /**
     * Applies an {@code update} to an existing task in place, persists the
     * change, and returns the updated task.
     *
     * @throws PercyException if the task number is out of range, or a date
     *     field does not apply to that task's type
     */
    private Task applyUpdate(Parser.UpdateSpec spec) throws PercyException {
        if (!tasks.isValidIndex(spec.getIndex())) {
            throw new PercyException("OOPS!!! That task number doesn't exist.");
        }
        Task task = tasks.get(spec.getIndex());

        // Validate first, so a rejected update leaves the task untouched.
        if (spec.getBy() != null && !(task instanceof Deadline)) {
            throw new PercyException("OOPS!!! Only a deadline has a '/by' date.");
        }
        if ((spec.getFrom() != null || spec.getTo() != null) && !(task instanceof Event)) {
            throw new PercyException("OOPS!!! Only an event has '/from' and '/to' dates.");
        }

        if (spec.getDescription() != null) {
            task.setDescription(spec.getDescription());
        }
        if (spec.getBy() != null) {
            ((Deadline) task).setBy(spec.getBy());
        }
        if (spec.getFrom() != null) {
            ((Event) task).setFrom(spec.getFrom());
        }
        if (spec.getTo() != null) {
            ((Event) task).setTo(spec.getTo());
        }

        storage.save(tasks);
        return task;
    }

    /**
     * Converts a 1-based task number typed by the user into a valid 0-based
     * index into the current list.
     *
     * @throws PercyException if the number is missing, non-numeric, or out of range.
     */
    private int resolveIndex(String arguments) throws PercyException {
        int index = Parser.parseIndex(arguments);
        if (!tasks.isValidIndex(index)) {
            throw new PercyException("OOPS!!! That task number doesn't exist.");
        }
        assert tasks.isValidIndex(index) : "resolveIndex must return an in-range index";
        return index;
    }

    /**
     * Returns the search keyword typed after {@code find}.
     *
     * @throws PercyException if no keyword was given.
     */
    private String requireKeyword(String arguments) throws PercyException {
        if (arguments.isEmpty()) {
            throw new PercyException("OOPS!!! Please tell me what keyword to search for.");
        }
        return arguments;
    }

    /**
     * Starts Percy, saving tasks to {@code ./data/percy.txt}.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        new Percy("./data/percy.txt").run();
    }
}

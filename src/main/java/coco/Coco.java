package coco;

/**
 * Entry point and main loop of the Coco chatbot. Wires together a Ui,
 * Storage, and TaskList, and dispatches each parsed command to them.
 */
public class Coco {
    private final Storage storage;
    private final Ui ui;
    private final TaskList tasks;

    /**
     * Creates a Coco instance backed by the data file at the given path,
     * loading any tasks already saved there.
     *
     * @param filePath Path to the data file used to save/load tasks.
     */
    public Coco(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Runs the chatbot's read-print loop until the user says "bye". Each
     * command's actual logic lives in getResponse(); this just prints its
     * result, so the CLI and the GUI never have two copies of that logic.
     */
    public void run() {
        ui.showWelcome();
        while (true) {
            String input = ui.readCommand();
            if (Parser.parseCommand(input) == Command.BYE) {
                break;
            }
            ui.showLine();
            System.out.println(getResponse(input));
            ui.showLine();
        }
        ui.close();
        ui.showGoodbye();
    }

    /**
     * Returns the greeting shown when a GUI session starts.
     *
     * @return The greeting text.
     */
    public String greet() {
        return ui.greetingMessage();
    }

    /**
     * Parses and executes one command, returning Coco's reply. Used
     * directly by the GUI (one call per message) and by run() (which
     * prints whatever this returns).
     *
     * @param input Raw user input line.
     * @return Coco's reply.
     */
    public String getResponse(String input) {
        Command command = Parser.parseCommand(input);
        if (command == Command.BYE) {
            return ui.goodbyeMessage();
        }
        try {
            switch (command) {
            case LIST:
                return ui.taskListMessage(tasks);
            case MARK:
                return handleMark(input);
            case UNMARK:
                return handleUnmark(input);
            case DELETE:
                return handleDelete(input);
            case TODO:
                return addTask(Parser.parseTodo(input));
            case DEADLINE:
                return addTask(Parser.parseDeadline(input));
            case EVENT:
                return addTask(Parser.parseEvent(input));
            case FIND:
                return ui.findResultsMessage(tasks.find(Parser.parseFind(input)));
            case COMMANDS:
                return ui.commandsMessage();
            default:
                throw new CocoException("Whoa, lost me there, chief. Try somethin' else?");
            }
        } catch (CocoException e) {
            return e.getMessage();
        }
    }

    /**
     * Marks the referenced task done, unless it's a recurring deadline - in
     * which case it advances to its next occurrence instead.
     *
     * @param input Raw user input line.
     * @return The confirmation message.
     * @throws CocoException If the task number is missing, invalid, or out
     *                       of range.
     */
    private String handleMark(String input) throws CocoException {
        int index = Parser.parseIndex(input, "mark", tasks.size());
        Task task = tasks.get(index);
        if (task instanceof Deadline deadline && deadline.isRecurring()) {
            deadline.advanceToNextOccurrence();
            storage.save(tasks.asList());
            return ui.taskRecurredMessage(deadline);
        }
        task.markAsDone();
        storage.save(tasks.asList());
        return ui.taskMarkedMessage(task);
    }

    /**
     * Marks the referenced task not done.
     *
     * @param input Raw user input line.
     * @return The confirmation message.
     * @throws CocoException If the task number is missing, invalid, or out
     *                       of range.
     */
    private String handleUnmark(String input) throws CocoException {
        int index = Parser.parseIndex(input, "unmark", tasks.size());
        tasks.get(index).markAsNotDone();
        storage.save(tasks.asList());
        return ui.taskUnmarkedMessage(tasks.get(index));
    }

    /**
     * Removes the referenced task from the list.
     *
     * @param input Raw user input line.
     * @return The confirmation message.
     * @throws CocoException If the task number is missing, invalid, or out
     *                       of range.
     */
    private String handleDelete(String input) throws CocoException {
        int index = Parser.parseIndex(input, "delete", tasks.size());
        Task removed = tasks.remove(index);
        storage.save(tasks.asList());
        return ui.taskRemovedMessage(removed, tasks.size());
    }

    /**
     * Adds a task to the list, persists the updated list, and returns the
     * addition message.
     *
     * @param task Task to add.
     * @return The confirmation message.
     */
    private String addTask(Task task) {
        tasks.add(task);
        storage.save(tasks.asList());
        return ui.taskAddedMessage(task, tasks.size());
    }

    /**
     * Starts the chatbot, saving/loading tasks from ./data/coco.txt.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        new Coco("data/coco.txt").run();
    }
}

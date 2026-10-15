package bubba;

/**
 * Coordinates user input, task operations and persistence for Bubba.
 */
public class Bubba {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private boolean isExitRequested;
    private boolean isLastResponseError;

    /**
     * Initializes the chatbot and loads saved tasks.
     *
     * @param filePath Path used to load and save tasks.
     */
    public Bubba(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
        isExitRequested = false;
        isLastResponseError = false;
    }

    /**
     * Processes commands until the user exits or input ends, then closes console input.
     */
    public void run() {
        ui.showMessage(getWelcomeMessage());
        try {
            String input;
            while ((input = ui.readCommand()) != null) {
                ui.showMessage(getResponse(input));
                if (isExitRequested) {
                    return;
                }
            }
        } finally {
            ui.close();
        }
    }

    /**
     * Returns the greeting and any warnings produced while loading saved tasks.
     *
     * @return Initial message for a user interface.
     */
    public String getWelcomeMessage() {
        StringBuilder message = new StringBuilder(ui.getWelcomeMessage());
        for (String warning : storage.getLoadWarnings()) {
            message.append('\n').append(ui.getErrorMessage(warning));
        }
        return message.toString();
    }

    /**
     * Processes one command and returns the response for display by any user interface.
     *
     * @param input Command entered by the user.
     * @return Bubba's response, including validation or storage errors.
     */
    public String getResponse(String input) {
        isLastResponseError = false;
        try {
            Parser.ParsedCommand command = Parser.parse(input);
            Task task;
            switch (command.type()) {
                case EXIT:
                    isExitRequested = true;
                    return ui.getGoodbyeMessage();
                case LIST:
                    return ui.getTaskListMessage(tasks);
                case HELP:
                    return ui.getHelpMessage();
                case FIND:
                    return ui.getMatchingTasksMessage(tasks.find(command.keyword()));
                case ADD:
                    task = command.task();
                    tasks.add(task);
                    saveTasks();
                    return ui.getTaskAddedMessage(task, tasks.size());
                case DELETE:
                    task = tasks.delete(command.taskNumber());
                    saveTasks();
                    return ui.getTaskDeletedMessage(task, tasks.size());
                case MARK:
                    // Fallthrough
                case UNMARK:
                    boolean isDone = command.type() == Parser.Type.MARK;
                    task = tasks.mark(command.taskNumber(), isDone);
                    saveTasks();
                    return ui.getTaskMarkedMessage(task, isDone);
                default:
                    throw new AssertionError("Unexpected command type: " + command.type());
            }
        } catch (BubbaException e) {
            isLastResponseError = true;
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /**
     * Returns whether the most recent response reports an error.
     *
     * @return {@code true} if processing the latest command failed.
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Returns whether the most recent valid command requested that Bubba exit.
     *
     * @return {@code true} after a valid {@code bye} command.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    private void saveTasks() throws BubbaException {
        storage.save(tasks.toList());
    }

    /**
     * Starts Bubba with its save file relative to the working directory.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Bubba("./data/bubba.txt").run();
    }
}

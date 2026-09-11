package lynn;

/** Coordinates the chatbot's user interface, task list, parser, and storage. */
public class Lynn {
    private final Storage storage;

    private final TaskList tasks;

    private final Ui ui;

    /** Creates Lynn and loads any previously saved task list. */
    public Lynn() {
        this(new Ui(), new Storage());
    }

    /** Creates Lynn with the given user interface. */
    Lynn(Ui ui) {
        this(ui, new Storage());
    }

    /** Creates Lynn with injectable collaborators so storage and UI behavior can be tested in isolation. */
    Lynn(Ui ui, Storage storage) {
        this.storage = storage;
        this.ui = ui;
        TaskList loadedTasks;
        try {
            loadedTasks = storage.load();
        } catch (LynnException e) {
            ui.showLoadingError(e.getMessage());
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /** Processes one command and writes its response through Lynn's user interface. */
    boolean respondTo(String command) {
        try {
            return handleCommand(command);
        } catch (LynnException e) {
            ui.showError(e.getMessage());
            return false;
        }
    }

    /** Runs Lynn until the user enters the bye command. */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            try {
                if (handleCommand(command)) {
                    return;
                }
            } catch (LynnException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /** Routes a user command to the matching operation. */
    private boolean handleCommand(String command) throws LynnException {
        String commandWord = Parser.getCommandWord(command);
        if (commandWord.isEmpty()) {
            throw new LynnException("Please enter a command. Try help to see what I can do.");
        }
        switch (commandWord) {
        case "bye":
            Parser.requireExactCommand(command, "bye");
            ui.showGoodbye();
            return true;
        case "list":
            Parser.requireExactCommand(command, "list");
            ui.showTaskList(tasks);
            return false;
        case "help":
            Parser.requireExactCommand(command, "help");
            ui.showHelp();
            return false;
        case "find":
            findTasks(command);
            return false;
        case "mark":
            markTask(command, true);
            return false;
        case "unmark":
            markTask(command, false);
            return false;
        case "delete":
            deleteTask(command);
            return false;
        default:
            addTask(command);
            return false;
        }
    }

    /** Finds all tasks whose descriptions contain the supplied keyword. */
    private void findTasks(String command) throws LynnException {
        String keyword = Parser.parseFindKeyword(command);
        Task[] matchingTasks = tasks.find(keyword);
        ui.showMatchingTasks(matchingTasks);
    }

    /** Parses, adds, saves, and displays a new task. */
    private void addTask(String command) throws LynnException {
        Task task = Parser.parseTask(command);
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (LynnException e) {
            tasks.delete(tasks.size() - 1);
            throw e;
        }
        ui.showTaskAdded(task, tasks.size());
    }

    /** Updates a task's completion state, then saves and displays it. */
    private void markTask(String command, boolean isDone) throws LynnException {
        String commandWord = Parser.getCommandWord(command);
        int taskNumber = Parser.parseTaskNumber(command, commandWord, tasks.size());
        Task task = tasks.get(taskNumber);
        boolean previousState = task.isDone();
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            storage.save(tasks);
        } catch (LynnException e) {
            if (previousState) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw e;
        }
        if (isDone) {
            ui.showTaskMarked(task);
        } else {
            ui.showTaskUnmarked(task);
        }
    }

    /** Removes the requested task, then saves and displays it. */
    private void deleteTask(String command) throws LynnException {
        int taskNumber = Parser.parseTaskNumber(command, "delete", tasks.size());
        Task deletedTask = tasks.delete(taskNumber);
        try {
            storage.save(tasks);
        } catch (LynnException e) {
            tasks.insert(taskNumber, deletedTask);
            throw e;
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

    /**
     * Starts the command-line application.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Lynn().run();
    }
}

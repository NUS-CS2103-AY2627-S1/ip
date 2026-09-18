package habi;

import java.nio.file.Path;

/** Coordinates HABI's UI, parser, task list, and storage components. */
public class Habi {
    private static final String FAILED_LOAD_MUTATION_ERROR =
            "OOPS! I could not load tasks from the data file. Fix the file before making changes.";

    private final Storage storage;
    private final TaskList tasks;
    private final NoteList notes;
    private final Ui ui;
    /** Error reported while loading data at startup, or {@code null} when loading succeeded. */
    private final String startupError;

    /**
     * Creates HABI and loads tasks from the specified data file.
     *
     * @param filePath Path of the data file used for persistence.
     */
    public Habi(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        NoteList loadedNotes;
        String loadError = null;
        try {
            HabiData loadedData = storage.loadData();
            loadedTasks = new TaskList(loadedData.getTasks());
            loadedNotes = new NoteList(loadedData.getNotes());
        } catch (HabiException exception) {
            loadError = exception.getMessage();
            loadedTasks = new TaskList();
            loadedNotes = new NoteList();
        }
        tasks = loadedTasks;
        notes = loadedNotes;
        startupError = loadError;
    }

    /**
     * Reads and executes commands until input ends or the user enters {@code bye}.
     */
    public void run() {
        ui.showGreeting();
        if (startupError != null) {
            ui.showResponse(startupError);
        }
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showResponse(getResponse(command).split("\\R", -1));
            if (command.trim().equals("bye")) {
                break;
            }
        }
    }

    /**
     * Executes one command and returns the response for display by any UI.
     *
     * @param command Command entered by the user.
     * @return HABI's response, including validation or storage errors.
     */
    public String getResponse(String command) {
        String trimmedCommand = command.trim();
        try {
            return handleCommand(trimmedCommand);
        } catch (HabiException exception) {
            return exception.getMessage();
        }
    }

    private String handleCommand(String command) throws HabiException {
        String keyword = Parser.getKeyword(command);
        return switch (keyword) {
            case "list" -> listTasks(command);
            case "notes" -> listNotes(command);
            case "bye" -> sayGoodbye(command);
            case "mark" -> updateTaskStatus(command, true);
            case "unmark" -> updateTaskStatus(command, false);
            case "delete" -> deleteTask(command);
            case "delete-note" -> deleteNote(command);
            case "find" -> Ui.formatTaskList("Here are the matching tasks in your list:",
                    tasks.find(Parser.parseFindKeyword(command)));
            case "todo" -> addTask(Parser.parseTodo(command));
            case "deadline" -> addTask(Parser.parseDeadline(command));
            case "event" -> addTask(Parser.parseEvent(command));
            case "note" -> addNote(Parser.parseNote(command));
            default -> throw new HabiException(
                    "OOPS! I don't know what \"" + command + "\" means.");
        };
    }

    /**
     * Returns the current task list after validating that {@code list} is argument-free.
     *
     * @param command Complete user command.
     * @return Formatted task list.
     * @throws HabiException If an argument was supplied.
     */
    private String listTasks(String command) throws HabiException {
        Parser.requireNoArguments(command, "list");
        return Ui.formatTaskList("Here are the tasks in your list:", tasks.asList());
    }

    /**
     * Returns the current note list after validating that {@code notes} is argument-free.
     *
     * @param command Complete user command.
     * @return Formatted note list.
     * @throws HabiException If an argument was supplied.
     */
    private String listNotes(String command) throws HabiException {
        Parser.requireNoArguments(command, "notes");
        return Ui.formatNoteList("Here are the notes in your list:", notes.asList());
    }

    /**
     * Returns HABI's farewell after validating that {@code bye} is argument-free.
     *
     * @param command Complete user command.
     * @return Existing HABI farewell text.
     * @throws HabiException If an argument was supplied.
     */
    private String sayGoodbye(String command) throws HabiException {
        Parser.requireNoArguments(command, "bye");
        return "Bye for now. Small steps build better days—see you soon!";
    }

    private String updateTaskStatus(String command, boolean shouldMark) throws HabiException {
        ensureDataLoaded();
        String keyword = shouldMark ? "mark" : "unmark";
        int taskIndex = Parser.parseTaskIndex(command, keyword, tasks.size());
        Task task = tasks.get(taskIndex);
        String response;
        if (shouldMark) {
            task.markAsDone();
            response = Ui.formatResponse("Nice! I've marked this task as done:", "  " + task);
        } else {
            task.markAsNotDone();
            response = Ui.formatResponse(
                    "OK, I've marked this task as not done yet:", "  " + task);
        }
        saveData();
        return response;
    }

    private String deleteTask(String command) throws HabiException {
        ensureDataLoaded();
        int taskIndex = Parser.parseTaskIndex(command, "delete", tasks.size());
        Task removedTask = tasks.delete(taskIndex);
        saveData();
        return Ui.formatResponse("Noted. I've removed this task:", "  " + removedTask,
                getTaskCountMessage());
    }

    private String addTask(Task task) throws HabiException {
        ensureDataLoaded();
        tasks.add(task);
        saveData();
        return Ui.formatResponse("Got it. I've added this task:", "  " + task,
                getTaskCountMessage());
    }

    private String addNote(Note note) throws HabiException {
        ensureDataLoaded();
        notes.add(note);
        saveData();
        return Ui.formatResponse("Got it. I've added this note:", "  " + note,
                getNoteCountMessage());
    }

    private String deleteNote(String command) throws HabiException {
        ensureDataLoaded();
        int noteIndex = Parser.parseNoteIndex(command, notes.size());
        Note removedNote = notes.delete(noteIndex);
        saveData();
        return Ui.formatResponse("Noted. I've removed this note:", "  " + removedNote,
                getNoteCountMessage());
    }

    private void saveData() throws HabiException {
        storage.save(new HabiData(tasks.asList(), notes.asList()));
    }

    /**
     * Stops changes after a failed startup load so the unreadable data file is never overwritten.
     *
     * @throws HabiException If HABI could not load its data at startup.
     */
    private void ensureDataLoaded() throws HabiException {
        if (startupError != null) {
            throw new HabiException(FAILED_LOAD_MUTATION_ERROR);
        }
    }

    /**
     * Returns the startup storage error for a user interface to display after its greeting.
     *
     * @return Startup error text, or {@code null} when data loaded successfully.
     */
    public String getStartupError() {
        return startupError;
    }

    private String getTaskCountMessage() {
        return "Now you have " + tasks.size() + " task"
                + (tasks.size() == 1 ? "" : "s") + " in the list.";
    }

    private String getNoteCountMessage() {
        return "Now you have " + notes.size() + " note"
                + (notes.size() == 1 ? "" : "s") + " in the list.";
    }

    /**
     * Starts HABI using its relative data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Habi(Path.of("data", "habi.txt")).run();
    }
}

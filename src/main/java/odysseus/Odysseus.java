package odysseus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import odysseus.parser.Command;
import odysseus.parser.Parser;
import odysseus.storage.Storage;
import odysseus.task.Task;
import odysseus.task.TaskList;
import odysseus.ui.Ui;

/**
 * The main chatbot: reads commands, manages tasks, produces responses.
 */
public class Odysseus {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private boolean isExit = false;
    private boolean lastResponseWasError = false;

    private static final String MARK_MSG = "Done. One less thing between us and home:%n  %s";
    private static final String UNMARK_MSG = "Undone. It waits for us again:%n  %s";
    private static final String BYE_MSG = "Enough for today. The tide will turn.";
    public static final String DEFAULT_STORAGE = "data/odysseus.txt";
    private static final String ADD_MSG = "Marked. The road grows longer:%n  %s%n%d trials stand between us and Ithaca.";
    private static final String UPDATE_MSG = "The course corrects. Task %d:%n  %s";

    /**
     * Constructs Odysseus cli backed by a given filePath.
     *
     * @param filePath string representing file path of storage
     */
    public Odysseus(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TaskList loaded;
        try {
            loaded = new TaskList(storage.load());
        } catch (OdysseusException e) {
            ui.showError(e);
            loaded = new TaskList();
        }
        tasks = loaded;
    }

    /**
     * Starts the chatting interface.
     */
    public void run() {
        ui.showWelcome();
        while (!this.isExit) {
            String input = ui.readCommand();
            String response = this.getResponse(input);
            ui.show(response);
        }
    }

    /**
     * Returns the greeting shown when a session starts. Delegates to the
     * {@link Ui} so the message text has a single source of truth shared by
     * the CLI and the GUI.
     *
     * @return the greeting text
     */
    public String getWelcome() {
        return ui.getWelcome();
    }

    /**
     * Processes a single user command and returns Odysseus's textual response.
     * This is the seam the GUI calls: it hands over a raw input line and
     * receives back the text to display, instead of anything being printed.
     *
     * @param input the raw command line entered by the user
     * @return the response text to show
     */
    public String getResponse(String input) {
        lastResponseWasError = false;
        try {
            if (input.isBlank()) {
                throw new OdysseusException("Say something. Silence won't get us home.");
            }
            // Normalise surrounding and repeated whitespace so sloppy spacing
            // (e.g. leading spaces or "mark  2") is tolerated. Splitting on \s+
            // collapses any run of whitespace into a single delimiter.
            String trimmedInput = input.strip();
            String[] inputSplit = trimmedInput.split("\\s+");
            String command = inputSplit[0];
            String rest = trimmedInput.substring(command.length()).trim();

            return switch (Command.fromInput(command)) {
                case Command.BYE -> handleBye();
                case Command.LIST -> formatTasks(tasks.getTasks());
                case Command.MARK -> handleMark(inputSplit);
                case Command.UNMARK -> handleUnmark(inputSplit);
                case Command.TODO -> handleAdd(Parser.parseTodo(rest));
                case Command.DEADLINE -> handleAdd(Parser.parseDeadline(rest));
                case Command.EVENT -> handleAdd(Parser.parseEvent(rest));
                case Command.DELETE -> handleDelete(inputSplit);
                case Command.ON -> handleOn(rest);
                case Command.FIND -> handleFind(rest);
                case Command.UPDATE -> handleUpdate(inputSplit, rest);
                case Command.UNKNOWN -> {
                    throw new OdysseusException("I don't know that command. Try again.");
                }
            };
        } catch (OdysseusException e) {
            lastResponseWasError = true;
            return e.getMessage();
        }
    }

    /**
     * Returns whether the most recent {@link #getResponse(String)} call produced
     * an error message rather than a normal reply. The GUI uses this to style
     * error responses distinctly.
     *
     * @return true if the last response was an error
     */
    public boolean wasLastResponseError() {
        return lastResponseWasError;
    }

    /**
     * Handles the {@code bye} command: flags the session to end.
     *
     * @return the farewell message to display
     */
    private String handleBye() {
        this.isExit = true;
        return BYE_MSG;
    }

    /**
     * Handles the {@code mark} command: marks the task at the given index as
     * done and persists the change.
     *
     * @param inputSplit the whitespace-split command line
     * @return the confirmation message to display
     * @throws OdysseusException if the index is missing or out of range
     */
    private String handleMark(String[] inputSplit) throws OdysseusException {
        int idx = Parser.parseIndex(inputSplit, tasks.size());
        Task markedTask = tasks.mark(idx);
        storage.save(tasks.getTasks());
        return String.format(MARK_MSG, markedTask);
    }

    /**
     * Handles the {@code unmark} command: marks the task at the given index as
     * not done and persists the change.
     *
     * @param inputSplit the whitespace-split command line
     * @return the confirmation message to display
     * @throws OdysseusException if the index is missing or out of range
     */
    private String handleUnmark(String[] inputSplit) throws OdysseusException {
        int idx = Parser.parseIndex(inputSplit, tasks.size());
        Task unmarkedTask = tasks.unmark(idx);
        storage.save(tasks.getTasks());
        return String.format(UNMARK_MSG, unmarkedTask);
    }

    /**
     * Handles the {@code delete} command: removes the task at the given index
     * and persists the change.
     *
     * @param inputSplit the whitespace-split command line
     * @return the confirmation message to display
     * @throws OdysseusException if the index is missing or out of range
     */
    private String handleDelete(String[] inputSplit) throws OdysseusException {
        int idx = Parser.parseIndex(inputSplit, tasks.size());
        Task removedTask = tasks.remove(idx);
        storage.save(tasks.getTasks());
        return String.format(
                "Gone. We don't look back:%n  %s%n%d remain.",
                removedTask, tasks.size());
    }

    /**
     * Handles the {@code on} command: lists the tasks occurring on the given
     * date.
     *
     * @param rest the command arguments after the keyword (the date, {@code yyyy-mm-dd})
     * @return the formatted list of tasks on that date
     * @throws OdysseusException if the date is empty or cannot be parsed
     */
    private String handleOn(String rest) throws OdysseusException {
        if (rest.isEmpty()) {
            throw new OdysseusException("Which day? Give me a date.");
        }
        try {
            LocalDate onDate = LocalDate.parse(rest);
            return formatTasks(tasks.on(onDate));
        } catch (DateTimeParseException e) {
            throw new OdysseusException("That date won't hold. Use YYYY-MM-DD.");
        }
    }

    /**
     * Handles the {@code find} command: lists the tasks whose descriptions
     * contain the given keyword.
     *
     * @param rest the command arguments after the keyword (the search term)
     * @return the formatted list of matching tasks
     * @throws OdysseusException if the keyword is empty
     */
    private String handleFind(String rest) throws OdysseusException {
        if (rest.isEmpty()) {
            throw new OdysseusException("What word am I looking for?");
        }
        return formatTasks(tasks.find(rest));
    }

    /**
     * Handles the {@code todo}, {@code deadline}, and {@code event} commands:
     * adds the parsed task to the list and persists the change.
     *
     * @param toAdd the task produced by the parser
     * @return the confirmation message to display
     * @throws OdysseusException if the task cannot be saved
     */
    private String handleAdd(Task toAdd) throws OdysseusException {
        if (tasks.hasDuplicate(toAdd)) {
            throw new OdysseusException("Already on the list. We don't repeat mistakes.");
        }
        tasks.add(toAdd);
        storage.save(tasks.getTasks());
        return String.format(ADD_MSG, toAdd, tasks.size());
    }

    /**
     * Handles the {@code update} command: edits one field of an existing task
     * in place and persists the change.
     *
     * @param inputSplit the whitespace-split command line (used to read the index)
     * @param rest the command arguments after the keyword ({@code INDEX /FLAG NEW_VALUE})
     * @return the confirmation message to display
     * @throws OdysseusException if the index is invalid or the flag/value is missing or unsupported
     */
    private String handleUpdate(String[] inputSplit, String rest) throws OdysseusException {
        int idx = Parser.parseIndex(inputSplit, tasks.size());   // reuse: validates + throws on bad index
        String[] parts = rest.split(" ", 3);       // [index, flag, value]
        if (parts.length < 3 || parts[2].isBlank()) {
            throw new OdysseusException("Say it plainly: update INDEX /desc|/by|/from|/to NEW_VALUE");
        }
        String flag = parts[1];
        String value = parts[2];
        Task updated = tasks.update(idx, flag, value);
        storage.save(tasks.getTasks());
        return String.format(UPDATE_MSG, idx + 1, updated);
    }

    /**
     * Returns whether the user has ended the conversation (typed {@code bye}).
     *
     * @return true if the chat session should close
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Formats a list of tasks into a numbered, newline-separated string for
     * display.
     *
     * @param list the tasks to format
     * @return the numbered list, or a placeholder message if the list is empty
     */
    private String formatTasks(List<Task> list) {
        if (list.isEmpty()) {
            return "Nothing remains. For now.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append((i + 1) + ". " + list.get(i) + "\n");
        }
        return sb.toString();
    }

    /**
     * The main entry point for the application.
     *
     * @param args command-line arguments passed to the program
     */
    public static void main(String[] args) {
        new Odysseus(DEFAULT_STORAGE).run();
    }
}

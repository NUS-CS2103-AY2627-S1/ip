package zabud.session;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import zabud.tasks.Task;

/**
 * Stores all mutable information that belongs to one running Zabud session.
 * Future session-level state, such as storage settings, can be added here.
 */
public final class Session {
    /**
     * Maximum number of commands retained for interactive recall.
     */
    public static final int COMMAND_HISTORY_LIMIT = 1000;
    /**
     * Relative path used for persistent session data.
     */
    public static final Path DEFAULT_DATA_PATH = Path.of("data", "zabud.txt");
    /**
     * The task list managed during this session.
     */
    private final TaskList taskList;

    /**
     * File used to persist this session.
     */
    private final Path dataPath;

    /**
     * Storage helper responsible for the session data file.
     */
    private final SessionStorage storage = new SessionStorage();
    /**
     * Commands entered during this and previous sessions, in chronological order.
     */
    private final List<String> commandHistory = new ArrayList<>();
    /**
     * Current position used while navigating command history.
     */
    private int historyCursor;

    /**
     * Creates a session using {@link #DEFAULT_DATA_PATH}.
     */
    public Session() {
        this(DEFAULT_DATA_PATH);
    }

    /**
     * Creates a session and loads its tasks and command history from the supplied path.
     *
     * @param dataPath relative or absolute path containing persisted session data.
     */
    public Session(Path dataPath) {
        this.dataPath = dataPath;
        SessionStorage.SessionData data = storage.load(dataPath);
        taskList = new TaskList();
        for (Task task : data.tasks()) {
            taskList.addLoaded(task);
        }
        int firstHistoryIndex = Math.max(0, data.commandHistory().size() - COMMAND_HISTORY_LIMIT);
        commandHistory.addAll(data.commandHistory().subList(firstHistoryIndex, data.commandHistory().size()));
        historyCursor = commandHistory.size();
        taskList.setChangeListener(this::save);
    }

    /**
     * Returns the task list managed by this session.
     *
     * @return the current session's task list.
     */
    public TaskList getTaskList() {
        return taskList;
    }

    /**
     * Records and persists a command, retaining only the most recent 1000 entries.
     *
     * @param command command entered by the user.
     */
    public void recordCommand(String command) {
        if (command.isBlank()) {
            return;
        }
        commandHistory.add(command);
        if (commandHistory.size() > COMMAND_HISTORY_LIMIT) {
            commandHistory.remove(0);
        }
        historyCursor = commandHistory.size();
        save();
    }

    /**
     * Moves backward through command history.
     *
     * @return the previous command, or the oldest command when already at the beginning.
     */
    public String previousCommand() {
        if (historyCursor > 0) {
            historyCursor--;
        }
        return historyCursor < commandHistory.size() ? commandHistory.get(historyCursor) : "";
    }

    /**
     * Moves forward through command history.
     *
     * @return the next command, or an empty string after the newest command.
     */
    public String nextCommand() {
        if (historyCursor < commandHistory.size()) {
            historyCursor++;
        }
        return historyCursor < commandHistory.size() ? commandHistory.get(historyCursor) : "";
    }

    /**
     * Resets navigation so the next previous request starts at the newest command.
     */
    public void resetHistoryNavigation() {
        historyCursor = commandHistory.size();
    }

    /**
     * Saves current session data and reports a user-friendly error if it fails.
     */
    public void save() {
        try {
            storage.save(dataPath, taskList, commandHistory);
        } catch (IOException exception) {
            System.out.println(" I could not save your session: " + exception.getMessage());
        }
    }
}

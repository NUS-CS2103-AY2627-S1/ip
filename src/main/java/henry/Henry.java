package henry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import henry.exception.HenryException;
import henry.exception.InvalidDateException;
import henry.parser.CommandType;
import henry.parser.Parser;
import henry.storage.Storage;
import henry.task.Task;
import henry.task.TaskList;
import henry.ui.Ui;

/**
 * Starts the Henry chatbot application.
 */
public class Henry {
    private static final Path DEFAULT_DATA_FILE_PATH = Path.of("data", "henry.txt");
    private static final String GOODBYE_MESSAGE = "That's all for now. Take care out there.";
    private static final String HELP_MESSAGE = String.join("\n",
            "Here are the commands I can help with:",
            "todo DESCRIPTION - Add a todo.",
            "deadline DESCRIPTION /by DATE - Add a deadline.",
            "event DESCRIPTION /from START_DATE /to END_DATE - Add an event.",
            "list - Show all tasks.",
            "find KEYWORD - Find tasks by description.",
            "mark TASK_NUMBER - Mark a task as completed.",
            "unmark TASK_NUMBER - Mark a task as not completed.",
            "delete TASK_NUMBER - Delete a task.",
            "help - Show this command list.",
            "bye - Exit Henry.");

    private final Storage storage;
    private final TaskList tasks;
    private final String startupMessage;

    private CommandType lastCommandType = CommandType.UNKNOWN;

    /**
     * Creates Henry using the default data file.
     */
    public Henry() {
        this(DEFAULT_DATA_FILE_PATH);
    }

    /**
     * Creates Henry using the specified data file.
     *
     * @param dataFilePath path of the file used to persist tasks.
     */
    public Henry(Path dataFilePath) {
        storage = new Storage(dataFilePath);

        TaskList loadedTasks;
        String loadingMessage = "";
        try {
            Storage.LoadResult result = storage.load();
            loadedTasks = new TaskList(result.tasks());
            if (result.skippedLineCount() > 0) {
                int skippedLineCount = result.skippedLineCount();
                String recordLabel = skippedLineCount == 1 ? "record" : "records";
                loadingMessage = "I skipped " + skippedLineCount + " malformed task "
                        + recordLabel + " while loading " + dataFilePath + ".";
            }
        } catch (IOException e) {
            loadedTasks = new TaskList();
            loadingMessage = "I couldn't load your saved tasks, so we're starting with an empty "
                    + "list.";
        }
        tasks = loadedTasks;
        startupMessage = loadingMessage;
    }

    /**
     * Greets the user, stores tasks, updates or deletes tasks, lists saved tasks, and exits
     * when the user enters bye.
     *
     * @param args command-line arguments; not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Henry henry = new Henry();
        ui.showWelcome();
        if (!henry.getStartupMessage().isEmpty()) {
            ui.showMessage(henry.getStartupMessage());
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showMessage(henry.getResponse(command));
            if (henry.getLastCommandType() == CommandType.BYE) {
                return;
            }
        }
    }

    /**
     * Returns any warning produced while loading saved tasks.
     *
     * @return loading warning, or an empty string when loading succeeded normally.
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Returns the type of the most recently processed command.
     *
     * @return most recent command type, or {@link CommandType#UNKNOWN} before processing begins.
     */
    public CommandType getLastCommandType() {
        return lastCommandType;
    }

    /**
     * Processes a user command and returns Henry's response.
     *
     * @param input user command to process.
     * @return response suitable for display in either the console or GUI.
     */
    public String getResponse(String input) {
        String command = input.trim();
        CommandType commandType = Parser.parseCommandType(command);
        lastCommandType = commandType;

        try {
            return switch (commandType) {
                case BYE -> GOODBYE_MESSAGE;
                case HELP -> HELP_MESSAGE;
                case LIST -> formatTaskList("Here's what's ahead:", tasks.asList());
                case FIND -> findTasks(command);
                case MARK, UNMARK -> updateTaskStatus(command, commandType);
                case DELETE -> deleteTask(command);
                case TODO, DEADLINE, EVENT -> addTask(
                        Parser.parseTask(command, commandType));
                case UNKNOWN -> throw new HenryException(
                        "I'm not quite sure what you mean. Try todo, deadline, event, list, find, "
                                + "mark, unmark, delete, help, or bye.");
            };
        } catch (InvalidDateException e) {
            return e.getMessage();
        } catch (HenryException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "I couldn't save that change. Your task list is unchanged.";
        }
    }

    private String addTask(Task task) throws IOException {
        int originalTaskCount = tasks.size();
        tasks.add(task);
        assert tasks.size() == originalTaskCount + 1
                : "Adding a task should increase the task count by one";
        saveOrRollback(() -> {
            tasks.delete(tasks.size() - 1);
            assert tasks.size() == originalTaskCount
                    : "A failed addition should restore the original task count";
        });
        return "Got it. I've added this to our route:\n" + task
                + "\n" + formatTaskCount();
    }

    private String deleteTask(String command) throws HenryException, IOException {
        int taskIndex = Parser.parseTaskIndex(command, CommandType.DELETE, tasks.size());
        int originalTaskCount = tasks.size();
        Task removedTask = tasks.delete(taskIndex);
        assert tasks.size() == originalTaskCount - 1
                : "Deleting a task should reduce the task count by one";
        saveOrRollback(() -> {
            tasks.add(taskIndex, removedTask);
            assert tasks.size() == originalTaskCount
                    : "A failed deletion should restore the original task count";
        });
        return "All right, I've cleared this from the list:\n" + removedTask
                + "\n" + formatTaskCount();
    }

    private String findTasks(String command) throws HenryException {
        List<Task> matchingTasks = tasks.find(Parser.parseKeyword(command));
        if (matchingTasks.isEmpty()) {
            return "I couldn't find a task for that keyword";
        }
        return formatTaskList("I found these matching tasks:", matchingTasks);
    }

    private String updateTaskStatus(String command, CommandType commandType)
            throws HenryException, IOException {
        int taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        boolean isDone = commandType == CommandType.MARK;
        tasks.setDone(taskIndex, isDone);
        assert task.isDone() == isDone
                : "Updating a task should apply the requested completion status";
        saveOrRollback(() -> {
            tasks.setDone(taskIndex, wasDone);
            assert task.isDone() == wasDone
                    : "A failed status update should restore the original status";
        });

        if (isDone) {
            return "Nice, that one's done.\n" + task;
        }
        return "No worries. I've put this back on the trail:\n" + task;
    }

    private String formatTaskCount() {
        String taskLabel = tasks.size() == 1 ? "task" : "tasks";
        return "You now have " + tasks.size() + " " + taskLabel + " on the list.";
    }

    private void saveOrRollback(Runnable rollbackAction) throws IOException {
        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            rollbackAction.run();
            throw e;
        }
    }

    private static String formatTaskList(String heading, List<Task> displayedTasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < displayedTasks.size(); i++) {
            response.append("\n").append(i + 1).append(". ").append(displayedTasks.get(i));
        }
        return response.toString();
    }
}

package duchess;

import java.time.Clock;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import duchess.parser.Parser;
import duchess.storage.Storage;
import duchess.task.Task;
import duchess.task.TaskList;
import duchess.task.TaskStatistics;
import duchess.ui.Ui;

/** Coordinates Duchess's task commands and provides responses to user interfaces. */
public class Duchess {
    private static final String COMMAND_TYPE_ADD = "add";
    private static final String COMMAND_TYPE_BYE = "bye";
    private static final String COMMAND_TYPE_DELETE = "delete";
    private static final String COMMAND_TYPE_ERROR = "error";
    private static final String COMMAND_TYPE_FIND = "find";
    private static final String COMMAND_TYPE_HELP = "help";
    private static final String COMMAND_TYPE_LIST = "list";
    private static final String COMMAND_TYPE_MARK = "mark";
    private static final String COMMAND_TYPE_OTHER = "other";
    private static final String COMMAND_TYPE_STATS = "stats";
    private static final String COMMAND_TYPE_UNMARK = "unmark";
    private static final String SAVE_ERROR = "OOPS!!! I couldn't save your task list to disk.";
    private static final String HELP_RESPONSE = """
            The squad desk is open.
            I'll keep the banter light and your case file organized.

            Squad playbook:
            • todo <description> — Add a todo task.
              case <description> — Squad alias for todo.
            • deadline <description> /by <date> — Add a deadline task.
              timer <description> /by <date> — Squad alias for deadline.
            • event <description> /from <date> /to <date> — Add an event.
              briefing <description> /from <date> /to <date> — Squad alias for event.
              Dates use yyyy-MM-dd. Legacy /at <time> is also supported.
            • list / rollcall — Show all tasks.
            • stats / report — Show task statistics.
            • find <keyword> / intel <keyword> — Find tasks by keyword.
            • mark <task number> / close <task number> — Mark a task done.
            • unmark <task number> / reopen <task number> — Reopen a task.
            • delete <task number> / archive <task number> — Delete a task.
            • help / brief — Show this playbook.
            • bye / signoff — Exit Duchess.

            Commands are not case-sensitive.
            """;

    /** Persists Duchess's tasks. */
    private final Storage storage;

    /** Stores the tasks shared by the CLI and GUI interfaces. */
    private final TaskList tasks;

    /** Identifies the most recently processed command for GUI styling. */
    private String commandType;

    /** Records whether the user has issued the {@code bye} command. */
    private boolean exitRequested;

    /** Supplies the current instant for completion timestamps and statistics. */
    private final Clock clock;

    /** Creates a Duchess application using the default storage location. */
    public Duchess() {
        this(new Storage(), null, Clock.systemUTC());
    }

    /** Creates a Duchess instance with collaborators supplied by a test. */
    Duchess(Storage storage, TaskList tasks) {
        this(storage, tasks, Clock.systemUTC());
    }

    /** Creates a Duchess instance with collaborators and a clock supplied by a test. */
    Duchess(Storage storage, TaskList tasks, Clock clock) {
        assert storage != null : "Duchess requires a storage collaborator";
        assert clock != null : "Duchess requires a clock";
        this.storage = storage;
        this.tasks = tasks == null ? storage.loadTasks() : tasks;
        this.clock = clock;
        commandType = COMMAND_TYPE_OTHER;
    }

    /**
     * Starts Duchess in its text-based interface.
     *
     * @param args command-line arguments, which Duchess does not currently use
     */
    public static void main(String[] args) {
        Duchess duchess = new Duchess();
        Ui ui = new Ui();
        ui.showWelcome();
        if (!duchess.getStartupWarning().isEmpty()) {
            ui.showMessage(duchess.getStartupWarning());
            ui.showSeparator();
        }

        while (ui.hasNextLine()) {
            String command = ui.readCommand();
            ui.showSeparator();
            ui.showMessage(duchess.getResponse(command));
            ui.showSeparator();

            if (duchess.isExitRequested()) {
                break;
            }
        }
    }

    /**
     * Processes one command and returns the response for a user interface.
     *
     * @param command the complete command entered by the user
     * @return the response that should be displayed to the user
     */
    public String getResponse(String command) {
        String safeCommand = normalizeAlias(command == null ? "" : command.strip());
        String lowerCaseCommand = safeCommand.toLowerCase(Locale.ROOT);

        try {
            if (hasCommandWord(lowerCaseCommand, "bye")) {
                requireNoArguments(safeCommand, "bye");
                commandType = COMMAND_TYPE_BYE;
                exitRequested = true;
                return "Bye. Hope to see you again soon!";
            } else if (hasCommandWord(lowerCaseCommand, "help")) {
                requireNoArguments(safeCommand, "help");
                commandType = COMMAND_TYPE_HELP;
                return HELP_RESPONSE;
            } else if (hasCommandWord(lowerCaseCommand, "list")) {
                requireNoArguments(safeCommand, "list");
                commandType = COMMAND_TYPE_LIST;
                return formatTasks(tasks, "Here are the tasks in your list:");
            } else if (hasCommandWord(lowerCaseCommand, "stats")) {
                requireNoArguments(safeCommand, "stats");
                commandType = COMMAND_TYPE_STATS;
                return formatStatistics();
            } else if (hasCommandWord(lowerCaseCommand, "find")) {
                commandType = COMMAND_TYPE_FIND;
                return findTasks(safeCommand);
            } else if (hasCommandWord(lowerCaseCommand, "mark")) {
                commandType = COMMAND_TYPE_MARK;
                return markTask(safeCommand);
            } else if (hasCommandWord(lowerCaseCommand, "unmark")) {
                commandType = COMMAND_TYPE_UNMARK;
                return unmarkTask(safeCommand);
            } else if (hasCommandWord(lowerCaseCommand, "delete")) {
                commandType = COMMAND_TYPE_DELETE;
                return deleteTask(safeCommand);
            }

            commandType = COMMAND_TYPE_ADD;
            Task task = Parser.parseTask(safeCommand);
            assert task != null : "The parser must return a task for a valid add command";
            if (tasks.containsEquivalent(task)) {
                throw new DuchessException("OOPS!!! That task already exists in your list.");
            }
            tasks.add(task);
            return withSaveWarning("added: " + task);
        } catch (DuchessException exception) {
            commandType = COMMAND_TYPE_ERROR;
            return exception.getMessage();
        }
    }

    /** Converts squad-room command aliases into the canonical command names. */
    private String normalizeAlias(String command) {
        if (command.isEmpty()) {
            return command;
        }

        String[] commandParts = command.split("\\s+", 2);
        String[][] aliases = {
            {"rollcall", "list"},
            {"report", "stats"},
            {"brief", "help"},
            {"signoff", "bye"},
            {"intel", "find"},
            {"close", "mark"},
            {"reopen", "unmark"},
            {"archive", "delete"}
        };
        for (String[] alias : aliases) {
            if (commandParts[0].equalsIgnoreCase(alias[0])) {
                return commandParts.length == 1 ? alias[1] : alias[1] + " " + commandParts[1];
            }
        }
        return command;
    }

    /** Returns whether input begins with the supplied complete command word. */
    private boolean hasCommandWord(String command, String commandWord) {
        return command.split("\\s+", 2)[0].equals(commandWord);
    }

    /** Rejects arguments supplied to a command that does not accept them. */
    private void requireNoArguments(String command, String commandWord) throws DuchessException {
        if (!command.equalsIgnoreCase(commandWord)) {
            throw new DuchessException("OOPS!!! Please use '" + commandWord + "' without arguments.");
        }
    }

    /**
     * Returns the category of the most recently processed command.
     *
     * @return the command category used by the GUI to style responses
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Returns any warning about restoring saved tasks for both user interfaces.
     *
     * @return the storage warning, or an empty string when startup needs no warning.
     */
    public String getStartupWarning() {
        return storage.getLoadWarning();
    }

    /**
     * Returns whether the user has requested to leave the conversation.
     *
     * @return {@code true} after the {@code bye} command has been processed
     */
    public boolean isExitRequested() {
        return exitRequested;
    }

    /** Returns the response to a find command. */
    private String findTasks(String command) throws DuchessException {
        String[] commandParts = command.split("\\s+", 2);
        String keyword = commandParts.length == 2 ? commandParts[1].strip() : "";
        if (keyword.isEmpty()) {
            throw new DuchessException("OOPS!!! Please use 'find <keyword>', "
                    + "for example: find book.");
        }

        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        String matches = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase(Locale.ROOT)
                        .contains(lowerCaseKeyword))
                .mapToObj(index -> (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining("\n"));
        String heading = "Here are the matching tasks in your list:";
        return matches.isEmpty() ? "No matching tasks found. Try a different keyword." : heading + "\n" + matches;
    }

    /** Returns the response to a mark command. */
    private String markTask(String command) throws DuchessException {
        requireTaskNumber(command, "mark");
        int taskIndex = Parser.parseTaskIndex(command, "mark ");
        validateTaskIndex(taskIndex);

        tasks.markAsDone(taskIndex, clock.instant());
        return withSaveWarning("Nice! I've marked this task as done:\n  " + tasks.get(taskIndex));
    }

    /** Returns the response to an unmark command. */
    private String unmarkTask(String command) throws DuchessException {
        requireTaskNumber(command, "unmark");
        int taskIndex = Parser.parseTaskIndex(command, "unmark ");
        validateTaskIndex(taskIndex);

        tasks.markAsNotDone(taskIndex);
        return withSaveWarning("Okay, I've marked this task as not done yet:\n  "
                + tasks.get(taskIndex));
    }

    /** Returns the response to a delete command. */
    private String deleteTask(String command) throws DuchessException {
        requireTaskNumber(command, "delete");
        int taskIndex = Parser.parseTaskIndex(command, "delete ");
        validateTaskIndex(taskIndex);

        Task deletedTask = tasks.delete(taskIndex);
        return withSaveWarning("Noted. I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    /** Returns a response after reporting a recoverable storage failure. */
    private String withSaveWarning(String response) {
        try {
            storage.saveTasks(tasks);
            return response;
        } catch (java.io.IOException exception) {
            commandType = COMMAND_TYPE_ERROR;
            return SAVE_ERROR + "\n" + response;
        }
    }

    /** Formats a task collection using the same numbering as the CLI. */
    private String formatTasks(TaskList taskList, String heading) {
        return taskList.size() == 0 ? heading : IntStream.range(0, taskList.size())
                .mapToObj(index -> (index + 1) + "." + taskList.get(index))
                .collect(Collectors.joining("\n", heading + "\n", ""));
    }

    /** Returns the formatted statistics response. */
    private String formatStatistics() {
        TaskStatistics statistics = tasks.getStatistics(clock.instant());
        return "Task statistics:\n"
                + "Total tasks: " + statistics.getTotalTasks() + "\n"
                + "Completed tasks: " + statistics.getCompletedTasks() + "\n"
                + "Incomplete tasks: " + statistics.getIncompleteTasks() + "\n"
                + "Completed in the past 7 days: " + statistics.getRecentlyCompletedTasks() + "\n"
                + "Completion rate: " + statistics.getCompletionRatePercentage() + "%";
    }

    /** Rejects a task command whose required number is missing. */
    private void requireTaskNumber(String command, String commandWord) throws DuchessException {
        if (command.equalsIgnoreCase(commandWord)) {
            throw new DuchessException("OOPS!!! Please use '" + commandWord
                    + " <task number>', for example: " + commandWord + " 1.");
        }
    }

    /** Validates a zero-based task index. */
    private void validateTaskIndex(int taskIndex) throws DuchessException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            if (tasks.size() == 0) {
                throw new DuchessException("OOPS!!! Your task list is empty, so there is no task to update.");
            }
            throw new DuchessException("OOPS!!! Please provide a valid task number between 1 and "
                    + tasks.size() + ".");
        }
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task index must refer to an existing task";
    }
}

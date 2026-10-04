package unicorn;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unicorn.storage.Storage;
import unicorn.task.DeadlineTask;
import unicorn.task.EventTask;
import unicorn.task.Task;
import unicorn.task.TaskList;
import unicorn.task.TodoTask;

/**
 * Processes commands for the Unicorn task chatbot.
 */
public class Unicorn {
    private static final String AVAILABLE_COMMANDS = """
            Available commands:
              hi
              todo DESCRIPTION
              deadline DESCRIPTION /by DATE
              event DESCRIPTION /from START /to END
              list
              find KEYWORD
              mark NUMBER
              unmark NUMBER
              delete NUMBER
              bye""";
    private static final Pattern DEADLINE_BY_MARKER = Pattern.compile("(?<!\\S)/by(?!\\S)");
    private static final Pattern DEADLINE_FORMAT = Pattern.compile("^(.+?)\\s+/by\\s+(.+)$");
    private static final String ERROR_COMMAND_TYPE = "error";
    private static final Pattern EVENT_FROM_MARKER = Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final Pattern EVENT_TO_MARKER = Pattern.compile("(?<!\\S)/to(?!\\S)");
    private static final Pattern EVENT_FORMAT = Pattern.compile("^(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$");
    private static final String HELP_MESSAGE = "That signal was unclear. Every great quest needs a map! "
            + "Add a quest with todo, event, or deadline; view quests with 'list' or 'find'; "
            + "or update them with 'mark', 'unmark', or 'delete' followed by the quest number.";
    private static final String HI_MESSAGE = "Hi! Prisma the tech unicorn is online and ready to help.\n"
            + AVAILABLE_COMMANDS;
    private static final String WELCOME_MESSAGE = "Hello! I'm Prisma, your wise tech unicorn. "
            + "Tell me what is on your quest list, and we'll make some magic.";

    private final TaskList tasks;
    private final TaskSaver taskSaver;
    private final String startupWarning;
    private String commandType;

    /**
     * Creates a chatbot using tasks loaded from the default data file.
     */
    public Unicorn() {
        this(loadTasks());
    }

    private Unicorn(LoadResult loadResult) {
        this(loadResult.tasks(), Storage::save, loadResult.warning());
    }

    /**
     * Creates a chatbot with supplied tasks and persistence behavior.
     *
     * @param tasks initial tasks managed by the chatbot
     * @param taskSaver operation used to save task changes
     */
    Unicorn(TaskList tasks, TaskSaver taskSaver) {
        this(tasks, taskSaver, "");
    }

    private Unicorn(TaskList tasks, TaskSaver taskSaver, String startupWarning) {
        assert tasks != null : "Task list must not be null";
        assert taskSaver != null : "Task saver must not be null";
        assert startupWarning != null : "Startup warning must not be null";

        this.tasks = tasks;
        this.taskSaver = taskSaver;
        this.startupWarning = startupWarning;
    }

    /**
     * Processes a user command and returns the chatbot's response.
     *
     * @param input command entered by the user
     * @return response describing the command result
     */
    public String getResponse(String input) {
        if (input == null || input.isBlank()) {
            return getErrorResponse("Please enter a command so I know which quest to follow.");
        }
        if (input.chars().anyMatch(Character::isISOControl)) {
            return getErrorResponse("Commands cannot contain line breaks or other control characters.");
        }
        String trimmedInput = input.trim();

        String[] commandParts = trimmedInput.split("\\s+", 2);
        commandType = commandParts[0];
        String argument = commandParts.length == 1 ? "" : commandParts[1].trim();

        switch (commandType) {
            case "hi":
                return argument.isEmpty()
                        ? HI_MESSAGE
                        : getErrorResponse("The hi command does not take any extra details.");
            case "list":
                return argument.isEmpty()
                        ? formatTasks(tasks.asList())
                        : getErrorResponse("The list command does not take any extra details.");
            case "find":
                return findTasks(argument);
            case "mark":
                return setTaskCompletion(argument, true);
            case "unmark":
                return setTaskCompletion(argument, false);
            case "delete":
                return deleteTask(argument);
            case "todo":
                return addTodo(argument);
            case "deadline":
                return addDeadline(argument);
            case "event":
                return addEvent(argument);
            case "bye":
                return argument.isEmpty()
                        ? "Keep shining! Prisma will be here when your next quest begins."
                        : getErrorResponse("The bye command does not take any extra details.");
            default:
                return getErrorResponse(HELP_MESSAGE);
        }
    }

    /**
     * Returns Prisma's introductory greeting.
     *
     * @return welcome message that introduces the chatbot's personality
     */
    public String getWelcomeMessage() {
        return startupWarning.isEmpty() ? WELCOME_MESSAGE : WELCOME_MESSAGE + "\n\n⚠ " + startupWarning;
    }

    /**
     * Returns the type of the most recently processed command.
     *
     * @return command type used to style the chatbot's response
     */
    public String getCommandType() {
        return commandType;
    }

    private String findTasks(String keyword) {
        if (keyword.isBlank()) {
            return getErrorResponse("My unicorn senses need a keyword before they can search.");
        }
        List<Task> matchingTasks = tasks.find(keyword);
        return "My unicorn senses found these matching quests:\n" + formatTasks(matchingTasks);
    }

    private String setTaskCompletion(String argument, boolean isDone) {
        Task task = getTask(argument);
        if (task == null) {
            return getTaskNumberError(argument);
        }

        boolean wasDone = task.isDone();
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }

        if (!saveTasks()) {
            restoreStatus(task, wasDone);
            return getSaveError();
        }
        if (isDone) {
            return "Brilliant work—another quest conquered!\n" + task;
        }
        return "Quest reopened. Even wise adventurers revise their plans.\n" + task;
    }

    private String deleteTask(String argument) {
        Integer taskIndex = parseTaskIndex(argument);
        if (taskIndex == null) {
            return getTaskNumberError(argument);
        }

        Task deletedTask = tasks.delete(taskIndex);
        if (!saveTasks()) {
            tasks.add(taskIndex, deletedTask);
            return getSaveError();
        }
        return "Poof! This quest has left the digital realm:\n  " + deletedTask
                + "\nYour quest log now holds " + tasks.size() + " quests.";
    }

    private String addTodo(String description) {
        if (description.isBlank()) {
            return getErrorResponse("A quest needs a description before I can weave it into the list.");
        }
        return addTask(new TodoTask(description));
    }

    private String addDeadline(String argument) {
        Matcher deadlineMatcher = DEADLINE_FORMAT.matcher(argument);
        if (!deadlineMatcher.matches() || !containsExactlyOne(DEADLINE_BY_MARKER, argument)) {
            return getErrorResponse("Use: deadline DESCRIPTION /by DATE.");
        }

        String description = deadlineMatcher.group(1).trim();
        String by = deadlineMatcher.group(2).trim();
        if (description.isBlank()) {
            return getErrorResponse("A deadline quest needs a description before I can save it.");
        } else if (by.isBlank()) {
            return getErrorResponse("A deadline quest needs a date after /by.");
        }
        try {
            return addTask(new DeadlineTask(description, DeadlineTask.parseBy(by)));
        } catch (DateTimeParseException e) {
            return getErrorResponse("A little time glitch! Please use yyyy-MM-dd, yyyy-MM-dd HHmm, "
                    + "or d/M/yyyy HHmm for deadlines.");
        }
    }

    private String addEvent(String argument) {
        Matcher eventMatcher = EVENT_FORMAT.matcher(argument);
        boolean hasSingleFrom = containsExactlyOne(EVENT_FROM_MARKER, argument);
        boolean hasSingleTo = containsExactlyOne(EVENT_TO_MARKER, argument);
        if (!eventMatcher.matches() || !hasSingleFrom || !hasSingleTo) {
            return getErrorResponse("Use: event DESCRIPTION /from START /to END.");
        }

        String description = eventMatcher.group(1).trim();
        String from = eventMatcher.group(2).trim();
        String to = eventMatcher.group(3).trim();
        if (description.isBlank()) {
            return getErrorResponse("An event quest needs a description before I can save it.");
        } else if (from.isBlank()) {
            return getErrorResponse("An event quest needs a start after /from.");
        } else if (to.isBlank()) {
            return getErrorResponse("An event quest needs an end after /to.");
        }
        try {
            return addTask(new EventTask(description, from, to));
        } catch (DateTimeParseException e) {
            return getErrorResponse("A little time glitch! Numeric event dates must use yyyy-MM-dd, "
                    + "yyyy-MM-dd HHmm, or d/M/yyyy HHmm.");
        } catch (IllegalArgumentException e) {
            return getErrorResponse("An event must end after it starts.");
        }
    }

    private static boolean containsExactlyOne(Pattern markerPattern, String argument) {
        Matcher markerMatcher = markerPattern.matcher(argument);
        return markerMatcher.find() && !markerMatcher.find();
    }

    private String addTask(Task task) {
        if (tasks.containsEquivalent(task)) {
            return getErrorResponse("That quest is already in your quest log.");
        }
        tasks.add(task);
        if (!saveTasks()) {
            tasks.delete(tasks.size() - 1);
            return getSaveError();
        }
        return "Your quest has been added to the rainbow:\n  " + task
                + "\nYour quest log now holds " + tasks.size() + " quests.";
    }

    private Task getTask(String argument) {
        Integer taskIndex = parseTaskIndex(argument);
        return taskIndex == null ? null : tasks.get(taskIndex);
    }

    private Integer parseTaskIndex(String argument) {
        if (argument.isBlank()) {
            return null;
        }
        try {
            int taskIndex = Integer.parseInt(argument) - 1;
            return taskIndex >= 0 && taskIndex < tasks.size() ? taskIndex : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getTaskNumberError(String argument) {
        if (argument.isBlank()) {
            return getErrorResponse("My quest compass needs a quest number.");
        }
        try {
            Integer.parseInt(argument);
            return getErrorResponse("That quest number has not appeared in this realm yet.");
        } catch (NumberFormatException e) {
            return getErrorResponse("That signal is not a valid quest number.");
        }
    }

    private String getErrorResponse(String message) {
        commandType = ERROR_COMMAND_TYPE;
        return "⚠ " + message;
    }

    private boolean saveTasks() {
        try {
            taskSaver.save(tasks.asList());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static LoadResult loadTasks() {
        try {
            return new LoadResult(new TaskList(Storage.load()), "");
        } catch (IOException e) {
            return new LoadResult(new TaskList(),
                    "I could not read the saved quest file. This session started with an empty list.");
        } catch (IllegalArgumentException e) {
            return new LoadResult(new TaskList(),
                    "The saved quest file contains invalid data. This session started with an empty list.");
        }
    }

    private static String formatTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Your quest log is clear. Enjoy the breathing room!";
        }
        StringBuilder response = new StringBuilder();
        for (int index = 0; index < tasks.size(); index++) {
            if (index > 0) {
                response.append('\n');
            }
            response.append(index + 1).append(". ").append(tasks.get(index));
        }
        return response.toString();
    }

    private static void restoreStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
    }

    private String getSaveError() {
        return getErrorResponse(
                "A little glitch disturbed the magic. I could not save your quests, so nothing changed."
        );
    }

    /**
     * Saves the current tasks to persistent storage.
     */
    @FunctionalInterface
    interface TaskSaver {
        void save(List<Task> tasks) throws IOException;
    }

    private record LoadResult(TaskList tasks, String warning) {
    }
}

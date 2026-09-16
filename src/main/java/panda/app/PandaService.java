package panda.app;

import java.util.List;

import panda.exception.ErrorType;
import panda.exception.PandaException;
import panda.model.CommandType;
import panda.model.SortDirection;
import panda.model.Task;
import panda.model.TaskList;
import panda.parser.Parser;
import panda.storage.Storage;

/**
 * Processes Panda commands independently of the user interface.
 */
public class PandaService {
    /** Response used by the console UI to end its session. */
    public static final String BYE_RESPONSE = "__PANDA_BYE__";
    private static final String DEFAULT_LIST_HEADING = "Here are the tasks in your list:";
    private static final String DATE_ORDER_LIST_HEADING = "Here are the tasks in date order:";
    private static final String EMPTY_SORT_RESPONSE = "There are no tasks to sort.";

    private final TaskList tasks;
    private final boolean shouldSaveTasks;
    private final String startupWarning;
    private boolean isDateSortingEnabled;
    private SortDirection sortDirection;

    /** Creates a service with tasks loaded from storage. */
    public PandaService() {
        TaskList loadedTasks;
        String loadWarning = null;
        try {
            loadedTasks = new TaskList(Storage.loadTasks());
        } catch (RuntimeException e) {
            loadedTasks = new TaskList();
            loadWarning = ErrorType.STORAGE_LOAD_FAILED.getMessage();
        }
        this.tasks = loadedTasks;
        this.shouldSaveTasks = true;
        this.startupWarning = loadWarning;
        isDateSortingEnabled = false;
        sortDirection = SortDirection.ASCENDING;
    }

    PandaService(TaskList tasks) {
        this(tasks, false);
    }

    private PandaService(TaskList tasks, boolean shouldSaveTasks) {
        this.tasks = tasks;
        this.shouldSaveTasks = shouldSaveTasks;
        this.startupWarning = null;
        isDateSortingEnabled = false;
        sortDirection = SortDirection.ASCENDING;
    }

    /**
     * Processes one command and returns text suitable for display in a chat bubble.
     *
     * @param command raw command text
     * @return Panda's response
     */
    public String getResponse(String command) {
        try {
            Parser.validateCommandFormat(command);
            CommandType commandType = Parser.getCommandType(command);
            switch (commandType) {
            case LIST:
                return getTaskListResponse();
            case MARK:
                return "Nice! I've marked this task as done:\n  "
                        + updateTask(command, "mark", true);
            case UNMARK:
                return "OK, I've marked this task as not done yet:\n  "
                        + updateTask(command, "unmark", false);
            case DELETE:
                Task deletedTask = getTaskForOperation(command, "delete");
                int taskCountBeforeDeletion = tasks.size();
                tasks.remove(tasks.asList().indexOf(deletedTask));
                assert tasks.size() == taskCountBeforeDeletion - 1
                        : "Removing one task must reduce the task count by one";
                saveTasks();
                return "Noted. I've removed this task:\n  " + deletedTask
                        + "\nNow you have " + tasks.size() + " tasks in the list.";
            case FIND:
                String keyword = Parser.getArguments(command, "find");
                if (keyword.isEmpty()) {
                    throw new PandaException(ErrorType.EMPTY_FIND_KEYWORD);
                }
                return formatTasks("Here are the matching tasks in your list:",
                        getTasksForDisplay(tasks.find(keyword)));
            case TODO:
            case DEADLINE:
            case EVENT:
                Task task = Parser.createTask(command);
                if (tasks.containsEquivalent(task)) {
                    throw new PandaException(ErrorType.DUPLICATE_TASK);
                }
                int taskCountBeforeAddition = tasks.size();
                tasks.add(task);
                assert tasks.size() == taskCountBeforeAddition + 1
                        : "Adding one task must increase the task count by one";
                saveTasks();
                return getTaskAddedResponse(task);
            case BYE:
                return BYE_RESPONSE;
            case UNKNOWN:
            default:
                throw new PandaException(ErrorType.UNKNOWN_COMMAND);
            }
        } catch (PandaException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns a warning generated while loading saved tasks, if any.
     *
     * @return the startup warning, or {@code null} when loading succeeded
     */
    public String getStartupWarning() {
        return startupWarning;
    }

    private Task updateTask(String command, String action, boolean markDone) throws PandaException {
        Task task = getTaskForOperation(command, action);
        int taskIndex = tasks.asList().indexOf(task);
        Task updatedTask = markDone ? tasks.markTask(taskIndex) : tasks.unmarkTask(taskIndex);
        String expectedStatusIcon = markDone ? "X" : " ";
        assert updatedTask == task : "Updating a task must return the task stored at the requested index";
        assert task.getStatusIcon().equals(expectedStatusIcon)
                : "Updating a task must set its requested completion status";
        saveTasks();
        return task;
    }

    /**
     * Enables or disables the date-sorted task view.
     *
     * @param isEnabled whether the sorted view is enabled
     * @return the updated task-list response
     */
    public String setDateSortingEnabled(boolean isEnabled) {
        isDateSortingEnabled = isEnabled;
        if (isDateSortingEnabled && tasks.size() == 0) {
            return EMPTY_SORT_RESPONSE;
        }
        return getTaskListResponse();
    }

    /**
     * Reverses the date-sorting direction.
     *
     * @return the updated task-list response
     */
    public String toggleDateSortDirection() {
        sortDirection = sortDirection.getOpposite();
        return getTaskListResponse();
    }

    /**
     * Returns the selected date-sorting direction.
     *
     * @return the selected sorting direction
     */
    public SortDirection getSortDirection() {
        return sortDirection;
    }

    private String getTaskAddedResponse(Task task) {
        String response = "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
        return isDateSortingEnabled ? response + "\n" + getTaskListResponse() : response;
    }

    private String getTaskListResponse() {
        String heading = isDateSortingEnabled ? DATE_ORDER_LIST_HEADING : DEFAULT_LIST_HEADING;
        return formatTasks(heading, getTasksForDisplay(tasks.asList()));
    }

    private List<Task> getTasksForDisplay(List<Task> taskList) {
        if (!isDateSortingEnabled) {
            return taskList;
        }
        return tasks.sortDeadlinesByDate(taskList, sortDirection);
    }

    private Task getTaskForOperation(String command, String action) throws PandaException {
        List<Task> displayedTasks = getTasksForDisplay(tasks.asList());
        int displayedIndex = Parser.getTaskIndex(command, action, displayedTasks.size());
        return displayedTasks.get(displayedIndex);
    }

    private void saveTasks() throws PandaException {
        if (shouldSaveTasks) {
            try {
                Storage.saveTasks(tasks.asList());
            } catch (RuntimeException e) {
                throw new PandaException(ErrorType.STORAGE_SAVE_FAILED);
            }
        }
    }

    private String formatTasks(String heading, List<Task> taskList) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < taskList.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(taskList.get(i));
        }
        return response.toString();
    }
}

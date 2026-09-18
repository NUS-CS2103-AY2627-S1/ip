package student.project.bob;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

import student.project.bob.exception.BobException;
import student.project.bob.model.Deadline;
import student.project.bob.model.Event;
import student.project.bob.model.Task;
import student.project.bob.model.TaskList;
import student.project.bob.model.UndoHistory;
import student.project.bob.parser.Command;
import student.project.bob.parser.Parser;
import student.project.bob.personality.PersonalityProfile;
import student.project.bob.storage.Storage;
import student.project.bob.storage.StorageException;
import student.project.bob.ui.Ui;
import student.project.bob.util.DateTimeParser;

/**
 * Runs the Bob task-management application.
 */
public class Bob {
    private static final String UNDO_ARGUMENT_ERROR =
            "The undo command does not take any arguments. Example: \"undo\".";
    private static final String NO_UNDO_ERROR = "There is no command to undo.";

    private final Parser parser;
    private final TaskList taskList;
    private final UndoHistory undoHistory;
    private final PersonalityProfile personality;

    /**
     * Creates Bob with tasks loaded from local storage for GUI use.
     *
     * <p>The command-line entry point keeps its existing startup error message, while the GUI starts with an empty
     * list when saved data cannot be loaded.
     */
    public Bob() {
        parser = new Parser();
        TaskList loadedTaskList;
        try {
            loadedTaskList = new TaskList(Storage.loadTasks());
        } catch (StorageException e) {
            loadedTaskList = new TaskList();
        }
        taskList = loadedTaskList;
        undoHistory = new UndoHistory();
        personality = PersonalityProfile.DEFAULT;
    }

    /**
     * Processes one command and returns the corresponding GUI response.
     *
     * @param input command entered by the user
     * @return response text to display in the GUI
     */
    public String getResponse(String input) {
        String commandInput = input.trim();
        Command command;
        try {
            command = parser.parseCommand(commandInput);
        } catch (BobException e) {
            return "Oops! " + e.getMessage();
        }

        try {
            return switch (command.getType()) {
                case BYE -> "Bye. Hope to see you again soon!";
                case LIST -> formatTaskList("Here are the tasks in your list:", taskList.asList());
                case UPCOMING -> {
                    int days = parser.parseUpcomingDays(commandInput);
                    yield formatTaskList(
                            "Here are the upcoming tasks in the next " + days + " days:",
                            getUpcomingTasks(taskList, days));
                }
                case ON -> {
                    LocalDate date = parser.parseOnDate(commandInput);
                    yield formatTaskList(
                            "Here are the tasks on " + DateTimeParser.format(date) + ":",
                            getTasksOnDate(taskList, date));
                }
                case OVERDUE -> {
                    if (!commandInput.equals("overdue")) {
                        yield "Oops! Please use the format: overdue.";
                    }
                    yield formatTaskList("Here are your overdue tasks:", getOverdueTasks(taskList));
                }
                case FIND ->
                    formatTaskList(
                            "Here are the matching tasks in your list:",
                            getMatchingTasks(taskList, parser.parseFindKeyword(commandInput)));
                case MARK -> updateTask(commandInput, true);
                case UNMARK -> updateTask(commandInput, false);
                case DELETE -> deleteTask(commandInput);
                case UNDO -> undo(commandInput);
                case TASK -> addTask(commandInput);
            };
        } catch (BobException e) {
            return "Oops! " + e.getMessage();
        }
    }

    /**
     * Formats a heading and tasks for display in a dialog box.
     *
     * @param heading heading to display
     * @param tasks tasks to list
     * @return formatted task-list response
     */
    private String formatTaskList(String heading, Iterable<Task> tasks) {
        StringBuilder response = new StringBuilder(heading).append("\n\n");
        int taskNumber = 1;
        for (Task task : tasks) {
            response.append(taskNumber++).append('.').append(task).append('\n');
        }
        return response.toString().stripTrailing();
    }

    /**
     * Marks or unmarks a task selected by its one-based number.
     *
     * @param input mark or unmark command
     * @param isMarking whether the task should be marked done
     * @return confirmation or validation response
     * @throws BobException if the task number is invalid
     */
    private String updateTask(String input, boolean isMarking) throws BobException {
        String commandName = isMarking ? "mark" : "unmark";
        int index = taskList.getIndex(input.split("\\s+"), commandName);
        Task task = taskList.get(index);
        if (isMarking) {
            boolean wasDone = task.isDone();
            task.setDone();
            if (!wasDone) {
                undoHistory.record(task::setUndone, "Undid the last command:\n  [ ] " + task.getDescription());
            }
            saveGuiTasks();
            return personality.formatMarkedTask(task);
        }

        boolean wasDone = task.isDone();
        task.setUndone();
        if (wasDone) {
            undoHistory.record(task::setDone, "Undid the last command:\n  [X] " + task.getDescription());
        }
        saveGuiTasks();
        return personality.formatUnmarkedTask(task);
    }

    /**
     * Undoes the most recent state-changing command in the GUI session.
     *
     * @param input undo command
     * @return confirmation or validation response
     * @throws BobException if the command has arguments or there is no command to undo
     */
    private String undo(String input) throws BobException {
        validateUndoInput(input);
        Optional<String> message = undoHistory.undo();
        if (message.isEmpty()) {
            throw new BobException(NO_UNDO_ERROR);
        }
        saveGuiTasks();
        return message.get();
    }

    /**
     * Undoes the most recent state-changing command in the CLI session.
     *
     * @param command parsed undo command
     * @param taskList current task list
     * @param ui user interface used for console output
     * @param undoHistory history of the most recent state-changing command
     */
    private static void undo(Command command, TaskList taskList, Ui ui, UndoHistory undoHistory) {
        try {
            validateUndoInput(command.getInput());
            Optional<String> message = undoHistory.undo();
            if (message.isEmpty()) {
                throw new BobException(NO_UNDO_ERROR);
            }
            saveTasks(taskList, ui);
            ui.showUndoResult(message.get());
        } catch (BobException e) {
            ui.showError(e);
        }
    }

    /**
     * Validates that undo has no arguments.
     *
     * @param input command input to validate
     * @throws BobException if the command has arguments
     */
    private static void validateUndoInput(String input) throws BobException {
        if (!input.equals("undo")) {
            throw new BobException(UNDO_ARGUMENT_ERROR);
        }
    }

    /**
     * Deletes a task selected by its one-based number.
     *
     * @param input delete command
     * @return confirmation or validation response
     * @throws BobException if the task number is invalid
     */
    private String deleteTask(String input) throws BobException {
        int index = taskList.getIndex(input.split("\\s+"), "delete");
        Task removedTask = taskList.remove(index);
        undoHistory.record(() -> taskList.add(index, removedTask), "Undid the last command:\n    " + removedTask);
        saveGuiTasks();
        return personality.formatDeletedTask(removedTask, taskList.size());
    }

    /**
     * Deletes a selected task.
     *
     * @param command parsed delete command
     * @param taskList current task list
     * @param ui user interface used for console output
     * @param undoHistory history of the most recent state-changing command
     */
    private static void deleteTask(Command command, TaskList taskList, Ui ui, UndoHistory undoHistory) {
        try {
            String[] parts = command.getInput().split("\\s+");
            int index = taskList.getIndex(parts, "delete");
            Task removedTask = taskList.remove(index);
            undoHistory.record(() -> taskList.add(index, removedTask), "Undid the last command:\n    " + removedTask);
            saveTasks(taskList, ui);
            ui.showDeletedTask(removedTask, taskList.size());
        } catch (BobException e) {
            ui.showError(e);
        }
    }

    /**
     * Adds a task parsed from a command.
     *
     * @param input task command
     * @return confirmation or validation response
     * @throws BobException if the task command is invalid
     */
    private String addTask(String input) throws BobException {
        Task task = parser.parseTask(input);
        int index = taskList.size();
        taskList.add(task);
        undoHistory.record(() -> taskList.remove(index), "Undid the last command:\n    " + task);
        saveGuiTasks();
        return personality.formatAddedTask(task, taskList.size());
    }

    /**
     * Parses and adds a new task.
     *
     * @param command parsed task command
     * @param parser parser used to construct the task
     * @param taskList current task list
     * @param ui user interface used for console output
     * @param undoHistory history of the most recent state-changing command
     */
    private static void addTask(Command command, Parser parser, TaskList taskList, Ui ui, UndoHistory undoHistory) {
        try {
            Task newTask = parser.parseTask(command.getInput());
            int index = taskList.size();
            taskList.add(newTask);
            undoHistory.record(() -> taskList.remove(index), "Undid the last command:\n    " + newTask);
            saveTasks(taskList, ui);
            ui.showAddedTask(newTask, taskList.size());
        } catch (BobException e) {
            ui.showError(e);
        }
    }

    /**
     * Saves the GUI task list and continues when persistence is unavailable.
     */
    private void saveGuiTasks() {
        try {
            Storage.saveTasks(taskList.asList());
        } catch (StorageException e) {
            // The GUI can continue operating even if persistence is temporarily unavailable.
        }
    }

    /**
     * Selects tasks whose relevant date falls within the upcoming date range.
     *
     * @param taskList tasks to search
     * @param days number of days after today to include
     */
    private static ArrayList<Task> getUpcomingTasks(TaskList taskList, int days) {
        assert days >= 0 : "The upcoming range cannot contain a negative number of days";
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return taskList.stream()
                .filter(task -> isUpcoming(task, today, endDate))
                .sorted(Comparator.comparing(Bob::getTaskDateTime))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Selects deadlines and events scheduled on a date.
     *
     * @param taskList tasks to search
     * @param date date to search for
     */
    private static ArrayList<Task> getTasksOnDate(TaskList taskList, LocalDate date) {
        return taskList.stream()
                .filter(task -> isOnDate(task, date))
                .sorted(Comparator.comparing(Bob::getTaskDateTime))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Checks whether a deadline or event is scheduled on a date.
     *
     * @param task task to inspect
     * @param date date to check
     * @return true if the task occurs on the date
     */
    private static boolean isOnDate(Task task, LocalDate date) {
        if (task instanceof Deadline) {
            return getTaskDateTime(task).toLocalDate().equals(date);
        }
        if (task instanceof Event event) {
            LocalDate fromDate = getTaskDateTime(event).toLocalDate();
            LocalDate toDate = getDateTime(event.getTo()).toLocalDate();
            return !date.isBefore(fromDate) && !date.isAfter(toDate);
        }
        return false;
    }

    /**
     * Converts a stored temporal value to a local date-time for comparisons.
     *
     * @param temporal date or local date-time to convert
     * @return local date-time representation
     */
    private static LocalDateTime getDateTime(Temporal temporal) {
        assert temporal instanceof LocalDate || temporal instanceof LocalDateTime
                : "Only LocalDate and LocalDateTime values are supported";
        if (temporal instanceof LocalDateTime dateTime) {
            return dateTime;
        }
        return ((LocalDate) temporal).atStartOfDay();
    }

    /**
     * Selects incomplete deadlines that are past their due date or time.
     *
     * @param taskList tasks to search
     */
    private static ArrayList<Task> getOverdueTasks(TaskList taskList) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        return taskList.stream()
                .filter(task -> isOverdue(task, today, now))
                .sorted(Comparator.comparing(Bob::getTaskDateTime))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Selects tasks whose descriptions contain the supplied keyword.
     *
     * @param taskList tasks to search
     * @param keyword keyword to find in task descriptions
     * @return matching tasks in their original list order
     */
    private static ArrayList<Task> getMatchingTasks(TaskList taskList, String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return taskList.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Checks whether a task is an incomplete deadline that has passed.
     *
     * @param task task to inspect
     * @param today current local date
     * @param now current local date-time
     * @return true if the task is an overdue deadline
     */
    private static boolean isOverdue(Task task, LocalDate today, LocalDateTime now) {
        if (!(task instanceof Deadline deadline) || task.isDone()) {
            return false;
        }

        Temporal temporal = deadline.getBy();
        if (temporal instanceof LocalDate date) {
            return date.isBefore(today);
        }
        return ((LocalDateTime) temporal).isBefore(now);
    }

    /**
     * Checks whether a deadline or event starts within the requested date range.
     *
     * @param task task to inspect
     * @param startDate first date in the range
     * @param endDate last date in the range
     * @return true if the task's relevant date is in the range
     */
    private static boolean isUpcoming(Task task, LocalDate startDate, LocalDate endDate) {
        assert !endDate.isBefore(startDate) : "An upcoming date range must be ordered";
        if (!(task instanceof Deadline) && !(task instanceof Event)) {
            return false;
        }

        LocalDate taskDate = getTaskDateTime(task).toLocalDate();
        return !taskDate.isBefore(startDate) && !taskDate.isAfter(endDate);
    }

    /**
     * Returns the date and time used to order a deadline or event.
     *
     * @param task task whose date or time should be returned
     * @return the deadline date or event start date and time
     */
    private static LocalDateTime getTaskDateTime(Task task) {
        assert task instanceof Deadline || task instanceof Event : "Only deadlines and events have a task date-time";
        Temporal temporal = task instanceof Deadline deadline ? deadline.getBy() : ((Event) task).getFrom();
        return getDateTime(temporal);
    }

    /**
     * Starts Bob and processes commands until the user says goodbye or input ends.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        UndoHistory undoHistory = new UndoHistory();
        ui.showWelcome();

        TaskList taskList;
        try {
            taskList = new TaskList(Storage.loadTasks());
        } catch (StorageException e) {
            taskList = new TaskList();
            ui.showStorageError("I couldn't load the saved tasks. Starting with an empty list.");
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();

            Command command;
            try {
                command = parser.parseCommand(input);
            } catch (BobException e) {
                ui.showError(e);
                continue;
            }

            if (processCommand(command, parser, taskList, ui, undoHistory)) {
                return;
            }
        }
    }

    /**
     * Executes one parsed command and reports whether Bob should exit.
     *
     * @param command parsed command to execute
     * @param parser parser used for command-specific arguments
     * @param taskList current task list
     * @param ui user interface used for console output
     * @param undoHistory history of the most recent state-changing command
     * @return true if the command requests that Bob exits
     */
    private static boolean processCommand(
            Command command, Parser parser, TaskList taskList, Ui ui, UndoHistory undoHistory) {
        switch (command.getType()) {
            case BYE -> {
                ui.showGoodbye();
                return true;
            }
            case LIST -> ui.showTaskList(taskList.asList());
            case UPCOMING -> showUpcomingTasks(command, parser, taskList, ui);
            case ON -> showTasksOnDate(command, parser, taskList, ui);
            case OVERDUE -> showOverdueTasks(command, taskList, ui);
            case FIND -> showMatchingTasks(command, parser, taskList, ui);
            case MARK -> markTask(command, taskList, ui, undoHistory);
            case UNMARK -> unmarkTask(command, taskList, ui, undoHistory);
            case DELETE -> deleteTask(command, taskList, ui, undoHistory);
            case UNDO -> undo(command, taskList, ui, undoHistory);
            case TASK -> addTask(command, parser, taskList, ui, undoHistory);
            default -> throw new AssertionError("Unhandled command type: " + command.getType());
        }
        return false;
    }

    /**
     * Displays tasks scheduled in the upcoming date range.
     *
     * @param command parsed upcoming command
     * @param parser parser used to read the number of days
     * @param taskList current task list
     * @param ui user interface used for console output
     */
    private static void showUpcomingTasks(Command command, Parser parser, TaskList taskList, Ui ui) {
        try {
            int days = parser.parseUpcomingDays(command.getInput());
            ui.showUpcomingTasks(getUpcomingTasks(taskList, days), days);
        } catch (BobException e) {
            ui.showError(e);
        }
    }

    /**
     * Displays deadlines and events scheduled on a requested date.
     *
     * @param command parsed on command
     * @param parser parser used to read the requested date
     * @param taskList current task list
     * @param ui user interface used for console output
     */
    private static void showTasksOnDate(Command command, Parser parser, TaskList taskList, Ui ui) {
        try {
            LocalDate date = parser.parseOnDate(command.getInput());
            ui.showTasksOnDate(getTasksOnDate(taskList, date), date);
        } catch (BobException e) {
            ui.showError(e);
        }
    }

    /**
     * Displays incomplete deadlines that are past their due date or time.
     *
     * @param command parsed overdue command
     * @param taskList current task list
     * @param ui user interface used for console output
     */
    private static void showOverdueTasks(Command command, TaskList taskList, Ui ui) {
        if (command.getInput().equals("overdue")) {
            ui.showOverdueTasks(getOverdueTasks(taskList));
        } else {
            ui.showError(new BobException("The overdue command does not take any arguments. Example: \"overdue\"."));
        }
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param command parsed find command
     * @param parser parser used to read the search keyword
     * @param taskList current task list
     * @param ui user interface used for console output
     */
    private static void showMatchingTasks(Command command, Parser parser, TaskList taskList, Ui ui) {
        try {
            String keyword = parser.parseFindKeyword(command.getInput());
            ui.showMatchingTasks(getMatchingTasks(taskList, keyword));
        } catch (BobException e) {
            ui.showError(e);
        }
    }

    /**
     * Marks a selected task as done.
     *
     * @param command parsed mark command
     * @param taskList current task list
     * @param ui user interface used for console output
     * @param undoHistory history of the most recent state-changing command
     */
    private static void markTask(Command command, TaskList taskList, Ui ui, UndoHistory undoHistory) {
        try {
            String[] parts = command.getInput().trim().split("\\s+");
            int index = taskList.getIndex(parts, "mark");
            Task task = taskList.get(index);
            boolean wasDone = task.isDone();
            task.setDone();
            if (!wasDone) {
                undoHistory.record(task::setUndone, "Undid the last command:\n  [ ] " + task.getDescription());
            }
            saveTasks(taskList, ui);
            ui.showMarkedTask(task);
        } catch (BobException e) {
            ui.showError(e);
        }
    }

    /**
     * Marks a selected task as not done.
     *
     * @param command parsed unmark command
     * @param taskList current task list
     * @param ui user interface used for console output
     * @param undoHistory history of the most recent state-changing command
     */
    private static void unmarkTask(Command command, TaskList taskList, Ui ui, UndoHistory undoHistory) {
        try {
            String[] parts = command.getInput().split("\\s+");
            int index = taskList.getIndex(parts, "unmark");
            Task task = taskList.get(index);
            boolean wasDone = task.isDone();
            ui.showUnmarkedTask(task);
            task.setUndone();
            if (wasDone) {
                undoHistory.record(task::setDone, "Undid the last command:\n  [X] " + task.getDescription());
            }
            saveTasks(taskList, ui);
            ui.showSeparator();
        } catch (BobException e) {
            ui.showError(e);
        }
    }

    /**
     * Saves tasks and reports a storage problem without terminating Bob.
     *
     * @param taskList current task list to save
     * @param ui user interface used to report a storage problem
     */
    private static void saveTasks(TaskList taskList, Ui ui) {
        try {
            Storage.saveTasks(taskList.asList());
        } catch (StorageException e) {
            ui.showStorageError("I couldn't save the task list.");
        }
    }
}

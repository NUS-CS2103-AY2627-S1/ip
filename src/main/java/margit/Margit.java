package margit;

import java.time.LocalDate;

import margit.parser.Parser;
import margit.storage.Storage;
import margit.task.DeadlineTask;
import margit.task.EventTask;
import margit.task.SortOrder;
import margit.task.SortedTaskLists;
import margit.task.Task;
import margit.task.TaskDateTime;
import margit.task.TaskList;
import margit.task.TodoTask;
import margit.ui.Ui;

/** A command-line task manager that coordinates user interaction, tasks, and storage. */
public class Margit {
    private static final String DEFAULT_FILE_PATH = "./data/Margit.txt";

    /** Handles all console interaction. */
    private final Ui ui;
    /** Holds the application's current tasks. */
    private final TaskList tasks;
    /** Persists tasks between application runs. */
    private final Storage storage;

    /** Creates the application using its default save-file location. */
    public Margit() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates the application and loads any existing tasks from {@code filePath}.
     *
     * @param filePath location of the task save file
     */
    public Margit(String filePath) {
        ui = new Ui();
        tasks = new TaskList();
        storage = new Storage(filePath);
        storage.load(tasks);
    }

    /** Returns Margit's greeting for a newly opened user interface. */
    public String getGreeting() {
        return ui.formatGreeting();
    }

    /** Processes a command and returns the same response shown by the terminal interface. */
    public String getResponse(String input) {
        Parser.Command command = Parser.parse(input);

        if (command.type == Parser.CommandType.BYE) {
            return ui.formatFarewell();
        }
        if (command.type == Parser.CommandType.LIST) {
            return ui.formatTaskList(tasks);
        }
        if (command.type == Parser.CommandType.SORT) {
            return processSortCommand(command.argument);
        }
        if (command.type == Parser.CommandType.FIND) {
            if (command.argument.isEmpty()) {
                return ui.formatMissingFindKeyword();
            }
            return ui.formatFindResults(tasks.findByKeyword(command.argument));
        }
        if (command.type == Parser.CommandType.ON) {
            return processOnCommand(command.argument);
        }
        if (command.type == Parser.CommandType.MARK || command.type == Parser.CommandType.UNMARK) {
            return processStatusCommand(command);
        }
        if (command.type == Parser.CommandType.DELETE) {
            return processDeleteCommand(command);
        }
        if (command.type == Parser.CommandType.TODO) {
            return processTodoCommand(command.argument);
        }
        if (command.type == Parser.CommandType.DEADLINE) {
            return processDeadlineCommand(command.argument);
        }
        if (command.type == Parser.CommandType.EVENT) {
            return processEventCommand(command.argument);
        }
        return ui.formatUnknownCommand();
    }

    /** Runs the interactive command loop until the user enters {@code bye}. */
    public void run() {
        ui.showWelcome();
        while (true) {
            String input = ui.readCommand();
            if (Parser.parse(input).type == Parser.CommandType.BYE) {
                break;
            }
            ui.showResponse(getResponse(input));
        }

        ui.showFarewell();
        ui.close();
    }

    /** Processes an on command and returns its response. */
    private String processOnCommand(String datePart) {
        if (datePart.isEmpty()) {
            return ui.formatMissingDate();
        }

        LocalDate targetDate = TaskDateTime.parseDateOnly(datePart);
        if (targetDate == null) {
            return ui.formatInvalidDate();
        }

        TaskList matchingTasks = new TaskList();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(targetDate)) {
                matchingTasks.add(tasks.get(i));
            }
        }
        return ui.formatTasksOnDate(targetDate, matchingTasks);
    }

    /** Processes a sort command and returns the grouped, sorted task list. */
    private String processSortCommand(String argument) {
        if (!argument.isEmpty()) {
            return ui.formatInvalidSortCommand();
        }

        SortedTaskLists sortedTasks = tasks.sort(SortOrder.ASCENDING);
        storage.save(tasks);
        return ui.formatSortedTaskLists(sortedTasks);
    }

    /** Processes a mark or unmark command and returns its response. */
    private String processStatusCommand(Parser.Command command) {
        boolean isMarked = command.type == Parser.CommandType.MARK;
        if (command.taskIndex == null) {
            return ui.formatInvalidTaskNumber();
        }
        int index = command.taskIndex;
        if (index < 0 || index >= tasks.size()) {
            return ui.formatTaskNotFound();
        }

        boolean hasChanged = isMarked ? tasks.get(index).mark() : tasks.get(index).unmark();
        if (!hasChanged) {
            return ui.formatUnchangedTaskStatus(tasks.get(index), isMarked);
        }

        storage.save(tasks);
        return ui.formatChangedTaskStatus(tasks.get(index), isMarked);
    }

    /** Processes a delete command and returns its response. */
    private String processDeleteCommand(Parser.Command command) {
        if (command.taskIndex == null) {
            return ui.formatInvalidTaskNumber();
        }
        int index = command.taskIndex;
        if (index < 0 || index >= tasks.size()) {
            return ui.formatTaskNotFound();
        }

        Task removed = tasks.remove(index);
        storage.save(tasks);
        return ui.formatTaskDeleted(removed, tasks.size());
    }

    /** Processes a todo command and returns its response. */
    private String processTodoCommand(String argument) {
        Parser.TodoArguments todo = Parser.parseTodo(argument);
        if (!todo.isValid) {
            return ui.formatMissingTodoDescription();
        }
        if (tasks.isFull()) {
            return ui.formatTaskListFull();
        }

        tasks.add(new TodoTask(todo.description));
        storage.save(tasks);
        return ui.formatTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    /** Processes a deadline command and returns its response. */
    private String processDeadlineCommand(String argument) {
        Parser.DeadlineArguments deadline = Parser.parseDeadline(argument);
        if (deadline.status == Parser.DeadlineStatus.MISSING_SEPARATOR) {
            return ui.formatMissingDeadlineSeparator();
        }
        if (deadline.status == Parser.DeadlineStatus.MISSING_COMPONENT) {
            return ui.formatMissingDeadlineComponent();
        }
        if (tasks.isFull()) {
            return ui.formatTaskListFull();
        }

        tasks.add(new DeadlineTask(deadline.description, TaskDateTime.parse(deadline.byRaw)));
        storage.save(tasks);
        return ui.formatTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    /** Processes an event command and returns its response. */
    private String processEventCommand(String argument) {
        Parser.EventArguments event = Parser.parseEvent(argument);
        if (!event.isValid) {
            return ui.formatInvalidEvent();
        }
        if (tasks.isFull()) {
            return ui.formatTaskListFull();
        }

        tasks.add(new EventTask(event.description, TaskDateTime.parse(event.fromRaw),
                TaskDateTime.parse(event.toRaw)));
        storage.save(tasks);
        return ui.formatTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
    }

    /** Starts the application using its default save-file location. */
    public static void main(String[] args) {
        new Margit().run();
    }
}

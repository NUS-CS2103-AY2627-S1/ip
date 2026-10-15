package nova.command;

import java.util.ArrayList;

import nova.exception.NovaException;
import nova.parser.Parser;
import nova.storage.Storage;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.TaskList;
import nova.task.Todo;
import nova.ui.Ui;

/** Executes validated commands against the task list and persistence layer. */
public class CommandHandler {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /** Creates a handler for the application's shared collaborators. */
    public CommandHandler(Storage storage, TaskList tasks, Ui ui) {
        this.storage = storage;
        this.tasks = tasks;
        this.ui = ui;
    }

    /** Executes a parsed command and returns whether the application should exit. */
    public boolean execute(Parser.ParsedCommand command) throws NovaException {
        switch (command.type()) {
            case EXIT -> {
                CommandAction exitCommand = new ExitCommand();
                exitCommand.execute(tasks, ui, storage);
                return exitCommand.isExit();
            }
            case LIST -> ui.showTaskList(tasks);
            case FIND -> {
                ArrayList<Task> matchingTasks = tasks.findByKeyword(command.description());
                ui.showMatchingTasks(matchingTasks);
            }
            case ON -> ui.showTasksOnDate(command.date(), tasks.getTasksOnDate(command.date()));
            case MARK -> {
                int taskNumber = command.taskNumber();
                Task task = tasks.getByNumber(taskNumber);
                tasks.markAsDone(taskNumber);
                saveOrRollback(task::markAsNotDone);
                ui.showMarkedDone(task);
            }
            case UNMARK -> {
                int taskNumber = command.taskNumber();
                Task task = tasks.getByNumber(taskNumber);
                tasks.markAsNotDone(taskNumber);
                saveOrRollback(task::markAsDone);
                ui.showMarkedNotDone(task);
            }
            case DELETE -> {
                int taskNumber = command.taskNumber();
                Task deletedTask = tasks.removeByNumber(taskNumber);
                saveOrRollback(() -> tasks.add(taskNumber - 1, deletedTask));
                ui.showDeleted(deletedTask, tasks.size());
            }
            case TAG -> tagAndShow(command);
            case UNTAG -> untagAndShow(command);
            case TODO -> addAndShow(new Todo(command.description()));
            case DEADLINE -> addAndShow(new Deadline(command.description(), command.date()));
            case EVENT -> addAndShow(new Event(command.description(), command.from(), command.to()));
            default -> {
                assert false : "Every command type should have an execution branch";
                throw new NovaException("Unsupported command.");
            }
        }
        return false;
    }

    /** Adds a tag, persists the change, and reports the updated task. */
    private void tagAndShow(Parser.ParsedCommand command) throws NovaException {
        Task task = tasks.getByNumber(command.taskNumber());
        boolean added = tasks.addTag(command.taskNumber(), command.tag());
        saveOrRollback(() -> {
            if (added) {
                task.removeTag(command.tag());
            }
        });
        ui.showTagged(task);
    }

    /** Removes a tag, persists the change, and reports the updated task. */
    private void untagAndShow(Parser.ParsedCommand command) throws NovaException {
        Task task = tasks.getByNumber(command.taskNumber());
        tasks.removeTag(command.taskNumber(), command.tag());
        saveOrRollback(() -> task.addTag(command.tag()));
        ui.showUntagged(task);
    }

    /** Saves the current task state and applies the rollback if saving fails. */
    private void saveOrRollback(Runnable rollback) throws NovaException {
        try {
            storage.save(tasks);
        } catch (NovaException exception) {
            rollback.run();
            throw exception;
        }
    }

    /** Adds a task, persists it, and reports the successful addition. */
    private void addAndShow(Task task) throws NovaException {
        tasks.addIfNotDuplicate(task);
        assert tasks.size() > 0 && tasks.get(tasks.size() - 1) == task
                : "Added task should be present at the end of the task list";
        try {
            storage.save(tasks);
        } catch (NovaException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showAdded(tasks.get(tasks.size() - 1), tasks.size());
    }
}

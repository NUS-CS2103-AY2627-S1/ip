package dude.command.commands;

import java.util.List;

import dude.command.core.Command;
import dude.command.core.CommandResult;
import dude.command.core.CommandType;
import dude.command.core.Undoable;
import dude.command.duplicate.DuplicateResolution;
import dude.command.duplicate.DuplicateTaskConflict;
import dude.command.support.CommandEnvironment;
import dude.exception.CommandExecutionException;
import dude.exception.UsageException;
import dude.task.Deadline;
import dude.task.Event;
import dude.task.Task;
import dude.task.Todo;

/**
 * Creates todo, deadline, and event tasks.
 */
public final class AddCommand extends Command implements Undoable {
    private final CommandType commandType;
    private final String argument;
    private Task addedTask;
    private Task previousTask;
    private int changedIndex = -1;
    private boolean editedExistingTask;

    /**
     * Creates an add command.
     *
     * @param commandType Task type to create.
     * @param argument    Raw task details.
     * @param environment Command collaborators and receiver.
     */
    public AddCommand(CommandType commandType, String argument, CommandEnvironment environment) {
        super(environment);
        this.commandType = commandType;
        this.argument = argument;
    }

    @Override
    public CommandResult execute() throws UsageException, CommandExecutionException {
        Task task = createTask();
        List<Integer> duplicateIndexes = receiver().findDuplicateIndexes(task.getDescription());
        if (!duplicateIndexes.isEmpty()) {
            List<DuplicateTaskConflict.DuplicateMatch> matches = duplicateIndexes.stream()
                    .map(index -> new DuplicateTaskConflict.DuplicateMatch(index, tasks().get(index)))
                    .toList();
            DuplicateResolution resolution = environment().resolutionHandler()
                    .resolve(new DuplicateTaskConflict(task, matches));
            if (resolution.action() == DuplicateResolution.Action.CANCEL) {
                return CommandResult.continueRunning();
            }
            if (resolution.action() == DuplicateResolution.Action.EDIT) {
                changedIndex = resolution.taskIndex();
                previousTask = receiver().replace(changedIndex, task);
                if (previousTask.isDone()) {
                    task.markAsDone();
                    receiver().replace(changedIndex, task);
                }
                addedTask = task;
                editedExistingTask = true;
                environment().ui().showEditedTask(task);
                save();
                return CommandResult.continueRunning();
            }
        }

        receiver().add(task);
        addedTask = task;
        changedIndex = tasks().size() - 1;
        environment().ui().showAddedTask(task, tasks().size());
        save();
        return CommandResult.continueRunning();
    }

    /**
     * Creates the concrete task represented by this add command.
     *
     * @return Parsed task.
     * @throws UsageException If the task details are invalid.
     */
    private Task createTask() throws UsageException {
        return switch (commandType) {
        case TODO -> Todo.fromInput(argument);
        case DEADLINE -> Deadline.fromInput(argument);
        case EVENT -> Event.fromInput(argument);
        default -> throw new IllegalArgumentException("Unsupported add command");
        };
    }

    @Override
    public void undo() {
        if (editedExistingTask) {
            receiver().replace(changedIndex, previousTask);
        } else {
            receiver().remove(changedIndex);
        }
    }

    @Override
    public void redo() {
        if (editedExistingTask) {
            receiver().replace(changedIndex, addedTask);
        } else {
            receiver().insert(changedIndex, addedTask);
        }
    }

    @Override
    public boolean isAvailableForUndo() {
        return addedTask != null;
    }

    @Override
    public String undoDescription() {
        return editedExistingTask ? "edit duplicate task" : "add task";
    }
}

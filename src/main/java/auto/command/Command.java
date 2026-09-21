package auto.command;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import auto.exception.AutoException;
import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;

/** Represents an executable user command. */
public abstract class Command {
    /**
     * Executes this command against the current application collaborators.
     *
     * @param tasks   Task list to query or modify.
     * @param storage Storage used to persist task-list mutations.
     * @return Result to display to the user.
     * @throws AutoException If the command refers to invalid user input or state.
     */
    public abstract CommandResult execute(TaskList tasks, Storage storage)
            throws AutoException;

    /** Saves a mutation, returning whether persistence succeeded. */
    protected boolean saveTasks(TaskList tasks, Storage storage) {
        try {
            storage.save(tasks.asList());
            return true;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    /** Returns the standard result shown after a mutation cannot be saved. */
    protected CommandResult storageFailure() {
        return new CommandResult(
                "Paiseh, I couldn't save your tasks. Your latest change was not applied.");
    }

    /** Formats selected tasks using their original one-based list numbers. */
    protected String formatTasks(String heading, List<Task> tasks, Predicate<Task> selection) {
        String taskLines = IntStream.range(0, tasks.size())
                .filter(i -> selection.test(tasks.get(i)))
                .mapToObj(i -> String.format("%d. %s", i + 1, tasks.get(i)))
                .collect(Collectors.joining(System.lineSeparator()));

        return taskLines.isEmpty() ? heading : heading + System.lineSeparator() + taskLines;
    }
}

package grug.command;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import grug.storage.TaskStorage;
import grug.task.TaskList;

/**
 * Command to list all tasks.
 */
public record ListTasksCommand() implements GrugCommand {
    /**
     * Lists all tasks in the tasks list.
     *
     * @param tasks   The task list to read from.
     * @param storage The task storage that is never used. Can be {@code null}.
     * @return A {@link CommandResult.Ok} result containing the list of tasks
     *         separated by newlines in {@code message} (or an appropriate message
     *         if task list is empty) and {@code shouldExit: false}.
     */
    @Override
    public CommandResult execute(TaskList tasks, TaskStorage storage) {
        if (tasks.isEmpty()) {
            return new CommandResult.Ok("Buh. No tasks added");
        }

        String msg = IntStream.range(0, tasks.getSize())
                .mapToObj(i -> "%d. %s".formatted(i + 1, tasks.getTaskUnchecked(i)))
                .collect(Collectors.joining("\n"));

        return new CommandResult.Ok(msg);
    }
}

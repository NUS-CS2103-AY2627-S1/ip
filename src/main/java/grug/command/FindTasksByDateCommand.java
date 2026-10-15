package grug.command;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import grug.storage.TaskStorage;
import grug.task.TaskList;

/**
 * Command to list tasks that coincide with a given date.
 */
public record FindTasksByDateCommand(LocalDate date) implements GrugCommand {
    /**
     * Lists all tasks in the tasks list that coincide with the target
     * {@link #date()}.
     *
     * @param tasks   The task list to read from.
     * @param storage The task storage that is never used. Can be {@code null}.
     * @return A {@link CommandResult.Ok} result containing the list of tasks
     *         coinciding the the {@link #date()} separated by newlines in
     *         {@code message} (or an appropriate message
     *         if task list is empty or no tasks coincide with the {@link #date()})
     *         and {@code shouldExit: false}.
     */
    @Override
    public CommandResult execute(TaskList tasks, TaskStorage storage) {
        if (tasks.isEmpty()) {
            return new CommandResult.Ok("Buh. No tasks added");
        }

        String msg = IntStream.range(0, tasks.getSize())
                .filter(i -> tasks.getTaskUnchecked(i).doesOccurOn(date))
                .mapToObj(i -> "%d. %s".formatted(i + 1, tasks.getTaskUnchecked(i)))
                .collect(Collectors.joining("\n"));

        if (msg.isEmpty()) {
            return new CommandResult.Ok("Buh. No tasks occuring on that date");
        }

        return new CommandResult.Ok(msg.toString().stripTrailing());
    }
}

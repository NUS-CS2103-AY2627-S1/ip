package grug.command;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import grug.storage.TaskStorage;
import grug.task.TaskList;

/**
 * Command to list tasks whose details contain the {@code detailsSubstring},
 * case insensitive, ignoring extra whitespace/newlines.
 *
 * @param detailsSubstring The details substring to use to search for the tasks.
 */
public record FindTasksByDetailsCommand(String detailsSubstring) implements GrugCommand {
    /**
     * Lists all tasks in the tasks list that contain the
     * {@link #detailsSubstring()} (case insensitive, ignoring extra
     * whitespace/newlines).
     *
     * @param tasks   The task list to read from.
     * @param storage The task storage that is never used. Can be {@code null}.
     * @return A {@link CommandResult.Ok} result containing the list of tasks
     *         containing the {@link #detailsSubstring()} separated by newlines in
     *         {@code message} (or an appropriate message
     *         if task list is empty or no tasks coincide with the {@link #date()})
     *         and {@code shouldExit: false}.
     */
    @Override
    public CommandResult execute(TaskList tasks, TaskStorage storage) {
        if (tasks.isEmpty()) {
            return new CommandResult.Ok("Buh. No tasks added");
        }

        String targetLower = detailsSubstring.strip().replaceAll("\\s+", " ").toLowerCase();
        String msg = IntStream.range(0, tasks.getSize())
                .filter(i -> tasks.getTaskUnchecked(i).getDetails().toLowerCase().contains(targetLower))
                .mapToObj(i -> "%d. %s".formatted(i + 1, tasks.getTaskUnchecked(i)))
                .collect(Collectors.joining("\n"));

        if (msg.isEmpty()) {
            return new CommandResult.Ok("Buh. No matching tasks found.");
        }

        return new CommandResult.Ok(msg.toString().stripTrailing());
    }
}

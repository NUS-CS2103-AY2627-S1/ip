package sioet.command;

import java.time.LocalDateTime;

import sioet.SioetException;
import sioet.storage.Storage;
import sioet.task.Deadline;
import sioet.task.Task;
import sioet.task.TaskList;
import sioet.ui.Ui;

/**
 * Represents the command that adds a deadline task.
 */
public class DeadlineCommand extends Command {
    private final String taskText;

    /**
     * Creates a deadline command.
     *
     * @param taskText the deadline details
     */
    public DeadlineCommand(String taskText) {
        this.taskText = taskText;
    }

    /**
     * Executes the deadline command.
     *
     * @param tasks   the task list
     * @param ui      the user interface
     * @param storage the task storage
     * @throws SioetException if the deadline format is invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws SioetException {
        int byMarkerIndex = taskText.indexOf(" /by ");

        if (byMarkerIndex < 1
                || byMarkerIndex + " /by ".length() >= taskText.length()) {
            throw new SioetException(
                    "use: deadline DESCRIPTION /by DATE. "
                            + "Example: deadline final project /by 1/2/2034 0000");
        }

        String description = taskText.substring(0, byMarkerIndex).trim();
        String byText = taskText.substring(
                byMarkerIndex + " /by ".length()).trim();

        LocalDateTime by = Parser.parseDateTime(byText);

        Task task = new Deadline(description, by);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}

package brucli.command;

import java.io.IOException;
import java.time.LocalDateTime;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.Ui;

/**
 * Adds a deadline task with a due time.
 */
public class DeadlineCommand extends AddCommand {
    private final LocalDateTime due;

    /**
     * Creates a deadline command with its description and due time.
     */
    public DeadlineCommand(String description, LocalDateTime due) {
        super(description);
        this.due = due;
    }

    /**
     * Adds the deadline, saves the list, and displays confirmation.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        tasks.addDeadline(description, due);
        saveTasks(tasks, storage);
        ui.showDeadlineAdded();
    }
}

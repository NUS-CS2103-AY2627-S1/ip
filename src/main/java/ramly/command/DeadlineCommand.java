package ramly.command;

import ramly.model.Deadline;
import ramly.model.Task;

/** Command that adds a deadline task. */
public class DeadlineCommand extends AddTaskCommand {
    private final String description;
    private final String deadline;
    /** Creates a deadline command for the supplied description and deadline. */
    public DeadlineCommand(String description, String deadline) {
        this.description = description;
        this.deadline = deadline;
    }
    @Override
    protected Task createTask() {
        return new Deadline(description, deadline);
    }
}

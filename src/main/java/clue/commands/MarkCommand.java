package clue.commands;

import clue.commands.base.TaskActionWithIndexCommand;
import clue.components.TaskList;
import clue.tasks.Task;

/** Command that marks a selected task as done. */
public class MarkCommand extends TaskActionWithIndexCommand {
    public static final String NAME = "mark";

    /** Creates a MarkCommand that operates on the given TaskList */
    public MarkCommand(TaskList tasks) {
        super(tasks,
                "which task to mark",
                "Woohoo! I've marked this task as done:");
    }

    @Override
    protected void action(Task task, int index) {
        task.setDone(true);
    }
}

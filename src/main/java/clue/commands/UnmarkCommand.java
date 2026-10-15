package clue.commands;

import clue.commands.base.TaskActionWithIndexCommand;
import clue.components.TaskList;
import clue.tasks.Task;

/** Command that unmarks a selected task, recording it as undone. */
public class UnmarkCommand extends TaskActionWithIndexCommand {
    public static final String NAME = "unmark";

    /** Creates an UnmarkCommand that operates on the given TaskList */
    public UnmarkCommand(TaskList tasks) {
        super(tasks,
                "which task to unmark",
                "OK, I've marked this task as unfinished::");
    }

    @Override
    protected void action(Task task, int index) {
        task.setDone(false);
    }
}

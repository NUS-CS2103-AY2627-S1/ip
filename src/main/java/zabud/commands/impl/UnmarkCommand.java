package zabud.commands.impl;

import java.util.List;

import zabud.commands.Buildable;
import zabud.commands.Command;
import zabud.commands.ParsedToken;
import zabud.commands.Validatable;
import zabud.session.Session;
import zabud.tasks.Task;

/**
 * Validates and builds commands that mark tasks as not done.
 */
public final class UnmarkCommand implements Validatable, Buildable {
    /**
     * The user input that selects this command type.
     */
    public static final String COMMAND = "unmark";

    /**
     * Creates an unmark command handler.
     */
    public UnmarkCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(List<ParsedToken> tokens, Session session) {
        return Validatable.hasExistingTaskNumber(tokens, session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hint() {
        return Validatable.formatHint(COMMAND + " " + TASK_NUMBER, TASK_NUMBER_REQUIREMENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command build(List<ParsedToken> tokens, Session session) {
        int taskNumber = Validatable.taskNumber(tokens);
        return new Command(session) {
            @Override
            public void execute() {
                Task task = session.getTaskList().get(taskNumber);
                task.markAsNotDone();
                session.save();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + task);
            }
        };
    }
}

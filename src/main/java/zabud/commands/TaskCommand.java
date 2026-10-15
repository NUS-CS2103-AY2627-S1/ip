package zabud.commands;

import zabud.session.Session;
import zabud.tasks.Task;

/**
 * Provides shared behavior for commands that add a task.
 */
public abstract class TaskCommand extends Command {
    /**
     * Creates a command that adds a task.
     *
     * @param session the current session.
     */
    protected TaskCommand(Session session) {
        super(session);
    }

    /**
     * Adds a task and prints the common confirmation.
     *
     * @param task the validated task to add.
     */
    protected void addTask(Task task) {
        if (session.getTaskList().add(task)) {
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + task);
        } else {
            System.out.println(" Sorry, your task list is full.");
        }
    }
}

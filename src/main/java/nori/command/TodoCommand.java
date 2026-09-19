package nori.command;

import nori.NoriException;
import nori.storage.Storage;
import nori.task.TaskList;
import nori.task.Todo;
import nori.ui.Ui;

/**
 * Adds a todo task.
 */
class TodoCommand extends AddTaskCommand {
    /**
     * Creates a command that adds a todo.
     *
     * @param details the description after the {@code todo} keyword.
     */
    TodoCommand(String details) {
        super(details);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NoriException {
        if (details.isEmpty()) {
            ui.showError("NOOT?! A todo needs a description."
                    + " Try \"todo borrow book\"; my flippers cannot read minds.");
            return;
        }
        addTask(tasks, ui, storage, new Todo(details));
    }
}

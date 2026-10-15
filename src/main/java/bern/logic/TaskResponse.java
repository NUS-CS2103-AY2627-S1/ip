package bern.logic;

import java.util.List;

import bern.task.Deadline;
import bern.task.Event;
import bern.task.Task;
import bern.task.Todo;
import bern.ui.DeadlineBox;
import bern.ui.DialogBox;
import bern.ui.EventBox;
import bern.ui.TodoBox;
import javafx.scene.Node;

/**
 * Represents a response from Bern that contains a task and an optional message.
 */
public class TaskResponse extends Response {
    private Task task;

    /**
     * Creates a response displaying a task without a message.
     *
     * @param task The task to be included in the response.
     */
    public TaskResponse(Task task) {
        this.task = task;
        this.text = "";
    }

    /**
     * Creates a response displaying a task with a message.
     *
     * @param task The task to be included in the response.
     * @param text The text to be included in the response.
     */
    public TaskResponse(Task task, String text) {
        this.task = task;
        this.text = text;
    }

    /**
     * Creates a task card, enclosing it in Bern's dialog box when a message is present.
     *
     * @return A one-element list containing the task card and optional message.
     * @throws UnsupportedOperationException If the task is not a todo, deadline, or event.
     */
    @Override
    public List<Node> getResponseNodes() {
        Node taskBox;
        if (task instanceof Todo) {
            taskBox = new TodoBox((Todo) task);
        } else if (task instanceof Deadline) {
            taskBox = new DeadlineBox((Deadline) task);
        } else if (task instanceof Event) {
            taskBox = new EventBox((Event) task);
        } else {
            throw new UnsupportedOperationException("Not yet implemented for this task type");
        }

        if (!this.text.isEmpty()) {
            DialogBox dialog = DialogBox.getBernDialog(text);
            dialog.add(taskBox);
            return List.of(dialog);
        }

        return List.of(taskBox);
    }
}

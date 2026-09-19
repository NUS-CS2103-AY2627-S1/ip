package daddy.gui;

import java.util.List;

import daddy.task.Task;
import daddy.task.TaskType;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Presents Daddy's tasks as graphical cards in the chat conversation.
 */
public class TaskListView extends VBox {
    /**
     * Creates a card-based view of the supplied tasks.
     *
     * @param tasks the tasks to display in their list order
     */
    public TaskListView(List<Task> tasks) {
        setSpacing(10.0);
        setMaxWidth(Double.MAX_VALUE);
        getStyleClass().add("task-list-view");
        getChildren().add(createListHeading(tasks.size()));

        if (tasks.isEmpty()) {
            getChildren().add(createEmptyMessage());
            return;
        }

        int taskNumber = 1;
        for (Task task : tasks) {
            getChildren().add(createTaskCard(task, taskNumber));
            taskNumber++;
        }
    }

    /**
     * Creates the heading that states how many tasks are in the view.
     *
     * @param taskCount the number of displayed tasks
     * @return the list heading
     */
    private HBox createListHeading(int taskCount) {
        Label heading = new Label("Your task list");
        heading.getStyleClass().add("task-list-heading");
        Label count = new Label(taskCount + (taskCount == 1 ? " task" : " tasks"));
        count.getStyleClass().add("task-count");

        HBox headingRow = new HBox(8.0, heading, count);
        HBox.setHgrow(heading, Priority.ALWAYS);
        headingRow.setAlignment(Pos.CENTER_LEFT);
        return headingRow;
    }

    /**
     * Creates a friendly prompt for an empty task list.
     *
     * @return the empty-list prompt
     */
    private Label createEmptyMessage() {
        Label emptyMessage = new Label("No tasks yet. Try: todo borrow book");
        emptyMessage.getStyleClass().add("empty-task-list");
        return emptyMessage;
    }

    /**
     * Creates one styled task card with its status, type, and any date details.
     *
     * @param task the task to display
     * @param taskNumber the one-based task number
     * @return the graphical task card
     */
    private HBox createTaskCard(Task task, int taskNumber) {
        Label status = createStatus(task);
        Label number = new Label(taskNumber + ".");
        number.getStyleClass().add("task-number");
        Label description = new Label(task.getDescription());
        description.setWrapText(true);
        description.getStyleClass().add("task-description");
        Label type = new Label(getTaskTypeLabel(task.getType()));
        type.getStyleClass().addAll("task-type", getTaskTypeStyle(task.getType()));

        HBox titleRow = new HBox(7.0, number, description, type);
        HBox.setHgrow(description, Priority.ALWAYS);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox taskDetails = new VBox(3.0, titleRow);
        String dateDetails = getDateDetails(task);
        if (!dateDetails.isEmpty()) {
            Label date = new Label(dateDetails);
            date.getStyleClass().add("task-date");
            taskDetails.getChildren().add(date);
        }

        HBox taskCard = new HBox(10.0, status, taskDetails);
        HBox.setHgrow(taskDetails, Priority.ALWAYS);
        taskCard.setAlignment(Pos.CENTER_LEFT);
        taskCard.getStyleClass().add("task-card");
        return taskCard;
    }

    /**
     * Creates the completion indicator for a task.
     *
     * @param task the task whose completion state is displayed
     * @return the graphical completion indicator
     */
    private Label createStatus(Task task) {
        boolean isDone = task.getStatusIcon().equals("X");
        Label status = new Label(isDone ? "✓" : "○");
        status.setAlignment(Pos.CENTER);
        status.getStyleClass().addAll("task-status", isDone ? "task-done" : "task-open");
        return status;
    }

    /**
     * Returns a short, readable label for a task type.
     *
     * @param taskType the task type to describe
     * @return the type label
     */
    private String getTaskTypeLabel(TaskType taskType) {
        return switch (taskType) {
            case TODO -> "TODO";
            case DEADLINE -> "DEADLINE";
            case EVENT -> "EVENT";
            case GENERAL -> "TASK";
        };
    }

    /**
     * Returns the style class associated with a task type.
     *
     * @param taskType the task type to style
     * @return the matching CSS style class
     */
    private String getTaskTypeStyle(TaskType taskType) {
        return switch (taskType) {
            case TODO -> "task-type-todo";
            case DEADLINE -> "task-type-deadline";
            case EVENT -> "task-type-event";
            case GENERAL -> "task-type-general";
        };
    }

    /**
     * Returns the type-specific date and time portion of a task's display text.
     *
     * @param task the task whose date details are needed
     * @return the date details, or an empty string for a todo
     */
    private String getDateDetails(Task task) {
        String displayDescription = task.getDisplayDescription();
        return displayDescription.substring(task.getDescription().length()).trim();
    }
}

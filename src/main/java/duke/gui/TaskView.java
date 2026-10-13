package duke.gui;

import java.util.ArrayList;
import java.util.List;

import duke.Task;
import duke.TaskMatch;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Displays task snapshots with a separate marker column and aligned, wrapping detail lines. */
final class TaskView extends VBox {
    /** Builds a snapshot so later task changes cannot rewrite an earlier chat reply. */
    private TaskView(String heading, List<TaskMatch> matches, boolean isNumbered, String footer) {
        super(12);
        getStyleClass().add("task-view");
        StringBuilder accessible = new StringBuilder(heading);
        if (!heading.isEmpty()) {
            getChildren().add(text(heading, "task-heading"));
        }
        if (!matches.isEmpty()) {
            getChildren().add(createRows(matches, isNumbered, accessible));
        }
        if (!footer.isEmpty()) {
            getChildren().add(text(footer, "task-summary"));
            accessible.append("\n\n").append(footer);
        }
        setAccessibleText(accessible.toString());
    }

    /** Shows all tasks with their current list numbers, or a useful empty-list message. */
    static TaskView tasks(List<Task> tasks) {
        return numberedTasks("Your tasks:", tasks);
    }

    /** Shows the newly saved order after sorting. */
    static TaskView sorted(List<Task> tasks) {
        return numberedTasks("Sorted: earliest deadlines first, then tasks without dates.\n\nYour tasks:", tasks);
    }

    /** Keeps full-list numbers in search results instead of renumbering matches. */
    static TaskView matches(List<TaskMatch> matches) {
        String heading = matches.isEmpty() ? TaskFormatter.renderMatches(matches) : "Matching tasks:";
        return new TaskView(heading, matches, true, "");
    }

    /** Shows one changed task and its progress summary using the same alignment as a list. */
    static TaskView confirmation(String heading, Task task, String summary) {
        return new TaskView(heading, List.of(new TaskMatch(1, task)), false, summary);
    }

    /** Assigns the same one-based task numbers as the command interface. */
    private static TaskView numberedTasks(String heading, List<Task> tasks) {
        List<TaskMatch> matches = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            matches.add(new TaskMatch(i + 1, tasks.get(i)));
        }
        return new TaskView(tasks.isEmpty() ? TaskFormatter.renderTasks(tasks) : heading, matches, true, "");
    }

    /** Creates a shared marker column for bullets and numbers of any supported width. */
    private GridPane createRows(List<TaskMatch> matches, boolean isNumbered, StringBuilder accessible) {
        GridPane rows = new GridPane();
        rows.setHgap(8);
        rows.setVgap(14);
        ColumnConstraints markerColumn = new ColumnConstraints();
        markerColumn.setMinWidth(Region.USE_PREF_SIZE);
        ColumnConstraints contentColumn = new ColumnConstraints();
        contentColumn.setMinWidth(0);
        contentColumn.setHgrow(Priority.ALWAYS);
        rows.getColumnConstraints().addAll(markerColumn, contentColumn);
        for (int i = 0; i < matches.size(); i++) {
            TaskMatch match = matches.get(i);
            String marker = isNumbered ? match.taskNumber() + "." : "•";
            Label markerLabel = text(marker, "task-marker");
            markerLabel.setMinWidth(Region.USE_PREF_SIZE);
            GridPane.setValignment(markerLabel, javafx.geometry.VPos.TOP);
            rows.add(markerLabel, 0, i);
            rows.add(createTaskContent(match.task()), 1, i);
            accessible.append("\n\n").append(marker).append(" ").append(TaskFormatter.formatTask(match.task()));
        }
        return rows;
    }

    /** Places the title and all details in one column, including when any line wraps. */
    private VBox createTaskContent(Task task) {
        VBox content = new VBox(4);
        content.setMinWidth(0);
        content.getStyleClass().add("task-content");
        content.getChildren().add(text(TaskFormatter.formatTitle(task), "task-title"));
        for (String detail : TaskFormatter.formatDetails(task)) {
            content.getChildren().add(text(detail, "task-detail"));
        }
        return content;
    }

    /** Keeps text literal and fully visible rather than truncating long descriptions or times. */
    private static Label text(String value, String styleClass) {
        Label label = new Label(value);
        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add(styleClass);
        return label;
    }
}

package wwaffle.gui;

import java.util.List;
import java.util.stream.IntStream;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import wwaffle.task.Deadline;
import wwaffle.task.Event;
import wwaffle.task.Task;

/** Read-only checklist snapshot; task changes still use the existing commands. */
public class TaskChecklist extends VBox {
    /**
     * Builds separate rows from task data without parsing the task's display string.
     *
     * @param tasks Tasks in their current command-number order.
     */
    public TaskChecklist(List<Task> tasks) {
        this(tasks, 1);
    }

    /**
     * Builds rows retaining their original command numbers, including a single selected task.
     *
     * @param tasks Tasks to display.
     * @param firstNumber Command number of the first task.
     */
    public TaskChecklist(List<Task> tasks, int firstNumber) {
        this(tasks, IntStream.range(firstNumber, firstNumber + tasks.size()).boxed().toList());
    }

    /**
     * Builds search-result rows using their actual task numbers.
     *
     * @param tasks Tasks to show.
     * @param numbers Corresponding one-based command numbers.
     */
    public TaskChecklist(List<Task> tasks, List<Integer> numbers) {
        Font.loadFont(TaskChecklist.class.getResourceAsStream("/fonts/Inter.ttf"), 15);
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        getStyleClass().add("task-checklist");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            Label status = new Label(task.isDone() ? "✓" : "");
            status.getStyleClass().add("checklist-status");
            status.setAccessibleText(task.isDone() ? "Completed" : "Incomplete");
            Text description = new Text(task.getDescription());
            description.getStyleClass().add("checklist-description");
            description.setStrikethrough(task.isDone());
            if (task.isDone()) {
                description.getStyleClass().add("checklist-done-description");
            }
            TextFlow title = new TextFlow(description);
            title.setMinWidth(0);
            title.setMinHeight(Region.USE_PREF_SIZE);
            String details = "TODO";
            if (task instanceof Deadline deadline) {
                details = "DEADLINE · Due " + deadline.getDueDate();
            } else if (task instanceof Event event) {
                details = "EVENT · " + event.getStart() + " → " + event.getEnd();
            }
            Label metadata = new Label(details);
            metadata.getStyleClass().add("checklist-meta");
            metadata.setWrapText(true);
            metadata.setMinHeight(Region.USE_PREF_SIZE);
            VBox content = new VBox(4, title, metadata);
            content.setMinWidth(0);
            HBox.setHgrow(content, Priority.ALWAYS);
            Label number = new Label(Integer.toString(numbers.get(i)));
            number.getStyleClass().add("checklist-number");
            number.setMinWidth(Region.USE_PREF_SIZE);
            HBox row = new HBox(12, status, content, number);
            row.setAlignment(Pos.TOP_LEFT);
            row.getStyleClass().add("checklist-row");
            if (i == tasks.size() - 1) {
                row.getStyleClass().add("checklist-row-last");
            }
            getChildren().add(row);
        }
    }
}

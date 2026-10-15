package carlo.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import carlo.task.CarloDateTime;
import carlo.task.Deadline;
import carlo.task.Event;
import carlo.task.Task;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * A pop-up form for adding a new task, or editing an existing one, without
 * typing a command.
 *
 * <p>Instead of creating the task itself, the form turns what the user
 * entered into the same text command the command bar accepts (for example
 * {@code deadline essay /by 2026-10-15 1800}). That way there is only one
 * place in {@code Carlo} that checks and creates tasks.
 *
 * <p>Use {@link #forAdding(String)} or {@link #forEditing(String, Task)}
 * to create one. These "static factory methods" have clearer names than
 * two constructors with different parameters would.
 */
public class TaskFormDialog extends Dialog<String> {
    private static final String TODO = "Todo";
    private static final String DEADLINE = "Deadline";
    private static final String EVENT = "Event";
    private static final DateTimeFormatter TIME_INPUT_FORMAT = DateTimeFormatter.ofPattern("HHmm");

    private final ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(TODO, DEADLINE, EVENT));
    private final TextField descriptionField = new TextField();
    private final Label startLabel = new Label();
    private final DatePicker startDate = new DatePicker(LocalDate.now());
    private final TextField startTime = new TextField();
    private final Label endLabel = new Label("To");
    private final DatePicker endDate = new DatePicker(LocalDate.now());
    private final TextField endTime = new TextField();

    private TaskFormDialog(String stylesheet, String title, String confirmText) {
        setTitle(title);
        setHeaderText(null);
        getDialogPane().getStylesheets().add(stylesheet);
        getDialogPane().getStyleClass().add("task-form-dialog");

        ButtonType confirmButton = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        descriptionField.setPromptText("Revise for midterms");
        startTime.setPromptText("1800 (optional)");
        endTime.setPromptText("1800 (optional)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Type"), typeBox);
        grid.addRow(1, new Label("What"), descriptionField);
        grid.addRow(2, startLabel, startDate, startTime);
        grid.addRow(3, endLabel, endDate, endTime);
        getDialogPane().setContent(grid);

        typeBox.valueProperty().addListener((observable, oldType, newType) -> updateFieldsFor(newType));
        typeBox.setValue(TODO);

        // Turns the form into a command when the confirm button is pressed; returns null for "Cancel".
        setResultConverter(button -> button == confirmButton ? toCommand() : null);
        setOnShown(event -> descriptionField.requestFocus());
    }

    /**
     * Creates an empty form for adding a new task.
     *
     * @param stylesheet the stylesheet URL to style the form with
     * @return the form
     */
    public static TaskFormDialog forAdding(String stylesheet) {
        return new TaskFormDialog(stylesheet, "Add a task", "Add");
    }

    /**
     * Creates a form filled in with an existing task's details, for editing it.
     *
     * @param stylesheet the stylesheet URL to style the form with
     * @param task the task to edit
     * @return the form
     */
    public static TaskFormDialog forEditing(String stylesheet, Task task) {
        TaskFormDialog dialog = new TaskFormDialog(stylesheet, "Edit task", "Save");
        dialog.fillFrom(task);
        return dialog;
    }

    private void fillFrom(Task task) {
        descriptionField.setText(task.getDescription());
        if (task instanceof Deadline deadline) {
            typeBox.setValue(DEADLINE);
            fillDateTime(deadline.getDueDateTime(), startDate, startTime);
        } else if (task instanceof Event event) {
            typeBox.setValue(EVENT);
            fillDateTime(event.getStartDateTime(), startDate, startTime);
            fillDateTime(event.getEndDateTime(), endDate, endTime);
        } else {
            typeBox.setValue(TODO);
        }
    }

    private static void fillDateTime(CarloDateTime dateTime, DatePicker datePicker, TextField timeField) {
        if (!dateTime.isParsed()) {
            return;
        }
        datePicker.setValue(dateTime.getDateTime().toLocalDate());
        timeField.setText(dateTime.hasTime() ? dateTime.getDateTime().format(TIME_INPUT_FORMAT) : "");
    }

    /**
     * Shows only the date fields that make sense for the chosen task type.
     */
    private void updateFieldsFor(String type) {
        boolean hasStart = !TODO.equals(type);
        boolean hasEnd = EVENT.equals(type);
        startLabel.setText(EVENT.equals(type) ? "From" : "Due");

        setShown(hasStart, startLabel, startDate, startTime);
        setShown(hasEnd, endLabel, endDate, endTime);
        // Resize the pop-up to fit the fields now shown (only once it is on screen).
        if (getDialogPane().getScene() != null && getDialogPane().getScene().getWindow() != null) {
            getDialogPane().getScene().getWindow().sizeToScene();
        }
    }

    private static void setShown(boolean isShown, Node... nodes) {
        for (Node node : nodes) {
            node.setVisible(isShown);
            node.setManaged(isShown);
        }
    }

    private String toCommand() {
        String description = descriptionField.getText().trim();
        return switch (typeBox.getValue()) {
            case DEADLINE -> "deadline " + description + " /by " + dateTimeText(startDate, startTime);
            case EVENT -> "event " + description + " /from " + dateTimeText(startDate, startTime)
                    + " /to " + dateTimeText(endDate, endTime);
            default -> "todo " + description;
        };
    }

    /**
     * Combines a date picker and an optional time box into text such as
     * {@code 2026-10-15} or {@code 2026-10-15 1800}.
     */
    private static String dateTimeText(DatePicker datePicker, TextField timeField) {
        LocalDate date = datePicker.getValue() == null ? LocalDate.now() : datePicker.getValue();
        String time = timeField.getText().trim();
        return time.isEmpty() ? date.toString() : date + " " + time;
    }
}

package kia.ui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import kia.Kia;
import kia.task.Deadline;
import kia.task.Event;
import kia.task.Task;

/**
 * FXML controller for Kia's JavaFX chatbot window.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private VBox editPanel;

    @FXML
    private ComboBox<String> taskSelector;

    @FXML
    private ComboBox<String> editTypeSelector;

    @FXML
    private TextField editDescription;

    @FXML
    private TextField editBy;

    @FXML
    private TextField editFrom;

    @FXML
    private TextField editTo;

    private Kia kia;
    private List<Task> editableTasks = List.of();

    /** Keeps the latest conversation visible after new messages are added. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(DialogBox.getKiaDialog(
                "Heyo! I'm Kia.\nWhat can I do for you?"));
        editTypeSelector.getItems().addAll("todo", "deadline", "event");
        taskSelector.valueProperty().addListener((observable, oldValue, newValue) -> populateEditFields());
        editTypeSelector.valueProperty().addListener((observable, oldValue, newValue) -> updateEditFieldState());
    }

    /**
     * Injects the chatbot logic used to process GUI commands.
     *
     * @param kia chatbot service for this window
     */
    public void setKia(Kia kia) {
        this.kia = kia;
        refreshTaskSelector();
    }

    /**
     * Displays the user's command and Kia's response, then clears the input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        String response = kia.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getKiaDialog(response));
        userInput.clear();
    }

    /** Opens the edit form and populates it with the current task list. */
    @FXML
    private void showEditPanel() {
        refreshTaskSelector();
        if (editableTasks.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getKiaDialog("Hey!!! There are no tasks to edit."));
            return;
        }
        editPanel.setManaged(true);
        editPanel.setVisible(true);
        taskSelector.getSelectionModel().select(0);
        populateEditFields();
    }

    /** Hides the edit form without changing any task. */
    @FXML
    private void cancelEdit() {
        editPanel.setVisible(false);
        editPanel.setManaged(false);
    }

    /** Sends the edit form values through Kia's normal update command path. */
    @FXML
    private void saveEdit() {
        int selectedIndex = taskSelector.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= editableTasks.size()) {
            return;
        }

        String command = buildUpdateCommand(selectedIndex);
        dialogContainer.getChildren().add(DialogBox.getUserDialog(command));
        String response = kia.getResponse(command);
        dialogContainer.getChildren().add(DialogBox.getKiaDialog(response));
        if (response.startsWith("Got it!") || response.startsWith("Nothing changed.")) {
            refreshTaskSelector();
            cancelEdit();
        }
    }

    /** Refreshes the edit form's task choices from a read-only Kia snapshot. */
    private void refreshTaskSelector() {
        if (kia == null) {
            return;
        }
        editableTasks = kia.getTasksSnapshot();
        taskSelector.getItems().clear();
        for (int i = 0; i < editableTasks.size(); i++) {
            taskSelector.getItems().add((i + 1) + ". " + editableTasks.get(i));
        }
        if (!editableTasks.isEmpty()) {
            taskSelector.getSelectionModel().select(0);
        }
    }

    /** Populates the edit form with the selected task's current values. */
    private void populateEditFields() {
        int selectedIndex = taskSelector.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= editableTasks.size()) {
            return;
        }
        Task task = editableTasks.get(selectedIndex);
        editDescription.setText(task.getDescription());
        editTypeSelector.setValue(task.getType().name().toLowerCase());
        editBy.setText(task instanceof Deadline ? ((Deadline) task).getBy().toString() : "");
        editFrom.setText(task instanceof Event ? ((Event) task).getFrom() : "");
        editTo.setText(task instanceof Event ? ((Event) task).getTo() : "");
        updateEditFieldState();
    }

    /** Enables only the date/time fields relevant to the selected task type. */
    private void updateEditFieldState() {
        String type = editTypeSelector.getValue();
        boolean isDeadline = "deadline".equals(type);
        boolean isEvent = "event".equals(type);
        editBy.setDisable(!isDeadline);
        editFrom.setDisable(!isEvent);
        editTo.setDisable(!isEvent);
    }

    /** Builds the canonical update command represented by the edit form. */
    private String buildUpdateCommand(int selectedIndex) {
        String type = editTypeSelector.getValue();
        StringBuilder command = new StringBuilder("update ")
                .append(selectedIndex + 1)
                .append(" /description ")
                .append(editDescription.getText())
                .append(" /type ")
                .append(type);
        if ("deadline".equals(type)) {
            command.append(" /by ").append(editBy.getText());
        } else if ("event".equals(type)) {
            command.append(" /from ").append(editFrom.getText())
                    .append(" /to ").append(editTo.getText());
        }
        return command.toString();
    }
}

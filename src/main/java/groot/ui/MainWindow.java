package groot.ui;

import java.util.List;

import groot.CommandResponse;
import groot.Groot;
import groot.TaskSummary;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Coordinates command chat and the read-only task panel.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private VBox taskContainer;
    @FXML
    private VBox welcomeCard;
    @FXML
    private Label taskCount;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Groot groot;

    /**
     * Keeps empty submissions disabled without permanently binding the conversation's scroll position.
     */
    @FXML
    public void initialize() {
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                userInput.getText().isBlank(), userInput.textProperty()));
    }

    /**
     * Loads the initial task-panel snapshot from the application.
     *
     * @param groot Application instance that owns command processing and task state.
     */
    public void setGroot(Groot groot) {
        this.groot = groot;
        refreshTasks();
    }

    /**
     * Submits input, updates the task snapshot, and reveals the latest reply after layout.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        CommandResponse response = groot.getCommandResponse(input);
        welcomeCard.setVisible(false);
        welcomeCard.setManaged(false);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input),
                response.isError() ? DialogBox.getErrorDialog(response.text())
                        : DialogBox.getGrootDialog(response.text()));
        refreshTasks();
        userInput.clear();
        userInput.requestFocus();
        Platform.runLater(() -> {
            scrollPane.applyCss();
            scrollPane.layout();
            scrollPane.setVvalue(scrollPane.getVmax());
        });
    }

    /**
     * Rebuilds the small task panel only after a command has completed, including any rollback.
     */
    private void refreshTasks() {
        List<TaskSummary> summaries = groot.getTaskSummaries();
        long completed = summaries.stream().filter(TaskSummary::isDone).count();
        taskCount.setText(summaries.size() + (summaries.size() == 1 ? " task" : " tasks")
                + " · " + completed + " completed");
        taskContainer.getChildren().clear();
        if (summaries.isEmpty()) {
            Label empty = new Label("Room to grow.\nAdd your first task in the chat.");
            empty.getStyleClass().add("empty-tasks");
            empty.setWrapText(true);
            taskContainer.getChildren().add(empty);
        }
        for (TaskSummary summary : summaries) {
            taskContainer.getChildren().add(createTaskCard(summary));
        }
    }

    /**
     * Creates a wrapping task card with the same number used by commands.
     */
    private VBox createTaskCard(TaskSummary summary) {
        Label number = new Label(String.format("%02d", summary.number()));
        number.getStyleClass().add("task-number");
        Label type = new Label(summary.type());
        type.getStyleClass().add("task-type");
        Label status = new Label(summary.isDone() ? "Done" : "To do");
        status.getStyleClass().add("task-status");
        HBox top = new HBox(8, number, type, status);
        HBox.setHgrow(type, Priority.ALWAYS);
        type.setMaxWidth(Double.MAX_VALUE);
        Label description = new Label(summary.description());
        description.getStyleClass().add("task-description");
        description.setWrapText(true);
        description.setMinHeight(Label.USE_PREF_SIZE);
        VBox card = new VBox(6, top, description);
        card.getStyleClass().add("task-card");
        if (summary.isDone()) {
            card.getStyleClass().add("completed-task");
        }
        if (!summary.schedule().isEmpty()) {
            Label schedule = new Label(summary.schedule());
            schedule.getStyleClass().add("task-schedule");
            schedule.setWrapText(true);
            schedule.setMinHeight(Label.USE_PREF_SIZE);
            card.getChildren().add(schedule);
        }
        return card;
    }
}

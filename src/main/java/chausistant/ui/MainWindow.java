package chausistant.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import chausistant.ChatResponse;
import chausistant.Chausistant;
import chausistant.TaskSummary;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controls the dashboard and session activity view defined in FXML.
 *
 * <p>The dashboard remains at the beginning of the scrollable content so users
 * can return to their task overview after working through a chat session.</p>
 */
public class MainWindow extends AnchorPane {
    private static final int MAXIMUM_SUMMARY_TASKS = 3;
    private static final double MINIMUM_USER_MESSAGE_WIDTH = 160.0;
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.68;
    private static final double HORIZONTAL_DIALOG_MARGIN = 38.0;
    private static final DateTimeFormatter TODAY_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.ENGLISH);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private VBox taskSummaryContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Label todayLabel;
    @FXML
    private Label taskCountLabel;
    @FXML
    private Label summaryHintLabel;

    private Chausistant chausistant;

    /** Configures the responsive dashboard and message layout. */
    @FXML
    private void initialize() {
        todayLabel.setText("Today · " + LocalDate.now().format(TODAY_FORMATTER));
        scrollPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) ->
                updateMessageWidths(newBounds.getWidth()));
    }

    /**
     * Supplies the chatbot whose responses and tasks the window displays.
     *
     * @param chausistant chatbot that processes commands
     */
    public void setChausistant(Chausistant chausistant) {
        this.chausistant = chausistant;
        refreshDashboard();
        Platform.runLater(userInput::requestFocus);
    }

    /** Adds a user command and the corresponding Chausistant response to the session history. */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText().strip();
        if (userText.isBlank()) {
            userInput.clear();
            return;
        }

        ChatResponse chausistantResponse = chausistant.getChatResponse(userText);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText),
                DialogBox.getChausistantDialog(chausistantResponse));
        userInput.clear();
        refreshDashboard();
        updateMessageWidths(scrollPane.getViewportBounds().getWidth());
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    /** Refreshes the compact task overview at the start of the scrollable page. */
    private void refreshDashboard() {
        List<TaskSummary> taskSummaries = chausistant.getTaskSummaries();
        taskSummaryContainer.getChildren().clear();

        taskCountLabel.setText(formatTaskCount(taskSummaries.size()));
        if (taskSummaries.isEmpty()) {
            summaryHintLabel.setText("Add your first task below to start your day.");
            return;
        }

        int displayedTaskCount = Math.min(taskSummaries.size(), MAXIMUM_SUMMARY_TASKS);
        for (int index = 0; index < displayedTaskCount; index++) {
            taskSummaryContainer.getChildren().add(createSummaryTask(taskSummaries.get(index)));
        }

        summaryHintLabel.setText(formatSummaryHint(taskSummaries.size(), displayedTaskCount));
    }

    /** Creates one task row for the dashboard summary. */
    private HBox createSummaryTask(TaskSummary taskSummary) {
        Label completionMark = new Label(taskSummary.isCompleted() ? "✓" : "");
        completionMark.getStyleClass().add("summary-completion-mark");
        if (taskSummary.isCompleted()) {
            completionMark.getStyleClass().add("summary-completion-mark-done");
        }

        Label taskText = new Label(taskSummary.text());
        taskText.getStyleClass().add("summary-task-text");
        taskText.setMaxWidth(Double.MAX_VALUE);
        taskText.setWrapText(true);

        HBox summaryTask = new HBox(10.0, completionMark, taskText);
        summaryTask.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(taskText, Priority.ALWAYS);
        summaryTask.getStyleClass().add("summary-task");
        return summaryTask;
    }

    /** Adjusts chat cards to make efficient use of the available scroll-pane width. */
    private void updateMessageWidths(double viewportWidth) {
        double assistantMessageWidth = Math.max(0.0, viewportWidth - HORIZONTAL_DIALOG_MARGIN);
        double userMessageWidth = Math.max(MINIMUM_USER_MESSAGE_WIDTH,
                viewportWidth * USER_MESSAGE_WIDTH_RATIO);
        dialogContainer.getChildren().stream()
                .filter(DialogBox.class::isInstance)
                .map(DialogBox.class::cast)
                .forEach(dialogBox -> dialogBox.setMaximumMessageWidth(
                        dialogBox.isUserMessage() ? userMessageWidth : assistantMessageWidth));
    }

    /** Formats the task count in the dashboard header. */
    private String formatTaskCount(int taskCount) {
        return taskCount == 1 ? "1 task" : taskCount + " tasks";
    }

    /** Describes omitted tasks without taking extra space in the overview. */
    private String formatSummaryHint(int taskCount, int displayedTaskCount) {
        if (taskCount > displayedTaskCount) {
            return "Showing " + displayedTaskCount + " of " + taskCount + " tasks.";
        }
        return "Your current task list.";
    }
}

package com.gihun456.gui;

import com.gihun456.ErrorMessages;
import com.gihun456.Gihun456;
import com.gihun456.ui.UiMessages;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Label inputHint;
    @FXML
    private Button sendButton;

    private Gihun456 gihun;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/GihunUser.png"));
    private Image gihunImage = new Image(this.getClass().getResourceAsStream("/images/GihunBot.png"));

    /**
     * Initializes the dialog container with the banner and greeting messages.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userInput.textProperty().addListener((observable, oldValue, newValue) ->
                inputHint.setVisible(newValue.isEmpty()));
        dialogContainer.getChildren().addAll(
                DialogBox.getGihunBannerDialog(UiMessages.BANNER, gihunImage),
                DialogBox.getGihunDialog(UiMessages.GREETING, gihunImage, "standard-label")
        );
    }

    /**
     * Injects the Gihun456 instance.
     */
    public void setGihun(Gihun456 g) {
        gihun = g;
        String reminderReport = gihun.getReminderReport();
        if (!reminderReport.isEmpty()) {
            dialogContainer.getChildren().add(
                    DialogBox.getGihunDialog(reminderReport, gihunImage, "list-label"));
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing the bot's reply, then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = gihun.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getGihunDialog(response, gihunImage, getResponseStyle(response))
        );
        userInput.clear();

        if (input.trim().equalsIgnoreCase("bye") && response.equals(UiMessages.FAREWELL)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(javafx.util.Duration.seconds(5));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /**
     * Displays an error that occurred while preparing the application.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        dialogContainer.getChildren().add(
                DialogBox.getGihunDialog(ErrorMessages.ERROR_PREFIX + message, gihunImage, "error-label"));
    }

    private String getResponseStyle(String response) {
        if (response.startsWith(ErrorMessages.ERROR_PREFIX)) {
            return "error-label";
        }
        if (response.startsWith(UiMessages.CONFLICT_WARNING)) {
            return "warning-label";
        }
        if (response.startsWith(UiMessages.UPCOMING_REMINDERS)
                || response.startsWith(UiMessages.MISSED_REMINDERS)) {
            return "reminder-label";
        }
        if (response.startsWith(UiMessages.LIST_TASKS)
                || response.startsWith(UiMessages.LIST_MATCHING_TASKS)
                || response.equals(UiMessages.NO_MATCHING_TASKS)) {
            return "list-label";
        }
        if (response.startsWith(UiMessages.ADD_TASK)) {
            return "add-label";
        }
        if (response.startsWith(UiMessages.MARK_TASK)) {
            return "mark-label";
        }
        if (response.startsWith(UiMessages.UNMARK_TASK)) {
            return "unmark-label";
        }
        return "standard-label";
    }
}

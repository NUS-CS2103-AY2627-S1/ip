package jelly;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import jelly.ui.Ui;

/** Controls the main Jelly window defined in MainWindow.fxml. */
public class MainWindow {
    private static final int MAX_HISTORY_SIZE = 50;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox messageArea;

    @FXML
    private TextField inputField;

    private Jelly jelly;
    private Stage stage;
    private Image userImage;
    private Image jellyImage;
    private boolean shouldAutoScroll;
    private final List<String> commandHistory = new ArrayList<>();
    private int historyIndex;

    /** Configures controls after FXML has injected them. */
    @FXML
    private void initialize() {
        messageArea.setPadding(new Insets(12));
        messageArea.setFillWidth(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        inputField.setOnKeyPressed(this::handleHistoryNavigation);
    }

    /**
     * Injects the Jelly command-processing instance.
     *
     * @param jelly the command-processing instance.
     */
    public void setJelly(Jelly jelly) {
        this.jelly = jelly;
    }

    /** Injects the JavaFX window controlled by this class. */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Injects the user and Jelly avatar images.
     *
     * @param userImage the user's avatar.
     * @param jellyImage Jelly's avatar.
     */
    public void setImages(Image userImage, Image jellyImage) {
        this.userImage = userImage;
        this.jellyImage = jellyImage;
    }

    /** Displays Jelly's welcome message when the window opens. */
    public void showWelcome() {
        messageArea.getChildren().add(DialogBox.getJellyDialog(Ui.GUI_WELCOME_MESSAGE, jellyImage));
    }

    /** Processes the command entered by the user. */
    @FXML
    private void handleInput() {
        String input = inputField.getText();
        if (input.isBlank()) {
            return;
        }

        addToHistory(input);
        shouldAutoScroll = isNearBottom();
        messageArea.getChildren().add(DialogBox.getUserDialog(input, userImage));
        String response = jelly.executeCommand(input);
        DialogBox responseDialog = DialogBox.getJellyDialog(response, jellyImage);
        if (jelly.wasLastResponseAnError()) {
            responseDialog.getStyleClass().add("error-message");
        }
        messageArea.getChildren().add(responseDialog);
        inputField.clear();

        if (input.equals("bye")) {
            inputField.setDisable(true);
            PauseTransition closeDelay = new PauseTransition(Duration.seconds(1));
            closeDelay.setOnFinished(event -> stage.close());
            closeDelay.play();
            return;
        }

        if (shouldAutoScroll) {
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        }
    }

    /** Navigates through commands stored during the current session. */
    private void handleHistoryNavigation(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            showPreviousCommand();
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            showNextCommand();
            event.consume();
        }
    }

    /** Adds a submitted command to the in-memory history. */
    private void addToHistory(String command) {
        if (commandHistory.isEmpty() || !commandHistory.get(commandHistory.size() - 1).equals(command)) {
            commandHistory.add(command);
            if (commandHistory.size() > MAX_HISTORY_SIZE) {
                commandHistory.remove(0);
            }
        }
        historyIndex = commandHistory.size();
    }

    /** Shows the previous command in history, if one exists. */
    private void showPreviousCommand() {
        if (historyIndex > 0) {
            historyIndex--;
            inputField.setText(commandHistory.get(historyIndex));
            inputField.positionCaret(inputField.getText().length());
        }
    }

    /** Shows the next command in history or clears the input field. */
    private void showNextCommand() {
        if (historyIndex < commandHistory.size() - 1) {
            historyIndex++;
            inputField.setText(commandHistory.get(historyIndex));
            inputField.positionCaret(inputField.getText().length());
        } else {
            historyIndex = commandHistory.size();
            inputField.clear();
        }
    }

    /** Returns whether the conversation is already positioned near its latest message. */
    private boolean isNearBottom() {
        double contentHeight = messageArea.getBoundsInLocal().getHeight();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        return contentHeight <= viewportHeight || scrollPane.getVvalue() >= 0.95;
    }
}

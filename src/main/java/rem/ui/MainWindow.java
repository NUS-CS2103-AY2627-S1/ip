package rem.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Duration;
import rem.Rem;
import rem.Response;

/**
 * Handles chat submissions without running the blocking console input loop.
 */
public class MainWindow {
    private static final double SCROLL_SPEED_MULTIPLIER = 1.5;
    private final CommandHistory history = new CommandHistory();
    private final PauseTransition idleTimer = new PauseTransition(Duration.seconds(45));
    @FXML
    private BorderPane window;
    @FXML
    private ImageView portrait;
    @FXML
    private Label sleepIndicator;
    @FXML
    private HBox emptyState;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    private Rem rem;

    @FXML
    private void initialize() {
        // Resizing should not interrupt reading older messages.
        userInput.textProperty().addListener((observable, oldValue, newValue) ->
                sendButton.setDisable(newValue.isBlank()));
        sendButton.setDisable(true);
        userInput.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryKey);
        scrollPane.addEventFilter(ScrollEvent.SCROLL, this::handleScroll);
        idleTimer.setOnFinished(event -> showSleepingPortrait());
        window.addEventFilter(KeyEvent.KEY_PRESSED, event -> wakePortrait());
        window.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> wakePortrait());
        window.addEventFilter(ScrollEvent.SCROLL, event -> wakePortrait());
        userInput.textProperty().addListener((observable, oldValue, newValue) -> wakePortrait());
    }

    /** Shows a quiet idle state without blocking the task manager. */
    private void showSleepingPortrait() {
        portrait.setOpacity(0.4);
        portrait.setAccessibleText("Rem is napping");
        sleepIndicator.setVisible(true);
    }

    /** Restores the portrait immediately and restarts the inactivity timer. */
    private void wakePortrait() {
        portrait.setOpacity(1.0);
        portrait.setAccessibleText("Rem is awake and ready to help");
        sleepIndicator.setVisible(false);
        if (!userInput.isDisabled()) {
            idleTimer.playFromStart();
        }
    }

    /** Keeps the helper illustration in sync with the current task list. */
    private void updateEmptyState() {
        emptyState.setVisible(!rem.hasTasks());
        emptyState.setManaged(!rem.hasTasks());
    }

    /**
     * Recalls commands with unmodified arrow keys without submitting them.
     */
    private void handleHistoryKey(KeyEvent event) {
        if (userInput.isDisabled() || event.isAltDown() || event.isControlDown()
                || event.isMetaDown() || event.isShiftDown()) {
            return;
        }
        if (event.getCode() == KeyCode.UP) {
            userInput.setText(history.previous(userInput.getText()));
        } else if (event.getCode() == KeyCode.DOWN) {
            userInput.setText(history.next(userInput.getText()));
        } else {
            return;
        }
        userInput.positionCaret(userInput.getLength());
        event.consume();
    }

    /**
     * Speeds up vertical wheel scrolling while preserving direct touch and modified gestures.
     */
    private void handleScroll(ScrollEvent event) {
        double overflow = dialogContainer.getHeight() - scrollPane.getViewportBounds().getHeight();
        if (overflow <= 0 || event.isDirect() || event.isControlDown() || event.isAltDown()
                || event.isMetaDown() || event.isShiftDown()
                || Math.abs(event.getDeltaX()) >= Math.abs(event.getDeltaY())) {
            return;
        }
        double range = scrollPane.getVmax() - scrollPane.getVmin();
        double position = scrollPane.getVvalue() - event.getDeltaY() * SCROLL_SPEED_MULTIPLIER / overflow * range;
        scrollPane.setVvalue(Math.clamp(position, scrollPane.getVmin(), scrollPane.getVmax()));
        event.consume();
    }

    /**
     * Connects the loaded task manager and displays its greeting.
     *
     * @param rem Task manager for this window.
     */
    public void setRem(Rem rem) {
        this.rem = rem;
        dialogContainer.getChildren().add(new DialogBox(rem.getWelcome(), false));
        updateEmptyState();
        wakePortrait();
    }

    /**
     * Offers a safe reset when Rem detected malformed saved task data.
     *
     * @param owner Window that owns the modal recovery prompt.
     */
    public void showRecoveryDialog(Window owner) {
        if (!rem.canStartFresh()) {
            return;
        }

        ButtonType startFreshButton = new ButtonType("Start Fresh", ButtonData.OK_DONE);
        ButtonType exitButton = new ButtonType("Exit", ButtonData.CANCEL_CLOSE);
        Alert recoveryAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "RemBot couldn't read your saved tasks. Your original file has not been changed.\n\n"
                        + "Would you like to start with an empty task list? The damaged file will be kept as a backup.",
                startFreshButton, exitButton);
        recoveryAlert.initOwner(owner);
        recoveryAlert.setTitle("Recover saved tasks");
        recoveryAlert.setHeaderText("The saved task file is damaged");

        Optional<ButtonType> result = recoveryAlert.showAndWait();
        if (result.isEmpty() || result.get() != startFreshButton) {
            Platform.exit();
            return;
        }

        try {
            Path backup = rem.startFresh();
            dialogContainer.getChildren().add(new DialogBox(
                    "Started fresh with an empty task list.\nYour damaged file was saved as "
                            + backup.getFileName() + ".",
                    false));
            updateEmptyState();
            userInput.clear();
            userInput.requestFocus();
        } catch (IOException e) {
            Alert failureAlert = new Alert(Alert.AlertType.ERROR,
                    "Nothing was replaced. " + e.getMessage(), ButtonType.OK);
            failureAlert.initOwner(owner);
            failureAlert.setTitle("Recovery failed");
            failureAlert.setHeaderText("RemBot could not create a safe backup");
            failureAlert.showAndWait();
            Platform.exit();
        }
    }

    @FXML
    private void handleUserInput() {
        if (userInput.isDisabled()) {
            return;
        }
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        history.add(input);
        dialogContainer.getChildren().add(new DialogBox(input, true));
        Response response = rem.getResponse(input);
        updateEmptyState();
        dialogContainer.getChildren().add(new DialogBox(response.text(), false, response.isError()));
        if (!response.isError()) {
            userInput.clear();
        }
        // Apply the new message heights before moving to the latest response.
        scrollPane.applyCss();
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
        userInput.requestFocus();
        if (response.isExit()) {
            idleTimer.stop();
            showSleepingPortrait();
            userInput.setDisable(true);
            sendButton.setDisable(true);
            // Let the user read the farewell before closing the window.
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

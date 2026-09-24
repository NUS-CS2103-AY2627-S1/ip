package dobby.ui.gui;

import dobby.Dobby;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main Dobby chat window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    private static final double EXIT_DELAY_SECONDS = 3.0;

    private final Image dobbyImage = new Image(getClass().getResourceAsStream("/images/DobbyProfile.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Dobby dobby;

    /** Configures listeners after FXML fields have been injected. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the application facade used to process user commands.
     *
     * @param dobby Dobby application facade.
     */
    public void setDobby(Dobby dobby) {
        this.dobby = dobby;
    }

    /** Adds the user's message and Dobby's response, then clears the input field. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        boolean isExitCommand = dobby.isExitCommand(input);
        String response = dobby.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getDobbyDialog(response, dobbyImage));
        userInput.clear();

        if (isExitCommand) {
            scheduleExit();
        }
    }

    /** Disables further input and closes the GUI after the farewell remains visible briefly. */
    private void scheduleExit() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition exitDelay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }
}

package nudge.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import nudge.Nudge;
import nudge.NudgeResponse;

/**
 * Controls user interaction with the main Nudge window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.millis(800);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Nudge nudge;

    /**
     * Configures automatic scrolling after the FXML controls are loaded.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the command-processing component and displays its greeting.
     *
     * @param nudge Nudge instance used to process commands.
     */
    public void setNudge(Nudge nudge) {
        this.nudge = nudge;
        dialogContainer.getChildren().add(
                DialogBox.getNudgeDialog(nudge.getStartupMessage()));
        userInput.requestFocus();
    }

    /**
     * Sends the entered command to Nudge and adds both sides of the exchange to the chat.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        NudgeResponse response = nudge.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getNudgeDialog(response.getDisplayText(), response.isError()));
        userInput.clear();

        if (response.shouldExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
            exitPause.setOnFinished(event -> Platform.exit());
            exitPause.play();
        }
    }
}

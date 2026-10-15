package yapbot.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for {@code MainWindow.fxml}: wires the text field and send
 * button to a {@link YapBotGui} session and renders each exchange as a pair
 * of {@link DialogBox} rows.
 */
public class MainWindow {

    private static final Duration EXIT_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private YapBotGui yapBotGui;

    /**
     * Sets up the auto-scrolling behaviour of the conversation view. Called
     * automatically by the FXML loader.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the YapBot session this window drives, and shows the initial
     * greeting.
     *
     * @param yapBotGui the session to drive.
     */
    public void setYapBotGui(YapBotGui yapBotGui) {
        this.yapBotGui = yapBotGui;
        dialogContainer.getChildren().add(DialogBox.forYapBot(yapBotGui.getGreeting()));
    }

    /**
     * Handles the user submitting input, via either the Send button or
     * pressing Enter in the text field: shows the exchange, then closes the
     * window shortly after a "bye" command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = yapBotGui.getResponse(input);
        DialogBox responseDialog = yapBotGui.wasLastResponseAnError()
                ? DialogBox.forYapBotError(response)
                : DialogBox.forYapBot(response);
        dialogContainer.getChildren().addAll(
                DialogBox.forUser(input),
                responseDialog);
        userInput.clear();

        if (yapBotGui.isExit()) {
            PauseTransition delay = new PauseTransition(EXIT_DELAY);
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

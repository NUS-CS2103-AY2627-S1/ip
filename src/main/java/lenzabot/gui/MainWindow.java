package lenzabot.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lenzabot.LenZaBot;

/**
 * Controls LenZaBot's main chat window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private LenZaBot lenZaBot;

    /**
     * Initializes behavior that depends on injected FXML controls.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the chatbot and displays its initial greeting.
     *
     * @param lenZaBot Chatbot used to process commands.
     */
    public void setLenZaBot(LenZaBot lenZaBot) {
        this.lenZaBot = lenZaBot;
        dialogContainer.getChildren().add(DialogBox.getBotDialog(
                "Lenza at your service. Add a task, or type `list` to review your desk."));
        if (lenZaBot.getStartupWarning() != null) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(lenZaBot.getStartupWarning()));
        }
    }

    /**
     * Submits the input and displays the user's command and LenZaBot's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = lenZaBot.getResponse(input);
        DialogBox responseDialog = LenZaBot.isErrorResponse(response)
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getBotDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog);
        userInput.clear();

        if (!lenZaBot.isRunning()) {
            userInput.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}

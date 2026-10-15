package catgpt;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main CatGPT chat window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    private static final double EXIT_DELAY_SECONDS = 1.0;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private CatGPT chatbot;

    /**
     * Keeps the newest conversation entry visible.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax())));
        userInput.requestFocus();
    }

    /**
     * Supplies the application logic used to process user commands.
     *
     * @param chatbot CatGPT instance backing this window.
     */
    public void setChatbot(CatGPT chatbot) {
        this.chatbot = chatbot;
        dialogContainer.getChildren().add(DialogBox.getCatDialog(chatbot.getWelcomeMessage()));
        String startupErrorMessage = chatbot.getStartupErrorMessage();
        if (startupErrorMessage != null) {
            dialogContainer.getChildren().add(DialogBox.getCatDialog(startupErrorMessage));
        }
    }

    /**
     * Sends the current input to CatGPT and appends both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        CommandResult result = chatbot.getCommandResult(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getCatDialog(result.response()));
        userInput.clear();
        userInput.requestFocus();

        if (result.shouldExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}

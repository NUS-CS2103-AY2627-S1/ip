package jassabot;

import jassabot.parser.CommandType;
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
    private Button sendButton;

    private JassaBot jassaBot;

    /**
     * Keeps the newest message visible when the conversation grows.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Connects the chatbot and displays the welcome message and storage warnings.
     */
    public void setJassaBot(JassaBot jassaBot) {
        this.jassaBot = jassaBot;
        dialogContainer.getChildren().add(
                DialogBox.getJassaBotDialog(jassaBot.getWelcome(), CommandType.UNKNOWN));
    }

    /**
     * Appends the user's message and JassaBot's reply to the conversation, then clears the input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = jassaBot.getResponse(input);
        CommandType commandType = jassaBot.getCommandType();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getJassaBotDialog(response, commandType)
        );
        userInput.clear();
        if (jassaBot.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            // Let the farewell render before closing the JavaFX application.
            PauseTransition farewellPause = new PauseTransition(Duration.seconds(1));
            farewellPause.setOnFinished(event -> Platform.exit());
            farewellPause.play();
        } else {
            userInput.requestFocus();
        }
    }
}

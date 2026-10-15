package whimsybot.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import whimsybot.Personality;
import whimsybot.WhimsyBot;

/** Controller for the main Whimsy Bot window. */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private WhimsyBot whimsyBot;

    /** Connects the controller to the chatbot logic. */
    public void setWhimsyBot(WhimsyBot bot) {
        whimsyBot = bot;
        dialogContainer.getChildren().add(DialogBox.getWhimsyBotDialog(Personality.introduction()));
    }

    /** Keeps the newest message visible as the conversation grows. */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> scrollPane.setVvalue(1.0));
    }

    /** Processes text entered by the user and displays both sides of the conversation. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        String response = whimsyBot.getResponse(input);
        DialogBox responseDialog = response.startsWith("OOPS!!!")
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getWhimsyBotDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog);
        userInput.clear();
    }
}

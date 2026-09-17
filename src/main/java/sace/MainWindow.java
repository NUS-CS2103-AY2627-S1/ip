package sace;

import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controls the main chat window and passes user commands to Sace.
 */
public class MainWindow {
    private static final String BOT_AVATAR_PATH = "/images/jing.jpeg";
    private static final String USER_AVATAR_PATH = "/images/consortyu.jpeg";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private Label statusLabel;

    private final Image botImage = loadImage(BOT_AVATAR_PATH);
    private final Image userImage = loadImage(USER_AVATAR_PATH);
    private Sace sace;

    /**
     * Keeps the conversation scrolled to the newest message.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot used to answer commands and displays its greeting.
     *
     * @param sace chatbot instance owned by the application.
     */
    public void setSace(Sace sace) {
        this.sace = sace;
        dialogContainer.getChildren().add(
                DialogBox.getBotDialog(sace.getWelcomeMessage(), botImage));
        userInput.requestFocus();
    }

    /**
     * Adds the user's command and Sace's reply to the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || sace == null) {
            return;
        }

        String response = sace.getResponse(input);
        DialogBox responseDialog = sace.didLastCommandFail()
                ? DialogBox.getErrorDialog(response, botImage)
                : DialogBox.getBotDialog(response, botImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                responseDialog);
        userInput.clear();

        statusLabel.getStyleClass().remove("error-status");
        if (sace.didLastCommandFail()) {
            statusLabel.setText("COMMAND NEEDS REVISION");
            statusLabel.getStyleClass().add("error-status");
        } else {
            statusLabel.setText("ORACLE ONLINE");
        }

        if (sace.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            statusLabel.setText("SESSION COMPLETE");
        }
    }

    /**
     * Loads a portrait from the application resources.
     */
    private static Image loadImage(String imagePath) {
        URL imageResource = Objects.requireNonNull(
                MainWindow.class.getResource(imagePath), "Avatar is missing: " + imagePath);
        return new Image(imageResource.toExternalForm());
    }
}

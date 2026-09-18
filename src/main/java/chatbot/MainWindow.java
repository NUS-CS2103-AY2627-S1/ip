package chatbot;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

    private Yun yun;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.jpg"));
    private final Image yunImage = new Image(this.getClass().getResourceAsStream("/images/yun.png"));

    /**
     * Binds the dialog container height to the scroll pane's vertical position.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Yun chatbot instance used to process user input.
     *
     * @param chatbot Chatbot instance used by this window.
     */
    public void setYun(Yun chatbot) {
        assert chatbot != null : "MainWindow requires a Yun instance";
        yun = chatbot;
        dialogContainer.getChildren().add(
            DialogBox.getYunDialog(yun.getWelcomeMessage(), yunImage)
        );
    }

    /**
     * Processes the user's input, displays the user and chatbot messages, and clears the input field.
     * Closes the application when the user enters the {@code bye} command.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        String chatbotReply = yun.getResponse(userText);
        dialogContainer.getChildren().addAll(
            DialogBox.getUserDialog(userText, userImage),
                DialogBox.getYunDialog(chatbotReply, yunImage)
        );
        userInput.clear();
        if (Parser.getCommand(userText).equalsIgnoreCase("bye")) {
            closeAfterDelay();
        }
    }

    /**
     * Disables further input and closes the application after a short delay.
     */
    private void closeAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(event -> {
            Stage stage = (Stage) userInput.getScene().getWindow();
            stage.close();
        });
        delay.play();
    }
}

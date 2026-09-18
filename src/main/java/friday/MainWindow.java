package friday;

import java.io.InputStream;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Coordinates the controls in {@code MainWindow.fxml} with a chatbot.
 */
public class MainWindow {
    @FXML
    private ScrollPane chatScroll;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField commandField;

    @FXML
    private Button sendButton;

    private final Image userImage;
    private final Image chatbotImage;
    private Chatbot chatbot;

    /**
     * Creates the controller and loads the fixed conversation profile images.
     */
    public MainWindow() {
        userImage = loadImage("/images/tonystark.jpeg");
        chatbotImage = loadImage("/images/friday.png");
    }

    /**
     * Connects the view to the chatbot used for generating responses.
     *
     * @param chatbot Chatbot that processes user input.
     */
    public void setChatbot(Chatbot chatbot) {
        this.chatbot = chatbot;
        if (dialogContainer.getChildren().isEmpty()) {
            DialogBox welcome = DialogBox.getChatbotDialog(
                    "Good day, Mr Stark. What can I assist you with?", chatbotImage);
            welcome.bindMessageWidth(chatScroll.widthProperty());
            dialogContainer.getChildren().add(welcome);
            for (String warning : chatbot.getStartupWarnings()) {
                DialogBox warningDialog = DialogBox.getErrorDialog(
                        "That presents a complication: " + warning, chatbotImage);
                warningDialog.bindMessageWidth(chatScroll.widthProperty());
                dialogContainer.getChildren().add(warningDialog);
            }
        }
    }

    /**
     * Keeps the newest dialog visible when the container grows.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> chatScroll.setVvalue(1.0)));
    }

    /**
     * Sends the current input to the chatbot and appends both messages.
     */
    @FXML
    private void handleSend() {
        String input = commandField.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        Chatbot.Response response = chatbot.getResponseResult(input);
        DialogBox userDialog = DialogBox.getUserDialog(input, userImage);
        DialogBox replyDialog = response.isError()
                ? DialogBox.getErrorDialog("That presents a complication: " + response.text(), chatbotImage)
                : DialogBox.getChatbotDialog(response.text(), chatbotImage);
        userDialog.bindMessageWidth(chatScroll.widthProperty());
        replyDialog.bindMessageWidth(chatScroll.widthProperty());
        replyDialog.setTaskCards(response.taskCards(), response.footer());
        dialogContainer.getChildren().addAll(userDialog, replyDialog);
        commandField.clear();
        if (response.isExit()) {
            commandField.setDisable(true);
            sendButton.setDisable(true);
            Platform.exit();
        }
    }

    /**
     * Loads an image resource and reports a useful error when it is missing.
     *
     * @param resourcePath Absolute classpath path of the image.
     * @return Loaded image.
     */
    private static Image loadImage(String resourcePath) {
        InputStream imageStream = MainWindow.class.getResourceAsStream(resourcePath);
        if (imageStream == null) {
            throw new IllegalStateException("Could not find image resource: " + resourcePath);
        }
        return new Image(imageStream);
    }
}

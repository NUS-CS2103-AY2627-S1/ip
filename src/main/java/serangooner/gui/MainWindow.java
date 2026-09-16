package serangooner.gui;

import java.io.InputStream;

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

import serangooner.Serangooner;
import serangooner.ui.SoundPlayer;

/**
 * Answers the user inside the chat window.
 * Every line typed is passed to the chatbot and the reply is added to the
 * conversation, so this class only has to decide how a turn of the
 * conversation looks, never what it says.
 */
public class MainWindow extends AnchorPane {
    private static final String IMAGE_PATH_USER = "/images/User.png";
    private static final String IMAGE_PATH_CHATBOT = "/images/Serangooner.png";
    private static final Duration FAREWELL_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = loadImage(IMAGE_PATH_USER);
    private final Image chatbotImage = loadImage(IMAGE_PATH_CHATBOT);
    private final SoundPlayer soundPlayer = new SoundPlayer(true);

    private Serangooner chatbot;

    /**
     * Keeps the newest message in view as the conversation grows.
     * The scroll position is moved rather than bound, so that the user can
     * still scroll back up to read earlier messages.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    public void setChatbot(Serangooner chatbot) {
        this.chatbot = chatbot;
    }

    /**
     * Opens the conversation with the chatbot's greeting, and with word of
     * what it read from the save file when there is any worth giving.
     */
    public void showGreeting() {
        addChatbotMessage(chatbot.getGreeting(), false);

        String loadReport = chatbot.getLoadReport();
        if (!loadReport.isEmpty()) {
            addChatbotMessage(loadReport, false);
        }
    }

    /**
     * Answers what the user typed, showing the question and the reply side by
     * side, and closes the window a moment later once the user says bye.
     * A blank line is ignored rather than refused, since pressing Enter on an
     * empty box is a slip rather than a command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        Serangooner.Response response = chatbot.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        addChatbotMessage(response.text(), response.isError());
        userInput.clear();

        if (response.isError()) {
            soundPlayer.play();
        }
        if (response.isExit()) {
            closeAfterFarewell();
        }
    }

    /**
     * Adds one message from the chatbot to the conversation.
     *
     * @param message Words to show.
     * @param isError True if the message explains a refused command.
     */
    private void addChatbotMessage(String message, boolean isError) {
        dialogContainer.getChildren().add(
                DialogBox.getChatbotDialog(message, chatbotImage, isError));
    }

    /**
     * Stops taking input and closes the window, leaving the farewell on screen
     * long enough to be read.
     */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(FAREWELL_PAUSE);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /**
     * Returns the image stored at the given path within the packaged program.
     *
     * @param path Absolute path of the image among the program's resources.
     * @return Image ready to be shown.
     * @throws IllegalStateException If the program was packaged without that image.
     */
    private static Image loadImage(String path) {
        InputStream stream = MainWindow.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalStateException("missing image: " + path);
        }
        return new Image(stream);
    }
}

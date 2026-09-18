package korvus.views;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import korvus.bot.AppKorvus;
import korvus.views.components.ChatBox;
import korvus.views.components.ChatMessage;

/**
 * The Main Layout of the Korvus Application.
 */
public class MainView extends AnchorPane {
    private static String USER_IMAGE = "/images/userImage.gif";
    private static String BOT_IMAGE = "/images/botImage.jpg";
    private static int DEFAULT_SIZE = 120;
    private static double DEFAULT_SPACING = 10.0;
    private static String DEFAULT_BUTTON_TEXT = "Send";
    private static String DEFAULT_PROMPT_TEXT = "Send a message here!";

    private AppKorvus korvus;
    private Image userImage = new Image(this.getClass().getResourceAsStream(USER_IMAGE));
    private Image korvusImage = new Image(
            this.getClass().getResourceAsStream(BOT_IMAGE),
            DEFAULT_SIZE, DEFAULT_SIZE, true, true);

    private ChatBox chatBox;
    private TextField userInput;
    private Button sendButton;

    /**
     * Returns an instance of the main layout.
     *
     * @param korvus Korvus instance to bind with the application.
     */
    public MainView(AppKorvus korvus) {
        this.korvus = korvus;

        chatBox = new ChatBox();

        userInput = new TextField();
        userInput.setPromptText(DEFAULT_PROMPT_TEXT);
        userInput.setOnAction(_ -> handleUserMessage());

        sendButton = new Button(DEFAULT_BUTTON_TEXT);
        sendButton.setOnAction(_ -> handleUserMessage());

        this.setAnchors();
        this.setStyling();

        this.getChildren().addAll(chatBox, userInput, sendButton);
    }

    /**
     * Anchors the elements in the layout properly.
     */
    private void setAnchors() {
        AnchorPane.setTopAnchor(chatBox, DEFAULT_SPACING);
        AnchorPane.setLeftAnchor(chatBox, DEFAULT_SPACING);
        AnchorPane.setRightAnchor(chatBox, DEFAULT_SPACING);

        AnchorPane.setBottomAnchor(userInput, DEFAULT_SPACING);
        AnchorPane.setLeftAnchor(userInput, DEFAULT_SPACING);

        AnchorPane.setBottomAnchor(sendButton, DEFAULT_SPACING);
        AnchorPane.setRightAnchor(sendButton, DEFAULT_SPACING);

        chatBox.prefHeightProperty().bind(
                this.heightProperty().subtract(userInput.heightProperty()).subtract(3 * DEFAULT_SPACING));
        userInput.prefWidthProperty().bind(
                this.widthProperty().subtract(sendButton.widthProperty()).subtract(3 * DEFAULT_SPACING));

        sendButton.prefHeightProperty().bindBidirectional(userInput.prefHeightProperty());
    }

    /**
     * Sets the style for the main layout (and its elements).
     */
    private void setStyling() {
        String mainCss = this.getClass().getResource("/css/main.css").toExternalForm();
        this.getStylesheets().add(mainCss);
    }

    /**
     * Handles the inputs from the user.
     */
    private void handleUserMessage() {
        chatBox.addMessageToQueue(new ChatMessage(false, userImage, userInput.getText().trim()));
        this.korvus.processUserMessage(userInput.getText().trim());

        userInput.clear();
    }

    /**
     * Handles the outputs from Korvus.
     *
     * @param msg Message to be outputted to the application.
     */
    public void handleKorvusMessage(String msg) {
        chatBox.addMessageToQueue(new ChatMessage(true, korvusImage, msg));
    }
}

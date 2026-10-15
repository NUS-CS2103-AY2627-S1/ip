package prism.ui;

import java.io.InputStream;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import prism.Prism;

/** Controls the main Prism chat window loaded from FXML. */
public class MainWindow {
    private static final String USER_AVATAR = "/images/user-avatar.png";
    private static final String PRISM_AVATAR = "/images/prism-avatar.png";
    private static final String BACKGROUND = "/images/prism-background.png";

    @FXML
    private ScrollPane conversationPane;

    @FXML
    private VBox conversation;

    @FXML
    private TextField commandField;

    @FXML
    private Button sendButton;

    private final Prism prism = new Prism("./data/prism.txt", new Ui(false));

    /** Initializes the conversation background, welcome message, and scroll behavior. */
    @FXML
    public void initialize() {
        conversation.setPadding(new Insets(16, 14, 16, 14));
        setConversationBackground();
        conversation.heightProperty().addListener((observable, oldHeight, newHeight) ->
                conversationPane.setVvalue(1.0));
        addMessage("Prism: Hello! I'm Prism, your quietly enthusiastic task companion.\n"
                + "Give me a command and we'll bring your day into focus.", false);
    }

    /** Handles commands submitted by the Send button or the Enter key. */
    @FXML
    private void handleUserInput() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        String response = prism.executeCommand(command);
        addMessage("You: " + command, true);
        if (command.equals("bye")) {
            response = "Until next time\u2014keep your day in focus!";
        }
        addMessage("Prism: " + response, false);
        commandField.clear();

        if (command.equals("bye")) {
            conversation.setDisable(true);
            sendButton.setDisable(true);
            commandField.setDisable(true);
        }
    }

    /** Adds an FXML-backed message row to the conversation. */
    private void addMessage(String message, boolean isUserMessage) {
        Image avatar = loadImage(isUserMessage ? USER_AVATAR : PRISM_AVATAR);
        conversation.getChildren().add(DialogBox.create(message, avatar, isUserMessage));
    }

    /** Applies the background image and falls back to a plain color if it is unavailable. */
    private void setConversationBackground() {
        Image background = loadImage(BACKGROUND);
        if (background == null) {
            conversation.setStyle("-fx-background-color: #f7f9fc;");
            return;
        }
        BackgroundSize size = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
        conversation.setBackground(new Background(backgroundImage));
    }

    /** Loads a classpath image resource, returning null when it is unavailable. */
    private Image loadImage(String resourcePath) {
        InputStream imageStream = getClass().getResourceAsStream(resourcePath);
        return imageStream == null ? null : new Image(imageStream);
    }
}

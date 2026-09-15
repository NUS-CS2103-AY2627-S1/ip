package monkey.ui;

import java.net.URL;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import monkey.CommandResult;
import monkey.Monkey;

/** Controls the main chat window and connects it to the Monkey chatbot. */
public class MainWindow extends AnchorPane {
    private static final String USER_IMAGE_PATH = "/images/yellow-man.png";
    private static final String MONKEY_IMAGE_PATH = "/images/curious-george.png";
    private static final String WELCOME_MESSAGE = "Hello! I'm Monkey, your cheeky little assistant.\n"
            + "What can I do for you today?";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private final Image userImage = loadImage(USER_IMAGE_PATH);
    private final Image monkeyImage = loadImage(MONKEY_IMAGE_PATH);
    private Monkey monkey;

    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        dialogContainer.getChildren().add(DialogBox.getMonkeyDialog(WELCOME_MESSAGE, monkeyImage));
    }

    /** Supplies the chatbot that processes commands entered in this window. */
    public void setMonkey(Monkey monkey) {
        this.monkey = monkey;
    }

    /** Sends the current text to Monkey and displays both sides of the conversation. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        CommandResult result = monkey.processCommand(input);
        String response = result.response();

        if (!input.isBlank()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        }
        if (!response.isBlank()) {
            dialogContainer.getChildren().add(DialogBox.getMonkeyDialog(response, monkeyImage));
        }
        userInput.clear();
        if (result.shouldExit()) {
            Platform.exit();
        }
    }

    /** Returns an image resource, or null so the FXML placeholder remains visible. */
    private static Image loadImage(String path) {
        URL imageUrl = MainWindow.class.getResource(path);
        return imageUrl == null ? null : new Image(imageUrl.toExternalForm());
    }
}

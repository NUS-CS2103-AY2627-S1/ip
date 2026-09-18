package bobby.gui;

import java.io.InputStream;
import java.util.Objects;

import bobby.Bobby;
import bobby.Bobby.CommandResult;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls Bobby's main chat window.
 */
public class MainWindow extends AnchorPane {
    private static final String LOADING_ERROR_MESSAGE =
            "Regrettably, the saved register could not be read.\n"
                    + "I shall therefore commence with an empty register.";

    private final Image bobbyImage = loadImage("/images/Lord_Bobby_Portrait.png");

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Bobby bobby;

    /**
     * Configures the conversation pane after its FXML controls are injected.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                userInput.getText().isBlank(), userInput.textProperty()));
        dialogContainer.getChildren().add(
                DialogBox.getBobbyDialog(
                        "Well met, most honoured patron. Speak thy charge, or enter list that I may present "
                                + "the royal register.",
                        bobbyImage, false));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Supplies the Bobby instance that executes user commands.
     *
     * @param bobby the application's Bobby instance.
     */
    public void setBobby(Bobby bobby) {
        assert bobby != null : "Bobby instance must not be null";
        this.bobby = bobby;
        if (bobby.hasLoadingError()) {
            dialogContainer.getChildren().add(
                    DialogBox.getBobbyDialog(LOADING_ERROR_MESSAGE, bobbyImage, true));
        }
    }

    /**
     * Displays the user's command and Bobby's response, then clears the input.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText().trim();
        if (userText.isEmpty()) {
            return;
        }

        CommandResult result = bobby.getCommandResult(userText);
        boolean shouldExit = bobby.isExitCommand(userText);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText),
                DialogBox.getBobbyDialog(result.getMessage(), bobbyImage, result.isError()));
        userInput.clear();
        if (shouldExit) {
            Platform.exit();
        }
    }

    /**
     * Loads an image resource bundled with the application.
     *
     * @param resourcePath the absolute classpath resource path.
     * @return the loaded image.
     */
    private static Image loadImage(String resourcePath) {
        InputStream imageStream = Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath);
        return new Image(imageStream);
    }
}

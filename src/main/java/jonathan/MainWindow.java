package jonathan;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main JavaFX window.
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

    private final Image userImage = new Image(
            MainWindow.class.getResourceAsStream(
                    "/jonathan/images/darthVader.png"));

    private final Image yodaImage = new Image(
            MainWindow.class.getResourceAsStream(
                    "/jonathan/images/yoda.png"));

    private final Jonathan jonathan = new Jonathan("data/jonathan.txt");

    /**
     * Performs setup after FXML injects the controls.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        dialogContainer.getChildren().add(
                DialogBox.getJonathanDialog(
                        "Hello! I'm Griddel. What can I do for you?",
                        yodaImage));
    }

    /**
     * Processes one command entered by the user.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText().trim();

        if (userText.isEmpty()) {
            return;
        }

        String jonathanText = jonathan.getResponse(userText);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getJonathanDialog(jonathanText, yodaImage));

        userInput.clear();

        if (userText.equalsIgnoreCase("bye")) {
            Platform.exit();
        }
    }
}

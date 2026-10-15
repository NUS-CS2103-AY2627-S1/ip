package jeff.gui;

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
import jeff.Jeff;
import jeff.parser.Parser;

/*
 * Adapted from the SE-EDU JavaFX Tutorial:
 * https://se-education.org/guides/tutorials/javaFx.html
 */

/**
 * Controls the main chatbot window.
 */
public class MainWindow extends AnchorPane {
    private final Image userImage =
            new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image jeffImage =
            new Image(getClass().getResourceAsStream("/images/DaJeff.png"));

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Jeff jeff;

    /**
     * Creates the main chatbot window controller.
     */
    public MainWindow() {
    }

    /**
     * Initializes the GUI after its FXML elements have been loaded.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null
                : "scrollPane was not injected by FXML";
        assert dialogContainer != null
                : "dialogContainer was not injected by FXML";
        assert userInput != null
                : "userInput was not injected by FXML";
        assert sendButton != null
                : "sendButton was not injected by FXML";

        scrollPane.vvalueProperty().bind(
                dialogContainer.heightProperty());
    }

    /**
     * Supplies the Jeff instance and displays the welcome message.
     *
     * @param jeff Jeff instance.
     */
    public void setJeff(Jeff jeff) {
        assert jeff != null : "Jeff instance must not be null";
        this.jeff = jeff;

        dialogContainer.getChildren().add(
                DialogBox.getJeffDialog(
                        "Hello! I'm Jeff.\nWhat can I do for you?",
                        jeffImage));
    }

    /**
     * Displays the user's command and Jeff's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = jeff.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getJeffDialog(response, jeffImage));

        userInput.clear();

        if (Parser.getCommandWord(input).equals("bye")) {
            closeAfterDelay();
        }
    }

    /**
     * Disables input and closes the application after showing the goodbye message.
     */
    private void closeAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition exitDelay =
                new PauseTransition(Duration.seconds(1));
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }
}

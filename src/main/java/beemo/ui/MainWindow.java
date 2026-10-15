package beemo.ui;

import beemo.Beemo;
import beemo.CommandResult;
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

/**
 * Controls Beemo's main chat window.
 */
public class MainWindow extends AnchorPane {
    private static final String ERROR_PREFIX = "OOPS...";
    private static final Duration EXIT_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image beemoImage = new Image(getClass().getResourceAsStream("/images/Beemo.png"));
    private Beemo beemo;

    /**
     * Connects scrolling to the height of the chat history.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the Beemo instance that processes commands.
     *
     * @param beemo Beemo instance.
     */
    public void setBeemo(Beemo beemo) {
        this.beemo = beemo;
        dialogContainer.getChildren().add(DialogBox.getBeemoDialog(
                "Hello! I'm Beemo (˶˃⤙˂˶)! What can I do for you?", beemoImage));
    }

    /**
     * Displays the user's command and Beemo's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        CommandResult result = beemo.getCommandResult(input);
        String response = result.response();
        DialogBox responseDialog = response.startsWith(ERROR_PREFIX)
                ? DialogBox.getErrorDialog(response, beemoImage)
                : DialogBox.getBeemoDialog(response, beemoImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                responseDialog);
        userInput.clear();

        if (result.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}

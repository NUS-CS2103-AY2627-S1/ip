package alzara.gui;

import alzara.Alzara;
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
 * Controller for the main GUI window: a scrollable dialog history plus a text
 * field and send button for typing commands.
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

    private Alzara alzara;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.jpg"));
    private final Image alzaraImage = new Image(this.getClass().getResourceAsStream("/images/Alzara.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Alzara} instance this window sends commands to, and
     * shows its welcome message as the first dialog box.
     *
     * @param alzara the Alzara instance to inject
     */
    public void setAlzara(Alzara alzara) {
        this.alzara = alzara;
        dialogContainer.getChildren().add(DialogBox.getAlzaraDialog(alzara.getWelcomeMessage(), alzaraImage));
    }

    /**
     * Creates two dialog boxes, one echoing the user's input and the other containing
     * Alzara's reply, and appends both to the dialog container. Clears the input field
     * afterwards.
     *
     * <p>If the command was {@code bye}, closes the window shortly after the reply
     * is shown, giving the user a moment to read it.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = alzara.getResponse(input);
        DialogBox replyDialog = alzara.isLastResponseError()
                ? DialogBox.getErrorDialog(response, alzaraImage)
                : DialogBox.getAlzaraDialog(response, alzaraImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                replyDialog
        );
        userInput.clear();

        if (alzara.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

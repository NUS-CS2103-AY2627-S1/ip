package sophon.ui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import sophon.Sophon;

/**
 * Controller for the main Sophon chat window.
 */
public class MainWindow {
    private static final int EXIT_DELAY_SECONDS = 3;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private final Image sophonImage = new Image(this.getClass().getResourceAsStream("/images/sophon.png"));

    private Sophon sophon;
    private Stage stage;

    @FXML
    private void initialize() {
        assert scrollPane != null : "scrollPane was not injected from FXML";
        assert dialogContainer != null : "dialogContainer was not injected from FXML";
        assert userInput != null : "userInput was not injected from FXML";
        assert sendButton != null : "sendButton was not injected from FXML";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Sophon instance and window used by this controller.
     *
     * @param sophon chatbot that generates responses.
     * @param stage window to close after the bye message.
     */
    public void setSophon(Sophon sophon, Stage stage) {
        this.sophon = sophon;
        this.stage = stage;
        dialogContainer.getChildren().add(DialogBox.getSophonDialog(sophon.getGreeting(), sophonImage));
    }

    /**
     * Shows the user's input and Sophon's response in the chat window.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText().trim();
        if (userText.isEmpty()) {
            return;
        }

        String sophonText = sophon.getResponse(userText);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getSophonDialog(sophonText, sophonImage)
        );
        userInput.clear();

        if (userText.equalsIgnoreCase("bye")) {
            closeAfterDelay();
        }
    }

    private void closeAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
        delay.setOnFinished(event -> stage.close());
        delay.play();
    }
}

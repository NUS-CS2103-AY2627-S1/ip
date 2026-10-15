package ace.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main chat window, defined in {@code MainWindow.fxml}.
 * Wires the FXML's declared controls to Ace's real command logic via
 * {@link AceCore}, the same role {@code Main} played before the FXML
 * refactor.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private AceCore aceCore;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/Ace User.png"));
    private Image aceImage = new Image(this.getClass().getResourceAsStream("/images/Ace.png"));

    /**
     * Called by the FXML loader once the annotated fields above have been
     * injected. Keeps the chat history scrolled to the newest message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link AceCore} instance this window talks to, and shows
     * its welcome message as the first chat bubble.
     *
     * @param aceCore the adapter running Ace's real command logic.
     */
    public void setAceCore(AceCore aceCore) {
        this.aceCore = aceCore;
        dialogContainer.getChildren().addAll(DialogBox.getAceDialog(aceCore.getWelcomeMessage(), aceImage, false));
    }

    /**
     * Reads the current text field content, sends it through {@link AceCore}
     * exactly as the CLI would process it, and displays both the user's
     * message and Ace's real response as dialog boxes. If that command was
     * "bye", closes the window shortly after so the goodbye message is
     * still visible for a moment first.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        String aceText = aceCore.getResponse(userText);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getAceDialog(aceText, aceImage, aceCore.isError()));
        userInput.clear();

        if (aceCore.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

package chai.ui;

import chai.Chai;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controls Chai's main conversation window. */
public class MainWindow {
    /** Prefix identifying responses that should use error styling. */
    private static final String ERROR_PREFIX = "Oops —";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    /** Chai session used to process commands from this window. */
    private Chai chai;

    /** Configures behavior that only depends on the injected FXML controls. */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Connects the chatbot session to the window and displays its greeting.
     *
     * @param chai Chai session that will process commands.
     */
    public void setChai(Chai chai) {
        this.chai = chai;
        dialogContainer.getChildren().add(createChaiDialog(chai.getWelcomeMessage()));
        Platform.runLater(userInput::requestFocus);
    }

    /** Sends the current text to Chai and displays both sides of the exchange. */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText().trim();
        if (userText.isEmpty() || chai == null) {
            return;
        }

        String chaiText = chai.getResponse(userText);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText),
                createChaiDialog(chaiText));
        userInput.clear();

        if (userText.equalsIgnoreCase("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition closeDelay = new PauseTransition(Duration.millis(700));
            closeDelay.setOnFinished(event -> Platform.exit());
            closeDelay.play();
        }
    }

    /** Creates a normal or error-styled Chai dialog from response text. */
    private DialogBox createChaiDialog(String text) {
        return text.startsWith(ERROR_PREFIX)
                ? DialogBox.getErrorDialog(text)
                : DialogBox.getChaiDialog(text);
    }
}

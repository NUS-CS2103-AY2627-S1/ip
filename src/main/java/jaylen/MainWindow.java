package jaylen;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controls the main Jaylen chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Jaylen jaylen;

    /** Creates the controller that JavaFX connects to the main-window FXML. */
    public MainWindow() {
    }

    /** Keeps the newest dialog visible as messages are added. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Supplies the Jaylen instance that handles commands and displays its greeting.
     *
     * @param jaylen the task assistant backing this window
     */
    public void setJaylen(Jaylen jaylen) {
        this.jaylen = jaylen;
        dialogContainer.getChildren().add(prepareDialog(
                DialogBox.getJaylenDialog(jaylen.getWelcomeMessage())));
        if (jaylen.getStartupErrorMessage() != null) {
            dialogContainer.getChildren().add(prepareDialog(
                    DialogBox.getErrorDialog(jaylen.getStartupErrorMessage())));
        }
    }

    /** Adds the user's command and Jaylen's response to the conversation. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = jaylen.getResponse(input);
        DialogBox responseDialog = jaylen.isErrorResponse(response)
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getJaylenDialog(response);
        dialogContainer.getChildren().addAll(
                prepareDialog(DialogBox.getUserDialog(input)),
                prepareDialog(responseDialog));
        userInput.clear();
        userInput.requestFocus();

        if (input.equals("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }

    private DialogBox prepareDialog(DialogBox dialogBox) {
        dialogBox.bindDialogWidth(scrollPane.widthProperty());
        return dialogBox;
    }
}

package meka.gui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import meka.Meka;

/**
 * Controls the main MEKA chat window.
 */
public class MainWindow {
    private static final String GREETING = "Hello! I'm MEKA.\nWhat can I do for you?";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Meka meka;

    /**
     * Configures automatic scrolling after the FXML controls are loaded.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the application instance and displays MEKA's greeting.
     *
     * @param meka application that processes commands.
     */
    public void setMeka(Meka meka) {
        this.meka = meka;
        dialogContainer.getChildren().add(DialogBox.getMekaDialog(GREETING));
    }

    /**
     * Sends non-empty input to MEKA and displays both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = meka.getResponse(input);
        DialogBox responseDialog = meka.isLastResponseError()
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getMekaDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseDialog);
        userInput.clear();

        if (meka.isExitRequested()) {
            userInput.setDisable(true);
            PauseTransition closingDelay = new PauseTransition(Duration.seconds(1));
            closingDelay.setOnFinished(event -> userInput.getScene().getWindow().hide());
            closingDelay.play();
        }
    }
}

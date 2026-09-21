package answerme.ui.gui;

import answerme.AnswerMe;
import answerme.command.CommandResult;
import answerme.ui.ResponseType;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controls user interactions in the primary AnswerMe window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private AnswerMe answerMe;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.jpg"));
    private Image botImage = new Image(this.getClass().getResourceAsStream("/images/AnswerMe.jpg"));

    /**
     * Configures the scroll pane to show the newest dialog box while allowing manual scrolling.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight)
                -> scrollPane.setVvalue(scrollPane.getVmax()));
    }

    /**
     * Supplies the AnswerMe instance that handles user commands.
     *
     * @param bot the AnswerMe instance to use.
     */
    public void setAnswerMe(AnswerMe bot) {
        answerMe = bot;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * AnswerMe's reply and then appends them to the dialog container. Clears
     * the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        CommandResult result = answerMe.processCommand(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAnswerMeDialog(result.response(), result.responseType(), botImage)
        );
        userInput.clear();

        if (result.shouldExit()) {
            closeAfterExitDelay();
        }
    }

    private void closeAfterExitDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        Stage stage = (Stage) userInput.getScene().getWindow();
        PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
        exitDelay.setOnFinished(event -> stage.close());
        exitDelay.play();
    }

    /**
     * Displays the chatbot's welcome message followed by any warning or error
     * encountered while loading saved tasks.
     */
    public void showWelcome() {
        String loadingErrorMessage = answerMe.getLoadingErrorMessage();
        String loadingWarningMessage = answerMe.getLoadingWarningMessage();

        dialogContainer.getChildren().addAll(
                DialogBox.getAnswerMeDialog(answerMe.showWelcomeMessage(), ResponseType.NORMAL, botImage)
        );

        if (loadingErrorMessage != null) {
            dialogContainer.getChildren().add(
                    DialogBox.getAnswerMeDialog(loadingErrorMessage, ResponseType.ERROR, botImage)
            );
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }

        if (loadingWarningMessage != null) {
            dialogContainer.getChildren().add(
                    DialogBox.getAnswerMeDialog(loadingWarningMessage, ResponseType.ERROR, botImage)
            );
        }
    }
}


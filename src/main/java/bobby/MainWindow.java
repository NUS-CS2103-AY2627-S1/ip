package bobby;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Controller for the main GUI. */
public class MainWindow extends AnchorPane {
    /** Pseudo-class used to highlight an input that Bobby could not process. */
    private static final PseudoClass INPUT_ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    /** Chatbot used to generate responses for user input. */
    private Bobby bobby;

    /** Image used for Bobby dialog boxes. */
    private final Image bobbyImage = new Image(this.getClass().getResourceAsStream("/images/Bobby.png"));

    /** Clears the input error highlight as soon as the user edits the command. */
    @FXML
    private void initialize() {
        userInput.textProperty().addListener((observable, oldValue, newValue) ->
                userInput.pseudoClassStateChanged(INPUT_ERROR_PSEUDO_CLASS, false));
    }

    /** Injects the Bobby instance used by this controller. */
    public void setBobby(Bobby bobby) {
        this.bobby = bobby;
        dialogContainer.getChildren().add(DialogBox.getBobbyDialog(
                bobby.getWelcomeMessage(), bobbyImage, null, false));
        scrollToLatestDialog();
    }

    /** Creates user and chatbot dialog boxes for the submitted input. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = bobby.getResponse(input);
        String commandType = bobby.getCommandType();
        boolean wasResponseError = bobby.wasLastResponseError();
        if (!input.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        }
        dialogContainer.getChildren().add(
                DialogBox.getBobbyDialog(response, bobbyImage, commandType, wasResponseError));
        scrollToLatestDialog();
        updateInputAfterResponse(wasResponseError);
        if ("ExitCommand".equals(commandType)) {
            Stage stage = (Stage) userInput.getScene().getWindow();
            stage.close();
        }
    }

    /** Scrolls to the latest dialog after JavaFX calculates the new content height. */
    private void scrollToLatestDialog() {
        Platform.runLater(() -> {
            dialogContainer.applyCss();
            dialogContainer.layout();
            scrollPane.applyCss();
            scrollPane.layout();

            // Changing the value first ensures the final maximum value triggers a scroll refresh.
            scrollPane.setVvalue(scrollPane.getVmin());
            scrollPane.setVvalue(scrollPane.getVmax());
        });
    }

    /** Clears submitted input and preserves its error highlight until the user types again. */
    private void updateInputAfterResponse(boolean wasResponseError) {
        userInput.clear();
        userInput.pseudoClassStateChanged(INPUT_ERROR_PSEUDO_CLASS, wasResponseError);
        if (wasResponseError) {
            userInput.requestFocus();
        }
    }

}

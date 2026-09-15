package ekud.ui;

import ekud.Ekud;
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
 * Controller for the main GUI window.
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

    private Ekud ekud;

    private final Image ekudImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    /**
     * Binds the scroll pane so it follows new dialog boxes to the bottom, and
     * lets the chat column shrink when the window is resized.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.setMinWidth(0);
        dialogContainer.setFillWidth(true);
    }

    /**
     * Injects the chatbot and shows its welcome message.
     * A save-file problem is shown in a separate error bubble so it is not
     * mixed into the greeting.
     *
     * @param ekud chatbot instance used to generate replies
     */
    public void setEkud(Ekud ekud) {
        // Main.java injects the chatbot after loading the FXML; the window cannot reply without it.
        assert ekud != null : "Chatbot must be injected before the window is used";
        this.ekud = ekud;
        dialogContainer.getChildren().add(
                DialogBox.getEkudDialog(ekud.getWelcomeMessage(), ekudImage, DialogStyle.NONE));
        String startupError = ekud.getStartupError();
        if (startupError != null) {
            dialogContainer.getChildren().add(
                    DialogBox.getEkudDialog(startupError, ekudImage, DialogStyle.ERROR));
        }
        Platform.runLater(() -> userInput.requestFocus());
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * Ekud's reply, then appends them to the dialog container. Clears the user
     * input after processing. Closes the window after {@code bye}.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        // setEkud is called when the window is created, before any user input.
        assert ekud != null : "setEkud must be called before handling input";
        String response = ekud.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getEkudDialog(response, ekudImage, ekud.getDialogStyle()));
        userInput.clear();
        setInputError(ekud.getDialogStyle() == DialogStyle.ERROR);

        if (ekud.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }

    /**
     * Tints the input field when the last reply was an error so a failed
     * command is visible even after the user looks away from the chat.
     *
     * @param isError whether the latest reply used {@link DialogStyle#ERROR}
     */
    private void setInputError(boolean isError) {
        userInput.getStyleClass().remove("error-input");
        if (isError) {
            userInput.getStyleClass().add("error-input");
        }
    }
}

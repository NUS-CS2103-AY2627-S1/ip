package erina.gui;

import erina.Erina;
import erina.Ui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main window: turns what the user types into a reply from
 * Erina, and shows both as dialog boxes.
 *
 * <p>Adapted from the {@code MainWindow} class in the se-education.org JavaFX
 * tutorial: https://se-education.org/guides/tutorials/javaFx.html
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

    private Erina erina;

    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private final Image erinaImage =
            new Image(this.getClass().getResourceAsStream("/images/erina.png"));

    /** Keeps the newest dialog box in view as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Sets the chatbot this window talks to, and shows its opening messages.
     *
     * @param erina the chatbot to use
     */
    public void setErina(Erina erina) {
        this.erina = erina;

        dialogContainer.getChildren().add(
                DialogBox.getErinaDialog(Ui.GREETING, erinaImage));

        // A save file that could not be read is worth saying out loud, in the
        // same place the user reads everything else, and marked as a problem.
        String loadError = erina.getLoadError();
        if (loadError != null) {
            dialogContainer.getChildren().add(
                    DialogBox.getErinaErrorDialog(loadError, erinaImage));
        }
    }

    /**
     * Shows the user's input and Erina's reply, then clears the input box.
     *
     * <p>A reply reporting a problem is highlighted, so the user notices that
     * the command did not work. After a {@code bye} command the window closes,
     * but only once the farewell has been shown, so the user sees it.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }

        String response = erina.getResponse(input);
        DialogBox reply = erina.isLastReplyError()
                ? DialogBox.getErinaErrorDialog(response, erinaImage)
                : DialogBox.getErinaDialog(response, erinaImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                reply
        );
        userInput.clear();

        // Clicking Send moves the focus to the button; give it back to the
        // input box so the next command can be typed straight away.
        userInput.requestFocus();

        if (erina.isExit()) {
            // Give the farewell a moment on screen before the window goes.
            PauseTransitionFactory.runAfterPause(Platform::exit);
        }
    }
}

package kenbot;

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
 * Controller for the main window.
 *
 * <p>The window's appearance lives in {@code MainWindow.fxml}; this class holds
 * only what it does. The fields below are not created here: {@link FXML} lets
 * the loader fill each one from the control carrying the matching
 * {@code fx:id}, which is why they are still private.</p>
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

    private Kenbot kenbot;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image kenbotImage = new Image(this.getClass().getResourceAsStream("/images/DaKenbot.png"));

    /**
     * Creates the controller. FXMLLoader calls this while building the
     * window, before any {@code @FXML} field has been filled in.
     */
    public MainWindow() {
    }

    /**
     * Finishes setting up the window once the controls exist.
     *
     * <p>This runs after the loader has filled in the fields above, so it is
     * the earliest point at which they can be used; a constructor would still
     * see them as null. Tying the scroll position to the conversation's height
     * keeps the newest message in view without anyone having to scroll.</p>
     */
    @FXML
    public void initialize() {
        // The loader fills these in by matching each fx:id in MainWindow.fxml
        // against a field name here, at run time. The compiler never opens the
        // FXML, so renaming one side and not the other still builds: the field
        // stays null, the window looks correct, and the first click fails with
        // nothing shown. This is the one link in the program that nothing else
        // verifies.
        assert scrollPane != null && dialogContainer != null
                && userInput != null && sendButton != null
                : "an fx:id in MainWindow.fxml no longer matches a field here";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives this window the Kenbot whose answers it should show.
     *
     * @param kenbot the chatbot that generates the replies
     */
    public void setKenbot(Kenbot kenbot) {
        this.kenbot = kenbot;
        dialogContainer.getChildren().add(
                DialogBox.getKenbotDialog(kenbot.getGreeting(), kenbotImage));

        // A save file that could not be read would otherwise look exactly like
        // an empty list, since the console's own report goes to a terminal a
        // window user may never see.
        String notice = kenbot.getStartupNotice();
        if (!notice.isEmpty()) {
            dialogContainer.getChildren().add(
                    DialogBox.getErrorDialog(notice, kenbotImage));
        }
    }

    /**
     * Shows what the user typed and what Kenbot said back, then clears the box.
     *
     * <p>All the thinking happens in {@link Kenbot#getResponse(String)}; this
     * method only moves text between the window and Kenbot.</p>
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = kenbot.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                kenbot.isLastResponseError()
                        ? DialogBox.getErrorDialog(response, kenbotImage)
                        : DialogBox.getKenbotDialog(response, kenbotImage));

        userInput.clear();

        if (kenbot.isExit()) {
            // Paused rather than closed at once, so the goodbye is readable
            // instead of vanishing with the window.
            PauseTransition wait = new PauseTransition(Duration.seconds(1.5));
            wait.setOnFinished(event -> Platform.exit());
            wait.play();
        }
    }
}

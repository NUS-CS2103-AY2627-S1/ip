package bucket;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 *
 * Each field marked FXML is filled in by the loader from the control carrying the
 * matching fx:id in MainWindow.fxml, so this class never builds any controls itself.
 *
 * That injection is the weak point worth asserting on. The compiler cannot check
 * that a Java field and an fx:id in an XML file still agree, so a renamed id leaves
 * a silently null field that only shows up later as a NullPointerException from
 * somewhere unrelated.
 */
public class MainWindow extends VBox {
    /** How long the goodbye stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private HBox inputBar;

    private Bucket bucket;

    // Only Bucket has a picture. The user knows who they are, and the width it would
    // take on every other line is better spent on the task lists Bucket sends back.
    private final Image bucketImage =
            new Image(this.getClass().getResourceAsStream("/images/DaBucket.png"));

    /**
     * Keeps the newest message in view.
     * Called automatically once the FXML has finished loading, which is the
     * earliest point at which the controls above actually exist.
     */
    @FXML
    public void initialize() {
        // Injection has just finished, so this is the first moment the mismatch above
        // can be detected, and the named message points straight at the broken fx:id.
        assert scrollPane != null : "fx:id \"scrollPane\" missing from MainWindow.fxml";
        assert dialogContainer != null : "fx:id \"dialogContainer\" missing from MainWindow.fxml";
        assert userInput != null : "fx:id \"userInput\" missing from MainWindow.fxml";
        assert sendButton != null : "fx:id \"sendButton\" missing from MainWindow.fxml";
        assert inputBar != null : "fx:id \"inputBar\" missing from MainWindow.fxml";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        // Typing is the normal thing to do on opening, so start there rather than
        // making the user click into the box first.
        Platform.runLater(() -> userInput.requestFocus());
    }

    /**
     * Hands over the chatbot to talk to, and shows its greeting.
     *
     * @param b Chatbot that answers the user's commands.
     */
    public void setBucket(Bucket b) {
        assert b != null : "the GUI needs a chatbot to talk to";

        bucket = b;
        dialogContainer.getChildren().add(
                DialogBox.getBucketDialog(Response.of(bucket.getWelcomeMessage()), bucketImage));
    }

    /**
     * Creates two dialog boxes, one echoing the user's input and the other holding
     * Bucket's reply, and appends them to the dialog container. Clears the user
     * input after processing.
     */
    @FXML
    private void handleUserInput() {
        // Main calls setBucket while the FXML is loading, before the stage is shown,
        // so there is no way for the user to reach this handler beforehand. If that
        // start-up order is ever rearranged, this fires instead of a bare NPE.
        assert bucket != null : "setBucket() must run before the user can type";

        String input = userInput.getText();

        // Enter on an empty box would otherwise add two blank dialog boxes.
        if (input.isBlank()) {
            return;
        }

        Response response = bucket.getResponse(input);
        assert response != null : "getResponse() must return a reply to put in a dialog box";

        int boxesBefore = dialogContainer.getChildren().size();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getBucketDialog(response, bucketImage));

        // One turn of conversation is always a pair, so the transcript stays readable
        // as alternating sides rather than drifting out of step.
        assert dialogContainer.getChildren().size() == boxesBefore + 2
                : "each exchange must add exactly two dialog boxes";

        userInput.clear();

        if (Bucket.isExitCommand(input)) {
            handleExit();
        }
    }

    /**
     * Closes the window a moment after the goodbye, so the user can read it.
     * The controls are disabled first, since anything typed during the pause
     * would be lost when the window goes.
     */
    private void handleExit() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}

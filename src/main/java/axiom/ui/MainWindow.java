package axiom.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import axiom.Axiom;
import axiom.AxiomReply;

/**
 * Controller for the main GUI.
 */
public class MainWindow {
    private static final double EXIT_DELAY_SECONDS = 1.5;
    private static final double HEADER_AVATAR_SIZE = 28.0;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ImageView headerAvatar;

    private Axiom axiom;

    /**
     * Initializes the main window after FXML fields are injected.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        setupHeaderAvatar();
        Platform.runLater(() -> userInput.requestFocus());
    }

    /**
     * Crops the header logo into a circle so it sits cleanly in the compact title bar.
     */
    private void setupHeaderAvatar() {
        Image axiomImage = new Image(this.getClass().getResourceAsStream("/images/DaAxiom.png"));
        headerAvatar.setImage(axiomImage);
        double radius = HEADER_AVATAR_SIZE / 2.0;
        headerAvatar.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Injects the AXIOM instance and shows the welcome message.
     *
     * @param axiom Chatbot instance that generates replies.
     */
    public void setAxiom(Axiom axiom) {
        this.axiom = axiom;
        addDialog(DialogBox.getAxiomDialog(axiom.getGuiWelcomeMessage()));
        if (axiom.getLoadError() != null) {
            addDialog(DialogBox.getErrorDialog(axiom.getLoadError()));
        }
    }

    /**
     * Creates dialog boxes for the user's command and AXIOM's reply, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        AxiomReply reply = axiom.getReply(input);
        addDialog(DialogBox.getUserDialog(input));
        if (reply.isError()) {
            addDialog(DialogBox.getErrorDialog(reply.getMessage()));
        } else {
            addDialog(DialogBox.getAxiomDialog(reply.getMessage()));
        }
        userInput.clear();

        if (axiom.isExit()) {
            exitAfterDelay();
        }
    }

    /**
     * Adds a dialog row that stretches with the chat pane so wrapped text follows window resizes.
     *
     * @param dialogBox Dialog row to display.
     */
    private void addDialog(DialogBox dialogBox) {
        dialogBox.prefWidthProperty().bind(dialogContainer.widthProperty());
        dialogContainer.getChildren().add(dialogBox);
    }

    /**
     * Disables input and closes the window after a short delay so the goodbye message is visible.
     */
    private void exitAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition delay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }
}

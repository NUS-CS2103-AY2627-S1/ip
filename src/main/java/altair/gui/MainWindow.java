package altair.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import altair.Altair;

/**
 * The controller for the main window.
 *
 * <p>It owns the scrolling list of dialog boxes and the input field, forwards
 * each typed line to {@link Altair#getResponse(String)}, and shows the reply.
 * When the user types {@code bye} it closes the window shortly after.</p>
 */
public class MainWindow {

    /** Scrolls the conversation when it grows past the window height. */
    @FXML
    private ScrollPane scrollPane;

    /** Holds one {@link DialogBox} per message, oldest first. */
    @FXML
    private VBox dialogContainer;

    /** Where the user types a command. */
    @FXML
    private TextField userInput;

    /** Sends the current input; the Enter key does the same. */
    @FXML
    private Button sendButton;

    /** The task manager that produces the replies. */
    private Altair altair;

    /** Avatar shown next to the user's messages. */
    private final Image userImage =
            new Image(MainWindow.class.getResourceAsStream("/images/DaUser.png"));

    /** Avatar shown next to Altair's replies. */
    private final Image altairImage =
            new Image(MainWindow.class.getResourceAsStream("/images/DaAltair.png"));

    /** Keeps the view scrolled to the newest message. */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the task manager and shows its opening greeting.
     *
     * @param altair the task manager to talk to.
     */
    public void setAltair(Altair altair) {
        this.altair = altair;
        DialogBox greeting = DialogBox.getAltairDialog(altair.getGreeting(), altairImage);
        greeting.bindBubbleWidth(dialogContainer.widthProperty());
        dialogContainer.getChildren().add(greeting);
    }

    /**
     * Handles one send: shows the user's line, shows Altair's reply, and clears
     * the input. If the line was {@code bye}, the window closes after a short
     * pause so the farewell stays visible.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isEmpty()) {
            return;
        }

        String response = altair.getResponse(input);
        DialogBox userDialog = DialogBox.getUserDialog(input, userImage);
        DialogBox altairDialog = DialogBox.getAltairDialog(response, altairImage, altair.hasError());
        userDialog.bindBubbleWidth(dialogContainer.widthProperty());
        altairDialog.bindBubbleWidth(dialogContainer.widthProperty());
        dialogContainer.getChildren().addAll(userDialog, altairDialog);
        userInput.clear();

        if (altair.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}

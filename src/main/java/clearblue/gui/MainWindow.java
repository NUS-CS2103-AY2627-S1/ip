package clearblue.gui;

import clearblue.Clearblue;
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
 * Controller for the Clearblue GUI's main window: a scrolling list of chat
 * bubbles above a text field and send button.
 */
public class MainWindow extends AnchorPane {
    // Console users get this via Ui.showWelcome() when Clearblue.run() starts;
    // the GUI has no equivalent entry point, so without this the window would
    // otherwise open completely blank until the user's first message.
    private static final String GREETING_MESSAGE = "Hello! I'm Clearblue. What can I do for you? :)";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Clearblue clearblue;

    private final Image userImage = new Image(MainWindow.class.getResourceAsStream("/images/User.png"));
    private final Image botImage = new Image(MainWindow.class.getResourceAsStream("/images/Bot.png"));

    /**
     * Keeps the scroll pane pinned to the bottom as new dialog boxes are
     * added.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Clearblue} instance this window sends commands to,
     * and shows Clearblue's greeting as the first dialog box.
     *
     * @param clearblue chatbot instance to wrap
     */
    public void setClearblue(Clearblue clearblue) {
        this.clearblue = clearblue;
        dialogContainer.getChildren().add(DialogBox.getClearblueDialog(GREETING_MESSAGE, botImage));
    }

    /**
     * Reads the text field, gets Clearblue's reply, and appends both as
     * dialog boxes to the conversation, styling the reply as an error if
     * {@link Clearblue#isLastResponseError()} says it was one. Closes the
     * window if the command was {@code bye}, after a short pause so the
     * farewell message is visible.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = clearblue.getResponse(input);
        DialogBox clearblueDialog = clearblue.isLastResponseError()
                ? DialogBox.getErrorDialog(response, botImage)
                : DialogBox.getClearblueDialog(response, botImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                clearblueDialog
        );
        userInput.clear();

        if (clearblue.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

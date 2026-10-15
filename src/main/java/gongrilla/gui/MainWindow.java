package gongrilla.gui;

import gongrilla.Gongrilla;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controls Gongrilla's main window. */
public class MainWindow {
    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button sendButton;

    private final Runnable exitAction;
    private final PauseTransition exitDelay = new PauseTransition(Duration.seconds(2));
    private boolean isExiting;

    private final Image userImage = new Image(
            MainWindow.class.getResourceAsStream("/images/Bananini.png"));
    private final Image gongrillaImage = new Image(
            MainWindow.class.getResourceAsStream("/images/Gongrillini.png"));
    private Gongrilla gongrilla;

    /** Creates the controller with the normal JavaFX shutdown action. */
    public MainWindow() {
        this(Platform::exit);
    }

    /** Supplies a shutdown action so tests can observe exit without stopping JavaFX. */
    MainWindow(Runnable exitAction) {
        this.exitAction = exitAction;
    }

    /**
     * Supplies the backend that processes commands.
     *
     * @param gongrilla backend shared by this window's conversations.
     */
    public void setGongrilla(Gongrilla gongrilla) {
        this.gongrilla = gongrilla;
        String welcome =
                  "  _--==--_  \n"
                + " / _    _ \\ \n"
                + " \\        / \n"
                + " |  (..)  |  \n"
                + " \\   __   / \n"
                + "  \\______/  \n"
                + "sup";
        dialogContainer.getChildren().add(DialogBox.getGongrillaDialog(welcome, gongrillaImage, "Welcome"));
        scrollToBottom();
    }

    /** Displays the user's command and Gongrilla's response. */
    @FXML
    private void handleUserInput() {
        if (isExiting) {
            return;
        }
        String input = userInput.getText();
        String response = gongrilla.getResponse(input);
        String commandType = gongrilla.getCommandType();

        DialogBox reply = DialogBox.getGongrillaDialog(response, gongrillaImage, commandType);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage), reply);
        scrollToBottom();
        userInput.clear();
        if ("ExitCommand".equals(commandType)) {
            isExiting = true;
            userInput.setDisable(true);
            sendButton.setDisable(true);
            // Keep the farewell visible without blocking JavaFX's UI thread.
            exitDelay.setOnFinished(event -> exitAction.run());
            exitDelay.playFromStart();
        }
    }

    /** Moves the conversation view to the newest message after JavaFX lays it out. */
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
}

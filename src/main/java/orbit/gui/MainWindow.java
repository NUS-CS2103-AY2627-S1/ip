package orbit.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import orbit.ChatSession;

/**
 * Connects the FXML chat controls to Orbit without embedding task logic in the view.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private ChatSession session;
    private Runnable exitAction;

    /**
     * Creates the controller before FXMLLoader injects its controls.
     */
    public MainWindow() {
    }

    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Attaches a conversation and shows its initial greeting.
     *
     * @param session conversation whose task state should be shown
     * @param exitAction action to run after a valid bye command
     */
    public void setSession(ChatSession session, Runnable exitAction) {
        this.session = session;
        this.exitAction = exitAction;
        dialogContainer.getChildren().setAll(DialogBox.getOrbitDialog(session.getGreeting()));
    }

    @FXML
    private void handleUserInput() {
        if (session == null || session.isExitRequested()) {
            return;
        }
        String input = userInput.getText();
        String response = session.getResponse(input);
        if (!input.isBlank()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        }
        dialogContainer.getChildren().add(DialogBox.getOrbitDialog(response));
        userInput.clear();
        userInput.requestFocus();
        if (session.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            exitAction.run();
        }
    }
}

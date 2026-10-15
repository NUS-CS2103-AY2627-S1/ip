package orbit.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import orbit.Orbit;

/**
 * Controls Orbit's main chat window.
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

    private Orbit orbit;

    /**
     * Creates the controller used by the main-window FXML loader.
     */
    public MainWindow() {
    }

    /**
     * Keeps the newest messages visible as the conversation grows.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Connects the view to Orbit and displays its greeting.
     *
     * @param orbit chatbot that processes commands
     */
    public void setOrbit(Orbit orbit) {
        this.orbit = orbit;
        dialogContainer.getChildren().add(DialogBox.getOrbitDialog(orbit.getWelcomeMessage()));
        userInput.requestFocus();
    }

    /**
     * Sends the current input to Orbit and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty() || orbit == null) {
            return;
        }

        String response = orbit.getResponse(input);
        DialogBox orbitDialog = orbit.isLastResponseError()
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getOrbitDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                orbitDialog);
        userInput.clear();

        if (orbit.isExit()) {
            userInput.setDisable(true);
            userInput.setPromptText("Orbit has signed off");
            sendButton.setDisable(true);
        }
    }
}

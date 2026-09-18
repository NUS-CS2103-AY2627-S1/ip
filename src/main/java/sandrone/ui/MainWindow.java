package sandrone.ui;

import java.util.concurrent.ThreadLocalRandom;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import sandrone.Sandrone;

/** Controller for Sandrone's main JavaFX chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Sandrone sandrone;
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/user.png"));
    private final Image sandroneImage = new Image(getClass().getResourceAsStream("/images/sandrone.png"));

    /** Keeps the newest dialog visible as messages are added. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects Sandrone's existing application logic and displays its greeting. */
    public void setSandrone(Sandrone sandrone) {
        this.sandrone = sandrone;
        dialogContainer.getChildren().add(
                DialogBox.getSandroneDialog(sandrone.showWelcome(), sandroneImage));
    }

    /** Sends the entered command to Sandrone and displays its response. */
    @FXML
    private void handleUserInput() {
        assert sandrone != null : "Sandrone must be injected before handling user input";
        String input = userInput.getText();
        if (input.isBlank()) {
            if (ThreadLocalRandom.current().nextDouble() < 0.40) {
                dialogContainer.getChildren().add(
                        DialogBox.getSandroneDialog("Ever heard of knocking?", sandroneImage));
            }
            return;
        }

        String response = sandrone.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                sandrone.wasLastResponseError()
                        ? DialogBox.getErrorDialog(response, sandroneImage)
                        : DialogBox.getSandroneDialog(response, sandroneImage));
        userInput.clear();

        if (sandrone.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}

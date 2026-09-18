package kopi;

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

/** Controls Kopi's main window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/User.png"));
    private final Image kopiImage = new Image(getClass().getResourceAsStream("/images/Kopi.png"));
    private Kopi kopi;

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
                DialogBox.getKopiDialog("Hello! I'm Kopi.\nWhat can I do for you?", kopiImage));
        Platform.runLater(userInput::requestFocus);
    }

    /** Supplies the application that handles commands. */
    public void setKopi(Kopi kopi) {
        this.kopi = kopi;
        if (kopi.getStartupMessage() != null) {
            dialogContainer.getChildren().add(getKopiDialog(kopi.getStartupMessage()));
        }
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = kopi.getResponse(input);
        if (input.isBlank()) {
            dialogContainer.getChildren().add(getKopiDialog(response));
            userInput.clear();
            return;
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                getKopiDialog(response));
        userInput.clear();
        if (kopi.isExitCommand(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(1));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    private DialogBox getKopiDialog(String response) {
        if (response.startsWith("OOPS!!! ")) {
            return DialogBox.getKopiErrorDialog(response, kopiImage);
        }
        return DialogBox.getKopiDialog(response, kopiImage);
    }
}

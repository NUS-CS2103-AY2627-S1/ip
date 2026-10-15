package nano.ui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import nano.Nano;

/**
 * Controller for the main GUI.
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

    private Nano nano;

    private Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));

    private Image nanoImage =
            new Image(this.getClass().getResourceAsStream("/images/DaNano.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Nano instance.
     */
    public void setNano(Nano nano) {
        this.nano = nano;
        DialogBox welcomeDialog = DialogBox.getNanoDialog(
                Ui.getWelcomeMessage(), nanoImage);
        welcomeDialog.getDialogLabel().fontProperty().unbind();
        welcomeDialog.getDialogLabel().setFont(Font.font("Courier New", 18));
        welcomeDialog.getDialogLabel().setWrapText(false);
        welcomeDialog.fitTextWidth();
        dialogContainer.getChildren().add(welcomeDialog);
    }

    /**
     * Processes the user's input and displays Nano's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = nano.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getNanoDialog(response, nanoImage)
        );

        userInput.clear();

        if (input.trim().equals("bye")) {
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                Stage stage = (Stage) userInput.getScene().getWindow();
                stage.close();
            });
            pause.play();
        }
    }
}

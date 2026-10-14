package verity.gui;

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
import verity.Verity;
import verity.ui.Ui;

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

    private Verity verity;
    private boolean isClosing;
    private boolean greetingShown;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private Image verityImage = new Image(this.getClass().getResourceAsStream("/images/Verity.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Verity instance this window talks to, and shows the opening greeting so
     * the window doesn't start blank.
     *
     * @param v Verity instance to use.
     */
    public void setVerity(Verity v) {
        verity = v;
        if (!greetingShown) {
            dialogContainer.getChildren().add(DialogBox.getVerityDialog(Ui.GREETING, verityImage));
            greetingShown = true;
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Verity's
     * reply, then appends them to the dialog container. Clears the user input afterwards.
     * If the input was an exit command, closes the application shortly after showing the
     * farewell message.
     */
    @FXML
    private void handleUserInput() {
        if (verity == null) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(
                    "Verity is still getting ready. Please try again in a moment.", verityImage));
            return;
        }
        if (isClosing) {
            return;
        }

        String input = userInput.getText();
        String response = verity.getResponse(input);
        if (!input.isBlank()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        }
        DialogBox responseDialog = Verity.isErrorResponse(response)
                ? DialogBox.getErrorDialog(response, verityImage)
                : DialogBox.getVerityDialog(response, verityImage);
        dialogContainer.getChildren().add(responseDialog);
        userInput.clear();

        if (verity.isExit()) {
            isClosing = true;
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

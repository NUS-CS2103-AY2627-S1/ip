package wiz;

import java.util.Objects;

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
 * Controller for MainWindow. Provides the layout for the other controls.
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

    private Wiz wiz;

    private final Image userImage = new Image(
            Objects.requireNonNull(this.getClass().getResourceAsStream("/images/DaUser.png"))
    );
    private final Image wizImage = new Image(
            Objects.requireNonNull(this.getClass().getResourceAsStream("/images/DaWiz.png"))
    );

    @FXML
    public void initialize() {
        assert scrollPane != null : "scrollPane was not injected properly";
        assert dialogContainer != null : "dialogContainer was not injected properly";
        assert userInput != null : "userInput was not injected properly";
        assert sendButton != null : "sendButton was not injected properly";

        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Wiz instance and shows the welcome message.
     *
     * @param d The Wiz instance to use.
     */
    public void setWiz(Wiz d) {
        assert d != null : "Wiz instance cannot be null";
        wiz = d;
        dialogContainer.getChildren().add(
                DialogBox.getWizDialog("✨ Greetings, seeker of order! I am Wiz, your mystical task companion.\n"
                        + "Cast a spell or type 'help' to see what we can do!", wizImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Wiz's reply and then appends them
     * to the dialog container. Highlights errors in distinct styling when appropriate. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert wiz != null : "Wiz instance must be initialized before handling user input";
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }
        String response = wiz.getResponse(input);
        boolean isError = Wiz.isErrorResponse(response);

        DialogBox userDialog = DialogBox.getUserDialog(input, userImage);
        DialogBox wizDialog = isError
                ? DialogBox.getErrorDialog(response, wizImage)
                : DialogBox.getWizDialog(response, wizImage);

        dialogContainer.getChildren().addAll(userDialog, wizDialog);
        userInput.clear();

        if (Parser.getCommandWord(input).equals("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

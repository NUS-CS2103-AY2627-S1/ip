package braun.ui;

import braun.Braun;
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
 * Controller for the main GUI layout of the Braun chatbot.
 * Manages the chat scroll pane, dialog container, and user command inputs.
 *
 * Adapted from the SE-EDU JavaFX tutorial:
 * https://se-education.org/guides/tutorials/javaFxPart4.html
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

    private Braun braun;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private final Image braunImage = new Image(this.getClass().getResourceAsStream("/images/braun.png"));

    /**
     * Constructs a new {@code MainWindow} controller instance.
     */
    public MainWindow() {
    }

    /**
     * Initializes the controller, binds the scroll pane to dialog container height,
     * and binds container width to scroll pane width for responsive resizing.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(15));
        Platform.runLater(() -> userInput.requestFocus());
    }

    /**
     * Injects the Braun application instance and displays Braun's opening broadcast greeting.
     *
     * @param b the Braun logic instance.
     */
    public void setBraun(Braun b) {
        assert b != null : "Braun application instance cannot be null.";
        braun = b;
        dialogContainer.getChildren().add(
                DialogBox.getBraunDialog(braun.getWelcomeMessage(), braunImage)
        );
    }

    /**
     * Handles user input triggered by pressing Enter or clicking the Send button.
     * Queries Braun for a response, styles error transmissions distinctly,
     * and initiates exit if requested.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }

        String response = braun.getResponse(input);
        boolean isError = response.startsWith("*static*") || response.startsWith("*bzzzt* Invalid");
        DialogBox braunDialog = isError
                ? DialogBox.getBraunErrorDialog(response, braunImage)
                : DialogBox.getBraunDialog(response, braunImage);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                braunDialog
        );
        userInput.clear();
        userInput.requestFocus();

        if (input.trim().equalsIgnoreCase("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

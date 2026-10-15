package elora;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Controller for MainWindow.fxml: the chat window that displays the
 * conversation between the user and Elora, and forwards typed input to
 * an {@link Elora} instance.
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
    @FXML
    private ImageView headerAvatar;

    private Elora elora;

    private final Image eloraImage = new Image(getClass().getResourceAsStream("/images/elora.png"));

    /**
     * Called automatically by the FXMLLoader once this window's fields
     * are injected: keeps the scroll pane pinned to the newest message,
     * and crops the header avatar to a circle.
     */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        headerAvatar.setClip(new Circle(18, 18, 18));
    }

    /**
     * Connects this window to the given Elora instance and displays its
     * welcome message as the first bubble in the conversation.
     *
     * @param elora The Elora instance backing this window.
     */
    public void setElora(Elora elora) {
        this.elora = elora;
        dialogContainer.getChildren().add(
                DialogBox.getEloraDialog(elora.getWelcomeMessage(), eloraImage, false));
    }

    /**
     * Reads the text in the input field, sends it to Elora, and appends
     * both the user's message and Elora's reply as dialog bubbles. Error
     * replies are styled differently so mistakes catch the eye. Exits
     * the application if the input was the "bye" command.
     */
    @FXML
    private void handleUserInput() {
        assert elora != null : "setElora() must be called before the window can handle input";
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        Response response = elora.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getEloraDialog(response.getText(), eloraImage, response.isError()));
        userInput.clear();
        if (elora.isExitCommand(input)) {
            // Give the scene graph one render pulse to paint the goodbye
            // bubble before the toolkit shuts down; exiting immediately
            // here would close the window before it was ever drawn.
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

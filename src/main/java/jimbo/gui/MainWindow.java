package jimbo.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import jimbo.Jimbo;

/**
 * Controller for the main chat window: holds the conversation history and
 * routes each line of user input to {@link Jimbo#getResponse(String)}.
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

    private Jimbo jimbo;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private final Image jimboImage = new Image(this.getClass().getResourceAsStream("/images/Jimbo.png"));

    /**
     * Called by the FXML loader after all {@code @FXML} fields are injected.
     * Makes the scroll pane track the container's width and auto-scrolls it
     * to the bottom whenever a new dialog box is added.
     */
    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the {@link Jimbo} instance this window sends user input to,
     * and shows Jimbo's welcome message as the first dialog box in the chat.
     */
    public void setJimbo(Jimbo jimbo) {
        this.jimbo = jimbo;
        dialogContainer.getChildren().add(
                DialogBox.getJimboDialog(jimbo.getWelcomeMessage(), jimboImage, false));
    }

    /**
     * Reads the text currently in {@code userInput}, gets Jimbo's reply,
     * appends both as dialog boxes to {@code dialogContainer}, then clears
     * the input field. If the command was "bye", closes the app shortly
     * after, once the user has had a moment to see the goodbye message.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        Jimbo.Response response = jimbo.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getJimboDialog(response.text(), jimboImage, response.isError())
        );
        userInput.clear();

        if (input.equals("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

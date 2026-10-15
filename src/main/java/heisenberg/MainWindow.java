package heisenberg;

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

/** Controls the main window defined in {@code MainWindow.fxml}. */
public class MainWindow extends AnchorPane {
    private final Image userImage = loadImage("/images/DaHank.png");
    private final Image heisenbergImage = loadImage("/images/DaHeisenberg.png");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Heisenberg heisenberg;

    /** Configures behavior that requires controls injected from the FXML view. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the chatbot used to answer commands and displays its welcome message.
     *
     * @param heisenberg Chatbot backing this window.
     */
    public void setHeisenberg(Heisenberg heisenberg) {
        this.heisenberg = heisenberg;
        dialogContainer.getChildren().add(
                DialogBox.getHeisenbergDialog(heisenberg.getWelcomeMessage(), heisenbergImage));
        Platform.runLater(userInput::requestFocus);
    }

    /** Sends the entered command and appends the user and chatbot dialog boxes. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = heisenberg.getResponse(input);
        DialogBox responseDialog = heisenberg.wasLastResponseAnError()
                ? DialogBox.getErrorDialog(response, heisenbergImage)
                : DialogBox.getHeisenbergDialog(response, heisenbergImage);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                responseDialog);
        userInput.clear();

        if (!heisenberg.isRunning()) {
            closeAfterGoodbye();
        }
    }

    /** Leaves the goodbye response visible briefly before closing the application. */
    private void closeAfterGoodbye() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    private Image loadImage(String resourcePath) {
        return new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath));
    }
}

package bob.gui;

import bob.Bob;
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
 * Controls Bob's main chat window.
 */
public class MainWindow extends AnchorPane {
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image bobImage = new Image(getClass().getResourceAsStream("/images/DaBob.png"));
    private final Image bobErrorImage = new Image(getClass().getResourceAsStream("/images/BobError.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Bob bob;

    /**
     * Keeps the newest dialog visible when the conversation grows.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Bob instance that owns the task list and command logic.
     *
     * @param bob Bob application used to process user input
     */
    public void setBob(Bob bob) {
        this.bob = bob;
    }

    /**
     * Sends the text field contents to Bob and appends both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = bob.getResponse(input);
        boolean isError = response.lines().anyMatch(line -> line.startsWith(" oopsies !!"));
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBobDialog(response, isError ? bobErrorImage : bobImage, isError));
        userInput.clear();
        if (input.strip().equals("bye")) {
            PauseTransition farewellDelay = new PauseTransition(Duration.seconds(2));
            farewellDelay.setOnFinished(event -> Platform.exit());
            farewellDelay.play();
        }
    }
}

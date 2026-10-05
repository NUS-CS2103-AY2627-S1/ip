package friday;

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
 * Controls the main GUI window and passes user input to Friday.
 */
public class MainWindow extends AnchorPane {
    private static final double EXIT_DELAY_SECONDS = 0.8;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Friday friday;
    private final Image fridayImage = new Image(getClass().getResourceAsStream("/images/DaDuke2.jpeg"));

    /**
     * Sets up automatic scrolling as new dialog boxes are added.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
    }

    /**
     * Gives the GUI the Friday instance it should talk to.
     *
     * @param friday chatbot instance
     */
    public void setFriday(Friday friday) {
        assert friday != null : "MainWindow should be initialized with a Friday instance";

        this.friday = friday;
        dialogContainer.getChildren().add(DialogBox.getWelcomeDialog(friday.getWelcome(), fridayImage));
    }

    /**
     * Handles the user's current input and shows both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        assert friday != null : "Friday instance should be set before handling input";

        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = friday.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getFridayDialog(response, fridayImage, friday.wasLastResponseError())
        );
        userInput.clear();

        if (input.trim().equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}

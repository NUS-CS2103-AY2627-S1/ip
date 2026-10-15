package bubu;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final double EXIT_DELAY_SECONDS = 3.0;
    private static final String USER_IMAGE_RESOURCE = "/images/user.png";
    private static final String BUBU_IMAGE_RESOURCE = "/images/bubu.png";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ImageView backgroundImage;

    private Bubu bubu;

    private final Image userImage = new Image(
            this.getClass().getResourceAsStream(USER_IMAGE_RESOURCE));
    private final Image bubuImage = new Image(
            this.getClass().getResourceAsStream(BUBU_IMAGE_RESOURCE));

    /** Binds the conversation view to the latest dialog and prepares scrolling. */
    @FXML
    public void initialize() {
        assert backgroundImage.getParent() instanceof AnchorPane
                : "Background image must be placed inside the main AnchorPane";
        AnchorPane root = (AnchorPane) backgroundImage.getParent();
        backgroundImage.fitWidthProperty().bind(root.widthProperty());
        backgroundImage.fitHeightProperty().bind(root.heightProperty());
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Bubu instance used to process user commands.
     *
     * @param bubu Bubu instance to connect to this window.
     */
    public void setBubu(Bubu bubu) {
        assert bubu != null : "Bubu instance injected into MainWindow cannot be null";
        this.bubu = bubu;
        if (dialogContainer.getChildren().isEmpty()) {
            showWelcomeMessage();
        }
    }

    /** Adds Bubu's welcome message to the conversation when the window opens. */
    private void showWelcomeMessage() {
        String welcomeMessage = bubu.getWelcomeMessage();
        dialogContainer.getChildren().add(DialogBox.getBubuDialog(welcomeMessage, bubuImage));
    }

    /**
     * Creates dialog boxes for the user input and Bubu's reply, then appends them
     * to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert bubu != null : "Bubu controller must be initialized before handling input";
        assert dialogContainer != null : "FXML dialogContainer injection failed";
        String input = userInput.getText();
        if (input.isBlank()) {
            userInput.requestFocus();
            return;
        }
        String response = bubu.getResponse(input);
        DialogBox reply = DialogBox.getBubuDialog(response, bubuImage);
        if (bubu.wasLastResponseAnError()) {
            reply.setErrorStyle();
        }
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage), reply);
        userInput.clear();
        if (bubu.wasLastResponseAnExit()) {
            scheduleWindowClose();
        }
    }

    /** Closes the application after giving the goodbye message time to display. */
    private void scheduleWindowClose() {
        PauseTransition exitDelay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
        exitDelay.setOnFinished(event -> {
            Stage stage = (Stage) userInput.getScene().getWindow();
            stage.close();
        });
        exitDelay.play();
    }
}

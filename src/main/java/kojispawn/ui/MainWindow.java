package kojispawn.ui;

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
import kojispawn.KojisPawn;
import kojispawn.command.CommandType;

/**
 * Controls the main Koji's Pawn chat window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/StickmanIcon.png"));
    private final Image kojiImage = new Image(getClass().getResourceAsStream("/images/MaskIcon.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private KojisPawn koji;

    /**
     * Configures the dialog area to follow the latest message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot used to generate responses.
     *
     * @param koji Koji's Pawn chatbot instance.
     */
    public void setKoji(KojisPawn koji) {
        this.koji = koji;
        dialogContainer.getChildren().add(DialogBox.getKojiDialog(koji.getGreeting(), kojiImage));
    }

    /**
     * Ignores blank input, or adds the user's message and Koji's response before clearing the text field.
     */
    @FXML
    private void handleUserInput() {
        assert koji != null : "Koji must be initialized before handling user input";

        String userText = userInput.getText();
        if (isBlankInput(userText)) {
            return;
        }

        String kojiText = koji.getResponse(userText);
        CommandType commandType = koji.getLastCommandType();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getKojiDialog(kojiText, kojiImage, commandType));
        userInput.clear();

        if (koji.isExitRequested()) {
            scheduleExit();
        }
    }

    static boolean isBlankInput(String input) {
        return input.isBlank();
    }

    /**
     * Leaves the farewell visible for one second before closing the application.
     */
    private void scheduleExit() {
        PauseTransition exitDelay = new PauseTransition(Duration.seconds(1.0));
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }
}

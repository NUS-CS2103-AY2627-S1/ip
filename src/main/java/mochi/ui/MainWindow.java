package mochi.ui;

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

import mochi.Mochi;

/**
 * Controls Mochi's main JavaFX window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final Image userImage = new Image(
            getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image mochiImage = new Image(
            getClass().getResourceAsStream("/images/DaMochi.png"));

    private Mochi mochi;

    /**
     * Creates the controller for the main window.
     */
    public MainWindow() {
    }

    /**
     * Initializes the controls loaded from the main-window FXML document.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the backend used to process user input.
     *
     * @param mochi Mochi backend to use.
     */
    public void setMochi(Mochi mochi) {
        this.mochi = mochi;
        mochi.getLoadingMessages().stream()
                .map(message -> DialogBox.getMochiDialog(message, mochiImage))
                .forEach(dialogContainer.getChildren()::add);
    }

    /**
     * Processes the current input and displays the resulting conversation.
     */
    @FXML
    private void handleUserInput() {
        assert mochi != null : "Mochi backend must be set before handling user input";

        String input = userInput.getText();
        String response = mochi.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMochiDialog(response, mochiImage));
        userInput.clear();

        if (mochi.isExitRequested()) {
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }
}

package yappa.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import yappa.Yappa;

/**
 * Controls the main JavaFX window and translates user actions into Yappa
 * commands.
 */
public class MainWindow extends BorderPane {
    private static final int AVATAR_RENDER_SIZE = 256;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Yappa yappa;

    // Decode the large source images at avatar size to avoid Linux JavaFX
    // rendering artefacts when the images are downscaled during layout.
    private final Image userImage = new Image(
            getClass().getResourceAsStream("/images/user.png"),
            AVATAR_RENDER_SIZE,
            AVATAR_RENDER_SIZE,
            true,
            true);
    private final Image yappaImage = new Image(
            getClass().getResourceAsStream("/images/yappa.png"),
            AVATAR_RENDER_SIZE,
            AVATAR_RENDER_SIZE,
            true,
            true);

    /**
     * Initializes scrolling behavior after the FXML controls are loaded.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Yappa instance used to process commands.
     *
     * @param yappa Application logic instance.
     */
    public void setYappa(Yappa yappa) {
        this.yappa = yappa;
        String greeting = yappa.getGreeting();
        dialogContainer.getChildren().add(
                DialogBox.getYappaDialog(greeting, yappaImage, false));
    }

    // Adds the user's message and Yappa's reply, then clears the input field.
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = yappa.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getYappaDialog(response, yappaImage, yappa.isLastError()));

        userInput.clear();

        if (yappa.isExitRequested()) {
            exitAfterDelay();
        }

    }

    private void exitAfterDelay() {
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(event -> Platform.exit());
        delay.play();

    }
}

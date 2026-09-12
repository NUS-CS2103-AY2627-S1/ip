package alfred.ui;

import alfred.Alfred;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main JavaFX chat window.
 */
public class MainWindow {
    private static final String WELCOME_MESSAGE =
            "Good day, sir. Alfred is at your service in the cave. What shall we accomplish?";
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private final Image userImage = loadImage("/images/User.png");
    private final Image alfredImage = loadImage("/images/Alfred.png");
    private Alfred alfred;

    /**
     * Connects the dialog area to the scroll position after FXML injection.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Alfred application used to generate responses.
     *
     * @param alfred application that generates chat responses.
     */
    public void setAlfred(Alfred alfred) {
        this.alfred = alfred;
        dialogContainer.getChildren().add(DialogBox.getAlfredDialog(WELCOME_MESSAGE, alfredImage));
    }

    /**
     * Appends the user's message and Alfred's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        String alfredText = alfred.getResponse(userText);
        String commandType = alfred.getLastCommandType();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getAlfredDialog(alfredText, alfredImage, commandType));
        userInput.clear();

        if (alfred.isExitRequested()) {
            closeAfterFarewell();
        }
    }

    /**
     * Closes the application after leaving the farewell response visible briefly.
     */
    private void closeAfterFarewell() {
        PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
        exitDelay.setOnFinished(event -> userInput.getScene().getWindow().hide());
        exitDelay.play();
    }

    /**
     * Loads an image packaged with the application.
     *
     * @param imagePath classpath location of the image.
     * @return loaded image.
     */
    private Image loadImage(String imagePath) {
        return new Image(getClass().getResourceAsStream(imagePath));
    }
}

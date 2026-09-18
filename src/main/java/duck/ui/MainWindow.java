package duck.ui;

import java.util.Objects;

import duck.Duck;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controls Duck's main chat window defined in {@code MainWindow.fxml}.
 */
public class MainWindow {
    /** Delay between showing the farewell response and closing the window, in seconds. */
    private static final int EXIT_DELAY_SECONDS = 3;

    /** Safe response shown when an unexpected GUI command failure occurs. */
    private static final String UNEXPECTED_ERROR_MESSAGE =
            "Something unexpected happened. Your saved tasks are safe.";

    /** Original Duck mascot displayed beside chatbot responses. */
    private final Image duckImage = loadImage("/images/DuckMascot.png");

    /** Avatar displayed beside the user's commands. */
    private final Image userImage = loadImage("/images/User.png");

    /** Scrollable region containing the conversation. */
    @FXML
    private ScrollPane scrollPane;

    /** Vertical container holding user and Duck dialog boxes. */
    @FXML
    private VBox dialogContainer;

    /** Field in which the user enters a command. */
    @FXML
    private TextField userInput;

    /** Button that submits the current command. */
    @FXML
    private Button sendButton;

    /** Chatbot that handles commands entered in this window. */
    private Duck duck;

    /** Creates the controller before its controls are injected from FXML. */
    public MainWindow() {
    }

    /** Connects behavior that depends on controls injected from FXML. */
    @FXML
    private void initialize() {
        this.dialogContainer.heightProperty().addListener(
                observable -> this.scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the chatbot used by this controller and displays its welcome message.
     *
     * @param duck Chatbot that will handle commands.
     */
    public void setDuck(Duck duck) {
        this.duck = Objects.requireNonNull(duck, "Duck cannot be null.");
        this.dialogContainer.getChildren().add(
                DialogBox.getDuckDialog(this.duck.getWelcomeMessage(), this.duckImage));
    }

    /**
     * Ignores blank input or displays the submitted command and Duck's response.
     * A valid bye command disables further input and closes the window after a short delay.
     */
    @FXML
    private void handleUserInput() {
        String userText = this.userInput.getText();
        if (userText.isBlank()) {
            this.userInput.clear();
            this.userInput.requestFocus();
            return;
        }

        try {
            displayCommandAndResponse(userText);
        } catch (RuntimeException e) {
            System.err.println("Unexpected error while handling GUI input.");
            e.printStackTrace(System.err);
            this.dialogContainer.getChildren().add(
                    DialogBox.getErrorDialog(UNEXPECTED_ERROR_MESSAGE, this.duckImage));
        } finally {
            this.userInput.clear();
            if (!this.userInput.isDisabled()) {
                this.userInput.requestFocus();
            }
        }
    }

    /** Displays a non-blank command and the response produced by Duck. */
    private void displayCommandAndResponse(String userText) {
        this.dialogContainer.getChildren().add(
                DialogBox.getUserDialog(userText, this.userImage));
        String duckText = this.duck.getResponse(userText);
        DialogBox responseDialog = this.duck.isLastResponseError()
                ? DialogBox.getErrorDialog(duckText, this.duckImage)
                : DialogBox.getDuckDialog(duckText, this.duckImage);
        this.dialogContainer.getChildren().add(responseDialog);

        if (this.duck.isExitRequested()) {
            scheduleExit();
        }
    }

    /** Disables input, announces the configured delay, and closes the stage afterward. */
    private void scheduleExit() {
        this.userInput.setDisable(true);
        this.sendButton.setDisable(true);
        this.dialogContainer.getChildren().add(
                DialogBox.getDuckDialog(
                        "The window will close in " + EXIT_DELAY_SECONDS + " seconds.",
                        this.duckImage));

        PauseTransition exitDelay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
        exitDelay.setOnFinished(event -> getStage().close());
        exitDelay.play();
    }

    /** Returns the stage containing the injected send button. */
    private Stage getStage() {
        return (Stage) this.sendButton.getScene().getWindow();
    }

    /** Loads a required image resource with a clear failure message. */
    private static Image loadImage(String resourcePath) {
        Image image = new Image(Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath),
                "Image resource is missing: " + resourcePath));
        if (image.isError()) {
            throw new IllegalStateException(
                    "Unable to load image resource: " + resourcePath, image.getException());
        }
        return image;
    }
}

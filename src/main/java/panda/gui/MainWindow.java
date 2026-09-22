package panda.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import panda.Panda;

/**
 * Controls Panda's main JavaFX window.
 */
public class MainWindow extends BorderPane {
    /** Gives JavaFX time to render Panda's farewell before closing the window. */
    private static final Duration EXIT_DELAY = Duration.millis(750);
    private final Image pandaImage = new Image(
            getClass().getResourceAsStream("/images/panda-avatar.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    @FXML
    private ImageView headerAvatar;
    @FXML
    private Label inputFeedback;
    @FXML
    private VBox composer;

    private Panda panda;

    /**
     * Configures automatic scrolling and displays Panda's welcome message.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "fx:id=\"scrollPane\" was not injected.";
        assert dialogContainer != null : "fx:id=\"dialogContainer\" was not injected.";
        assert userInput != null : "fx:id=\"userInput\" was not injected.";
        assert sendButton != null : "fx:id=\"sendButton\" was not injected.";

        headerAvatar.setImage(pandaImage);
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
                DialogBox.getPandaDialog(
                        "Ready to tackle the bamboo pile? (^_^)", pandaImage));
    }

    /**
     * Supplies the Panda instance that processes entered commands.
     *
     * @param panda the application coordinator.
     */
    public void setPanda(Panda panda) {
        this.panda = panda;
    }

    /**
     * Displays the entered command and Panda's response, then handles exit requests.
     */
    @FXML
    private void handleUserInput() {
        assert panda != null : "Panda must be supplied before user input is handled.";

        String input = userInput.getText();
        boolean shouldExit = panda.isExitCommand(input);
        String response = panda.getResponse(input);
        boolean isError = response.startsWith("OOPS!!!");
        String mood = shouldExit ? "(^_^)/" : isError ? "(>_<)" : "(^_^)";
        String displayResponse = formatResponse(response, mood);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                isError ? DialogBox.getErrorDialog(displayResponse, pandaImage)
                        : DialogBox.getPandaDialog(displayResponse, pandaImage));
        inputFeedback.setText(isError ? "Check the command below. Panda kept it here for editing." : "");
        inputFeedback.setVisible(isError);
        inputFeedback.setManaged(isError);
        if (!isError) {
            userInput.clear();
        }
        FadeTransition arrival = new FadeTransition(Duration.millis(160),
                dialogContainer.getChildren().getLast());
        arrival.setFromValue(0.4);
        arrival.setToValue(1);
        arrival.play();
        userInput.requestFocus();

        if (shouldExit) {
            closeAfterFarewell();
        }
    }

    /**
     * Adds a compact mood marker to the first response line.
     */
    private String formatResponse(String response, String mood) {
        String normalized = response.stripTrailing().replace("\r\n", "\n");
        int firstBreak = normalized.indexOf('\n');
        if (firstBreak < 0) {
            return normalized + " " + mood;
        }
        return normalized.substring(0, firstBreak) + " " + mood + normalized.substring(firstBreak);
    }

    /**
     * Prevents further input and closes JavaFX after the farewell is rendered.
     */
    private void closeAfterFarewell() {
        composer.setDisable(true);
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }
}

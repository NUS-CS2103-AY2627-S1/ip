package surveyprogram.gui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import surveyprogram.processor.CommandResult;
import surveyprogram.processor.TaskCommandProcessor;
import surveyprogram.ui.Secret;

/**
 * Controller for the main GUI.
 */
public class MainWindow {
    private static final Duration OPENING_FADE_DURATION = Duration.seconds(1);
    private static final Duration CLOSING_FADE_DURATION = Duration.seconds(3);

    @FXML
    private Pane backgroundPane;
    @FXML
    private Pane backgroundFadeOverlay;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private TaskCommandProcessor commandProcessor;
    private DepthBackground depthBackground;

    private Image userImage = new Image(this.getClass().getResourceAsStream(
            "/images/Kris (Art by JMK-Prime).png"));
    private Image assistantImage = new Image(this.getClass().getResourceAsStream(
            "/images/Double Green Gaster from Deltarune.png"));
    private Image farewellImage = new Image(this.getClass().getResourceAsStream(
            "/images/Not a man.png"));

    /** Initializes the animated background and automatic dialogue scrolling. */
    @FXML
    public void initialize() {
        backgroundFadeOverlay.setOpacity(1);
        depthBackground = new DepthBackground(backgroundPane);
        depthBackground.start();
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        Platform.runLater(this::playOpeningFade);
    }

    /**
     * Injects the command processor and displays the introductory dialogue.
     *
     * @param commandProcessor processor that handles text-field commands
     */
    public void setCommandProcessor(TaskCommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
        dialogContainer.getChildren().add(
                DialogBox.getReplyDialog(Secret.getOpeningDialogue(), assistantImage));
    }

    /**
     * Displays the user's input and the application's reply, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        assert commandProcessor != null
                : "Command processor must be initialized before accepting input";
        String input = userInput.getText();
        CommandResult result = commandProcessor.process(input);
        String response = result.shouldContinue()
                ? result.response()
                : Secret.getClosingDialogue();
        Image replyImage = result.shouldContinue() ? assistantImage : farewellImage;
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getReplyDialog(response, replyImage, result.replyType())
        );
        userInput.clear();

        if (!result.shouldContinue()) {
            closeAfterFarewell();
        }
    }

    private void playOpeningFade() {
        FadeTransition openingFade = new FadeTransition(OPENING_FADE_DURATION, backgroundFadeOverlay);
        openingFade.setFromValue(1);
        openingFade.setToValue(0);
        openingFade.play();
    }

    /** Disables further interaction, fades the interface out, and then closes the application. */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        FadeTransition closingFade = new FadeTransition(CLOSING_FADE_DURATION, backgroundFadeOverlay);
        closingFade.setFromValue(backgroundFadeOverlay.getOpacity());
        closingFade.setToValue(1);
        closingFade.setOnFinished(event -> Platform.exit());
        closingFade.play();
    }
}

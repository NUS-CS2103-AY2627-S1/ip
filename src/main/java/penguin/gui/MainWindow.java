package penguin.gui;

import java.io.IOException;
import java.io.InputStream;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import penguin.Penguin;
import penguin.PenguinIdentity;

/**
 * Controller for the main GUI.
 */
public class MainWindow {
    /** Duration used for dialog entrance animations. */
    private static final double ANIMATION_DURATION_MILLIS = 260;

    /** Initial vertical offset used for dialog entrance animations. */
    private static final double SLIDE_DISTANCE = 12;

    /** Scrollable area containing the conversation. */
    @FXML
    private ScrollPane scrollPane;

    /** Container holding user and chatbot dialog boxes. */
    @FXML
    private VBox dialogContainer;

    /** Header label displaying the chatbot name. */
    @FXML
    private Label headerTitle;

    /** Header label displaying the chatbot description. */
    @FXML
    private Label headerSubtitle;

    /** Text field used to compose commands. */
    @FXML
    private TextField userInput;

    /** Button used to submit the current command. */
    @FXML
    private Button sendButton;

    /** Chatbot instance that processes GUI commands. */
    private Penguin penguin;

    /** Image displayed beside user messages. */
    private final Image userImage = loadImage("/images/user.png");

    /** Image displayed beside Penguin messages. */
    private final Image penguinImage = loadImage("/images/chatbot.png");

    /**
     * Creates the FXML controller for the main window.
     *
     * @throws IllegalStateException if a required avatar image cannot be loaded.
     */
    public MainWindow() {
    }

    /**
     * Initializes shared labels, the welcome dialog, and automatic scrolling.
     *
     * @throws IllegalStateException if the dialog layout cannot be loaded.
     */
    @FXML
    public void initialize() {
        headerTitle.setText("Chat with " + PenguinIdentity.CHATBOT_NAME);
        headerSubtitle.setText(PenguinIdentity.CHATBOT_DESCRIPTION);
        userInput.setPromptText(PenguinIdentity.INPUT_PROMPT);
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
                DialogBox.getPenguinDialog(
                        PenguinIdentity.GREETING_MESSAGE,
                        penguinImage));
    }

    /**
     * Injects the Penguin chatbot instance.
     *
     * @param penguinInstance chatbot instance used to process commands.
     */
    public void setPenguin(Penguin penguinInstance) {
        assert penguinInstance != null : "Penguin instance must not be null";

        penguin = penguinInstance;

        String startupResponse = penguin.getStartupResponse();
        if (!startupResponse.isBlank()) {
            addAnimatedDialog(DialogBox.getPenguinDialog(
                    startupResponse, penguinImage)).play();
        }
    }

    /**
     * Creates dialog boxes for the input and response, appends them to the dialog
     * container, and clears the input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert penguin != null : "Penguin must be injected before handling input";

        String input = userInput.getText();

        String response = penguin.getResponse(input);

        ParallelTransition responseAnimation;
        if (input.isBlank()) {
            responseAnimation = addAnimatedDialog(
                    DialogBox.getPenguinDialog(response, penguinImage));
        } else {
            Node userDialog = DialogBox.getUserDialog(input, userImage);
            Node penguinDialog = DialogBox.getPenguinDialog(response, penguinImage);
            addAnimatedDialog(userDialog).play();
            responseAnimation = addAnimatedDialog(penguinDialog);
        }

        userInput.clear();

        if (penguin.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            responseAnimation.setOnFinished(event -> Platform.exit());
        }
        responseAnimation.play();
    }

    /**
     * Applies a task-oriented suggestion to the message composer.
     * The list suggestion is submitted immediately because it needs no further input.
     *
     * @param event click event from a suggestion button.
     */
    @FXML
    private void handleSuggestion(ActionEvent event) {
        assert event.getSource() instanceof Button : "Suggestion source must be a button";
        assert ((Button) event.getSource()).getUserData() instanceof String
                : "Suggestion command template must be a string";

        Button suggestion = (Button) event.getSource();
        String commandTemplate = (String) suggestion.getUserData();
        userInput.setText(commandTemplate);
        userInput.positionCaret(commandTemplate.length());
        userInput.requestFocus();

        if ("list".equals(commandTemplate)) {
            handleUserInput();
        }
    }

    /**
     * Loads a required GUI image.
     *
     * @param resourcePath classpath path of the image.
     * @return loaded image.
     * @throws IllegalStateException if the image cannot be found.
     */
    private Image loadImage(String resourcePath) {
        try (InputStream imageStream = getClass().getResourceAsStream(resourcePath)) {
            if (imageStream == null) {
                throw new IllegalStateException("Unable to load image: " + resourcePath);
            }
            return new Image(imageStream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read image: " + resourcePath, e);
        }
    }

    /**
     * Adds a dialog with a short fade-and-slide entrance animation.
     *
     * @param dialog dialog node to add.
     * @return prepared animation for the added dialog.
     */
    private ParallelTransition addAnimatedDialog(Node dialog) {
        dialogContainer.getChildren().add(dialog);

        Duration duration = Duration.millis(ANIMATION_DURATION_MILLIS);
        FadeTransition fade = new FadeTransition(duration, dialog);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(duration, dialog);
        slide.setFromY(SLIDE_DISTANCE);
        slide.setToY(0);

        ParallelTransition animation = new ParallelTransition(fade, slide);
        return animation;
    }
}

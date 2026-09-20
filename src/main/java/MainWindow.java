import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

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
import loki.exception.LokiExceptions;
import loki.logic.Logic;

/**
 * Controls the FXML-defined Loki chat window.
 */
public class MainWindow extends AnchorPane {
    private static final double FAREWELL_DELAY_SECONDS = 1.2;
    private static final String GREETING = "Ah, a new quest. Loki, God of Mischief, is ready to bend your tasks "
            + "to his will.\nType 'help' to see commands and examples.";
    private static final String ERROR_PREFIX = "Loki error:";

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

    private final Image lokiImage = loadImage("/images/loki.png");
    private Logic logic;
    private boolean isClosing;

    /**
     * Initializes scrolling and input feedback after FXML injection.
     */
    @FXML
    public void initialize() {
        headerAvatar.setImage(lokiImage);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0));
        userInput.textProperty().addListener((observable, oldText, newText) ->
                sendButton.setDisable(newText.trim().isEmpty()));
        sendButton.setDisable(true);
    }

    /**
     * Injects the command-processing logic and adds the initial greeting.
     *
     * @param logic the logic instance used to process chatbot commands.
     */
    public void setLogic(Logic logic) {
        this.logic = Objects.requireNonNull(logic);
        dialogContainer.getChildren().add(DialogBox.getLokiDialog(GREETING, lokiImage));
        userInput.requestFocus();
    }

    /**
     * Processes the current text field value and appends both sides of the conversation.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText().trim();
        if (userText.isEmpty()) {
            return;
        }

        String response = logic.processCommand(userText);
        boolean isError = response.startsWith(ERROR_PREFIX);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText),
                DialogBox.getLokiDialog(response, lokiImage, isError));
        userInput.clear();

        if (isExitCommand(userText) && !isError) {
            isClosing = true;
            userInput.setDisable(true);
            sendButton.setDisable(true);

            PauseTransition farewellDelay = new PauseTransition(Duration.seconds(FAREWELL_DELAY_SECONDS));
            farewellDelay.setOnFinished(event -> closeWindow());
            farewellDelay.play();
        }
    }

    /**
     * Saves the current task state before the JavaFX window closes.
     */
    public void saveData() {
        if (logic == null || isClosing) {
            return;
        }

        try {
            logic.save();
        } catch (LokiExceptions exception) {
            dialogContainer.getChildren().add(
                    DialogBox.getLokiDialog(exception.getMessage(), lokiImage, true));
        }
    }

    /**
     * Loads the Loki icon from the application resources.
     *
     * @param resourcePath the classpath path of the image.
     * @return the loaded image.
     */
    private static Image loadImage(String resourcePath) {
        InputStream imageStream = MainWindow.class.getResourceAsStream(resourcePath);
        if (imageStream == null) {
            throw new IllegalStateException("Missing GUI image resource: " + resourcePath);
        }

        try (imageStream) {
            return new Image(imageStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load GUI image resource: " + resourcePath, exception);
        }
    }

    /**
     * Closes the JavaFX window after the farewell response has been displayed.
     */
    private void closeWindow() {
        Stage stage = (Stage) userInput.getScene().getWindow();
        stage.close();
    }

    /**
     * Checks whether a command requests that the application close.
     *
     * @param input the normalized command entered by the user.
     * @return true if the command is an exit command.
     */
    private static boolean isExitCommand(String input) {
        return input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("faretheewell");
    }
}

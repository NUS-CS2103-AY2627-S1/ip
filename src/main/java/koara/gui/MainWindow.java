package koara.gui;

import java.util.Objects;

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
import koara.Koara;
import koara.Koara.CommandResult;

/**
 * Controls the main chatbot window defined in {@code MainWindow.fxml}.
 */
public class MainWindow extends AnchorPane {
    private static final double SCROLL_BOTTOM_POSITION = 1.0;
    private static final Duration EXIT_DELAY = Duration.seconds(1);
    private static final String WELCOME_MESSAGE =
            "Wassup! Koara here—your steady productivity sidekick.\n"
                    + "Drop me a task and let's lock in.";

    private final Image koaraImage = loadImage("/images/DaKoara.png");

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Koara koara;

    /**
     * Configures behavior that depends on the controls injected from FXML.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "FXML loader must inject the scroll pane";
        assert dialogContainer != null : "FXML loader must inject the dialog container";
        assert userInput != null : "FXML loader must inject the user input field";
        assert sendButton != null : "FXML loader must inject the send button";
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(SCROLL_BOTTOM_POSITION));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Supplies the Koara instance that processes commands.
     *
     * @param koara Koara instance used by this window.
     */
    public void setKoara(Koara koara) {
        this.koara = Objects.requireNonNull(koara);
        addKoaraDialog(WELCOME_MESSAGE);
        koara.getStartupError().ifPresent(this::addErrorDialog);
    }

    /**
     * Processes the current input and displays the conversation.
     */
    @FXML
    private void handleUserInput() {
        assert koara != null : "Koara must be set before processing input";
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            addErrorDialog("Alamak, the command box is empty. Type something and we go again!");
            return;
        }

        CommandResult result = koara.getCommandResult(input);
        DialogBox responseDialog = result.isError()
                ? DialogBox.getErrorDialog(result.message(), koaraImage)
                : DialogBox.getKoaraDialog(result.message(), koaraImage);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input), responseDialog);
        userInput.clear();

        if (input.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
            exitDelay.setOnFinished(event -> Platform.exit());
            exitDelay.play();
        }
    }

    /**
     * Adds a response from Koara without a preceding user message.
     *
     * @param response Response to display.
     */
    private void addKoaraDialog(String response) {
        dialogContainer.getChildren().add(
                DialogBox.getKoaraDialog(response, koaraImage));
    }

    /**
     * Adds a visually prominent error response from Koara.
     *
     * @param response Error response to display.
     */
    private void addErrorDialog(String response) {
        dialogContainer.getChildren().add(
                DialogBox.getErrorDialog(response, koaraImage));
    }

    /**
     * Loads an image required by the interface.
     *
     * @param resourcePath Classpath location of the image.
     * @return Loaded image.
     */
    private static Image loadImage(String resourcePath) {
        return new Image(Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath));
    }
}

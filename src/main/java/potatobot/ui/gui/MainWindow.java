package potatobot.ui.gui;

import java.net.URL;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import potatobot.logic.CommandResult;
import potatobot.logic.PotatoBot;

/**
 * Controls the main PotatoBot chat window.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.seconds(1);
    /**
     * Welcome text matching the console greeting, displayed beside PotatoBot's
     * avatar.
     */
    private static final String GREETING = "Hello! I'm PotatoBot, your trusty spud assistant.\n"
            + "What tasks do you want me to store for you? "
            + "(Say \"bye\" if you want me to leave you alone)";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private PotatoBot potatoBot;
    private SmoothScroll smoothScroll;

    private final Image userImage = loadImage("/userPic.jpg");
    private final Image potatoBotImage = loadImage("/potato-trans-background.png");

    /**
     * Enables smooth scrolling and greets the user when the chat window opens.
     */
    @FXML
    public void initialize() {
        smoothScroll = new SmoothScroll(scrollPane);
        dialogContainer.getChildren().add(DialogBox.getBotDialog(GREETING, potatoBotImage, false));
    }

    /**
     * Supplies the backend instance shared throughout the GUI session.
     *
     * @param potatoBot PotatoBot backend used by this window.
     */
    public void setPotatoBot(PotatoBot potatoBot) {
        this.potatoBot = potatoBot;

        String startupErrorMessage = potatoBot.getStartupErrorMessage();
        if (startupErrorMessage != null) {
            dialogContainer.getChildren().add(
                    DialogBox.getBotDialog(startupErrorMessage, potatoBotImage, true));
            scrollToLatestMessage();
        }
    }

    /**
     * Sends the user's input to PotatoBot and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        CommandResult result = potatoBot.respondTo(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBotDialog(result.message(), potatoBotImage, result.isError()));
        userInput.clear();
        scrollToLatestMessage();

        if (result.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            closeAfterDelay();
        } else {
            userInput.requestFocus();
        }
    }

    /**
     * Waits for the new dialogs to be laid out before revealing the latest reply.
     */
    private void scrollToLatestMessage() {
        Platform.runLater(() -> {
            scrollPane.applyCss();
            scrollPane.layout();
            smoothScroll.stop();
            scrollPane.setVvalue(scrollPane.getVmax());
        });
    }

    /**
     * Gives JavaFX time to display the final response before closing the
     * application.
     */
    private static void closeAfterDelay() {
        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /**
     * Loads a required image resource and reports its path clearly if it is absent.
     *
     * @param resourcePath Classpath location of the image.
     * @return Loaded JavaFX image.
     */
    private static Image loadImage(String resourcePath) {
        URL imageUrl = MainWindow.class.getResource(resourcePath);
        if (imageUrl == null) {
            throw new IllegalStateException("Missing image resource: " + resourcePath);
        }
        return new Image(imageUrl.toExternalForm());
    }
}

package habpyduck.ui;

import habpyduck.HabpyDuck;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main chat window.
 */
public class MainWindow {
    private static final double EXIT_DELAY_SECONDS = 3.0;
    private static final String EXIT_COMMAND_TYPE = "ExitCommand";
    private static final String WELCOME_MESSAGE = "Hi friend! I'm HabpyDuck.\nWhat can I do for you today?";
    private static final double MIN_SCROLL_VALUE = 0.0;
    private static final double MAX_SCROLL_VALUE = 1.0;

    private HabpyDuck habpyDuck;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    @FXML
    private HBox inputArea;

    /**
     * Initializes JavaFX bindings and the first greeting message.
     */
    @FXML
    public void initialize() {
        scrollPane.setMinHeight(0);
        inputArea.setMinHeight(Region.USE_PREF_SIZE);
        inputArea.setMaxHeight(Region.USE_PREF_SIZE);
        userInput.setMinHeight(Region.USE_PREF_SIZE);
        sendButton.setMinHeight(Region.USE_PREF_SIZE);
        dialogContainer.addEventFilter(ScrollEvent.SCROLL, getDialogScrollHandler());
        dialogContainer.getChildren().add(DialogBox.getHabpyDuckDialog(WELCOME_MESSAGE));
        scrollToLatestDialog();
    }

    /**
     * Sets the chatbot used to answer user commands.
     *
     * @param habpyDuck the chatbot used by this window.
     */
    public void setHabpyDuck(HabpyDuck habpyDuck) {
        this.habpyDuck = habpyDuck;
    }

    /**
     * Handles one command entered through the JavaFX input field.
     */
    @FXML
    public void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        String response = habpyDuck.getResponse(input);
        String commandType = habpyDuck.getCommandType();
        dialogContainer.getChildren().add(DialogBox.getHabpyDuckDialog(response, commandType));
        userInput.clear();
        scrollToLatestDialog();
        if (commandType.equals(EXIT_COMMAND_TYPE)) {
            closeApplicationAfterDelay();
        }
    }

    /**
     * Returns a scroll handler that lets touchpad and mouse-wheel scrolling work over chat messages.
     *
     * @return a handler for scroll events on the dialog container.
     */
    private EventHandler<ScrollEvent> getDialogScrollHandler() {
        return event -> {
            double contentHeight = dialogContainer.getBoundsInLocal().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();
            double scrollableHeight = contentHeight - viewportHeight;
            if (scrollableHeight <= 0) {
                return;
            }

            double nextScrollValue = scrollPane.getVvalue() - event.getDeltaY() / scrollableHeight;
            scrollPane.setVvalue(clampScrollValue(nextScrollValue));
            event.consume();
        };
    }

    /**
     * Keeps the newest message visible after HabpyDuck adds a reply.
     */
    private void scrollToLatestDialog() {
        Platform.runLater(() -> scrollPane.setVvalue(MAX_SCROLL_VALUE));
    }

    /**
     * Gives the user time to read the farewell message before closing the JavaFX application.
     */
    private void closeApplicationAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition pauseBeforeExit = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
        pauseBeforeExit.setOnFinished(event -> Platform.exit());
        pauseBeforeExit.play();
    }

    /**
     * Keeps a scroll value inside the range accepted by ScrollPane.
     *
     * @param scrollValue the requested scroll value.
     * @return the nearest valid scroll value.
     */
    private double clampScrollValue(double scrollValue) {
        if (scrollValue < MIN_SCROLL_VALUE) {
            return MIN_SCROLL_VALUE;
        }
        if (scrollValue > MAX_SCROLL_VALUE) {
            return MAX_SCROLL_VALUE;
        }
        return scrollValue;
    }
}

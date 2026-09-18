package bibi;

import java.io.InputStream;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main window: the transcript, the input box, and the send button.
 *
 * <p>It does no task handling of its own. Every command is passed to {@link Bibi},
 * which is the same object the console interface drives, and the reply that comes
 * back is put on screen as a {@link DialogBox}.
 */
public class MainWindow {
    /** Bibi's picture, shown beside every reply and reused by {@link Main} as the window icon. */
    static final String BIBI_ICON_PATH = "/images/DaBibi.png";

    /**
     * How long the goodbye stays on screen before the window closes. Long enough
     * to read, short enough not to feel stuck.
     */
    private static final Duration GOODBYE_PAUSE = Duration.seconds(1.5);

    /** How close to the newest output the user must be for new replies to follow automatically. */
    private static final double SCROLL_FOLLOW_DISTANCE = 40.0;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Bibi bibi;

    private final Image bibiIcon = loadImage(BIBI_ICON_PATH);
    private final CommandHistory commandHistory = new CommandHistory();

    /**
     * Creates the controller. The FXML loader calls this itself, so it must stay
     * public and take no arguments; the window's parts are filled in afterwards,
     * before {@link #initialize()} runs.
     */
    public MainWindow() {
    }

    /**
     * Enables history navigation within the command input.
     *
     * <p>Called by the FXML loader once the window's parts have been created.
     */
    @FXML
    public void initialize() {
        // Inspect selection before the text field's default arrow handling clears it.
        userInput.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryKey);
    }

    /**
     * Supplies the chatbot this window talks to, and shows its greeting.
     *
     * <p>Separate from the constructor because the FXML loader builds the
     * controller itself and cannot pass arguments to it.
     *
     * @param bibi the chatbot that answers what the user types
     */
    public void setBibi(Bibi bibi) {
        this.bibi = bibi;
        dialogContainer.getChildren().add(DialogBox.forBibi(bibi.getGreeting(), bibiIcon));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Answers the input, keeping rejected commands available for correction.
     *
     * <p>Wired to both the send button and the Enter key by the FXML.
     */
    @FXML
    private void handleUserInput() {
        // Main calls setBibi immediately after loading the FXML. Without it the
        // next line would throw a NullPointerException from inside an FXML event
        // handler, where JavaFX swallows the cause into a long trace.
        assert bibi != null : "setBibi was not called before the window accepted input";

        String input = userInput.getText();
        if (input.isBlank()) {
            userInput.requestFocus();
            return;
        }

        double scrollOffset = getScrollOffset();
        boolean shouldFollow = getScrollRange() - scrollOffset <= SCROLL_FOLLOW_DISTANCE;
        Reply reply = bibi.getResponse(input);
        commandHistory.add(input);
        dialogContainer.getChildren().addAll(
                DialogBox.forUser(input),
                DialogBox.forBibi(reply, bibiIcon));
        if (!bibi.isLastCommandRejected()) {
            userInput.clear();
        }
        userInput.requestFocus();
        userInput.positionCaret(userInput.getLength());
        restoreScrollPosition(scrollOffset, shouldFollow);

        if (bibi.isExitRequested()) {
            closeAfterGoodbye();
        }
    }

    /**
     * Recalls commands only for unmodified arrow keys in the input field.
     *
     * <p>Selection and modified keys keep their ordinary editing behavior. No
     * event handler on the transcript can accidentally recall or execute a command.
     *
     * @param event the key pressed while editing the input
     */
    private void handleHistoryKey(KeyEvent event) {
        if (event.isAltDown() || event.isControlDown() || event.isMetaDown() || event.isShiftDown()
                || userInput.getSelection().getLength() > 0) {
            return;
        }
        if (event.getCode() == KeyCode.UP) {
            userInput.setText(commandHistory.recallPrevious(userInput.getText()));
        } else if (event.getCode() == KeyCode.DOWN) {
            userInput.setText(commandHistory.recallNext(userInput.getText()));
        } else {
            return;
        }
        userInput.positionCaret(userInput.getLength());
        event.consume();
    }

    /**
     * Returns the height of transcript content outside the visible viewport.
     */
    private double getScrollRange() {
        return Math.max(0, dialogContainer.getHeight() - scrollPane.getViewportBounds().getHeight());
    }

    /**
     * Returns the reading position in pixels so appending text cannot shift older output.
     */
    private double getScrollOffset() {
        double valueRange = scrollPane.getVmax() - scrollPane.getVmin();
        return valueRange == 0 ? 0
                : (scrollPane.getVvalue() - scrollPane.getVmin()) / valueRange * getScrollRange();
    }

    /**
     * Restores the reading position after the new replies have been laid out.
     *
     * @param scrollOffset the previous distance from the beginning of the transcript
     * @param shouldFollow whether the user was close enough to the bottom to follow new output
     */
    private void restoreScrollPosition(double scrollOffset, boolean shouldFollow) {
        Platform.runLater(() -> {
            scrollPane.applyCss();
            scrollPane.layout();
            double scrollRange = getScrollRange();
            double fraction = shouldFollow || scrollRange == 0 ? 1 : scrollOffset / scrollRange;
            scrollPane.setVvalue(scrollPane.getVmin()
                    + fraction * (scrollPane.getVmax() - scrollPane.getVmin()));
        });
    }

    /**
     * Closes the window a moment after Bibi has said goodbye.
     *
     * <p>Closing immediately would wipe the parting message off the screen before
     * anyone could read it.
     */
    private void closeAfterGoodbye() {
        userInput.setDisable(true);

        PauseTransition pause = new PauseTransition(GOODBYE_PAUSE);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }

    /**
     * Reads one of the pictures packaged with the application.
     *
     * <p>Package-private so that {@link Main} loads the window icon the same
     * way, with the same clear error when a picture is missing.
     *
     * @param resourcePath the image's path inside the resources folder
     * @return the loaded image
     */
    static Image loadImage(String resourcePath) {
        InputStream stream = MainWindow.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalStateException("Missing bundled image: " + resourcePath);
        }
        return new Image(stream);
    }
}

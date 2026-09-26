package amadeus.ui;

import amadeus.Amadeus;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main window: turns what the user types into dialog boxes.
 * <p>
 * The controls themselves are described in {@code MainWindow.fxml}; JavaFX creates them
 * and drops them into the {@code @FXML} fields below, matching each field to the
 * {@code fx:id} of the same name. This class only reacts to what the user does with
 * them, and asks {@link Amadeus} what to reply.
 * <p>
 * It is a plain class rather than a subclass of a layout: the FXML names it as its
 * controller, which does not require the two to be the same object.
 */
public class MainWindow {

    /** How long the goodbye stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Amadeus amadeus;

    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image amadeusImage = loadImage("/images/DaAmadeus.png");

    /**
     * Prepares the window once JavaFX has finished building it.
     * <p>
     * Called automatically after the FXML is loaded, which is the earliest point at
     * which the {@code @FXML} fields above are guaranteed to exist - so anything that
     * touches a control belongs here rather than in a constructor.
     */
    @FXML
    public void initialize() {
        // Tying the scroll position to the container's height keeps the newest message
        // in view: every time the container grows, the pane scrolls to the bottom.
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives this window the chatbot it should talk to, and shows its greeting.
     *
     * @param amadeus the chatbot answering the user's commands.
     */
    public void setAmadeus(Amadeus amadeus) {
        this.amadeus = amadeus;
        // A greeting that reports a damaged save file is news the user needs, so it gets
        // the same amber treatment a failed command would.
        addReply(amadeus.getWelcome());
    }

    /**
     * Shows the user's message and the chatbot's reply, then clears the input box.
     * <p>
     * Wired to both the Send button and the Enter key by {@code onAction} in the FXML,
     * so the two behave identically.
     */
    @FXML
    private void handleUserInput() {
        // setAmadeus() runs in Main.start() right after the FXML is loaded and before
        // stage.show(), so no user input can reach this handler before amadeus is set.
        // The assertion documents that ordering instead of leaving it as something a
        // reader has to trace through Main.java to confirm.
        assert amadeus != null : "setAmadeus() must be called before the window can accept input";

        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = amadeus.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        addReply(response);
        userInput.clear();

        if (amadeus.isExit()) {
            // Further input would be pointless once the user has said goodbye, and the
            // pause gives them time to read the farewell before the window disappears.
            userInput.setDisable(true);
            sendButton.setDisable(true);

            PauseTransition pause = new PauseTransition(EXIT_DELAY);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }

    /**
     * Adds one reply from the chatbot, as an error if that is what it turned out to be.
     * <p>
     * The chatbot is asked rather than the text inspected, because whether something went
     * wrong is known where the failure happened; working it out again here by reading the
     * words would mean two places having to agree about what an error looks like.
     */
    private void addReply(String text) {
        dialogContainer.getChildren().add(amadeus.isError()
                ? DialogBox.getErrorDialog(text, amadeusImage)
                : DialogBox.getAmadeusDialog(text, amadeusImage));
    }

    /**
     * Loads a bundled image, or returns null if it is missing.
     * <p>
     * A missing avatar is not worth stopping the app for: an {@link javafx.scene.image.ImageView}
     * with no image simply draws nothing, so the conversation still works.
     */
    private static Image loadImage(String path) {
        var stream = MainWindow.class.getResourceAsStream(path);
        return stream == null ? null : new Image(stream);
    }
}

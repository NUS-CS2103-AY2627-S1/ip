package crack.gui;

import crack.Crack;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Runs the chat window: takes what the user types, hands it to Crack, and
 * shows both sides of the conversation.
 */
public class MainWindow {
    private static final String SAVE_PATH = "./data/data.txt";
    private static final Duration CLOSE_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final GuiUi ui = new GuiUi();
    private final Crack crack = new Crack(SAVE_PATH, ui);

    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        ui.showGreeting();
        dialogContainer.getChildren().add(createReplyBox(ui.takeReply()));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        userInput.clear();

        boolean isRunning = crack.accept(input);
        dialogContainer.getChildren().addAll(DialogBox.forUser(input), createReplyBox(ui.takeReply()));
        if (!isRunning) {
            closeShortly();
        }
    }

    /**
     * Returns the box a finished reply belongs in, tinted if it reports a problem.
     *
     * @param reply What Crack said, and whether it went wrong.
     * @return The box to add to the chat.
     */
    private static DialogBox createReplyBox(GuiUi.Reply reply) {
        return reply.isError() ? DialogBox.forError(reply.text()) : DialogBox.forCrack(reply.text());
    }

    /**
     * Leaves the goodbye on screen for a moment, then shuts the window.
     */
    private void closeShortly() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition pause = new PauseTransition(CLOSE_DELAY);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}

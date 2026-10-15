package ff15.gui;

import ff15.FF15;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main window. It owns the controls declared in
 * {@code MainWindow.fxml} and turns each line the user sends into a pair of
 * dialog boxes: theirs, then FF15's reply. A command the AI suggests is put in
 * the text field, so the user can check it, then send it or change it.
 *
 * <p>The composer is where the window gets its voice: the hint in the empty
 * text field changes after every message, in the manner of a colleague who
 * cannot quite leave a joke alone.
 */
public class MainWindow extends AnchorPane {
    /**
     * Hints shown in the empty composer, one at a time, in this order. They
     * alternate between something Michael would say and something that actually
     * shows what to type, so the jokes never leave a new user without a clue.
     */
    private static final String[] PROMPTS = {
        "That's what she said. No wait, type a command.",
        "Try: todo read book",
        "I'm not superstitious, but I am a little stitious.",
        "Try: deadline report /by 2026-09-20",
        "Would I rather be feared or loved? Both.",
        "Try: event party /from 2026-09-20 1800 /to 2026-09-20 2200",
        "Ask me anything. Except about the Dundies.",
        "Try: list, mark 1, delete 1",
        "Sometimes I'll start a sentence and I don't even know where it's going.",
        "Try: contact add Pam /phone 91234567",
        "Bears. Beets. Battlestar Galactica. Also, tasks.",
        "Try: find book, or on 2026-09",
        "I am Beyonce, always. Also, I have an AI now.",
        "Try: @ai can I add a phone number to a contact?",
        "I don't need an AI. The AI needs me.",
        "Try: @do remind me to call Pam tomorrow at 3pm",
    };

    /** Shown once the user has said goodbye and the composer has shut. */
    private static final String FAREWELL_PROMPT = "Clocked out. Don't forget your timesheet.";

    /** How long the window lingers after goodbye, so the farewell can be read. */
    private static final Duration FAREWELL_PAUSE = Duration.seconds(1.5);

    /** Shown while a reply is slow in coming, which in practice means the AI is thinking. */
    private static final String THINKING_MESSAGE = "Hold on. I'm consulting my people.";

    /** How long a reply may take before the thinking bubble appears. */
    private static final Duration THINKING_DELAY = Duration.millis(300);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image ff15Image = new Image(
            this.getClass().getResourceAsStream("/images/michael-scott.png"));

    private FF15 ff15;
    private int nextPrompt;

    /**
     * Wires up the behaviour the FXML cannot express: the log follows the newest
     * message, the send button only lights up when there is something to send,
     * Escape clears a half-typed line, and the cursor starts in the composer so
     * the first thing the user does can be to type.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() -> userInput.getText().isBlank(),
                userInput.textProperty()));
        userInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                userInput.clear();
            }
        });
        showNextPrompt();
        Platform.runLater(userInput::requestFocus);
    }

    /** Gives the window the chatbot to send the user's lines to, and shows its greeting. */
    public void setFf15(FF15 chatbot) {
        ff15 = chatbot;
        showFf15Reply(ff15.getStartupMessage());
    }

    /**
     * Shows what the user typed, then works out FF15's reply in the background.
     * A blank line is ignored rather than sent, since Enter still fires here even
     * while the send button is disabled.
     *
     * <p>JavaFX draws the window and runs this method on one thread, the JavaFX
     * application thread. An {@code @ai} reply can take seconds to come back over
     * the network, and working it out here would leave the window frozen until
     * it did. So the reply is worked out on a background thread, in a
     * {@link Task}, and shown once it is ready. The composer is shut until then,
     * so a second line cannot be sent while FF15 is still busy with the first.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        userInput.clear();
        userInput.setDisable(true);

        Task<String> replyTask = new Task<>() {
            @Override
            protected String call() {
                return ff15.getResponse(input); // the only line that runs off the application thread
            }
        };

        // Most replies are instant, so the bubble is held back briefly and only
        // shows when there is a real wait, rather than flickering on every command.
        DialogBox thinkingBox = DialogBox.getThinkingDialog(THINKING_MESSAGE, ff15Image);
        PauseTransition thinkingDelay = new PauseTransition(THINKING_DELAY);
        thinkingDelay.setOnFinished(event -> dialogContainer.getChildren().add(thinkingBox));

        // These two run back on the application thread, where the window may be changed.
        replyTask.setOnSucceeded(event -> {
            thinkingDelay.stop();
            dialogContainer.getChildren().remove(thinkingBox);
            finishReply(replyTask.getValue());
        });
        replyTask.setOnFailed(event -> {
            thinkingDelay.stop();
            dialogContainer.getChildren().remove(thinkingBox);
            showCrash(replyTask.getException());
        });

        Thread worker = new Thread(replyTask, "ff15-reply");
        // A daemon thread never keeps the program running, so closing the window
        // mid-reply still ends it rather than waiting on the network.
        worker.setDaemon(true);
        worker.start();
        thinkingDelay.play();
    }

    /**
     * Shows FF15's reply, then opens the composer again for the next line, with
     * any command the AI suggested already in it. Closes the window instead if
     * the reply ended the session.
     */
    private void finishReply(String reply) {
        showFf15Reply(reply);
        if (ff15.isFinished()) {
            endSession();
            return;
        }
        showNextPrompt();
        userInput.setDisable(false);
        // Focus first: a text field selects all its text when it gains focus, and a
        // selected suggestion would vanish at the first key the user pressed.
        userInput.requestFocus();
        offerSuggestion(ff15.getSuggestedCommand());
    }

    /**
     * Reports a reply that failed with an exception FF15 did not expect, which
     * means a bug rather than a mistyped command, and opens the composer again so
     * the user is not left with a window that cannot be typed into.
     */
    private void showCrash(Throwable problem) {
        dialogContainer.getChildren().add(DialogBox.getErrorDialog(
                "No. GOD. NO. Something broke on my end: " + problem, ff15Image));
        userInput.setDisable(false);
        userInput.requestFocus();
    }

    /**
     * Closes the window once the user has said goodbye. The controls are disabled
     * straight away so nothing more can be typed, but the window lingers for a
     * moment first, otherwise it vanishes before the farewell can be read.
     */
    private void endSession() {
        sendButton.disableProperty().unbind();
        sendButton.setDisable(true);
        userInput.setDisable(true);
        userInput.setPromptText(FAREWELL_PROMPT);

        PauseTransition farewellPause = new PauseTransition(FAREWELL_PAUSE);
        farewellPause.setOnFinished(event -> Platform.exit());
        farewellPause.play();
    }

    /**
     * Puts a command the AI suggested into the composer, with the cursor at the
     * end, so one press of Enter sends it and anything else can edit it first.
     * Does nothing when there is no suggestion.
     */
    private void offerSuggestion(String command) {
        if (command.isEmpty()) {
            return;
        }
        userInput.setText(command);
        userInput.end();
    }

    /** Puts the next hint in the empty composer, starting over once they run out. */
    private void showNextPrompt() {
        userInput.setPromptText(PROMPTS[nextPrompt]);
        nextPrompt = (nextPrompt + 1) % PROMPTS.length;
    }

    /** Adds one of FF15's replies to the conversation, drawn to stand out if it reported an error. */
    private void showFf15Reply(String reply) {
        DialogBox box = ff15.isLastReplyError()
                ? DialogBox.getErrorDialog(reply, ff15Image)
                : DialogBox.getFf15Dialog(reply, ff15Image);
        dialogContainer.getChildren().add(box);
    }
}

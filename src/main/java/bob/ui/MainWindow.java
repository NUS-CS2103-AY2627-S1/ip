package bob.ui;

import bob.Bob;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * Represents the chatbot's window: the conversation so far, and the box the user
 * types into.
 *
 * <p>This class is to the window what {@link bob.Bob Bob}'s command loop is to the console. It
 * does the same three things — take a line from the user, have the chatbot answer
 * it, show the answer — but driven by the user pressing a key or a button rather
 * than by a loop, because that is how a window works: nothing happens until
 * somebody does something, and then a method runs in response.
 *
 * <p>What the window looks like is not here. It is in
 * {@code /view/MainWindow.fxml}, which names this class as its controller and each
 * of the four controls below with the same name the field has. That split is worth
 * having because the two are changed for different reasons and by different means:
 * the arrangement, sizes and colors are adjusted by eye, and can be adjusted in
 * the FXML without recompiling anything, while what the buttons <em>do</em> is
 * decided here.
 *
 * <p>It knows nothing about tasks, commands or save files. It hands the line the
 * user typed to {@link Bob#getResponse} and is given back the text to show, which
 * is why adding a command to the chatbot needs no change here at all.
 */
public class MainWindow extends AnchorPane {

    /**
     * How long the chatbot appears to think before its answer appears.
     *
     * <p>The answer is ready the moment the user presses Enter — nothing here waits
     * on anything — so this pause buys nothing but the impression of one. That is
     * the point: an answer that appears in the same instant as the question reads
     * as a lookup, and a conversation is what this window is meant to look like.
     *
     * <p>Short enough not to be a delay the user has to sit through. Much longer
     * and a chatbot that answers instantly would have been made to feel slow, which
     * is a worse trade than the one being made here.
     */
    private static final Duration TYPING_PAUSE = Duration.seconds(0.4);

    /** How long the farewell stays on screen before the window closes itself. */
    private static final Duration FAREWELL_PAUSE = Duration.seconds(1.5);

    /** The scrolling view of the conversation. */
    @FXML
    private ScrollPane scrollPane;

    /** The conversation itself, one {@link DialogBox} per thing said. */
    @FXML
    private VBox dialogContainer;

    /** Where the user types. */
    @FXML
    private TextField userInput;

    /** The other way to send what has been typed, for users who would rather click. */
    @FXML
    private Button sendButton;

    /** The chatbot this window is talking to, handed over by {@link Main}. */
    private Bob bob;

    /**
     * Prepares the window once JavaFX has built it from the FXML.
     *
     * <p>Named {@code initialize} because {@link javafx.fxml.FXMLLoader FXMLLoader} looks for that
     * name and calls it. It cannot be a constructor: the controls are filled in
     * after the controller is made, so a constructor would run while every field
     * below was still {@code null}.
     *
     * <p>Scrolling to the bottom whenever the conversation gets taller is what keeps
     * the newest bubble in view. The conversation only ever grows downwards, so the
     * user is never left looking at an answer given several turns ago. The price is
     * that a user who has scrolled up to reread something is brought back down when
     * the next answer arrives or the window is resized.
     *
     * <p>The scroll position is set each time rather than bound to the height. A
     * bound property cannot be set by anything else, and the scroll pane sets it
     * itself whenever the mouse wheel turns, so a binding would leave the wheel
     * doing nothing. The scroll is also put off with {@link Platform#runLater} until
     * the conversation has been laid out at its new height, so that it lands at the
     * bottom of the new content rather than of the old.
     */
    @FXML
    public void initialize() {
        // Each control is filled in by FXMLLoader from the fx:id that matches its
        // name, so one left empty means the FXML and this class have drifted apart.
        assert scrollPane != null && dialogContainer != null
                && userInput != null && sendButton != null
                : "MainWindow.fxml is missing an fx:id named here";
        //@@author adwaithanoop-reused
        //Reused from https://github.com/NUS-CS2103-AY2627-S1/forum/issues/160
        // with minor modifications
        // A vvalue of 1.0 is the bottom of the scroll range, however tall the content is.
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> scrollPane.setVvalue(1.0)));
        //@@author
    }

    /**
     * Gives this window the chatbot it is to talk to, and shows its greeting.
     *
     * <p>The chatbot is handed in rather than made here, so that this class is
     * responsible for showing a conversation and not for deciding whose it is or
     * where its tasks are kept. That decision is {@link Main}'s.
     *
     * @param bob the chatbot to talk to, which must be one made by
     *            {@link Bob#forGui}.
     */
    public void setBob(Bob bob) {
        this.bob = bob;
        dialogContainer.getChildren().add(DialogBox.getBobDialog(bob.getGreeting(), Images.BOB));
    }

    /**
     * Answers the line the user has just typed: shows it, shows the chatbot's
     * reply below it, and empties the box ready for the next one.
     *
     * <p>Run when the user presses Enter or clicks Send, both of which the FXML
     * points at this method, so the two are the same action and not two that have
     * to be kept in step.
     *
     * <p>An empty box is ignored. At a console an empty line is worth complaining
     * about, since the user may not have meant to send one; here the box is in
     * front of them and plainly empty, so a complaint would say only what they can
     * already see. The box is emptied before that is decided, so that a box holding
     * nothing but spaces is left properly empty — showing its prompt again — rather
     * than looking blank while quietly refusing to send.
     *
     * <p>The chatbot is asked before either bubble is shown, and its answer is then
     * shown by {@link #showReply}.
     */
    @FXML
    private void handleUserInput() {
        // Main hands the chatbot over before the window is shown, and nothing can be
        // typed into a window that is not on screen yet.
        assert bob != null : "The window was given input before it was given a chatbot";
        String input = userInput.getText().trim();
        userInput.clear();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, Images.USER));
        showReply(bob.getResponse(input));
    }

    /**
     * Keeps the window open when the user closes it with a change still unsaved, and
     * answers as if they had said goodbye instead.
     *
     * <p>Run by {@link Main} when the window's close button is clicked. Closing at once
     * would lose a change an earlier save failed to write, without a word, which is
     * what {@code bye} was changed to prevent. So the close is cancelled and the
     * chatbot is asked for its goodbye: the save is tried once more, the window closes
     * after the farewell if it worked, and stays open with the reason if it did not.
     * Closing a second time then quits without saving, as a second {@code bye} does,
     * so a problem the user cannot fix never traps them in the window.
     *
     * <p>With nothing unsaved, or once the chatbot has already said goodbye, the event
     * is left alone and the window closes at once, as it always has.
     *
     * @param event the request to close the window, consumed to keep it open.
     */
    public void handleCloseRequest(WindowEvent event) {
        assert bob != null : "The window was closed before it was given a chatbot";
        if (bob.isExit() || !bob.hasUnsavedChanges()) {
            return;
        }
        event.consume();
        showReply(bob.getResponseToClosing());
    }

    /**
     * Shows the chatbot's answer to what it was just asked, and closes the window
     * after it if that answer was a goodbye.
     *
     * <p>Shared by typing a line and closing the window, so that an answer looks the
     * same however the chatbot came to give it.
     *
     * <p>The chatbot's bubble appears straight away but empty, and is filled in
     * after {@link #TYPING_PAUSE}, so that the answer looks composed rather than
     * looked up. What the chatbot said is settled before the bubble is shown — the
     * pause is in the showing, not in the answering — which is why everything about
     * the answer is read out of {@link Bob} here rather than inside the lambda: by
     * the time that runs, the chatbot may have been asked something else and be
     * describing that instead.
     *
     * <p>An answer the chatbot could not give is colored differently, so that a
     * mistyped command does not look like a command that worked.
     *
     * <p>Saying goodbye closes the window, but not at once. The farewell is shown
     * first and the window closes a moment later, so the user sees the chatbot
     * answer rather than the window vanishing as they press the key. Typing is
     * disabled from the moment the goodbye is understood, so nothing can be sent to
     * a chatbot that has already said goodbye.
     *
     * @param response the text the chatbot answered with.
     */
    private void showReply(String response) {
        boolean isError = bob.isLastResponseError();
        boolean isExiting = bob.isExit();

        DialogBox reply = DialogBox.getTypingBobDialog(Images.BOB);
        dialogContainer.getChildren().add(reply);

        if (isExiting) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }

        PauseTransition typingPause = new PauseTransition(TYPING_PAUSE);
        typingPause.setOnFinished(event -> {
            reply.showResponse(response, isError);
            if (isExiting) {
                closeAfterFarewell();
            }
        });
        typingPause.play();
    }

    /**
     * Closes the window once the farewell has been on screen long enough to read.
     *
     * <p>Closing the instant the chatbot says goodbye would take the goodbye with
     * it, and the user would be left unsure whether the command had worked or the
     * program had fallen over.
     */
    private void closeAfterFarewell() {
        PauseTransition farewellPause = new PauseTransition(FAREWELL_PAUSE);
        farewellPause.setOnFinished(event -> Platform.exit());
        farewellPause.play();
    }
}

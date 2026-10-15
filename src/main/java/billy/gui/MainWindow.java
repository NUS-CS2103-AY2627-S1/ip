package billy.gui;

import billy.Billy;
import billy.ui.Ui;
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

/**
 * The window's contents, and what happens when the user types in it.
 *
 * <p>This is the counterpart of the console loop in {@link Billy#run()}. There
 * is no loop here: the window sits idle until the user presses Enter or the
 * send button, and each of those calls {@link #handleUserInput()} once.
 */
public class MainWindow extends AnchorPane {

    /** How long Billy's goodbye stays on screen before the window closes. */
    private static final Duration GOODBYE_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    /** The Billy this window is talking to. Set once, before the window is shown. */
    private Billy billy;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image billyImage = new Image(this.getClass().getResourceAsStream("/images/DaBilly.png"));

    /**
     * Prepares the window once JavaFX has built it from the layout file.
     *
     * <p>Watching the height of the conversation is what keeps the newest
     * message in view: every time a dialog box is added the container grows
     * taller, and the scroll pane is sent to the bottom to follow it.
     *
     * <p>A listener rather than a binding, deliberately. Binding the scroll
     * position would keep it at the bottom just as well, but a bound property
     * cannot be set by anything else — including the user, whose mouse wheel and
     * trackpad would then be ignored. Setting it from a listener leaves the
     * scroll position free to be moved by hand afterwards.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Hands this window the Billy it should talk to, and greets the user.
     *
     * <p>The greeting cannot be part of {@link #initialize()}, which runs before
     * there is a Billy to ask about the saved task list.
     *
     * @param billy the chatbot answering this window's commands
     */
    public void setBilly(Billy billy) {
        this.billy = billy;

        showBilly(Ui.getGreeting(), false);
        String startup = billy.getStartupMessage();
        if (startup != null) {
            showBilly(startup, billy.isStartupMessageError());
        }
    }

    /**
     * Answers whatever the user has typed, and clears the box ready for more.
     *
     * <p>Blank input is ignored rather than sent on, since pressing Enter on an
     * empty box is far more likely to be a slip than a question.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        userInput.clear();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        String response = billy.getResponse(input);
        showBilly(response, billy.isLastResponseError());

        if (billy.isExitRequested()) {
            // Typing `bye` closes the window, as it ends the conversation in the
            // console. The pause is so the goodbye can be read before it goes.
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(GOODBYE_PAUSE);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }

    /**
     * Adds one message from Billy to the conversation.
     *
     * <p>A failure is shown in a form of its own, rather than in the same card
     * as every other reply, so that a mistyped command is noticed instead of
     * being scrolled past.
     *
     * @param message what Billy has to say
     * @param isError whether that message reports something going wrong
     */
    private void showBilly(String message, boolean isError) {
        DialogBox box = isError
                ? DialogBox.getErrorDialog(message, billyImage)
                : DialogBox.getBillyDialog(message, billyImage);
        dialogContainer.getChildren().add(box);
    }
}

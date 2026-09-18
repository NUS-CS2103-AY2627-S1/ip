package zhangwei.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import zhangwei.ZhangWei;

/**
 * Runs the chat window: it collects what the user types, asks the chatbot for
 * a reply, and adds both to the conversation.
 */
public class MainWindow {

    /** How long the farewell stays on screen before the window closes. */
    private static final Duration GOODBYE_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private ZhangWei zhangWei;

    private final Image userImage =
            new Image(MainWindow.class.getResourceAsStream("/images/DaUser.png"));

    private final Image zhangWeiImage =
            new Image(MainWindow.class.getResourceAsStream("/images/DaZhangWei.png"));

    /** Keeps the newest message in view as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives this window the chatbot it speaks to, and shows its greeting.
     *
     * @param zhangWei the chatbot that answers the user's commands.
     */
    public void setZhangWei(ZhangWei zhangWei) {
        assert zhangWei != null : "The chat window needs a chatbot to talk to.";

        this.zhangWei = zhangWei;
        dialogContainer.getChildren().add(
                DialogBox.getZhangWeiDialog(zhangWei.getWelcomeMessage(), zhangWeiImage));
    }

    /**
     * Answers whatever is in the input box, then empties it.
     *
     * <p>Called when the user presses Send or Enter. A blank line asks for
     * nothing, so it is cleared without troubling the chatbot.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        userInput.clear();
        if (input.isBlank()) {
            return;
        }

        String response = zhangWei.getResponse(input);
        DialogBox reply = zhangWei.isErrorResponse()
                ? DialogBox.getErrorDialog(response, zhangWeiImage)
                : DialogBox.getZhangWeiDialog(response, zhangWeiImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                reply);

        if (zhangWei.isExitRequested()) {
            closeAfterGoodbye();
        }
    }

    /**
     * Closes the window a moment after the farewell appears, so that the user
     * gets to read it instead of watching the window vanish mid-sentence.
     */
    private void closeAfterGoodbye() {
        userInput.setDisable(true);
        PauseTransition pause = new PauseTransition(GOODBYE_PAUSE);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}

package harvey.ui;

import harvey.Harvey;
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
 * Controller for the main window.
 * <p>
 * It owns no logic of its own: each line the user types is handed to {@link Harvey}, and
 * whatever comes back is put on screen. That is the whole of the graphical interface's
 * job, and it is why adding this window needed no change to the parser, the task list or
 * the save file.
 */
public class MainWindow extends AnchorPane {
    /** How long the goodbye stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.2);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    /** The chatbot answering the user. Supplied by {@link Main} after this is loaded. */
    private Harvey harvey;

    private final Image harveyImage = new Image(this.getClass().getResourceAsStream("/images/Harvey.png"));

    /**
     * Scrolls to the newest message whenever the conversation grows.
     * <p>
     * A listener is used rather than binding the scroll position, because a bound value
     * can no longer be changed by anything else: the user could not scroll back up to
     * read earlier messages.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(scrollPane.getVmax()));
    }

    /**
     * Supplies the chatbot and shows its opening message.
     * <p>
     * The greeting is shown here rather than in {@link #initialize()} because at that
     * point there is no chatbot yet to ask for it.
     *
     * @param harvey the chatbot this window talks to.
     */
    public void setHarvey(Harvey harvey) {
        this.harvey = harvey;
        dialogContainer.getChildren().add(
                DialogBox.getHarveyDialog(harvey.getGreeting(), harveyImage, null));

        // Shown as a second bubble so a complaint about the save file cannot be mistaken
        // for part of the welcome.
        String warning = harvey.getStartupWarning();
        if (warning != null) {
            dialogContainer.getChildren().add(
                    DialogBox.getHarveyDialog(warning, harveyImage, "ErrorCommand"));
        }
    }

    /**
     * Answers whatever the user typed, adding both their message and the reply to the
     * conversation. A {@code bye} closes the window after a short pause, so that the
     * parting message can be read before it disappears.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            // Nothing typed, so there is nothing to answer and no empty bubble to add.
            return;
        }

        String response = harvey.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getHarveyDialog(response, harveyImage, harvey.getCommandType())
        );
        userInput.clear();

        if (harvey.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(EXIT_DELAY);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}

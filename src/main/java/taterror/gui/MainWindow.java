package taterror.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import taterror.TaTerror;

/**
 * FXML controller for the main chat window ({@code view/MainWindow.fxml}): the
 * scrollable dialog history, the text input field, and the Send button. Wires
 * user input to a {@link TaTerror} instance and renders each exchange as a pair
 * of {@link DialogBox} bubbles.
 */
public class MainWindow {
    private static final double EXIT_DELAY_SECONDS = 1.5;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private TaTerror taTerror;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image taTerrorImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    /**
     * Binds the scroll position to the dialog container's height (an FXML
     * lifecycle callback), so the view auto-scrolls to the newest message as
     * the conversation grows.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot instance this window talks to, and shows its intro
     * greeting as the first dialog bubble. Must be called once, right after this
     * controller is loaded from FXML, before the user can send anything.
     *
     * @param t the chatbot instance to route all future user input through
     */
    public void setTaTerror(TaTerror t) {
        taTerror = t;
        dialogContainer.getChildren().add(
                DialogBox.getTaTerrorDialog("Oh. It's you. I'm TA Terror. Try not to waste my time.", taTerrorImage)
        );
    }

    /**
     * Reads the current input, gets the chatbot's reply, and appends both as a
     * new pair of dialog bubbles - unless the input is blank, in which case
     * nothing happens. Fired when the user presses Enter in the input field or
     * clicks Send. After a {@code bye}, the window closes itself shortly after
     * showing the farewell.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }
        String response = taTerror.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getTaTerrorDialog(response, taTerrorImage)
        );
        userInput.clear();
        if (taTerror.isExitCommand(input)) {
            closeAfterFarewell();
        }
    }

    /**
     * Ends the session after {@code bye}: disables further input so nothing can
     * be typed during the pause, then closes the app after a short delay so the
     * farewell bubble is actually readable before the window disappears.
     */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition delay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }
}

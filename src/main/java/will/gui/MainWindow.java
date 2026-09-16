package will.gui;

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
import will.Will;

/**
 * The main window's controller: forwards each line the user types to
 * {@link Will#getResponse(String)} and shows the exchange as a pair of
 * dialog boxes. Loaded from {@code MainWindow.fxml} by {@link MainApp}.
 */
public class MainWindow extends AnchorPane {
    private static final Image USER_IMAGE = new Image(MainWindow.class.getResourceAsStream("/images/DaUser.png"));
    private static final Image WILL_IMAGE = new Image(MainWindow.class.getResourceAsStream("/images/DaWill.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Will will;

    /**
     * Scrolls to the newest message whenever one is added, without
     * locking the scroll bar in place. A permanent
     * {@code vvalueProperty().bind(...)} would force the view to the
     * bottom on every layout pass, silently blocking the user from
     * scrolling up to read earlier messages; a listener instead nudges
     * the view down only when the dialog list actually grows, leaving
     * manual scrolling free the rest of the time.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) ->
                scrollPane.setVvalue((Double) newValue));
    }

    /**
     * Gives this window the {@link Will} instance to send input to, and
     * shows its startup banner as the first message in the chat.
     *
     * @param will The chatbot backing this window.
     */
    public void setWill(Will will) {
        this.will = will;
        dialogContainer.getChildren().add(DialogBox.getWillDialog(will.getGreeting(), WILL_IMAGE));
    }

    /**
     * Sends the text in the input field to {@link Will}, shows the
     * exchange as a pair of dialog boxes, then clears the input field.
     * If that input was "bye", closes the window shortly after so the
     * user sees the goodbye message before it disappears.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = will.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, USER_IMAGE),
                DialogBox.getWillDialog(response, WILL_IMAGE)
        );
        userInput.clear();

        if (will.isLastResponseExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

package duncan.gui;

import java.util.List;

import duncan.Duncan;
import duncan.Ui;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI: turns what the user types into a call on
 * {@link Duncan} and shows both sides of the conversation as dialog boxes.
 */
public class MainWindow {
    /** How long the farewell stays on screen before the window closes. */
    private static final double EXIT_DELAY_IN_SECONDS = 1.5;

    private static final String STYLE_LOGO = "logo";

    /** The scroll position that shows the bottom of the conversation. */
    private static final double SCROLLED_TO_BOTTOM = 1.0;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Duncan duncan;

    /** Keeps the newest dialog box in view as the conversation grows. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(SCROLLED_TO_BOTTOM));
    }

    /**
     * Injects the Duncan instance this window talks to, and starts the
     * conversation with the logo followed by Duncan's greeting.
     */
    public void setDuncan(Duncan d) {
        duncan = d;
        dialogContainer.getChildren().addAll(
                createLogo(duncan.getBanner()),
                DialogBox.getDuncanDialog(duncan.getWelcomeLines())
        );
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Creates two dialog boxes, one echoing the user's input and the other
     * holding Duncan's reply, and appends them to the dialog container.
     * Clears the input field afterwards, ready for the next command.
     */
    @FXML
    private void handleUserInput() {
        assert duncan != null;
        String input = userInput.getText();
        List<Ui.MessageLine> reply = duncan.getResponseLines(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getDuncanDialog(reply)
        );
        userInput.clear();

        if (duncan.isExit()) {
            closeAfterShowingGoodbye();
        }
    }

    /**
     * Creates the ASCII-art logo. It is never wrapped or truncated, because
     * either would scramble the picture; the window's minimum width is chosen
     * so that it always fits.
     */
    private Label createLogo(String banner) {
        Label logo = new Label(banner);
        logo.getStyleClass().add(STYLE_LOGO);
        logo.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return logo;
    }

    /**
     * Stops the user typing anything more and closes the window a moment
     * later, so that the farewell is on screen long enough to be read.
     */
    private void closeAfterShowingGoodbye() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(EXIT_DELAY_IN_SECONDS));
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}

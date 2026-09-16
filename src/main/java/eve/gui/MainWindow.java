package eve.gui;

import java.util.Random;

import eve.Eve;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/** Controller for the main GUI window: shows the conversation and forwards user input to {@link Eve}. */
public class MainWindow {
    private static final Color[] AVATAR_COLORS = {
        Color.web("#ff5c8a"),
        Color.web("#ff9f1c"),
        Color.web("#ffca3a"),
        Color.web("#8ac926"),
        Color.web("#1982c4"),
        Color.web("#6a4c93"),
        Color.web("#ff6f59"),
        Color.web("#2ec4b6"),
    };

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Eve eve;
    private Color userAvatarColor;
    private Color eveAvatarColor;

    /** Picks a random, distinct avatar color for the user and for Eve, kept for the rest of this session. */
    public void initialize() {
        // As in DialogBox: a typo'd or removed fx:id in MainWindow.fxml would leave
        // the corresponding field null instead of failing the FXML load.
        assert scrollPane != null && dialogContainer != null && userInput != null && sendButton != null
                : "MainWindow.fxml did not inject one of the @FXML fields";

        Random random = new Random();
        userAvatarColor = AVATAR_COLORS[random.nextInt(AVATAR_COLORS.length)];
        do {
            eveAvatarColor = AVATAR_COLORS[random.nextInt(AVATAR_COLORS.length)];
        } while (eveAvatarColor.equals(userAvatarColor));
        // Postcondition of the loop above: it should be structurally impossible to
        // exit it with equal colors, but assert it explicitly so a future edit to
        // the loop condition that breaks this fails fast in testing.
        assert !eveAvatarColor.equals(userAvatarColor) : "User and Eve ended up with the same avatar color";
    }

    /**
     * Injects the Eve instance this window sends user input to, and shows
     * its greeting as the first chat message so the window doesn't open on
     * a blank conversation.
     *
     * @param eve the chatbot instance to use.
     */
    public void setEve(Eve eve) {
        this.eve = eve;
        dialogContainer.getChildren().add(DialogBox.getEveDialog(eve.getWelcomeMessage(), eveAvatarColor));
        // Deliberately don't scroll here: the view already starts at the top, which is
        // exactly where a reader wants to start on a greeting long enough to need scrolling.
    }

    /**
     * Sends the text field's content to Eve, shows both it and Eve's
     * response as new dialog boxes, scrolls to show them, and closes the
     * window shortly after a "bye" response. Called when the user presses
     * Enter in the text field or clicks "Send".
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = eve.getResponse(input);
        DialogBox eveDialog = eve.isLastResponseError()
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getEveDialog(response, eveAvatarColor);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userAvatarColor), eveDialog);
        userInput.clear();
        // Deferred one pulse so the newly added dialog boxes are already sized/wrapped --
        // scrolling immediately would use their pre-layout height and land short of the bottom.
        Platform.runLater(() -> scrollPane.setVvalue(1.0));

        if (eve.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

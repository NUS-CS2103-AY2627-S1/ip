package milo.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import milo.Milo;

/**
 * Controller for the main GUI window: a scrolling history of dialog boxes,
 * a text field for typing commands, and a button that does the same thing
 * as pressing Enter in that field.
 */
public class MainWindow extends VBox {
    /** How long the goodbye message stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    /** The chatbot this window talks to, injected once the FXML is loaded. */
    private Milo milo;

    /** Milo's avatar. The user has none: see {@link DialogBox}. */
    private final Image miloImage = new Image(getClass().getResourceAsStream("/images/Milo.png"));

    /** Keeps the dialog history scrolled to the newest message. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot this window talks to, and shows its opening
     * greeting as the first message in the dialog history.
     *
     * @param milo the chatbot backing this window
     */
    public void setMilo(Milo milo) {
        this.milo = milo;
        addDialog(DialogBox.getMiloDialog(milo.getWelcomeMessage(), miloImage));
    }

    /**
     * Sends whatever the user typed to Milo, shows both the user's message
     * and Milo's reply, and closes the window shortly after a "bye".
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        // isBlank() rather than isEmpty(): a field holding only spaces is
        // just as empty from the user's point of view, and letting it
        // through reaches the parser as an unknown command word.
        if (input.isBlank()) {
            return;
        }

        String response = milo.getResponse(input);
        addDialog(DialogBox.getUserDialog(input));
        // A command that could not be carried out is shown in its own style,
        // so a mistyped command is not mistaken for a normal reply.
        addDialog(milo.isError()
                ? DialogBox.getErrorDialog(response, miloImage)
                : DialogBox.getMiloDialog(response, miloImage));
        userInput.clear();

        if (milo.isExit()) {
            PauseTransition delay = new PauseTransition(EXIT_DELAY);
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }

    /**
     * Adds a dialog box to the history, tying its wrapping width to the
     * history's current width so it keeps wrapping correctly if the window
     * is resized later.
     *
     * @param dialogBox the dialog box to add
     */
    private void addDialog(DialogBox dialogBox) {
        dialogBox.bindMaxWidthTo(scrollPane.widthProperty());
        dialogContainer.getChildren().add(dialogBox);
    }
}

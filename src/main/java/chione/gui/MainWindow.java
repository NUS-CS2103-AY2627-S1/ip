package chione.gui;

import chione.Chione;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * The behavior of Chione's main window.
 *
 * <p>The layout itself lives in {@code MainWindow.fxml}; this class is its
 * <em>controller</em>, holding the handful of controls the program needs to reach
 * and the one thing that happens in the window — the user sends a line, and two
 * dialog boxes appear.
 *
 * <p>Each field marked {@code @FXML} is filled in by the FXML loader with the
 * control of the same {@code fx:id}. The annotation is what allows them to stay
 * private: without it they would have to be public, and the whole window would be
 * open to changes from anywhere.
 */
public class MainWindow extends VBox {
    /** How long the goodbye stays on screen before the window closes. */
    private static final Duration GOODBYE_PAUSE = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    /** The chatbot this window is talking to; supplied by {@link Main}. */
    private Chione chione;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image chioneImage = new Image(this.getClass().getResourceAsStream("/images/DaChione.png"));

    /**
     * Creates the controller.
     *
     * <p>The FXML loader constructs this class and then fills in the fields
     * marked {@code @FXML}, so nothing can be set up here yet; that waits for
     * {@link #initialize()}.
     */
    public MainWindow() {
    }

    /**
     * Finishes setting up the window once the FXML has been read.
     *
     * <p>Called by the FXML loader, after the controls exist but before the window
     * is shown — which is the earliest point at which {@code dialogContainer} can
     * be referred to at all.
     */
    @FXML
    public void initialize() {
        // Tying the scroll position to the height of the conversation keeps the
        // newest message in view as the conversation grows.
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Hands this window the chatbot it should talk to, and shows its greeting.
     *
     * @param chione the chatbot to send the user's lines to
     */
    public void setChione(Chione chione) {
        this.chione = chione;
        dialogContainer.getChildren().add(
                DialogBox.getChioneDialog(chione.getWelcomeMessage(), chioneImage));
    }

    /**
     * Sends what the user typed to Chione, and shows both sides of the exchange.
     *
     * <p>Adds a dialog box for the user's line and another for the reply, which
     * is marked as an error if the line could not be carried out, then empties
     * the text field ready for the next one. A {@code bye} closes the window,
     * but only after a pause, so that the goodbye can be read.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            // Nothing was typed, so there is nothing to answer. Showing an empty
            // dialog box here would look like a fault in the program.
            userInput.clear();
            return;
        }

        String response = chione.getResponse(input);
        // A refusal is shown in a different dress from a success, so the user
        // sees at a glance that the command did not go through.
        DialogBox reply = chione.isLastResponseError()
                ? DialogBox.getErrorDialog(response, chioneImage)
                : DialogBox.getChioneDialog(response, chioneImage);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage), reply);
        userInput.clear();

        if (chione.isExit()) {
            PauseTransition pause = new PauseTransition(GOODBYE_PAUSE);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}

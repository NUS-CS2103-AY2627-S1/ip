package elsa.gui;

import elsa.Elsa;
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
 * What the window does, as opposed to what it looks like.
 *
 * <p>The look is in {@code /view/MainWindow.fxml}, which names this class as its
 * controller. Loading that file builds the controls and then fills in the fields
 * below, pairing each one with the control whose {@code fx:id} matches its name.
 * Nothing here creates a control or places one; this class only says what should
 * happen when the user acts, which is the point of keeping the two apart.
 *
 * <p>The fields are private and marked {@code @FXML}, which is what allows the
 * loader to reach them. Dropping the annotation and making them public would
 * work too, and would let anything else reach them as well.
 */
public class MainWindow {
    /**
     * How long the farewell stays on screen before the window closes.
     * Closing the moment the reply is added would take the farewell away before
     * it could be read.
     */
    private static final Duration FAREWELL_PAUSE = Duration.seconds(1.5);

    /** Scrolls the conversation once it is taller than the window. */
    @FXML
    private ScrollPane scrollPane;

    /** The conversation itself, one dialog box per line spoken. */
    @FXML
    private VBox dialogContainer;

    /** Where the user types. */
    @FXML
    private TextField userInput;

    /** What the user presses to send what they typed. */
    @FXML
    private Button sendButton;

    /** The picture shown beside what the user says. */
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/OlafPortrait.png"));

    /** The picture shown beside what Elsa says. */
    private final Image elsaImage = new Image(this.getClass().getResourceAsStream("/images/ElsaPortrait.png"));

    /**
     * The chatbot the window is a face for.
     * Not final, and not built here, because the loader makes this object itself
     * and can only call a constructor that takes nothing. It is handed in
     * afterwards, through {@link #startWith(Elsa)}.
     */
    private Elsa elsa;

    /**
     * Creates the controller.
     * The loader makes this object by reflection, so it needs a constructor that
     * takes no arguments. Java supplies one to any class that declares none, but
     * declaring it says out loud that the loader depends on it.
     */
    public MainWindow() {
    }

    /**
     * Finishes setting the window up, once the loader has filled in the controls.
     * This is called by the loader, by name, after every field above has been
     * assigned; anything here that ran in the constructor instead would find
     * them all still null.
     */
    @FXML
    public void initialize() {
        // Keeps the newest line in view. Bound to the column's height, so every
        // line added scrolls to the bottom without anything having to notice
        // that one was.
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Gives the window the chatbot it is a face for, and shows what that chatbot
     * says before anything is typed.
     * Starting the session is what reads the tasks saved by an earlier run, so it
     * has to happen before any command does.
     *
     * @param elsa the chatbot to answer through.
     */
    public void startWith(Elsa elsa) {
        this.elsa = elsa;
        dialogContainer.getChildren().add(DialogBox.getElsaDialog(elsa.startSession(), elsaImage));

        // Deferred, because a control cannot take the focus until it is on
        // screen, and the window is not shown until this method has returned.
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Shows what the user typed, and what Elsa says back, then empties the text
     * field ready for the next line.
     * A line with nothing in it is ignored, so that pressing Enter on an empty
     * field does not put a blank entry in the conversation.
     *
     * <p>Called by the loader, by name: both the text field and the button name
     * this method in their {@code onAction}, so either way of sending a line
     * does the same thing.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        if (userText.isBlank()) {
            return;
        }

        // Asked after the reply is in hand, the way isExiting is below: the
        // reply is text, and text alone does not say whether the command worked.
        String reply = elsa.getResponse(userText);
        DialogBox answer = elsa.wasLastReplyAnError()
                ? DialogBox.getElsaErrorDialog(reply, elsaImage)
                : DialogBox.getElsaDialog(reply, elsaImage);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage), answer);
        userInput.clear();

        if (elsa.isExiting()) {
            closeAfterFarewell();
        }
    }

    /**
     * Closes the window once the farewell has been on screen long enough to read.
     * The wait cannot be a sleep: that would stop the thread JavaFX draws on, so
     * the farewell would never appear and the window would freeze instead. A
     * PauseTransition instead asks JavaFX to call back later, leaving it free to
     * draw in the meantime.
     */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition pause = new PauseTransition(FAREWELL_PAUSE);
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}

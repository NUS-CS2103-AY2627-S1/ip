package davidgoggins.gui;

import davidgoggins.DavidGoggins;
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
 * Controller for the main window: the scrolling conversation and the input box.
 *
 * <p>Holds no chatbot logic of its own. It turns a typed line into a call on
 * {@link DavidGoggins#getResponse(String)} and puts the two sides of the exchange on
 * screen as dialog boxes.
 */
public class MainWindow extends AnchorPane {

    /** How long the farewell stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.5);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    /** The chatbot that answers each command; set by the class that builds the window. */
    private DavidGoggins davidGoggins;

    /** The help window, or null until the user first types {@code help}. */
    private HelpWindow helpWindow;

    /** The chatbot's picture, shown beside each of its replies; the user has none. */
    private final Image botImage = new Image(this.getClass().getResourceAsStream("/images/DaDavidGoggins.png"));

    /**
     * Keeps the newest message in view as the conversation grows.
     *
     * <p>A listener sets the scroll position rather than a binding fixing it, because a
     * bound position cannot be changed by the user, which froze scrolling after a resize.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(scrollPane.getVmax()));
    }

    /**
     * Gives the window the chatbot to talk to, and greets the user.
     *
     * <p>The greeting is shown here rather than in {@link #initialize()} because it
     * needs the chatbot, which does not exist yet when the FXML is loaded.
     *
     * @param davidGoggins the chatbot that answers the user's commands
     */
    public void setDavidGoggins(DavidGoggins davidGoggins) {
        this.davidGoggins = davidGoggins;
        addBotDialog(davidGoggins.getGreeting());

        // Trouble with the save file is otherwise only printed to a console the GUI user
        // never sees, which would leave them wondering where their tasks went.
        String loadWarnings = davidGoggins.getLoadWarnings();
        if (!loadWarnings.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getWarningDialog(loadWarnings, botImage));
        }

        // A returning user is shown the list restored from the save file, matching
        // what the text UI does at start-up.
        String restored = davidGoggins.getRestoredTasks();
        if (!restored.isEmpty()) {
            addBotDialog(restored);
        }
    }

    /**
     * Answers whatever the user typed and shows both sides of the exchange.
     *
     * <p>On {@code bye} the reply is shown first and the window closes a moment later,
     * so the farewell is readable instead of vanishing with the window.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        assert davidGoggins != null : "setDavidGoggins() must be called before the user can type";
        String response = davidGoggins.getResponse(input);
        DialogBox reply;
        if (davidGoggins.isLastResponseError()) {
            reply = DialogBox.getErrorDialog(response, botImage);
        } else if (davidGoggins.isLastResponseWarning()) {
            reply = DialogBox.getWarningDialog(response, botImage);
        } else {
            reply = DialogBox.getBotDialog(response, botImage);
        }
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input), reply);
        userInput.clear();

        if (davidGoggins.isHelpCommand(input)) {
            showHelpWindow();
        }

        if (davidGoggins.isExitCommand(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition pause = new PauseTransition(EXIT_DELAY);
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }

    /**
     * Shows the help window, creating it the first time help is asked for.
     *
     * <p>Created lazily because its owner, the main window's stage, does not exist yet
     * when the FXML is loaded.
     */
    private void showHelpWindow() {
        if (helpWindow == null) {
            // This controller is not itself on screen (the FXML names it as fx:controller),
            // so the owning window is found through a control that is.
            helpWindow = new HelpWindow(userInput.getScene().getWindow(), davidGoggins.getHelp());
        }
        helpWindow.showOrFocus();
    }

    /** Adds one message from the chatbot, used for messages the user did not prompt. */
    private void addBotDialog(String message) {
        dialogContainer.getChildren().add(DialogBox.getBotDialog(message, botImage));
    }
}

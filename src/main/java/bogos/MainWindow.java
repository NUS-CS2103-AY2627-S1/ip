package bogos;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main Bogos chat window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration GOODBYE_DISPLAY_DURATION = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    private Bogos bogos;
    private boolean isScrollToBottomRequested;
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/photosprinted.png"));
    private final Image bogosImage = new Image(getClass().getResourceAsStream("/images/bogosbinted.png"));

    /**
     * Configures scrolling after FXML fields have been injected.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) -> {
            scrollToBottomIfRequested();
        });
    }

    /**
     * Supplies the command-processing application used by this window.
     *
     * @param bogos Bogos application instance.
     */
    public void setBogos(Bogos bogos) {
        this.bogos = bogos;
        addDialogs(DialogBox.getBogosDialog(bogos.getWelcomeMessage(), bogosImage));
    }

    /**
     * Adds the user's command and Bogos's reply to the dialog history.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = bogos.getResponse(input);
        addDialogs(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBogosDialog(response, bogosImage));
        userInput.clear();

        if (bogos.isExitCommand(input)) {
            userInput.setDisable(true);
            PauseTransition goodbyePause = new PauseTransition(GOODBYE_DISPLAY_DURATION);
            goodbyePause.setOnFinished(event -> Platform.exit());
            goodbyePause.play();
        }
    }

    /**
     * Adds dialogs and requests that their completed layout be shown.
     *
     * @param dialogs Dialogs to add to the conversation history.
     */
    private void addDialogs(DialogBox... dialogs) {
        isScrollToBottomRequested = true;
        dialogContainer.getChildren().addAll(dialogs);
    }

    /**
     * Scrolls to the latest dialog only after an added dialog changes the content height.
     */
    private void scrollToBottomIfRequested() {
        if (isScrollToBottomRequested) {
            scrollPane.setVvalue(scrollPane.getVmax());
            isScrollToBottomRequested = false;
        }
    }
}

package lion;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {

    /** Greeting shown as Lion's first message when the window opens. */
    private static final String GREETING = "Roar! I'm Lion. Tell me what's on your to-do list today.";

    private static final double BACKGROUND_DESIGN_WIDTH = 400.0;

    private static final double BACKGROUND_DESIGN_HEIGHT = 600.0;

    private static final double EXIT_DELAY_SECONDS = 0.8;

    @FXML
    private Pane chatBackground;

    @FXML
    private Group backgroundArt;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Lion lion;

    private Image lionImage =
            new Image(this.getClass().getResourceAsStream("/images/DaLion.png"));

    /**
     * Keeps the scroll pane pinned to the latest dialog as new ones are added,
     * and shows Lion's greeting as the first message in the conversation.
     */
    @FXML
    public void initialize() {
        backgroundArt.scaleXProperty().bind(chatBackground.widthProperty().divide(BACKGROUND_DESIGN_WIDTH));
        backgroundArt.scaleYProperty().bind(chatBackground.heightProperty().divide(BACKGROUND_DESIGN_HEIGHT));
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(DialogBox.getLionDialog(GREETING, lionImage, false, false));
    }

    /**
     * Injects the Lion instance this window sends commands to.
     *
     * @param lion Lion instance backing this window.
     */
    public void setLion(Lion lion) {
        this.lion = lion;
    }

    /**
     * Sends the current text-field contents to Lion and renders both the
     * user's message and Lion's reply as chat bubbles. Blank input (e.g. the
     * user pressing enter on an empty field) is ignored rather than shown as
     * an empty bubble.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        if (input.isBlank()) {
            return;
        }

        String response = lion.getResponse(input);
        boolean isError = Lion.isErrorResponse(response);
        boolean isTaskList = Lion.isTaskListResponse(response);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getLionDialog(response, lionImage, isError, isTaskList)
        );

        userInput.clear();

        if (Lion.isByeResponse(response)) {
            sendLionOffToRest();
        }
    }

    /**
     * Disables further input and closes the app after Lion's farewell bubble is visible.
     */
    private void sendLionOffToRest() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition exitDelay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }
}

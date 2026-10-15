package summer.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import summer.Summer;

/**
 * Controller for the main window: a scrolling list of dialog boxes above a text
 * field and a send button.
 */
public class MainWindow {
    private static final String GREETING = "Hey hey, Summer here.\nWhat's the move today?";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Summer summer;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/cartman.png"));
    private final Image summerImage = new Image(getClass().getResourceAsStream("/images/summer.png"));

    /** Keeps the scroll pane pinned to the newest message. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Summer} instance and shows its greeting.
     *
     * @param summer the chatbot backing this window
     */
    public void setSummer(Summer summer) {
        this.summer = summer;
        dialogContainer.getChildren().add(DialogBox.getSummerDialog(GREETING, summerImage));
    }

    /**
     * Shows the user's input and Summer's reply as a pair of dialog boxes, then
     * clears the text field. Closes the window shortly after a {@code bye}.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = summer.getResponse(input);
        DialogBox summerDialog = DialogBox.getSummerDialog(response, summerImage);
        if (summer.isError()) {
            summerDialog.markAsError();
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                summerDialog);
        userInput.clear();

        if (summer.isExit()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}

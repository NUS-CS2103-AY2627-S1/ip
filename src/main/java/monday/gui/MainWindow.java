package monday.gui;

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
import monday.Monday;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Monday monday;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image mondayImage = new Image(this.getClass().getResourceAsStream("/images/DaMonday.png"));

    /**
     * Initializes the GUI after FXML fields have been injected.
     * Binds the scroll pane to the dialog container so that new messages remain visible.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Monday instance.
     */
    public void setMonday(Monday monday) {
        this.monday = monday;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Monday's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = monday.getResponse(input);

        if (!monday.isRunning()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMondayDialog(response, mondayImage));
        userInput.clear();
    }

    /**
     * Display's Monday's welcome message in the dialog area.
     */
    public void showWelcomeMessage() {
        String welcomeMessage = monday.getWelcome();
        dialogContainer.getChildren().addAll(DialogBox.getMondayDialog(welcomeMessage, mondayImage)
        );
    }
}

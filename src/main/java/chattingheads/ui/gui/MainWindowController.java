package chattingheads.ui.gui;

import chattingheads.ChattingHeads;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
 */
public class MainWindowController extends AnchorPane {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button enterButton;

    private ChattingHeads chattingHeads;

    /**
     * Initialises the main window after its FXML components have been loaded.
     * Keeps the dialog container at least as tall as the display area.
     */
    @FXML
    public void initialize() {
        dialogContainer.minHeightProperty().bind(
                scrollPane.heightProperty()
        );
    }

    /**
     * Injects the chatbot instance and displays the startup content.
     *
     * @param chattingHeads Instance of the chatbot to be injected.
     */
    public void setChattingHeads(ChattingHeads chattingHeads) {
        this.chattingHeads = chattingHeads;

        ImageView startupImage = new ImageView(
                new Image(getClass().getResourceAsStream("/images/TalkingHeadsRemaininLight.png"))
        );
        startupImage.setFitWidth(100);
        startupImage.setFitHeight(100);
        startupImage.setPreserveRatio(true);

        dialogContainer.getChildren().add(startupImage);
        dialogContainer.getChildren().add(
                DialogBox.getDialog(chattingHeads.getStartupMessage())
        );
    }

    /**
     * Processes the current user input and displays the resulting dialogs.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        CommandResult result = chattingHeads.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog("> " + input),
                DialogBox.getDialog(result.response())
        );
        userInput.clear();
        Platform.runLater(() -> scrollPane.setVvalue(1.0));

        if (result.shouldExit()) {
            userInput.setDisable(true);
            enterButton.setDisable(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}

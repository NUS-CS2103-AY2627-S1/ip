package tblade.gui;

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
import tblade.TBlade;

/**
 * Controller for the main GUI window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private TBlade tblade;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image tbladeImage = new Image(this.getClass().getResourceAsStream("/images/DaTBlade.png"));

    /**
     * Binds the scroll pane so it always stays scrolled to the latest message, and keeps the
     * dialog container's width tracking the scroll pane's viewport width so dialog boxes reflow
     * (rewrap their text) as the window is resized.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.prefWidthProperty().bind(scrollPane.widthProperty());
    }

    /**
     * Injects the TBlade instance this window talks to, and shows its greeting.
     *
     * @param tblade the TBlade instance backing this GUI
     */
    public void setTblade(TBlade tblade) {
        this.tblade = tblade;
        dialogContainer.getChildren().add(
                DialogBox.getTBladeDialog("Yo, chat! I'm TBlade — Blood God of your task list. "
                        + "What's the move?", tbladeImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing TBlade's reply,
     * then appends them to the dialog container. Clears the user input after processing, and
     * closes the window shortly after TBlade processes a {@code bye}.
     */
    @FXML
    private void handleUserInput() {
        assert tblade != null
                : "tblade must already be set: Main.start() calls setTblade() right after loading the FXML "
                + "and before the stage (and its input controls) is shown";
        String input = userInput.getText();
        String response = tblade.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getTBladeDialog(response, tbladeImage)
        );
        userInput.clear();

        if (!tblade.isRunning()) {
            PauseTransition delay = new PauseTransition(EXIT_DELAY);
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}

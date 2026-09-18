package bot.gui;

import bot.Bot;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main GUI window.
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
    @FXML
    private ImageView headerAvatar;

    private Bot bot;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image botImage = new Image(this.getClass().getResourceAsStream("/images/DaBot.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Bot instance and shows its startup greeting. */
    public void setBot(Bot b) {
        bot = b;
        headerAvatar.setImage(botImage);
        dialogContainer.getChildren().add(DialogBox.getBotDialog(bot.getGreetingMessage(), botImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other
     * containing Bot's reply, and appends them to the dialog container.
     * Typing "bye" shows the farewell message and then closes the window
     * shortly after, matching the console UI's exit behavior.
     */
    @FXML
    private void handleUserInput() {
        // Main.start() always calls setBot() right after loading this
        // controller from FXML and before showing the stage, so the user
        // can't type into userInput (and trigger this handler) before bot
        // is set - a null bot here would mean that startup order broke.
        assert bot != null : "setBot() must be called before the user can interact with the window";
        String input = userInput.getText();
        if (input.isEmpty()) {
            return;
        }
        userInput.clear();

        if (bot.isExitCommand(input)) {
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getBotDialog(bot.getFarewellMessage(), botImage));
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
            return;
        }

        String response = bot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBotDialog(response, botImage));
    }
}

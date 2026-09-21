import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controller for the main chat window, wired up from {@code view/MainWindow.fxml}. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private MaggiGorengAyamBot bot;

    private Image maggigorengayamImage = new Image(this.getClass()
            .getResourceAsStream("/images/maggigorengayam.png"));

    /** Called by the FXML loader once the view's fields are injected; keeps the view scrolled to the bottom. */
    @FXML
    public void initialize() {
        // Each @FXML field is injected by FXMLLoader matching fx:id in MainWindow.fxml;
        // a null here means the FXML and this controller have drifted out of sync (e.g.
        // a renamed fx:id), which would otherwise only surface later as a confusing NPE.
        assert scrollPane != null && dialogContainer != null && userInput != null && sendButton != null
                : "FXML injection failed for one or more @FXML fields";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the bot instance and shows its startup message as the first chat bubble. */
    public void setBot(MaggiGorengAyamBot bot) {
        this.bot = bot;
        addDialog(DialogBox.getMaggiGorengAyamDialog(bot.getStartupMessage(), maggigorengayamImage, false));
    }

    /**
     * Creates dialog boxes echoing the user's input and the bot's reply, appends them to the
     * dialog container, and clears the input field. Exits the application if the bot signals
     * that the last command (e.g. {@code bye}) should end the session.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = bot.getResponse(input);
        addDialog(DialogBox.getUserDialog(input));
        addDialog(DialogBox.getMaggiGorengAyamDialog(response, maggigorengayamImage, bot.isLastResponseError()));
        userInput.clear();
        if (bot.isExit()) {
            Platform.exit();
        }
    }

    /** Appends {@code dialogBox} to the conversation, letting its bubble width track the window's. */
    private void addDialog(DialogBox dialogBox) {
        dialogBox.bindMaxWidth(dialogContainer.widthProperty());
        dialogContainer.getChildren().add(dialogBox);
    }
}

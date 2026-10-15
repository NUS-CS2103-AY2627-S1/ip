package bot67.gui;

import java.util.Objects;

import bot67.Bot67;
import bot67.ui.Ui;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/** Controls Bot67's main chat window. */
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
    private ImageView headerPicture;

    private final Image userImage = loadImage("/images/User.png");
    private final Image botImage = loadImage("/images/Bot67.png");
    private Bot67 bot;

    /** Keeps the newest messages visible as the conversation grows. */
    @FXML
    public void initialize() {
        headerPicture.setImage(botImage);
        double side = Math.min(botImage.getWidth(), botImage.getHeight());
        double x = (botImage.getWidth() - side) / 2;
        double y = (botImage.getHeight() - side) / 2;
        headerPicture.setViewport(new Rectangle2D(x, y, side, side));
        Rectangle clip = new Rectangle(48, 48);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        headerPicture.setClip(clip);
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(() -> userInput.getText().isBlank(),
                userInput.textProperty()));
        Platform.runLater(userInput::requestFocus);
    }

    /** Injects the application logic after FXML creates this controller. */
    public void setBot(Bot67 bot) {
        this.bot = bot;
        DialogBox welcome = DialogBox.getBotDialog(Ui.WELCOME + "\n\n"
                + "Try: todo read a book\nOpen the command guide below for more ideas.", botImage);
        welcome.showWelcomeArt(Bot67.getBanner(), Bot67.getPersonalityArt());
        dialogContainer.getChildren().add(welcome);
        if (!bot.getStartupWarning().isEmpty()) {
            DialogBox warning = DialogBox.getBotDialog(bot.getStartupWarning(), botImage);
            warning.highlightError();
            dialogContainer.getChildren().add(warning);
        }
    }

    /** Sends one command and displays the user and Bot67 messages. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        String response = bot.getResponse(input);
        if (response.isBlank()) {
            response = "Your task list is empty. Try: todo read a book";
        }
        DialogBox reply = DialogBox.getBotDialog(response, botImage);
        if (bot.isLastResponseError()) {
            reply.highlightError();
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                reply);
        userInput.clear();
        userInput.requestFocus();
        Platform.runLater(() -> {
            scrollPane.applyCss();
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
        if (bot.isExitRequested()) {
            Platform.runLater(Platform::exit);
        }
    }

    /** Loads a required image resource and reports a clear error if it is missing. */
    private static Image loadImage(String path) {
        return new Image(Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(path), "Missing image resource: " + path));
    }
}

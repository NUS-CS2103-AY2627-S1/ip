package kotha.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import kotha.KothaEngine;
import kotha.Parser;

/** Controller for Kotha's chatbot conversation window. */
public class MainWindow extends AnchorPane {
    private static final int PROFILE_IMAGE_SIZE = 36;
    private static final Duration BYE_CLOSE_DELAY = Duration.seconds(5);

    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private Button profileButton;
    private final KothaEngine chat = new KothaEngine();
    private final Parser parser = new Parser();
    private Image userImage = createDefaultImage(Color.STEELBLUE);
    private final Image botImage = new Image(
            MainWindow.class.getResource("/images/kotha-icon.png").toExternalForm());

    @FXML private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren()
                .add(DialogBox.getBotDialog(chat.createGreeting(), botImage, chat.getLastResponseStyle()));
    }

    /** Adds the user's message and Kotha's response to the conversation. */
    @FXML private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        String response = chat.processCommand(input);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage),
                DialogBox.getBotDialog(response, botImage, chat.getLastResponseStyle()));
        userInput.clear();

        if (parser.parse(input) == Parser.CommandType.BYE) {
            closeWindowAfterGoodbye();
        }
    }

    /** Closes the GUI after allowing the goodbye response to remain visible. */
    private void closeWindowAfterGoodbye() {
        userInput.setDisable(true);
        PauseTransition closeTimer = new PauseTransition(BYE_CLOSE_DELAY);
        closeTimer.setOnFinished(event -> {
            Window window = userInput.getScene().getWindow();
            if (window != null) {
                window.hide();
            }
            Platform.exit();
        });
        closeTimer.play();
    }

    /** Lets the user choose the icon shown beside their messages. */
    @FXML private void chooseProfilePicture() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose profile picture");
        chooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        var file = chooser.showOpenDialog(profileButton.getScene().getWindow());
        if (file != null) {
            userImage = new Image(file.toURI().toString());
        }
    }

    private static Image createDefaultImage(Color color) {
        WritableImage image = new WritableImage(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE);
        for (int x = 0; x < PROFILE_IMAGE_SIZE; x++) {
            for (int y = 0; y < PROFILE_IMAGE_SIZE; y++) {
                image.getPixelWriter().setColor(x, y, color);
            }
        }
        return image;
    }
}

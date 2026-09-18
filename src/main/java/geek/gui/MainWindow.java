package geek.gui;

import java.net.URL;
import java.util.Objects;

import geek.Geek;
import geek.ui.MessageFormatter;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Coordinates user input and response dialog boxes in the main window.
 */
public class MainWindow {
    private static final String USER_IMAGE_PATH = "/images/user.png";
    private static final String GEEK_IMAGE_PATH = "/images/geek.png";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final Image userImage = loadImage(USER_IMAGE_PATH);
    private final Image geekImage = loadImage(GEEK_IMAGE_PATH);

    private Geek geek;

    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((
                observable, oldHeight, newHeight) ->
                scrollPane.setVvalue(1.0)
        );
        sendButton.disableProperty().bind(
                userInput.textProperty().isEmpty()
        );
    }

    /**
     * Supplies the application instance used to process every command.
     *
     * @param geek Geek application to use for responses.
     */
    public void setGeek(Geek geek) {
        this.geek = Objects.requireNonNull(geek);
        dialogContainer.getChildren().add(
                DialogBox.getGeekDialog(
                        MessageFormatter.welcome(),
                        geekImage
                )
        );
        Platform.runLater(userInput::requestFocus);
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();

        if (input.isEmpty()) {
            userInput.clear();
            return;
        }

        String response = geek.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getGeekDialog(response, geekImage)
        );
        userInput.clear();

        if (input.equals("bye")) {
            closeWindowAfterGoodbye();
        }
    }

    private void closeWindowAfterGoodbye() {
        PauseTransition delay = new PauseTransition(
                Duration.millis(850)
        );
        delay.setOnFinished(event -> {
            Stage stage = (Stage) userInput
                    .getScene()
                    .getWindow();
            stage.close();
        });
        delay.play();
    }

    private static Image loadImage(String path) {
        URL imageUrl = Objects.requireNonNull(
                MainWindow.class.getResource(path),
                "Missing avatar resource: " + path
        );
        return new Image(imageUrl.toExternalForm());
    }
}

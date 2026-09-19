package yuno.ui;

import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import yuno.Yuno;
import yuno.command.CommandResult;
import yuno.exception.FileStorageException;

/**
 * Controls Yuno's main JavaFX window and displays the chatbot conversation.
 */
public class MainWindow extends AnchorPane {
    /** Time for which a final message remains visible before the window closes. */
    private static final Duration WINDOW_CLOSE_DELAY = Duration.seconds(3);

    /** Profile image displayed beside user messages. */
    private final Image userImage = new Image(Objects.requireNonNull(
            getClass().getResourceAsStream("/images/User.jpg")));
    /** Profile image displayed beside Yuno messages. */
    private final Image yunoImage = new Image(Objects.requireNonNull(
            getClass().getResourceAsStream("/images/Yuno.jpg")));

    private Ui ui;
    private Yuno yuno;
    private Stage stage;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ImageView headerImage;

    /**
     * Creates the controller instantiated when the main-window FXML is loaded.
     */
    public MainWindow() {
    }

    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        configureHeaderImage();
    }

    /**
     * Displays a top-centered square crop of Yuno's profile image in the header.
     */
    private void configureHeaderImage() {
        double cropSize = Math.min(yunoImage.getWidth(), yunoImage.getHeight());
        double cropX = (yunoImage.getWidth() - cropSize) / 2;
        headerImage.setImage(yunoImage);
        headerImage.setViewport(new Rectangle2D(cropX, 0, cropSize, cropSize));
        headerImage.setClip(new Circle(22, 22, 22));
    }

    /**
     * Initializes Yuno, displays its greeting, and reports any storage error.
     *
     * @param stage Stage containing the main window.
     */
    public void initializeYuno(Stage stage) {
        this.stage = stage;
        ui = new Ui();
        ui.printGreeting();
        showYunoMessage(ui.getResponse());

        try {
            yuno = new Yuno(ui);
        } catch (FileStorageException exception) {
            ui.printException(exception.getMessage());
            showYunoMessage(ui.getResponse());
            scheduleWindowClose();
        }
    }

    @FXML
    private void handleUserInput() {
        String userText = userInput.getText().strip();
        if (userText.isEmpty() || yuno == null) {
            return;
        }

        CommandResult commandResult = yuno.handleCommand(userText);
        String yunoText = ui.getResponse();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getYunoDialog(yunoText, yunoImage));
        userInput.clear();

        if (commandResult == CommandResult.EXIT) {
            scheduleWindowClose();
        }
    }

    private void showYunoMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.getYunoDialog(message, yunoImage));
    }

    private void scheduleWindowClose() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition closeDelay = new PauseTransition(WINDOW_CLOSE_DELAY);
        closeDelay.setOnFinished(event -> stage.close());
        closeDelay.play();
    }
}

package ruby;

import java.io.InputStream;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ruby.control.DialogBox;

/**
 * Represents the main window of Ruby's graphical interface, hosting the dialog
 * area, the input field, and the send button.
 */
public class MainWindow extends AnchorPane {
    private static final String WELCOME_MESSAGE = "Hi, I'm Ruby — the gem your task list has been missing.\n"
            + "What are we polishing today?";
    private static final String RUBY_IMAGE_PATH = "/image/cat.jpg";
    private static final String USER_IMAGE_PATH = "/image/mona.jpg";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image rubyImage = loadImage(RUBY_IMAGE_PATH);
    private final Image userImage = loadImage(USER_IMAGE_PATH);

    private Ruby ruby;
    private Stage stage;

    /**
     * Binds the scroll pane to the dialog area so the newest message stays in
     * view as messages are added.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Loads the avatar image stored at the given classpath location.
     *
     * @param path Classpath location of the image file.
     * @return The loaded image, or {@code null} if the file is missing.
     */
    private static Image loadImage(String path) {
        InputStream stream = MainWindow.class.getResourceAsStream(path);
        if (stream == null) {
            return null;
        }
        return new Image(stream);
    }

    /**
     * Supplies the Ruby instance the window should talk to and greets the user.
     *
     * @param ruby instance that answers the user's commands.
     */
    public void setRuby(Ruby ruby) {
        this.ruby = ruby;
        dialogContainer.getChildren().add(DialogBox.getRubyDialog(WELCOME_MESSAGE, rubyImage));
    }

    /**
     * Supplies the stage the window appears on so it can be closed on exit.
     *
     * @param stage The stage that hosts this window.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sends the typed command to Ruby and shows the exchange in the dialog area.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = ruby.getResponse(input);
        assert response != null : "Ruby's response should not be null";
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getRubyDialog(response, rubyImage));
        userInput.clear();
        if (ruby.isExitRequested()) {
            stage.close();
        }
    }

}

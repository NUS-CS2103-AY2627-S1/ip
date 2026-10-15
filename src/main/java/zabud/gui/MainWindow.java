package zabud.gui;

import java.util.Objects;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import zabud.Zabud;

/**
 * Controls the main chat window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    /**
     * Avatar displayed beside user messages.
     */
    private final Image userImage = loadImage("/images/DaUser.png");
    /**
     * Avatar displayed beside Zabud messages.
     */
    private final Image zabudImage = loadImage("/images/DaDuke.png");

    /**
     * Command application receiving the user's input.
     */
    private Zabud zabud;

    /**
     * Keeps the conversation scrolled to its newest message.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the command application after the FXML controller is created.
     *
     * @param zabud command application used to handle input.
     */
    public void setZabud(Zabud zabud) {
        this.zabud = zabud;
        dialogContainer.getChildren().add(DialogBox.getZabudDialog(
                "Greetings, King Solomon. I am Zabud, your royal task keeper. Type 'help' to see my commands.",
                zabudImage, false));
    }

    /**
     * Adds the user's input and Zabud's response to the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        Zabud.Response response = zabud.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getZabudDialog(response.message(), zabudImage, response.isError()));
        userInput.clear();

        if (!response.shouldContinue()) {
            Platform.runLater(Platform::exit);
        }
    }

    /**
     * Loads a required classpath image.
     */
    private static Image loadImage(String path) {
        return new Image(Objects.requireNonNull(MainWindow.class.getResourceAsStream(path)));
    }
}

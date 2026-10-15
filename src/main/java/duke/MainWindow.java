package duke;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/** Controls the main Hermes chat window defined in FXML. */
public class MainWindow {

    private final Image userImage = loadImage("/images/DaUser.png");
    private final Image hermesImage = loadImage("/images/DaDuke.png");

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private HermesMini hermes;

    /** Keeps the newest message visible when the conversation grows. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot that processes commands entered in this window.
     *
     * @param hermes chatbot to connect to the GUI
     */
    public void setHermes(HermesMini hermes) {
        assert hermes != null : "The GUI requires a chatbot instance";
        this.hermes = hermes;
        dialogContainer.getChildren().add(DialogBox.getHermesDialog(hermes.getGreeting(), hermesImage));
    }

    /** Displays the user's command and the corresponding chatbot response. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        assert hermes != null : "The chatbot must be injected before handling input";
        String response = hermes.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getHermesDialog(response, hermesImage));
        userInput.clear();

        if (hermes.shouldExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        } else {
            userInput.requestFocus();
        }
    }

    /** Loads an image bundled with the application. */
    private Image loadImage(String resourcePath) {
        return new Image(MainWindow.class.getResourceAsStream(resourcePath));
    }
}

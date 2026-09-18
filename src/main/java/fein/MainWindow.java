package fein;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls Fein's FXML-based chat window. */
public class MainWindow extends AnchorPane {
    private static final String CHATBOT_IMAGE_PATH = "/images/travis_scott.png";
    private static final String WELCOME_MESSAGE = "LET'S GET LIT!\nWhat's feining champ?";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    /** The application logic that parses and executes user commands. */
    private Fein fein;

    /** The compact avatar displayed beside Fein's replies. */
    private final Image feinImage;

    /** Creates the controller and loads Fein's avatar. */
    public MainWindow() {
        feinImage = new Image(Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(CHATBOT_IMAGE_PATH),
                "Chatbot image was not found: " + CHATBOT_IMAGE_PATH));
    }

    /** Initializes the initial greeting and keeps the chat scrolled to the latest message. */
    @FXML
    public void initialize() {
        dialogContainer.getChildren().add(DialogBox.getFeinDialog(WELCOME_MESSAGE, feinImage, "welcome"));
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
    }

    /** Connects the controller to Fein's existing command-processing logic. */
    public void setFein(Fein fein) {
        // User input cannot be processed until the controller has a command processor.
        assert fein != null : "The main window must be connected to Fein";
        this.fein = fein;
    }

    /** Processes the command entered by the user and displays Fein's response. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        String response = fein.getResponse(input);
        String commandType = fein.getCommandType();
        if (!input.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        }
        dialogContainer.getChildren().add(DialogBox.getFeinDialog(response, feinImage, commandType));
        userInput.clear();

        if (commandType.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
        }
    }
}

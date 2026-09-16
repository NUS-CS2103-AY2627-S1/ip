package bags.ui;

import bags.Bags;
import bags.exception.BagsException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controls the main JavaFX window of the Bags application.
 */
public class MainWindow {

    private static final String BYE_COMMAND = "bye";
    private static final String BAGS_IMAGE_PATH = "/images/Response.png";

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    private Bags bags;
    private Image bagsImage;

    /**
     * Initializes the JavaFX interface components, loads resources, and displays greeting.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        loadImages();
        this.bags = new Bags();

        addBagsMessage("🎒 Hi! I'm your trusty bag, here to store all your tasks.");
        addBagsMessage("What can I do for you?");

        String startupMessage = bags.getStartupMessage();
        if (startupMessage != null) {
            addErrorMessage(startupMessage);
        }
    }

    private void loadImages() {
        assert getClass().getResourceAsStream(BAGS_IMAGE_PATH) != null
                : "Bags response image resource missing: " + BAGS_IMAGE_PATH;
        this.bagsImage = new Image(this.getClass().getResourceAsStream(BAGS_IMAGE_PATH));
    }

    /**
     * Handles user input entered through the text field or send button.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        addUserMessage(input);

        try {
            String response = bags.processCommand(input);
            addBagsMessage(response);

            if (BYE_COMMAND.equalsIgnoreCase(input)) {
                sendButton.setDisable(true);
                userInput.setDisable(true);
            }
        } catch (BagsException e) {
            addErrorMessage(e.getMessage());
        } finally {
            userInput.clear();
        }
    }

    private void addBagsMessage(String message) {
        DialogBox dialogBox = DialogBox.getBagsDialog(message, bagsImage);
        dialogContainer.getChildren().add(dialogBox);
    }

    private void addUserMessage(String message) {
        DialogBox dialogBox = DialogBox.getUserDialog(message);
        dialogContainer.getChildren().add(dialogBox);
    }

    private void addErrorMessage(String message) {
        DialogBox dialogBox = DialogBox.getErrorDialog(message, bagsImage);
        dialogContainer.getChildren().add(dialogBox);
    }
}

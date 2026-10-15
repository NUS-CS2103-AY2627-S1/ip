package sylveon;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controller for the main Sylveon window. */
public class MainWindow extends AnchorPane {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;

    private Sylveon sylveon;

    private final Image userImage = new Image(getClass().getResourceAsStream("/images/jigglypic.jpg"));
    private final Image sylveonImage = new Image(getClass().getResourceAsStream("/images/sylveonpic.jpg"));

    /** Keeps the conversation scrolled to the latest message. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(DialogBox.getSylveonDialog(
                "Hi! I'm Sylveon 💗 Ready to get your tasks sorted?", sylveonImage));
    }

    /**
     * Injects the Sylveon instance used to generate responses.
     *
     * @param sylveon the chatbot logic
     */
    public void setSylveon(Sylveon sylveon) {
        this.sylveon = sylveon;
    }

    /** Displays the user's message and a temporary Sylveon response. */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText().trim();
        if (userText.isEmpty()) {
            return;
        }
        String response = sylveon.getResponse(userText);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getSylveonDialog(response, sylveonImage));
        userInput.clear();

        if (new Parser().getCommandWord(userText).equals("bye")) {
            Platform.exit();
        }
    }
}

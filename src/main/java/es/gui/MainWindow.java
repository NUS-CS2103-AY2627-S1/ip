package es.gui;
import es.Es;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controller for the Es chat window. */
public class MainWindow extends AnchorPane {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    private Es es;
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image esImage = new Image(getClass().getResourceAsStream("/images/DaDuke.png"));
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the chatbot. */
    public void setEs(Es chatbot) {
        assert chatbot != null : "The GUI requires a chatbot instance";
        es = chatbot;
    }

    /** Adds Es's opening message to the conversation. */
    public void showWelcome() {
        dialogContainer.getChildren().add(DialogBox.reply(
                "Welcome to the grand library of dreams, Somniareves. What can I do for you?", esImage));
    }

    /** Handles a submitted command. */
    @FXML private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }
        String command = input.trim();
        String response = es.getResponse(input);
        dialogContainer.getChildren().addAll(DialogBox.user(input, userImage),
                DialogBox.reply(response, esImage, es.wasLastResponseAnError()));
        userInput.clear();
        if (command.equalsIgnoreCase("bye")) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}

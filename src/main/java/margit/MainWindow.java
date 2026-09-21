package margit;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls the main JavaFX chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Margit margit;
    private final Image margitImage = loadImage("/images/margit-icon.jpeg");

    /** Binds scrolling to the bottom of the dialog container. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the application instance that generates chat responses. */
    public void setMargit(Margit margit) {
        this.margit = margit;
        dialogContainer.getChildren().add(DialogBox.getMargitDialog(
                margit.getGreeting(), margitImage, false));
    }

    /** Appends the user's input and Margit's response, then clears the input field. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = margit.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getMargitDialog(response, margitImage, isErrorResponse(response)));
        userInput.clear();
    }

    /** Returns whether a response describes a rejected command or invalid input. */
    private static boolean isErrorResponse(String response) {
        return response.startsWith("A ")
                || response.startsWith("An event")
                || response.startsWith("Do not")
                || response.startsWith("Name")
                || response.startsWith("No such")
                || response.startsWith("Supply")
                || response.startsWith("That")
                || response.startsWith("Thy command")
                || response.startsWith("Thy task");
    }

    /** Loads an image bundled in the application's resources. */
    private Image loadImage(String resourcePath) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath));
    }
}

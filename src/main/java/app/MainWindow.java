package app;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import parser.Parser;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/goblin1.png"));
    private Image destroyerImage = new Image(this.getClass().getResourceAsStream("/images/DaDestroyerOfWorlds.png"));

    /**
     * Binds the scroll position and dialog width to the viewport so message bubbles
     * reflow correctly as the window is resized, then displays the welcome message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        scrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) ->
                dialogContainer.setPrefWidth(newBounds.getWidth()));
        dialogContainer.getChildren().addAll(
                DialogBox.getDestroyerOfWorldsDialog(DestroyerOfWorlds.start(), destroyerImage, false)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * the task manager's reply, then appends them to the dialog container.
     */
    @FXML
    private void handleUserInput(ActionEvent event) {
        String input = userInput.getText();
        ArrayList<Object> output = Parser.parseCommand(
            input, DestroyerOfWorlds.getTasks());
        String response = (String) output.get(0);
        DestroyerOfWorlds.updateTaskFile();
        boolean shouldClose = "bye".equals(input.trim());
        boolean isError = Parser.isErrorMessage(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDestroyerOfWorldsDialog(response, destroyerImage, isError)
        );
        userInput.clear();

        if (shouldClose) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }

    }
}

package atom.ui;

import atom.Atom;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

//@@author LucasNgui-reused
//Reused from https://se-education.org/guides/tutorials/javaFx.html
// with minor modifications
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

    private Atom atom;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image atomImage = new Image(this.getClass().getResourceAsStream("/images/DaAtom.png"));

    /**
     * Initializes the main window GUI.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Atom instance */
    public void setAtom(Atom a) {
        atom = a;
        dialogContainer.getChildren().add(
                DialogBox.getAtomDialog(a.getGreeting(), atomImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Atom's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        if (input.isEmpty()) {
            return;
        }
        String response = atom.getResponse(input);
        String responseWithEmoticon = response.startsWith("Oh no!")
                ? "⚠ " + response
                : response;
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAtomDialog(responseWithEmoticon, atomImage)
        );
        userInput.clear();
    }
}
//@@author

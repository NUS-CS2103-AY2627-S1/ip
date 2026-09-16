package anaconda.gui;

import anaconda.Anaconda;
import anaconda.ui.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final String GOODBYE_RESPONSE = "Alright, off you go. Try to get something done.";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Anaconda anaconda;

    private final Image anacondaImage = new Image(this.getClass().getResourceAsStream("/images/Anaconda.png"));

    /**
     * Shows the greeting once when the window loads and keeps the newest dialog visible.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        DialogBox greeting = DialogBox.getAnacondaDialog("Yo, it's Anaconda. What do you need?", anacondaImage);
        greeting.setResponseStatus(Anaconda.ResponseStatus.SUCCESS);
        dialogContainer.getChildren().add(greeting);
    }

    /**
     * Supplies the Anaconda instance that processes user commands.
     *
     * @param anaconda Anaconda instance shared by this window.
     */
    public void setAnaconda(Anaconda anaconda) {
        this.anaconda = anaconda;
        if (anaconda.hasLoadingError()) {
            DialogBox warning = DialogBox.getAnacondaDialog(Ui.LOADING_ERROR_MESSAGE, anacondaImage);
            warning.setResponseStatus(Anaconda.ResponseStatus.ERROR);
            dialogContainer.getChildren().add(warning);
        }
    }

    /**
     * Appends the user input and Anaconda's response to the dialog container, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        Anaconda.CommandResponse response = anaconda.getCommandResponse(input);
        DialogBox userDialog = DialogBox.getUserDialog(input);
        DialogBox anacondaDialog = DialogBox.getAnacondaDialog(response.text(), anacondaImage);
        userDialog.setResponseStatus(response.status());
        anacondaDialog.setResponseStatus(response.status());
        if (GOODBYE_RESPONSE.equals(response.text())) {
            anacondaDialog.fadeDisplayPicture();
        }
        dialogContainer.getChildren().addAll(
                userDialog,
                anacondaDialog
        );
        userInput.clear();
    }
}

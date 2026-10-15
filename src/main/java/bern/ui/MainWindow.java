package bern.ui;

import bern.logic.Controller;
import bern.logic.Response;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

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

    private Controller control;

    /**
     * Creates a main window controller whose view fields are populated later by the FXML loader.
     */
    public MainWindow() {
    }

    /**
     * Binds scrolling to the dialog container's height and displays the greeting.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        /* Greet User */
        dialogContainer.getChildren().addAll(
                DialogBox.getBernDialog(Dialog.getInstance().greetUser().toString())
        );
    }

    /**
     * Injects the Controller instance.
     *
     * @param c The controller used to process user commands.
     */
    public void setController(Controller c) {
        control = c;
    }

    /**
     * Loads saved tasks through the injected controller and displays a message when at least one task is loaded.
     */
    public void displayStartupMessages() {
        /* Load Save Data */
        // TODO: figure out how to send the error messages
        if (control.loadTasks()) {
            dialogContainer.getChildren().add(
                    DialogBox.getBernDialog(Dialog.getInstance().printTasksLoaded().toString())
            );
        }
    }

    /**
     * Processes non-blank input and appends the user's message and Bern's response nodes to the dialog container.
     * Clears the user input after processing and ignores blank input.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        assert control != null;
        Response response = control.getResponse(input);
        ObservableList<Node> children = dialogContainer.getChildren();

        children.add(DialogBox.getUserDialog(input));
        children.addAll(response.getResponseNodes());
        userInput.clear();
    }
}

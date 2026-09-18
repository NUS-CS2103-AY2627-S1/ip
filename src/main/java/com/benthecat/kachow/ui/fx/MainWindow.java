package com.benthecat.kachow.ui.fx;

import com.benthecat.kachow.Kachow;
import com.benthecat.kachow.ui.printer.FxPrinter;

import javafx.application.Platform;
import javafx.fxml.FXML;
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

    private Kachow kachow;

    /**
     * Initializes the chatbot and keeps the latest dialog visible.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        kachow = new Kachow(new FxPrinter(dialogContainer));
        kachow.sendWelcomeMessage();
    }

    /**
     * Displays the user command and Kachow's reply, then clears the input.
     * Closes the application when the command requests an exit.
     */
    @FXML
    private void handleUserInput() {
        dialogContainer.getChildren().add(DialogBox.createUserDialog(userInput.getText()));
        boolean shouldContinue = kachow.handleUserInput(userInput.getText());
        userInput.clear();
        if (!shouldContinue) {
            Platform.exit();
        }
    }
}

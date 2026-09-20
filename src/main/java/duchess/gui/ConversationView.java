package duchess.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/** A reusable JavaFX conversation area that displays dynamically-created dialog boxes. */
public class ConversationView extends ScrollPane {
    @FXML
    private VBox dialogContainer;

    /** Loads the conversation layout from its FXML view. */
    public ConversationView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ConversationView.class.getResource(
                    "/view/ConversationView.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException | NullPointerException exception) {
            throw new IllegalStateException("Unable to load the conversation resource.", exception);
        }
    }

    /** Keeps the newest dialog visible after the conversation grows. */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> setVvalue(1.0));
    }

    /**
     * Adds one or more dialog boxes to the conversation.
     *
     * @param dialogs the dialog boxes to append
     */
    public void addDialogs(DialogBox... dialogs) {
        dialogContainer.getChildren().addAll(dialogs);
    }
}

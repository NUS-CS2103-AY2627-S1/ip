package orbit.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays a wrapping message and a speaker badge using the tutorial's FXML control pattern.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Label avatar;

    private DialogBox(String message, boolean isUser) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load the conversation view.", exception);
        }
        dialog.setText(message);
        dialog.maxWidthProperty().bind(widthProperty().subtract(76));
        avatar.setText(isUser ? "You" : "O");
        avatar.setAccessibleText(isUser ? "You" : "Orbit");
        getStyleClass().add(isUser ? "user-dialog" : "orbit-dialog");
        if (isUser) {
            setAlignment(Pos.TOP_RIGHT);
            getChildren().setAll(dialog, avatar);
        } else {
            setAlignment(Pos.TOP_LEFT);
            getChildren().setAll(avatar, dialog);
            if (message.startsWith("OOPS!")) {
                avatar.setText("!");
                avatar.setAccessibleText("Orbit error");
                getStyleClass().add("error-dialog");
            }
        }
    }

    /**
     * Creates a right-aligned user message.
     *
     * @param message user command
     * @return new conversation row
     */
    public static DialogBox getUserDialog(String message) {
        return new DialogBox(message, true);
    }

    /**
     * Creates a left-aligned Orbit response.
     *
     * @param message command response
     * @return new conversation row
     */
    public static DialogBox getOrbitDialog(String message) {
        return new DialogBox(message, false);
    }
}

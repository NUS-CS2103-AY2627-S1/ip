package walnut;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private TextFlow dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a new DialogBox with the specified text and image.
     *
     * @param text the text to be displayed in the dialog box
     * @param img the image representing the speaker
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(walnut.MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            Logger.getLogger(DialogBox.class.getName())
                    .log(Level.SEVERE, "Failed to load DialogBox FXML", e);
        }

        dialog.getChildren().clear();

        Text message = new Text(text);
        message.getStyleClass().add("dialog-text");

        if (text.contains("_") && text.contains("|")) {
            message.getStyleClass().add("banner-text");
        }

        dialog.getChildren().add(message);
        dialog.getStyleClass().add("dialog-bubble");

        dialog.setMinWidth(0);
        dialog.setPrefWidth(Region.USE_COMPUTED_SIZE);
        dialog.setMaxWidth(650);

        dialog.setMinHeight(0);
        dialog.setPrefHeight(Region.USE_COMPUTED_SIZE);
        dialog.setMaxHeight(Double.MAX_VALUE);

        dialog.setLineSpacing(2);

        HBox.setHgrow(dialog, Priority.NEVER);

        displayPicture.setImage(img);
    }

    /**
     * Creates a dialog box for displaying a user's message.
     *
     * @param text the user's message
     * @param img the image representing the user
     * @return a dialog box containing the user's message
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);

        db.getChildren().setAll(db.dialog, db.displayPicture);
        db.setFillHeight(false);
        db.setAlignment(Pos.CENTER_RIGHT);

        db.setMaxWidth(Double.MAX_VALUE);
        db.setPrefWidth(Region.USE_COMPUTED_SIZE);

        db.dialog.getStyleClass().add("user-label");
        db.dialog.setTextAlignment(TextAlignment.RIGHT);
        db.dialog.setMinWidth(0);
        db.dialog.setPrefWidth(Region.USE_COMPUTED_SIZE);
        db.dialog.setMaxWidth(500);

        HBox.setHgrow(db.dialog, Priority.NEVER);

        return db;
    }

    /**
     * Creates a dialog box for displaying Walnut's response.
     *
     * @param text Walnut's response
     * @param img the image representing Walnut
     * @return a dialog box containing Walnut's response
     */
    public static DialogBox getWalnutDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);

        db.getChildren().setAll(db.displayPicture, db.dialog);
        db.setFillHeight(false);
        db.setAlignment(Pos.CENTER_LEFT);

        db.setMaxWidth(Region.USE_PREF_SIZE);
        db.setPrefWidth(Region.USE_COMPUTED_SIZE);

        db.dialog.setTextAlignment(TextAlignment.LEFT);
        db.dialog.setMinWidth(0);
        db.dialog.setPrefWidth(Region.USE_COMPUTED_SIZE);
        db.dialog.setMaxWidth(650);

        HBox.setHgrow(db.dialog, Priority.NEVER);

        db.addErrorStyleIfNeeded();

        return db;
    }

    /**
     * Applies the error CSS style to the dialog if the message text indicates an error.
     *
     * This method examines the dialog's first text node and adds the "error-label"
     * style class when the message starts with common error prefixes or contains
     * required phrases (for example, "must be").
     */
    private void addErrorStyleIfNeeded() {
        String message = dialog.getChildren()
                .stream()
                .filter(node -> node instanceof Text)
                .map(node -> ((Text) node).getText())
                .findFirst()
                .orElse("")
                .toLowerCase();

        if (message.startsWith("invalid")
                || message.startsWith("error")
                || message.startsWith("please")
                || message.startsWith("that command")
                || message.startsWith("that task")
                || message.startsWith("which task")
                || message.startsWith("the description")
                || message.startsWith("the event command")
                || message.startsWith("that deadline")
                || message.startsWith("that date")
                || message.startsWith("i could not")
                || message.startsWith("your task data")
                || message.startsWith("i did not hear")
                || message.startsWith("this task")
                || message.startsWith("that event")
                || message.contains("must be")) {
            dialog.getStyleClass().add("error-label");
        }
    }
}

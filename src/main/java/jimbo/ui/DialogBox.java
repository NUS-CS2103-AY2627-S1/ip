package jimbo.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    // styleClass handling adapted from Claude Sonnet 5 output
    private DialogBox(String text, Image img, String styleClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.getStyleClass().add(styleClass);
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(20, 20, 20));
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for the user input.
     *
     * @param text The user's input.
     * @param img The user's image.
     * @return The dialog box.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img, "user-label");
    }

    /**
     * Creates a dialog box for Jimbo's response.
     *
     * @param text Jimbo's response.
     * @param img Jimbo's image.
     * @return The dialog box.
     */
    public static DialogBox getJimboDialog(String text, Image img) {
        var db = new DialogBox(text, img, "bot-label");
        db.flip();
        return db;
    }

    /**
     * Creates a dialog box for an error.
     *
     * @param text The error message.
     * @param img Jimbo's image.
     * @return The dialog box.
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        var db = new DialogBox(text, img, "error-label");
        db.flip();
        return db;
    }
}

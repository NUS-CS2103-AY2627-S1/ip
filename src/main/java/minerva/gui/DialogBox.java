package minerva.gui;

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
 * Represents a dialog box consisting of an ImageView and a label.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
        displayPicture.setClip(new Circle(22, 22, 22));
        if (isErrorMessage(text)) {
            dialog.getStyleClass().add("error-label");
        }
    }

    /** Identifies responses that should receive error styling. */
    private boolean isErrorMessage(String text) {
        return text.startsWith("OOPS") || text.startsWith("Please")
                || text.startsWith("Sorry") || text.startsWith("No worries")
                || text.startsWith("I couldn’t") || text.startsWith("I’m sorry")
                || text.startsWith("That ") || text.startsWith("An event")
                || text.startsWith("A deadline") || text.startsWith("mark needs")
                || text.startsWith("unmark needs") || text.startsWith("delete needs")
                || text.startsWith("Missing") || text.startsWith("Deadline has")
                || text.startsWith("There’s nothing");
    }

    /** Flips the dialog box so the image is on the left and text on the right. */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    public static DialogBox getDukeDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}

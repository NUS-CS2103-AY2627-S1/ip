package kevin.javafx;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
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
        setStyle("-fx-background-color: #E8F1FF;"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: #90CAF9;"
                + "-fx-border-radius: 14;"
                + "-fx-border-width: 6.5;");
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        setStyle("-fx-background-color: #F3E5F5;"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: #CE93D8;"
                + "-fx-border-radius: 14;"
                + "-fx-border-width: 8;");
    }

    private void getErrorDialog() {
        setStyle("-fx-background-color: #FFCDD2;"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: #EF5350;"
                + "-fx-border-radius: 14;"
                + "-fx-border-width: 8;");
    }

    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, null);
        db.displayPicture.setVisible(false);
        db.displayPicture.setManaged(false);
        db.setPadding(new Insets(10, 24, 10, 12));
        return db;
    }

    public static DialogBox getKevinDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.flip();

        if (text.startsWith("???")) {
            db.getErrorDialog();
        }
        return db;
    }
}


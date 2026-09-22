package echo;

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
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    protected static Background blackBackground = new Background(new BackgroundFill(Color.BLACK,
            CornerRadii.EMPTY,
            Insets.EMPTY));

    private DialogBox(String text, Boolean isEcho) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        dialog.setBackground(blackBackground);
        double fontSize = 12.0;

        if (isEcho) {
            String fontName = "Monospaced";
            if (text.substring(5).trim().startsWith("ERROR:")) {
                dialog.setFont(Font.font(fontName, FontWeight.BOLD, fontSize));
                dialog.setTextFill(Color.RED);
            } else {
                dialog.setFont(Font.font(fontName, fontSize));
                dialog.setTextFill(Color.GREEN);
            }

        } else {
            String fontName = "Times New Roman";
            dialog.setFont(Font.font(fontName, FontWeight.BOLD, FontPosture.ITALIC, fontSize));
            dialog.setTextFill(Color.WHITE);
        }
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
     * Creates a dialog box for the user's input.
     *
     * @param text user input
     * @return the dialog box containing the user's input
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, false);
    }

    /**
     * Creates a dialog box with Echo's response, moves it to the left and flips it.
     *
     * @param text Echo's response
     * @return the dialog box with Echo's response
     */
    public static DialogBox getEchoDialog(String text) {
        var db = new DialogBox(text, true);
        db.flip();
        return db;
    }
}


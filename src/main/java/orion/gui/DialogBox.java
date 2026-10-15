package orion.gui;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Represents a dialog box consisting of an {@code ImageView} to represent the speaker's profile picture
 * and a {@code Label} containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        assert text != null : "Dialog text should not be null";
        assert image != null : "Dialog image should not be null";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> temp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(temp);
        getChildren().setAll(temp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for the user's input.
     *
     * @param text The user's input text.
     * @param image The user's display picture.
     * @return A {@code DialogBox} containing the user's text and image.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-bubble");
        Polygon tail = new Polygon(
                0.0, 0.0,
                15.0, 0.0,
                0.0, 15.0
        );
        tail.setFill(Color.web("#1D82F5"));
        dialogBox.getChildren().add(1, tail);
        HBox.setMargin(tail, new Insets(0, 0, 0, -15));
        return dialogBox;
    }

    /**
     * Creates a dialog box for Orion's response.
     *
     * @param text Orion's response text.
     * @param image Orion's display picture.
     * @return A {@code DialogBox} containing Orion's text and image, flipped to the left.
     */
    public static DialogBox getOrionDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.dialog.getStyleClass().add("orion-bubble");
        Polygon tail = new Polygon(
                15.0, 0.0,
                0.0, 0.0,
                15.0, 15.0
        );
        tail.setFill(Color.web("#E5E5EA"));
        dialogBox.getChildren().add(1, tail);
        HBox.setMargin(tail, new Insets(0, -15, 0, 0));
        return dialogBox;
    }

    /**
     * Creates a dialog box for Orion's error message.
     *
     * @param text The error message to be displayed.
     * @param image Orion's display picture.
     * @return A {@code DialogBox} containing the error text and image, flipped to the left.
     */
    public static DialogBox getOrionErrorDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.dialog.getStyleClass().add("orion-error-bubble");
        Polygon tail = new Polygon(
                15.0, 0.0,
                0.0, 0.0,
                15.0, 15.0
        );
        tail.setFill(Color.web("#E5E5EA"));
        dialogBox.getChildren().add(1, tail);
        HBox.setMargin(tail, new Insets(0, -15, 0, 0));
        return dialogBox;
    }
}

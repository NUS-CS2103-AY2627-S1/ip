package com.benthecat.kachow.ui.fx;

import java.io.IOException;
import java.util.Objects;

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

/**
 * Represents one user or Kachow message in the graphical interface.
 */
public class DialogBox extends HBox {

    private static final Image USER_IMAGE = new Image(
            Objects.requireNonNull(DialogBox.class.getResourceAsStream("/images/DaUser.png")));
    private static final Image KACHOW_IMAGE = new Image(
            Objects.requireNonNull(DialogBox.class.getResourceAsStream("/images/DaKachow.png")));

    @FXML
    private Label dialogText;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialogText.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Aligns this dialog to the left, places the portrait before the text, and applies reply styling.
     *
     * @return This dialog for use by the factory methods.
     */
    private DialogBox formatAsKachowDialog() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(children);
        this.getChildren().setAll(children);
        dialogText.getStyleClass().add("reply-label");
        return this;
    }

    /** Creates a dialog containing a message from the user. */
    public static DialogBox createUserDialog(String text) {
        return new DialogBox(text, USER_IMAGE);
    }

    /** Creates a dialog containing a response from Kachow. */
    public static DialogBox createKachowDialog(String text) {
        return new DialogBox(text, KACHOW_IMAGE).formatAsKachowDialog();
    }

    /** Creates an attention-grabbing dialog containing an error from Kachow. */
    public static DialogBox createErrorDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, KACHOW_IMAGE).formatAsKachowDialog();
        dialogBox.dialogText.getStyleClass().add("error-label");
        return dialogBox;
    }
}
